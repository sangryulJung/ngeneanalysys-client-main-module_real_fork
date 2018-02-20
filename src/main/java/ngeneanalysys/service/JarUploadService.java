package ngeneanalysys.service;

import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ReportComponent;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-02-19
 */
public class JarUploadService {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    /**
     * 인스턴스 생성 제한
     */
    private JarUploadService() {
        apiService = APIService.getInstance();
    }

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class JarUploadHelper{
        private JarUploadHelper() {}
        private static final JarUploadService INSTANCE = new JarUploadService();
    }

    /**
     * 싱글톤 객체 반환
     * @return
     */
    public static JarUploadService getInstance() {
        return JarUploadHelper.INSTANCE;
    }

    public HttpClientResponse uploadJar(int templateId, File jarFile, MainController mainController) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpClientResponse httpClientResponse = null;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", jarFile.getName());
            params.put("reportTemplateId", templateId);
            params.put("size", jarFile.length());

            httpClientResponse = apiService.post("/admin/reportComponent", params, null, true);

            ReportComponent component = httpClientResponse.getObjectBeforeConvertResponseToJSON(ReportComponent.class);

            String url = "/admin/reportComponent/" + component.getId();

            String connectURL = apiService.getConvertConnectURL(url);
            HttpPut put = new HttpPut(connectURL);

            Map<String, Object> headerMap = apiService.getDefaultHeaders(true);
            headerMap.remove("Content-Type");

            // 지정된 헤더 삽입 정보가 있는 경우 추가
            if (headerMap != null && headerMap.size() > 0) {
                Iterator<String> keys = headerMap.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    put.setHeader(key, headerMap.get(key).toString());
                }
            }

            FileBody fileParam = new FileBody(jarFile);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileParam)
                    .build();

            put.setEntity(reqEntity);

            httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
            if (httpclient != null) response = httpclient.execute(put);
            if (response == null) throw new Exception();

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("upload file", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpClientResponse;
    }
}
