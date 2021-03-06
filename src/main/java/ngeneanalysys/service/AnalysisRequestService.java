package ngeneanalysys.service;

import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.task.FileUploadTask;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import ngeneanalysys.util.httpclient.ProgressFileBody;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-22
 */
public class AnalysisRequestService {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    /**
     * 인스턴스 생성 제한
     */
    private AnalysisRequestService() {
        apiService = APIService.getInstance();
    }

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class AnalysisRequestHelper{
        private AnalysisRequestHelper() {}
        private static final AnalysisRequestService INSTANCE = new AnalysisRequestService();
    }

    /**
     * 싱글톤 객체 반환
     * @return
     */
    public static AnalysisRequestService getInstance() {
        return AnalysisRequestHelper.INSTANCE;
    }

    public HttpClientResponse uploadFile(int sampleFileServerId, File file, FileUploadTask task) throws WebAPIException {

        String url = "/analysisFiles/" + sampleFileServerId;

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpClientResponse result = null;

        try {
            String connectURL = apiService.getConvertConnectURL(url);
            HttpPut put = new HttpPut(connectURL);

            Map<String, Object> headerMap = apiService.getDefaultHeaders(true);
            headerMap.remove("Content-Type");

            // 지정된 헤더 삽입 정보가 있는 경우 추가
            if(headerMap.size() > 0) {
                Iterator<Map.Entry<String, Object>> entryIterator = headerMap.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    String key = entryIterator.next().getKey();
                    put.setHeader(key, headerMap.get(key).toString());
                }
            }

            FileBody fileParam = new ProgressFileBody(file, task);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileParam)
                    .build();

            put.setEntity(reqEntity);

            httpclient = HttpClients.custom().setSSLSocketFactory(SSLConnectService.getInstance().getSSLFactory()).build();
            if(httpclient != null) response = httpclient.execute(put);
            if(response == null) return null;
            result = HttpClientUtil.getHttpClientResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("upload file", e);
        }

        return result;
    }

    public HttpClientResponse removeRequestedJob(int id) throws WebAPIException {
        return apiService.delete("/runs/" + id);
    }
}
