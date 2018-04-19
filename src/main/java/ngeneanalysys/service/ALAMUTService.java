package ngeneanalysys.service;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class ALAMUTService {
    private static Logger logger = LoggerUtil.getLogger();

    private MainController mainController;

    /** ALAMUT 연동 포트 */
    private int alamutPort = 10000;

    private ALAMUTService() {}

    private static class ALAMUTHelper{
        private ALAMUTHelper(){}
        private static final ALAMUTService INSTANCE = new ALAMUTService();
    }

    public static ALAMUTService getInstance() { return ALAMUTHelper.INSTANCE; }

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
     * 어플리케이션 연결가능한지 체크 여부
     * @return
     */
    public boolean isConnect() {
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress("localhost", alamutPort), 500);
            logger.debug("alamut connect socket : " + socket);
        } catch (Exception arg3) {
            return false;
        }
        return true;
    }

    /**
     * Alamut Visual 연동
     * @param transcript
     * @param cDNA
     * @param sampleId
     * @param bamFileName
     */
    public void call(String transcript, String cDNA, String sampleId, String bamFileName) {
        try {
            if(isConnect()) {
                requestAlamutURL(transcript, cDNA, sampleId, bamFileName);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("To use this feature, first check that the external software tool you are using enables such communication with Alamut® Visual (see list below).");
                sb.append("\n\n");
                sb.append("Second, in the Options dialog box (menu 'Tools' > 'Options' > 'API' tab), check the 'Local HTTP Server Port' option and possibly change the port according to the external software specifications. Then restart Alamut® Visual ");
                DialogUtil.warning("Alamut is not running or is not listening on port 10000", sb.toString(), getMainController().getPrimaryStage(), true);
            }
        } catch (Exception e) {
            DialogUtil.error("Alamut Error", "We could not open Alamut", getMainController().getPrimaryStage(), true);
        }
    }

    /**
     * Alamut Visual 연동 URL 요청
     * @param transcript
     * @param cDNA
     * @param sampleId
     * @param bamFileName
     * @throws InterruptedException
     */
    public void requestAlamutURL(String transcript, String cDNA, String sampleId, String bamFileName) {
        String host = String.format("http://localhost:%s/show", alamutPort);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 연동 URL 호출
                    URL url = new URL(host + "?" + String.format("request=%s:%s&synchronous=true", transcript, cDNA));
                    logger.debug("first step url : " + url.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);

                    if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                        url = new URL(host + "?" + String.format("request=BAM<http://127.0.0.1:%s/analysisFiles/%s/%s", CommonConstants.HTTP_PROXY_SERVER_PORT, sampleId, bamFileName));
                        logger.debug("second step url : " + url.toString());

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setDoOutput(true);

                        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                            logger.debug("alamut second step request success!!");
                        } else {
                            logger.error("alamut second step request fail!!");
                        }
                    } else {
                        logger.error("alamut first step request fail!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void finalize() throws Throwable {
                logger.debug("alamut call thread finished..");
                super.finalize();
            }
        });
        thread.setDaemon(true);
        thread.start();

        try {
            logger.debug(String.format("Waits for this alamut call thread to die...[%s]", thread.getName()));
            thread.join();
        } catch (InterruptedException e) {
            logger.error("alamut request url thread die process exception");
            e.printStackTrace();
        }
    }
}
