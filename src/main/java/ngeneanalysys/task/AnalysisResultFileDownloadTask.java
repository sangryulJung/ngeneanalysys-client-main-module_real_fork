package ngeneanalysys.task;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ngeneanalysys.controller.AnalysisDetailRawDataController;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2017-10-10
 */
public class AnalysisResultFileDownloadTask extends Task<Void> {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private AnalysisDetailRawDataController controller;
    /** 분석 결과 파일 객체 */
    private AnalysisFile analysisResultFile;
    /** 저장 파일 객체 */
    private File saveFile;

    /** 진행상태 박스 id */
    private String progressBoxId;
    public AnalysisResultFileDownloadTask(AnalysisDetailRawDataController controller, AnalysisFile analysisResultFile, File saveFile) {
        this.controller = controller;
        this.analysisResultFile = analysisResultFile;
        this.saveFile = saveFile;
        this.progressBoxId = "DOWN_" + analysisResultFile.getSampleId() + "_" + analysisResultFile.getId();
    }

    @Override
    protected Void call() throws Exception {
        if(analysisResultFile != null && saveFile != null) {
            APIService apiService = APIService.getInstance();
            apiService.setStage(controller.getMainController().getPrimaryStage());

            CloseableHttpClient httpclient = null;
            CloseableHttpResponse response = null;
            String downloadUrl = "/analysisFiles/" + analysisResultFile.getSampleId() + "/" +
                    analysisResultFile.getName();

            OutputStream os = null;
            try {
                String connectURL = apiService.getConvertConnectURL(downloadUrl);

                // 헤더 삽입 정보 설정
                Map<String,Object> headerMap = apiService.getDefaultHeaders(true);

                HttpGet get = new HttpGet(connectURL);
                logger.debug("GET:" + get.getURI());

                // 지정된 헤더 삽입 정보가 있는 경우 추가
                if(headerMap != null && headerMap.size() > 0) {
                    Iterator<String> keys = headerMap.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        get.setHeader(key, headerMap.get(key).toString());
                    }
                }

                httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
                if (httpclient != null)
                    response = httpclient.execute(get);
                if (response == null){
                    logger.error("httpclient response is null");
                    throw new NullPointerException();
                }
                int status = response.getStatusLine().getStatusCode();

                if(status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    long fileLength = entity.getContentLength();

                    os = Files.newOutputStream(Paths.get(saveFile.toURI()));

                    long nread = 0L;
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = content.read(buf)) > 0) {
                        if (isCancelled()) {
                            break;
                        }
                        os.write(buf, 0, n);
                        nread += n;
                        updateProgress(nread, fileLength);
                        updateMessage(String.valueOf(Math.round(((double) nread / (double) fileLength) * 100)) + "%");
                    }
                    content.close();
                    os.flush();
                    if (httpclient != null) httpclient.close();
                    if (response != null) response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(os != null) {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        //logger.error(String.format("download task fail!! [original : %s, save : %s]", anlysisResultFile.getName(), saveFile.getName()));
        controller.getMainController().removeProgressTaskItemById(progressBoxId);
        //DialogUtil.error("Failed Application Update File Download.", "An error occurred during download.\n file : " + saveFile.getName(), this.controller.getMainController().getPrimaryStage(), false);
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        //logger.debug(String.format("download task complete [original : %s, save : %s]", analysisResultFile.getName(), saveFile.getName()));
        controller.getMainController().removeProgressTaskItemById(progressBoxId);

        /*try {
            // 다운로드 파일 실행 여부 확인
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(this.controller.getMainController().getPrimaryStage());
            alert.setWidth(500);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("File downloading was completed.");
            //alert.setContentText(String.format("Do you want to check the downloaded File?\n[Filename : %s]\n ", saveFile.getName()));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                //controller.getMainApp().getHostServices().showDocument(saveFile.toURI().toURL().toExternalForm());
            } else {
                alert.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
