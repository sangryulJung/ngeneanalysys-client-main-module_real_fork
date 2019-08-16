package ngeneanalysys.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import ngeneanalysys.controller.AnalysisDetailReportController;
import ngeneanalysys.controller.AnalysisDetailReportGermlineController;
import ngeneanalysys.controller.AnalysisDetailTSTRNAReportController;
import ngeneanalysys.model.ReportComponent;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-02-19
 */
public class WordDownloadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private AnalysisDetailReportController controller;

    private AnalysisDetailTSTRNAReportController tstrnaReportController;

    private AnalysisDetailReportGermlineController analysisDetailReportGermlineController;

    private ReportComponent component;

    private String value;

    private File file;

    /** 진행상태 박스 id */
    private String progressBoxId;

    public WordDownloadTask(AnalysisDetailReportController controller, ReportComponent component, String value,
                            File file) {
        this.controller = controller;
        this.component = component;
        progressBoxId = "DOWNLOAD WORD";
        this.value = value;
        this.file = file;
    }

    public WordDownloadTask(AnalysisDetailReportGermlineController controller, ReportComponent component, String value,
                            File file) {
        this.analysisDetailReportGermlineController = controller;
        this.component = component;
        progressBoxId = "DOWNLOAD WORD";
        this.value = value;
        this.file = file;
    }

    public WordDownloadTask(AnalysisDetailTSTRNAReportController controller, ReportComponent component, String value,
                            File file) {
        this.tstrnaReportController = controller;
        this.component = component;
        progressBoxId = "DOWNLOAD WORD";
        this.value = value;
        this.file = file;
    }

    @Override
    protected Void call() throws Exception {
        if(component != null) {
            APIService apiService = APIService.getInstance();
            if(controller != null) {
                apiService.setStage(controller.getMainController().getPrimaryStage());
            } else if(analysisDetailReportGermlineController != null) {
                apiService.setStage(analysisDetailReportGermlineController.getMainController().getPrimaryStage());
            } else {
                apiService.setStage(tstrnaReportController.getMainController().getPrimaryStage());
            }

            CloseableHttpClient httpclient = null;
            CloseableHttpResponse response = null;


            String downloadUrl = "/reportTest/";

            OutputStream os = null;
            try {
                String connectURL = apiService.getConvertConnectURL(downloadUrl);

                // 헤더 삽입 정보 설정
                Map<String, Object> headerMap = apiService.getDefaultHeaders(true);

                HttpPost post = new HttpPost(connectURL);
                logger.debug("POST:" + post.getURI());

                // 지정된 헤더 삽입 정보가 있는 경우 추가
                if(headerMap != null && headerMap.size() > 0) {
                    for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                        post.setHeader(entry.getKey(), entry.getValue().toString());
                    }
                }

                Map<String, Object> params = new HashMap<>();
                params.put("value", value);
                params.put("reportId", component.getId());
                params.put("reportName", component.getName());

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonStr = objectMapper.writeValueAsString(params);
                StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");

                post.setEntity(stringEntity);

                httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
                if (httpclient != null)
                    response = httpclient.execute(post);
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
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(controller != null) {
                    controller.createdCheck(false, file);
                } else if(analysisDetailReportGermlineController != null) {
                    analysisDetailReportGermlineController.createdCheck(false, file);
                } else {
                    tstrnaReportController.createdCheck(false, file);
                }
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
            controller.createdCheck(false, file);
        } else if(analysisDetailReportGermlineController != null) {
            analysisDetailReportGermlineController.getMainController().removeProgressTaskItemById(progressBoxId);
            analysisDetailReportGermlineController.createdCheck(false, file);
        } else {
            tstrnaReportController.getMainController().removeProgressTaskItemById(progressBoxId);
            tstrnaReportController.createdCheck(false, file);
        }
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        if(controller != null) {
            controller.getMainController().removeProgressTaskItemById(progressBoxId);
            controller.createdCheck(true, file);
        } else if(analysisDetailReportGermlineController != null) {
            analysisDetailReportGermlineController.getMainController().removeProgressTaskItemById(progressBoxId);
            analysisDetailReportGermlineController.createdCheck(true, file);
        } else {
            tstrnaReportController.getMainController().removeProgressTaskItemById(progressBoxId);
            tstrnaReportController.createdCheck(true, file);
        }
    }
}
