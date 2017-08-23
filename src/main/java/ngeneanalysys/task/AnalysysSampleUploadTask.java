package ngeneanalysys.task;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ngeneanalysys.controller.AnalysisSampleUploadProgressTaskController;
import ngeneanalysys.service.AnalysisRequestService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

/**
 * @author Jang
 * @since 2017-08-23
 */
public class AnalysysSampleUploadTask extends Task<Void>{
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

    public void AnalysisSampleUploadTask(AnalysisSampleUploadProgressTaskController analysisSampleUploadProgressTaskController) {
        if(analysisSampleUploadProgressTaskController != null) {
            this.analysisSampleUploadProgressTaskController = analysisSampleUploadProgressTaskController;
            this.analysisRequestService = AnalysisRequestService.getInstance();
        }
    }

    /**
     * 메인 화면 상태 출력 영역 표시 메시지 update 처리
     * @param completeSampleFileSize
     * @param totalSampleFileSize
     * @param completeSampleCount
     * @param totalSampleCount
     */
    public void updateProgressInfoForMain(double completeSampleFileSize, double totalSampleFileSize, int completeSampleCount, int totalSampleCount) {
        logger.info("updateProgressInfoForMain.. ");
        // 전체 진행율 정보 update..
        updateProgress(completeSampleFileSize, totalSampleFileSize);
        // 전체 진행정보 text update..
        updateMessage(String.valueOf(completeSampleCount));
        // 현재 진행중인 그룹의 총 샘플 수 출력
        try {
            Thread.sleep(100);
            Platform.runLater(() -> {
                this.analysisSampleUploadProgressTaskController.updateTotalCount(String.valueOf(totalSampleCount));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 상세 진행정보 Dialog 업데이트..
     */
    public void updateProgressInfoForDetail() {
        try {
            Thread.sleep(100);
            Platform.runLater(() -> {
                this.analysisSampleUploadProgressTaskController.updateProgressInfoTargetDetailDialog(
                        this.currentUploadSampleFileId, this.currentUploadSampleFileProgress);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Void call() throws Exception {
        logger.info("start upload task...");


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
        super.succeeded();
    }


}
