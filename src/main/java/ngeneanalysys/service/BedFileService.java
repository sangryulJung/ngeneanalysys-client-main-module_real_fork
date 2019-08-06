package ngeneanalysys.service;

import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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
 * @since 2017-10-13
 */
public class BedFileService {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    private BedFileService() { apiService = APIService.getInstance(); }

    private static class BedFileServiceHelper{
        private BedFileServiceHelper() {}
        private static final BedFileService INSTANCE = new BedFileService();
    }

    public static BedFileService getInstance() {
        return BedFileService.BedFileServiceHelper.INSTANCE;
    }

    public HttpClientResponse uploadFile(int panelId, File file) throws WebAPIException {

        String url = "/admin/targetROIs/uploadROIFile/" + panelId;

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpClientResponse result = null;

        try {
            String connectURL = apiService.getConvertConnectURL(url);
            HttpPost post = new HttpPost(connectURL);

            Map<String, Object> headerMap = apiService.getDefaultHeaders(true);
            headerMap.remove("Content-Type");

            // 지정된 헤더 삽입 정보가 있는 경우 추가
            if(headerMap.size() > 0) {
                Iterator<Map.Entry<String, Object>> entryIterator = headerMap.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    String key = entryIterator.next().getKey();
                    post.setHeader(key, headerMap.get(key).toString());
                }
            }

            FileBody fileParam = new FileBody(file);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileParam)
                    .build();

            post.setEntity(reqEntity);

            httpclient = HttpClients.custom().setSSLSocketFactory(SSLConnectService.getInstance().getSSLFactory()).build();
            if(httpclient != null) response = httpclient.execute(post);
            if(response == null) return null;
            result = HttpClientUtil.getHttpClientResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("upload file", e);
        }

        return result;
    }

}
