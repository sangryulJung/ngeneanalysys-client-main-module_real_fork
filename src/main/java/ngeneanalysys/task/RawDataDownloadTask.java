package ngeneanalysys.task;

import javafx.application.Platform;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.SnvTableColumnCode;
import ngeneanalysys.controller.RawDataDownloadProgressTaskController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.RawDataDownloadService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-06-25
 */
public class RawDataDownloadTask extends FileUploadTask<Void> {
    private Logger logger = LoggerUtil.getLogger();

    /** Main Controller Application Object */
    private RawDataDownloadProgressTaskController controller;

    /** 분석 샘플 요청 관리 서비스 */
    private RawDataDownloadService rawDataDownloadService;

    /** 업로드 진행 상세 메시지 Dialog 창 헤더 텍스트 */
    private String msgDialogHeader;
    /** 업로드 진행 상세 메시지 Dialog 창 메시지 내용 텍스트 */
    private String msgDialogContent;

    /** 현재 업로드 중인 요청 그룹 아이디 */
    private Integer currentDownloadGroupId;
    /** 현재 업로드 중인 요청 그룹명 */
    private String currentDownloadGroupRefName;

    private RunSampleView runSampleView;

    private File folder;

    private List<String> type;

    private boolean taskStatus = true;

    private APIService apiService;

    public RawDataDownloadTask(RawDataDownloadProgressTaskController controller,
                               RunSampleView runSampleView, File folder, List<String> type) {
        super(runSampleView.getSampleViews().size());
        this.runSampleView = runSampleView;
        this.folder = folder;
        this.type = type;
        apiService = APIService.getInstance();
        if(controller != null) {
            this.controller = controller;
            this.rawDataDownloadService = RawDataDownloadService.getInstance();
        }
    }

