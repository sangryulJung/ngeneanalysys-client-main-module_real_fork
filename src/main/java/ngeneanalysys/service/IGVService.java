package ngeneanalysys.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisFileList;
import ngeneanalysys.task.IGVInstallTask;
import ngeneanalysys.task.IGVProcessExecuteTask;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.slf4j.Logger;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class IGVService {
    private static Logger logger = LoggerUtil.getLogger();

    private MainController mainController;

    /** igv library path */
    private File igvPath = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.IGV_PATH);

    /** jre bin path */
    private File jreBinPath = new File(CommonConstants.BASE_FULL_PATH, (CommonConstants.IS_WINDOWS) ? CommonConstants.JRE_BIN_PATH_FOR_WIN : CommonConstants.JRE_BIN_PATH_FOR_MAC);

    /** igv 연동 포트 */
    private int igvPort = 60151;

    private boolean isStartOfIGV = false;

    /** igv 어플리케이션 실행 쓰레드 */
    private Thread igvProcessExecuteThread;

    /** 마지막 출력 bam 파일 경로 */
    private AtomicReference<String> currentPath = new AtomicReference<>();

    /** 분석 샘플 ID */
    private String sampleId;
    /** 분석 샘픔명 */
    private String sampleName;
    /** 변이 ID*/
    private String variantId;
    /** 분석 샘플의 BAM 파일명 */
    private String bamFileName;
    /** Gene */
    private String gene;
    /** 확대 위치 */
    private String locus;
    /** 분석 Human Reference Version */
    private String genome;
    private APIService apiService = null;

    private IGVService() {}

    private static class IGVHelper{
        private IGVHelper() {}
        private static final IGVService INSTANCE = new IGVService();
    }

    public static IGVService getInstance() {
        return IGVHelper.INSTANCE;
    }

    /**
     * @return the mainController
     */
    public MainController getMainController() {
        return mainController;
    }
    /**
     * @param mainController the mainController to set
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * 파일 다운로드
     * @param jreInstall boolean
     * @param igvInstall boolean
     */
    @SuppressWarnings("static-access")
    private void install(boolean jreInstall, boolean igvInstall) {
        logger.debug(String.format("JRE Install : %s, IGV Install : %s", jreInstall, igvInstall));

        String progressBoxId = "DOWN_" + System.currentTimeMillis();

        // 다운로드 작업 객체 생성
        Task<Void> task = new IGVInstallTask(this, jreInstall, igvInstall, progressBoxId);
        // 다운로드 작업 객체 실행 쓰레드 생성
        Thread downloadThread = new Thread(task);

        // 메인화면 진행상태 영역 화면 객체
        HBox mainProgressTaskPane = getMainController().getProgressTaskContentArea();
        // 다운로드 진행삳태 출력 객체
        HBox box = new HBox();
        box.setId(progressBoxId);
        box.getStyleClass().add("general_progress");

        if(mainProgressTaskPane.getChildren().size() > 0) {
            Label separatorLabel = new Label("|");
            box.getChildren().add(separatorLabel);
            box.setMargin(separatorLabel, new Insets(0, 0, 0, 5));
        }

        // 타이틀 : 다운로드 파일명 출력
        Label titleLabel = new Label();
        titleLabel.textProperty().bind(task.titleProperty());
        // 다운로드 진행 상태바
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(task.progressProperty());
        // 다운로드 진행상태 메시지 라벨
        Label messageLabel = new Label();
        messageLabel.textProperty().bind(task.messageProperty());
        // 다운로드 취소 버튼
        Button cancelButton = new Button("cancel");
        cancelButton.getStyleClass().add("btn_cancel_normal");
        cancelButton.setOnAction(event -> {
            try {
                logger.error("igv install cancel..");
                Thread.sleep(100);
                Platform.runLater(() -> {
                    downloadThread.interrupt();
                    task.cancel();
                    getMainController().removeProgressTaskItemById(progressBoxId);
                });
            } catch (Exception e) {
                logger.error("download cancel failed!!");
                DialogUtil.error("File Download Cancel Fail.", "An error occurred during the cancel file download.", getMainController().getPrimaryStage(), false);
            }
            // 연동 진행 여부값 초기화
            isStartOfIGV = false;
        });

        box.getChildren().add(titleLabel);
        box.setMargin(titleLabel, new Insets(0, 0, 0, 5));
        box.getChildren().add(progressBar);
        box.setMargin(progressBar, new Insets(0, 0, 0, 5));
        box.getChildren().add(messageLabel);
        box.setMargin(messageLabel, new Insets(0, 0, 0, 5));
        box.getChildren().add(cancelButton);
        box.setMargin(cancelButton, new Insets(0, 0, 0, 5));

        // 메인 화면 Progress Task 영역에 화면 출력
        mainProgressTaskPane.getChildren().add(box);

        // Thread 실행
        downloadThread.setDaemon(true);
        downloadThread.start();
    }

    /**
     * IGV 어플리케이션 구동여부 체크
     * @return boolean
     */
    public boolean isRunningIGVApp() {
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress("localhost", igvPort), 500);
        } catch (Exception arg3) {
            return false;
        }
        return true;
    }

    /**
     * IGV 어플리케이션 실행.
     */
    @SuppressWarnings("static-access")
    public void launchIGV() throws Exception {
        boolean isRunningIGV = isRunningIGVApp();
        if(!isRunningIGV) {
            logger.debug("launch igv application start.");
            if(igvProcessExecuteThread == null) {
                // 초기화
                this.currentPath.set(null);
                String progressBoxId = "IGV_RUNNING_" + System.currentTimeMillis();

                // Task 생성
                Task<Void> task = new IGVProcessExecuteTask(this, progressBoxId);
                // 쓰레드 생성
                igvProcessExecuteThread = new Thread(task);

                // 메인화면 진행상태 영역 화면 객체
                HBox mainProgressTaskPane = getMainController().getProgressTaskContentArea();
                // 다운로드 진행삳태 출력 객체
                HBox box = new HBox();
                box.setId(progressBoxId);
                box.getStyleClass().add("general_progress");

                if(mainProgressTaskPane.getChildren().size() > 0) {
                    Label separatorLabel = new Label("|");
                    box.getChildren().add(separatorLabel);
                    box.setMargin(separatorLabel, new Insets(0, 0, 0, 5));
                }

                // 타이틀 : 다운로드 파일명 출력
                Label titleLabel = new Label();
                titleLabel.textProperty().bind(task.messageProperty());
                // Progress Indicator
                ProgressIndicator progressIndicator = new ProgressIndicator();

                box.getChildren().add(titleLabel);
                box.setMargin(titleLabel, new Insets(0, 0, 0, 5));
                box.getChildren().add(progressIndicator);

                // 메인 화면 Progress Task 영역에 화면 출력
                mainProgressTaskPane.getChildren().add(box);

                // Thread 실행
                igvProcessExecuteThread.setDaemon(true);
                igvProcessExecuteThread.start();
            } else {
                DialogUtil.alert("The application is currently running.", "The current application is running. Please wait.", getMainController().getPrimaryStage(), true);
            }
        } else {
            logger.debug("aleady running to igv application.");
            request();
        }
    }

    /**
     * IGV 어플리케이션 실행 쓰레드 종료 후 연동 요청
     */
    public void requestAndLauncherThreadStop() throws Exception {
        if(igvProcessExecuteThread != null) {
            igvProcessExecuteThread.interrupt();
            igvProcessExecuteThread = null;
        }
        request();
    }

    /**
     * IGV 어플리케이션 연동 요청
     */
    public void request() throws Exception {
        HttpClientResponse response = null;
        long finalBamFileCount;
        //GATK4 Mutect2 Bam related file count
        long mutectBamFileCount;
        //GATK4 Haplotypecaller Bam related file count
        long hcBamFileCount;
        // Normal Sample Bam file count
        long normalBamFileCount;
        // Check whether Mutec2 or HaplotypeCaller Bam file exists.
        try {
            apiService = APIService.getInstance();
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("sampleId", this.sampleId);
            response = apiService.get("/analysisFiles", paramMap, null, false);
            AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);
            finalBamFileCount = analysisFileList.getResult().stream().filter(f ->
                    f.getName().endsWith("final.bam") || f.getName().endsWith("final.bam.bai") ||
                            f.getName().endsWith("final.bam.tdf")).count();
            mutectBamFileCount = analysisFileList.getResult().stream().filter(f ->
                    f.getName().endsWith("mutect.bam") || f.getName().endsWith("mutect.bam.bai") ||
                            f.getName().endsWith("mutect.bam.tdf")).count();
            hcBamFileCount = analysisFileList.getResult().stream().filter(f ->
                    f.getName().endsWith("hc.bam") || f.getName().endsWith("hc.bam.bai") ||
                            f.getName().endsWith("hc.bam.tdf")).count();
            normalBamFileCount = analysisFileList.getResult().stream().filter(f ->
                    f.getName().endsWith("normal.bam") || f.getName().endsWith("normal.bam.bai") ||
                            f.getName().endsWith("normal.bam.tdf")).count();
        } catch (Exception e) {
            finalBamFileCount = 0;
            mutectBamFileCount = 0;
            hcBamFileCount = 0;
            normalBamFileCount = 0;
            logger.error("Fail to get IGV viewing file list", e);
        }
        try {
            logger.info(String.format("request igv [sample id : %s, bam file : %s, genome : %s, locus : %s, gene : %s]", this.sampleId, this.bamFileName, this.genome, this.locus, this.gene));

            //String name = String.format("[%s:%s:%s]", this.sampleId, this.sampleName, this.variantId);
            String finalBamFileUrl = String.format("http://127.0.0.1:%s/analysisFiles/%s/%s", CommonConstants.HTTP_PROXY_SERVER_PORT, this.sampleId, this.bamFileName + "_final.bam");
            String mutectBamFileUrl = String.format("http://127.0.0.1:%s/analysisFiles/%s/%s", CommonConstants.HTTP_PROXY_SERVER_PORT, this.sampleId, this.bamFileName + "_mutect.bam");
            String hcBamFileUrl = String.format("http://127.0.0.1:%s/analysisFiles/%s/%s", CommonConstants.HTTP_PROXY_SERVER_PORT, this.sampleId, this.bamFileName + "_hc.bam");
            String normalBamFileUrl = String.format("http://127.0.0.1:%s/analysisFiles/%s/%s", CommonConstants.HTTP_PROXY_SERVER_PORT, this.sampleId, this.bamFileName + "_normal.bam");
            Map<String,Object> params = new HashMap<>();
            if (finalBamFileCount == 3 && mutectBamFileCount == 3 && normalBamFileCount == 3) {
                params.put("file", mutectBamFileUrl + "," + finalBamFileUrl + "," + normalBamFileUrl);
            } if (finalBamFileCount == 3 && mutectBamFileCount == 3) {
                params.put("file", mutectBamFileUrl + "," + finalBamFileUrl);
            } else if (finalBamFileCount == 3 && hcBamFileCount == 3) {
                params.put("file", hcBamFileUrl + "," + finalBamFileUrl);
            } else if (finalBamFileCount == 3) {
                params.put("file", finalBamFileUrl);
            } else {
                throw new Exception("Can not find BAM file.");
            }

            //params.put("name", name);
            params.put("genome", this.genome);
            params.put("merge", "false");
            if(StringUtils.isNotEmpty(this.gene)) {
                params.put("locus", this.gene);
            }
            if(StringUtils.isNotEmpty(this.locus)) {
                params.put("locus", this.locus);
            }

            boolean isSame = finalBamFileUrl.equals(this.currentPath.get());
            this.currentPath.set(finalBamFileUrl);

            String cmd = (isSame) ? "goto" : "load";
            String host = String.format("http://localhost:%s/%s", igvPort, cmd);

            response = HttpClientUtil.get(host, params, null, false);

            if(response.getStatus() < 200 || response.getStatus() > 300) {
                logger.error("igv request fail!!");
            }
        } catch (WebAPIException wae){
            throw new WebAPIException(response, Alert.AlertType.ERROR, "IGV connection fail", "Check IGV software", false);
        } catch (Exception e) {
            throw e;
        } finally {
            // 연동 진행 여부값 초기화
            isStartOfIGV = false;
        }
    }

    /**
     * IGV 연동
     * @param sampleId String
     * @param sampleName String
     * @param gene String
     * @param locus String
     * @param genome String
     * @throws WebAPIException
     */
    public void load(String sampleId, String sampleName, String variantId, String gene, String locus, String genome) throws Exception {
        // 현재 IGV 연동중이지 않은 경우 연동 시작함.
        if(!isStartOfIGV) {
            isStartOfIGV = true;
            logger.debug(String.format("jre binary path : %s", jreBinPath.getAbsolutePath()));
            this.sampleId = sampleId;
            this.sampleName = sampleName;
            this.variantId = variantId;
            //this.bamFileName = sampleName + "_final.bam";
            this.bamFileName = sampleName;
            this.gene = gene;
            this.locus = locus;
            this.genome = genome;

            // JRE 존재여부
            boolean isExistJRE = this.jreBinPath.exists();
            // IGV 존재여부
            boolean isExistIGV = this.igvPath.exists();

            // JRE와 IGV가 모두 설치되지 않은 경우 설치 실행.
            logger.debug(String.format("JRE Installed : %s, IGV Installed : %s", isExistJRE, isExistIGV));
            if(!isExistJRE || !isExistIGV) {
                // JRE 설치실행여부 true시 설치 실행
                boolean isJREInstall = !isExistJRE;
                // IGV 설치실행여부 true시 설치 실행
                boolean isIGVInstall = !isExistIGV;
                install(isJREInstall, isIGVInstall);
            } else {
                launchIGV();
            }
        } else {
            DialogUtil.warning("IGV Link", "This software are launching IGV\nPlease Wait 60 seconds", getMainController().getPrimaryStage(), true);
        }
    }
}
