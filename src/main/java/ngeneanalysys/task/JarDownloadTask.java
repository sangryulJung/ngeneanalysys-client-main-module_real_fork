package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.AnalysisDetailReportController;
import ngeneanalysys.controller.AnalysisDetailReportGermlineController;
import ngeneanalysys.model.ReportComponent;
import ngeneanalysys.service.APIService;
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

/**
 * @author Jang
 * @since 2018-02-19
 */
public class JarDownloadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private AnalysisDetailReportController controller;

    private AnalysisDetailReportGermlineController analysisDetailReportGermlineController;

    private ReportComponent component;

    /** 진행상태 박스 id */
    private String progressBoxId;

    public JarDownloadTask(AnalysisDetailReportController controller, ReportComponent component) {
        this.controller = controller;
        this.component = component;
        progressBoxId = "DOWNLOAD JAR";
    }

    public JarDownloadTask(AnalysisDetailReportGermlineController controller, ReportComponent component) {
        this.analysisDetailReportGermlineController = controller;
        this.component = component;
        progressBoxId = "DOWNLOAD JAR";
    }

    @Override
    protected Void call() throws Exception {
        if(component != null) {
            APIService apiService = APIService.getInstance();
            apiService.setStage(controller.getMainController().getPrimaryStage());

            CloseableHttpClient httpclient = null;
            CloseableHttpResponse response = null;


            String downloadUrl = "/admin/reportComponent/" + component.getId();
            //String path = CommonConstants.BASE_FULL_PATH  + File.separator + "fop" + File.separator + reportImage.getReportTemplateId() + File.separator + reportImage.getName();
            String path = CommonConstants.BASE_FULL_PATH  + File.separator + "word" + File.separator + component.getId() + File.separator + component.getName();

            File file = new File(path);

            OutputStream os = null;
            try {
                String connectURL = apiService.getConvertConnectURL(downloadUrl);

                // 헤더 삽입 정보 설정
                Map<String, Object> headerMap = apiService.getDefaultHeaders(true);

                HttpGet get = new HttpGet(connectURL);
                logger.debug("GET:" + get.getURI());

                // 지정된 헤더 삽입 정보가 있는 경우 추가
                if (headerMap != null && headerMap.size() > 0) {
                    Iterator<String> keys = headerMap.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        get.setHeader(key, headerMap.get(key).toString());
                    }
                }

                httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
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

                    os = Files.newOutputStream(Paths.get(file.toURI()));

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
        if(controller != null) {
            controller.getMainController().removeProgressTaskItemById(progressBoxId);
        } else {
            analysisDetailReportGermlineController.getMainController().removeProgressTaskItemById(progressBoxId);
        }
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        if(controller != null) {
            controller.getMainController().removeProgressTaskItemById(progressBoxId);
        } else {
            analysisDetailReportGermlineController.getMainController().removeProgressTaskItemById(progressBoxId);
        }
    }
}
