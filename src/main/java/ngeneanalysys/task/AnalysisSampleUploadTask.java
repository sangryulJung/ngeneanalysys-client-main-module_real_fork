package ngeneanalysys.task;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ngeneanalysys.controller.AnalysisSampleUploadProgressTaskController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.Run;
import ngeneanalysys.service.AnalysisRequestService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.LoginSessionUtil;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Jang
 * @since 2017-08-23
 */
public class AnalysisSampleUploadTask extends FileUploadTask<Void>{
    Logger logger = LoggerUtil.getLogger();

    /** Main Controller Application Object */
    private AnalysisSampleUploadProgressTaskController analysisSampleUploadProgressTaskController;

    /** 분석 샘플 요청 관리 서비스 */
    private AnalysisRequestService analysisRequestService;

    /** 업로드 진행 상세 메시지 Dialog 창 헤더 텍스트 */
    private String msgDialogHeader;
    /** 업로드 진행 상세 메시지 Dialog 창 메시지 내용 텍스트 */
    private String msgDialogContent;

    /** 현재 업로드 중인 요청 그룹 아이디 */
    private Integer currentUploadGroupId;
    private Integer currentUploadGroupServerId;
    /** 현재 업로드 중인 요청 그룹명 */
    private String currentUploadGroupRefName;
    /** 현재 업로드 중인 분석 샘플 파일의 인덱스 [Local DB] */
    private Integer currentUploadSampleFileId;
    /** 현재 업로드 중인 분석 샘플 파일의 진행률 */
    private double currentUploadSampleFileProgress;

    private boolean taskStatus = true;

    public AnalysisSampleUploadTask(AnalysisSampleUploadProgressTaskController analysisSampleUploadProgressTaskController, int totalCount) {
        super(totalCount);
        if(analysisSampleUploadProgressTaskController != null) {
            this.analysisSampleUploadProgressTaskController = analysisSampleUploadProgressTaskController;
            this.analysisRequestService = AnalysisRequestService.getInstance();
        }
    }

    @Override
    public void updateProgress(long workDone, long max) {
        long total = getNumberOfWork() * 100;
        long complete = (long)(((double)getCompleteWorkCount() + (workDone / (double)max)) * 100);
        updateMessage(String.valueOf(getCompleteWorkCount()));
        try {
            Platform.runLater(() -> {
                this.analysisSampleUploadProgressTaskController.updateTotalCount(String.valueOf(getNumberOfWork()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.updateProgress(complete, total);
    }

    @Override
    protected Void call() throws Exception {
        logger.info("start upload task...");

        try {

            String loginId = LoginSessionUtil.getAccessLoginId();

            List<AnalysisFile> fileDataList = (List<AnalysisFile>) analysisSampleUploadProgressTaskController.getParamMap().get("fileMap");

            List<File> fileList = (List<File>) analysisSampleUploadProgressTaskController.getParamMap().get("fileList");

            Run run = (Run)analysisSampleUploadProgressTaskController.getParamMap().get("run");

            currentUploadGroupId = run.getId();
            currentUploadGroupRefName = run.getName();

            List<AnalysisFile> completeFile = new ArrayList<>();

            updateCurrentUploadGroupInfo();

            for (AnalysisFile fileData : fileDataList) {
                fileList.stream().forEach(file -> {

                    if (this.analysisSampleUploadProgressTaskController.isStop) {
                       return;
                    }

                    if (fileData.getName().equals(file.getName())) {
                        try {
                            analysisRequestService.uploadFile(fileData.getId(), file, this);
                            completeFile.add(fileData);
                            setCompleteWorkCount(completeFile.size());

                        } catch (WebAPIException e) {
                            e.printStackTrace();
                        }
                    }

                    // Task 실행 Controller 객체의 업로드 일시정지 실행 여부 동기화하면서 일시정지 기능 실행.
                    synchronized (this.analysisSampleUploadProgressTaskController) {
                        long preCheckTimeMillis = System.currentTimeMillis();
                        while (this.analysisSampleUploadProgressTaskController.isPause) {
                            // 10초 마다 체크?
                            if((System.currentTimeMillis() - preCheckTimeMillis) == 10000) {
                                logger.warn("pause : " + this.analysisSampleUploadProgressTaskController.isPause);
                                preCheckTimeMillis = System.currentTimeMillis();
                            }
                            if(this.analysisSampleUploadProgressTaskController.isStop) {
                                logger.warn("upload work stop call.");
                                break;
                            }
                        }
                    }

                    // 일시정지, 재시작 대기 상태인 경우
                    if(this.analysisSampleUploadProgressTaskController.progressIndicator.isVisible()) {
                        this.analysisSampleUploadProgressTaskController.progressIndicator.setVisible(false);
                    }

                    if (this.analysisSampleUploadProgressTaskController.isStop) {
                        return;
                    }

                });

                // 업로드 작업 중단
                if (this.analysisSampleUploadProgressTaskController.isCancel) {
                    Thread.sleep(100);
                    if (currentUploadGroupId > 0) {
                        // 현재 업로드중인 분석 요청 그룹 데이터 삭제
                        //analysisRequestService.removeRequestedJob(currentUploadGroupId);
                        this.analysisSampleUploadProgressTaskController.getMainController().clearProgressTaskArea();
                    }
                }

                //completeFile.add(fileData);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        logger.error("upload task failed!!");
        this.taskStatus = false;
    }

    @Override
    protected void succeeded() {
        // 메인 화면 Progress Task 영역 삭제
        Platform.runLater(() -> {
            this.analysisSampleUploadProgressTaskController.clearWhenUploadTaskSucceeded();
        });

        // 업로드 작업 중단
        if(this.analysisSampleUploadProgressTaskController.isCancel) {
            logger.warn("complete stop upload work..!!");
            // 취소 완료 Alert창 출력
            this.analysisSampleUploadProgressTaskController.showCancelCompleteDialog();
        } else {
            if(!this.analysisSampleUploadProgressTaskController.isStop) {
                logger.info("upload task work finished!!");

                if(this.taskStatus) {
                    // 완료 메시지 출력
                    DialogUtil.alert("Analysis Request Upload Finished", "The analysis request has been completed.", this.analysisSampleUploadProgressTaskController.getMainController().getPrimaryStage(), false);
                } else {
                    // 오류 발생시 실패 메시지 출력
                    DialogUtil.error("Upload Failed", String.format("[%s] %s", this.msgDialogHeader, this.msgDialogContent), analysisSampleUploadProgressTaskController.getMainController().getPrimaryStage(), false);
                }
            } else {
                logger.info("upload task work stop!!");
            }
        }
    }

    /**
     * 현재 업로드 진행중인 분석 요청 그룹 정보 전달 [MainController 화면]
     */
    private void updateCurrentUploadGroupInfo() {
        logger.info("updateCurrentUploadGroupInfo..");
        try {
            Thread.sleep(50);
            Platform.runLater(() -> {
                this.analysisSampleUploadProgressTaskController.setCurrentUploadGroupId(currentUploadGroupId);
                this.analysisSampleUploadProgressTaskController.setCurrentUploadGroupRefName(currentUploadGroupRefName);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