    @Override
    public void updateProgress(long workDone, long max) {
        long total = getNumberOfWork() * 100L;
        long complete = (long)(((double)getCompleteWorkCount() + (workDone / (double)max)) * 100);
        updateMessage(String.valueOf(getCompleteWorkCount()));
        try {
            Platform.runLater(() ->
                this.controller.updateTotalCount(String.valueOf(getNumberOfWork())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.updateProgress(complete, total);
    }

    @Override
    protected Void call() throws Exception {
        logger.debug("start upload task...");

        try {

            List<SampleView> completeSamples = new ArrayList<>();

            updateCurrentDownloadGroupInfo();

            currentDownloadGroupId = runSampleView.getRun().getId();
            currentDownloadGroupRefName = runSampleView.getRun().getName();
            List<SampleView> sampleViewList;
            if(type != null && type.size() == 1 && type.get(0).equalsIgnoreCase("xlsx")) {
                sampleViewList = new ArrayList<>();
                sampleViewList
                        .add(runSampleView.getSampleViews().stream()
                                .min(Comparator.comparing(SampleView::getId))
                                .orElse(null));
            } else {
                sampleViewList = runSampleView.getSampleViews();
            }

            if(type.contains("variant")) {
                SampleView sampleView = runSampleView.getSampleViews().stream()
                        .min(Comparator.comparing(SampleView::getId))
                        .orElse(null);
                if(sampleView != null) {
                    String[] columnList = null;
                    try {
                        HttpClientResponse response;
                        String key = getDefaultColumnOrderKey(sampleView.getPanel());
                        response = apiService.get("/member/memberOption/" + key, null, null, null);
                        if (response != null && response.getStatus() == 200) {
                            columnList = response.getContentString().split(",");
                            columnList = Arrays.stream(columnList).map(column -> column.split(":")[0])
                                    .toArray(String[]::new);
                        }
                    } catch (WebAPIException wae) {
                        if (wae.getResponse().getStatus() != 404) throw wae;
                    }

                    if(columnList == null) {
                        ResourceUtil resourceUtil = new ResourceUtil();
                        String path = resourceUtil.getDefaultColumnOrderResourcePath(sampleView.getPanel());
                        String str = PropertiesUtil.getJsonString(path);
                        List<TableColumnInfo> columnInfos = (List<TableColumnInfo>) JsonUtil.getObjectList(str, TableColumnInfo.class);
                        if(columnInfos != null) {
                            columnInfos.sort(Comparator.comparing(TableColumnInfo::getOrder));
                            columnList = columnInfos.stream().map(TableColumnInfo::getColumnName).toArray(String[]::new);
                        }

                    }
                    if(columnList != null) {
                        rawDataDownloadService.downloadRunExcel(runSampleView.getRun().getId(), sampleViewList,
                                Arrays.stream(columnList)
                                        .map(SnvTableColumnCode::getIdFromName)
                                        .collect(Collectors.joining(",")),
                                folder);
                    }
                }
                type.remove("variant");
            }

            for (SampleView sampleView : sampleViewList) {

                if(type == null || type.isEmpty()) break;

                try {
                    Map<String,Object> paramMap = new HashMap<>();
                    paramMap.put("sampleId", sampleView.getId());
                    HttpClientResponse response = apiService.get("/analysisFiles", paramMap, null, false);
                    AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);

                    List<AnalysisFile> analysisFiles = new ArrayList<>();

                    for(String singleType : type) {
                        analysisFiles.addAll(analysisFileList.getResult().stream()
                                .filter(file -> file.getFileType().equalsIgnoreCase(singleType)).collect(Collectors.toList()));
                    }

                    if(!analysisFiles.isEmpty()) {
                        final long size = analysisFiles.stream().mapToLong(AnalysisFile::getSize).sum();
                        final List<Long> completeSize = new ArrayList<>();
                        analysisFiles.forEach(file -> {

                            if (this.controller.isStop) {
                                return;
                            }
                            long sizeSum = completeSize.stream().mapToLong(Long::longValue).sum();
                            try {
                                rawDataDownloadService.downloadFile(file, folder, this, sizeSum, size);
                                completeSize.add(file.getSize());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Task 실행 Controller 객체의 업로드 일시정지 실행 여부 동기화하면서 일시정지 기능 실행.
                            synchronized (this.controller) {
                                long preCheckTimeMillis = System.currentTimeMillis();
                                while (this.controller.isPause) {
                                    // 10초 마다 체크?
                                    if((System.currentTimeMillis() - preCheckTimeMillis) == 10000) {
                                        logger.warn("pause : " + this.controller.isPause);
                                        preCheckTimeMillis = System.currentTimeMillis();
                                    }
                                    if(this.controller.isStop) {
                                        logger.warn("download work stop call.");
                                        break;
                                    }
                                }
                            }

                            // 일시정지, 재시작 대기 상태인 경우
                            if(this.controller.progressIndicator.isVisible()) {
                                this.controller.progressIndicator.setVisible(false);
                            }

                            if (this.controller.isStop) {
                                return;
                            }

                        });

                        completeSamples.add(sampleView);
                        setCompleteWorkCount(completeSamples.size());

                        // 업로드 작업 중단
                        if (this.controller.isCancel) {
                            Thread.sleep(100);
                            if (currentDownloadGroupId > 0) {
                                // 현재 업로드중인 분석 요청 그룹 데이터 삭제
                                this.controller.clearWhenUploadTaskSucceeded();
                            }
                        }
                    } else {
                        completeSamples.add(sampleView);
                        setCompleteWorkCount(completeSamples.size());
                    }

                } catch (WebAPIException wae) {
                    logger.debug(wae.getMessage());
                }
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
            e.printStackTrace();
        }


        return null;
    }

    private String getDefaultColumnOrderKey(Panel panel) {
        String key = null;
        if(PipelineCode.isHemePipeline(panel.getCode())) {
            key = "hemeColumnOrder";
        } else if(PipelineCode.isSolidPipeline(panel.getCode())) {
            key = "solidColumnOrder";
        } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
            key = "tstDNAColumnOrder";
        } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CNV_DNA_V2_SNU.getCode())) {
            key = "brcaSnuColumnOrder";
        } else if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            key = "brcaColumnOrder";
        } else if(PipelineCode.isHeredPipeline(panel.getCode())) {
            key = "heredColumnOrder";
        }
        return key;
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        logger.error("download task failed!!");
        this.taskStatus = false;
    }

    @Override
    protected void succeeded() {
        // 메인 화면 Progress Task 영역 삭제
        Platform.runLater(() -> this.controller.clearWhenUploadTaskSucceeded());

        // 업로드 작업 중단
        if(this.controller.isCancel) {
            logger.warn("complete stop download work..!!");
            // 취소 완료 Alert창 출력
            this.controller.showCancelCompleteDialog();
        } else {
            if(!this.controller.isStop) {
                logger.debug("download task work finished!!");

                if(this.taskStatus) {
                    // 완료 메시지 출력
                    DialogUtil.alert(null, "Raw Data Download Finished", this.controller.getMainController().getPrimaryStage(), false);
                } else {
                    // 오류 발생시 실패 메시지 출력
                    DialogUtil.error("Download Failed", String.format("[%s] %s", this.msgDialogHeader, this.msgDialogContent), controller.getMainController().getPrimaryStage(), false);
                }
            } else {
                logger.debug("download task work stop!!");
            }
        }
    }

    /**
     * 현재 업로드 진행중인 분석 요청 그룹 정보 전달 [MainController 화면]
     */
    private void updateCurrentDownloadGroupInfo() {
        logger.debug("updateCurrentDownloadGroupInfo..");
        try {
            Thread.sleep(50);
            Platform.runLater(() -> {
                this.controller.setCurrentUploadGroupId(currentDownloadGroupId);
                this.controller.setCurrentUploadGroupRefName(currentDownloadGroupRefName);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
