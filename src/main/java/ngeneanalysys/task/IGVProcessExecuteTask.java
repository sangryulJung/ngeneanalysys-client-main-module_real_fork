package ngeneanalysys.task;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class IGVProcessExecuteTask extends Task<Void> {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private MainController controller;

    /** IGV 서비스 클래스 */
    private IGVService igvService;

    /** 프로세스 정상 실행 여부 */
    private boolean isExecute = true;

    /** 진행상태 박스 id */
    private String progressBoxId;

    public IGVProcessExecuteTask(IGVService igvService, String progressBoxId) {
        this.igvService = igvService;
        this.controller = igvService.getMainController();
        this.progressBoxId = progressBoxId;
    }

    @Override
    protected Void call() {
        try {
            updateMessage("Running to IGV Application. Please wait.");

            // java 실행 경로
            File jreBinPath = new File(CommonConstants.BASE_FULL_PATH, (CommonConstants.IS_WINDOWS) ? CommonConstants.JRE_BIN_PATH_FOR_WIN : CommonConstants.JRE_BIN_PATH_FOR_MAC);
            // igv jar 파일 경로
            File igvPath = new File(CommonConstants.BASE_FULL_PATH, "IGV_2.4.1");
            // igv jar list array
            String[] jarArr = new String[]{"batik-codec__V1.7.jar", "goby-io-igv__V1.0.jar", "igv.jar"};

            List<String> cmdArray = new ArrayList<>();
            if(CommonConstants.IS_WINDOWS) {
                cmdArray.add(jreBinPath.getAbsolutePath() + "/java.exe");
            } else {
                cmdArray.add(jreBinPath.getAbsolutePath() + "/java");
            }
            // 메모리 설정
            cmdArray.add("-Xmx700M");
            // 클래스패스 설정
            cmdArray.add("-cp");
            StringBuilder classPathSb = new StringBuilder();
            int idx = 0;
            for (String jar : jarArr) {
                if(idx > 0) {
                    classPathSb.append(File.pathSeparator);
                }
                classPathSb.append(new File(igvPath, jar).getAbsolutePath());
                idx++;
            }
            cmdArray.add(classPathSb.toString());
            // 실행 클래스 설정
            cmdArray.add("org.broad.igv.ui.Main");

            try {
                logger.debug(String.format("CMD %s", cmdArray.toString()));
                ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
                processBuilder.redirectErrorStream(true);
                processBuilder.redirectOutput(new File(igvPath, "igv-launch.log"));
                processBuilder.start();
                Thread.sleep(100L);
            } catch (Exception e) {
                isExecute = false;
                logger.debug("igv launch fail.." + e.getMessage());
                e.printStackTrace();
            }

            // 정상실행된 경우.
            if(isExecute) {
                // IGV 어플리케이션이 실행되는 동안 기다림.
                int delaySecond = 60;
                for(int i = 0; i < delaySecond; i++) {
                    boolean isRunning = igvService.isRunningIGVApp();
                    if(isRunning) {
                        break;
                    } else {
                        logger.debug(String.format("waiting for igv application to running..[%s]", i));
                        try {
                            Thread.sleep(1000L);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        logger.error(String.format("igv process execute task fail!!"));
        DialogUtil.error("IGV Application Running Fail", "An error occurred during the application start-up.", controller.getMainApp().getPrimaryStage(), true);
        controller.removeProgressTaskItemById(progressBoxId);
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        logger.debug("igv process execute task complete");
        // 정상실행된 경우.
        if(isExecute) {
            // IGV 어플리케이션 연동 요청
            try {
                igvService.requestAndLauncherThreadStop();
            } catch (WebAPIException wae) {
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                        controller.getPrimaryStage(), true);
            } catch (Exception e) {
                DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch\n" + e.getMessage(),
                        controller.getPrimaryStage(), true);
            }
        } else {
            DialogUtil.error("IGV Application Running Fail", "An error occurred during the application start-up.", controller.getMainApp().getPrimaryStage(), true);
        }
        controller.removeProgressTaskItemById(progressBoxId);
    }
}
