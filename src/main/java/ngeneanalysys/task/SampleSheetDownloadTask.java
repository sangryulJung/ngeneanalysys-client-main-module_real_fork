package ngeneanalysys.task;

import javafx.concurrent.Task;
import javafx.stage.Stage;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.SampleUploadController;
import ngeneanalysys.controller.SampleUploadScreenFirstController;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.SSLConnectService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
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
import java.util.Map;

/**
 * @author Jang
 * @since 2018-03-13
 */
public class SampleSheetDownloadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private SampleUploadController controller;

    private SampleUploadScreenFirstController sampleUploadScreenFirstController;

    private String runDir;

    public SampleSheetDownloadTask(SampleUploadController controller,
                                   SampleUploadScreenFirstController sampleUploadScreenFirstController,
                                   String runDir) {
        this.controller = controller;
        this.sampleUploadScreenFirstController = sampleUploadScreenFirstController;
        this.runDir = runDir;
    }

    @Override
    protected Void call() throws Exception {
        if(runDir != null) {
            APIService apiService = APIService.getInstance();

            CloseableHttpClient httpclient;
            CloseableHttpResponse response = null;


            //String downloadUrl = "/sampleSheet?runDir=" + runDir;
            String downloadUrl = "runDir/sampleSheet?runDir=" + runDir;
            String path = CommonConstants.BASE_FULL_PATH  + File.separator + "SampleSheet.csv";
            OutputStream os = null;
            try {
                String connectURL = apiService.getConvertConnectURL(downloadUrl);

                // 헤더 삽입 정보 설정
                Map<String, Object> headerMap = apiService.getDefaultHeaders(true);

                HttpGet get = new HttpGet(connectURL);
                logger.debug("GET:" + get.getURI());
                // 지정된 헤더 삽입 정보가 있는 경우 추가
                if(headerMap != null && headerMap.size() > 0) {
                    for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                        get.setHeader(entry.getKey(), entry.getValue().toString());
                    }
                }

                httpclient = HttpClients.custom().setSSLSocketFactory(SSLConnectService.getInstance().getSSLFactory()).build();
                if (httpclient != null)
                    response = httpclient.execute(get);
                if (response == null) {
                    logger.error("httpclient response is null");
                    throw new NullPointerException();
                }
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    long fileLength = entity.getContentLength();

                    os = Files.newOutputStream(Paths.get(path));

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
                    httpclient.close();
                    response.close();
                } else {
                    logger.debug(response.getStatusLine().toString());
                    throw new Exception("Not Found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (os != null) {
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
        //controller.getMainController().removeProgressTaskItemById(progressBoxId);
        if ("Not Found".equals(this.getException().getMessage())) {
            DialogUtil.warning("TST170 SampleSheet Download", "No sample sheet file.", (Stage)controller.getWindow(), true);
        } else {
            DialogUtil.warning("TST170 SampleSheet Download", "Sample Sheet Download faild.\n" + this.getException().getMessage(), (Stage)controller.getWindow(), true);
        }
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        String path = CommonConstants.BASE_FULL_PATH  + File.separator + "SampleSheet.csv";
        sampleUploadScreenFirstController.setSampleSheet(path);

    }

}
