package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.controller.AnalysisDetailRawDataController;
import ngeneanalysys.model.AnalysisFile;
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
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-10-10
 */
public class AnalysisResultFileDownloadTask extends Task<Void> {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private AnalysisDetailRawDataController controller;
    /** 분석 결과 파일 객체 */
    private List<AnalysisFile> analysisResultFiles;
    /** 저장 파일 객체 */
    private File downloadDirectory;

    /** 진행상태 박스 id */
    private String taskID;
    public AnalysisResultFileDownloadTask(AnalysisDetailRawDataController controller, List<AnalysisFile> analysisResultFiles, File downloadDirectory, String taskID) {
        this.controller = controller;
        this.analysisResultFiles = analysisResultFiles;
        this.downloadDirectory = downloadDirectory;
        this.taskID = taskID;
    }

    @Override
    protected Void call() throws Exception {
        if(analysisResultFiles != null && !analysisResultFiles.isEmpty() && downloadDirectory != null) {
            APIService apiService = APIService.getInstance();
            SSLConnectService sslConnectService = SSLConnectService.getInstance();
            apiService.setStage(controller.getMainController().getPrimaryStage());
            double downloadFileIndex = 0;
            double totalDownlodFileCount = analysisResultFiles.size();
            for(AnalysisFile analysisResultFile : analysisResultFiles) {
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
                        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                            get.setHeader(entry.getKey(), entry.getValue().toString());
                        }
                    }

                    httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectService.getSSLFactory()).build();
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
                        double fileLength = (double)(entity.getContentLength());

                        os = Files.newOutputStream(Paths.get(downloadDirectory.getAbsolutePath(), analysisResultFile.getName()));

                        double nread = 0L;
                        byte[] buf = new byte[8192];
                        int n;
                        while ((n = content.read(buf)) > 0) {
                            if (isCancelled()) {
                                break;
                            }
                            os.write(buf, 0, n);
                            nread += n;
                            updateProgress(((nread / fileLength) + downloadFileIndex) / totalDownlodFileCount, 1.0);
                            updateMessage(String.valueOf(Math.round(((nread / fileLength) + downloadFileIndex) / totalDownlodFileCount * 100.0)) + "%");
                        }
                        content.close();
                        os.flush();
                        httpclient.close();
                        response.close();
                        downloadFileIndex += 1;
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
        }

        return null;
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        controller.getMainController().removeProgressTaskItemById(taskID);
        getException().printStackTrace();
        DialogUtil.showWebApiException(getException(), this.controller.getMainController().getPrimaryStage());
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        controller.getMainController().removeProgressTaskItemById(taskID);
    }
}
