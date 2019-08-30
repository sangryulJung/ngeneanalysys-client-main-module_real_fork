package ngeneanalysys.service;

import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ReportImage;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
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
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-22
 */
public class ImageUploadService {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    /**
     * 인스턴스 생성 제한
     */
    private ImageUploadService() {
        apiService = APIService.getInstance();
    }

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class ImageUploadHelper{
        private ImageUploadHelper() {}
        private static final ImageUploadService INSTANCE = new ImageUploadService();
    }

    /**
     * 싱글톤 객체 반환
     * @return
     */
    public static ImageUploadService getInstance() {
        return ImageUploadHelper.INSTANCE;
    }

    public HttpClientResponse uploadImage(int templateId, File imageFile, MainController mainController) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpClientResponse httpClientResponse = null;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", imageFile.getName());
            params.put("reportTemplateId", templateId);
            params.put("size", imageFile.length());

            httpClientResponse = apiService.post("admin/reportImage", params, null, true);

            ReportImage image = httpClientResponse.getObjectBeforeConvertResponseToJSON(ReportImage.class);

            String url = "/admin/reportImage/" + image.getId();

            String connectURL = apiService.getConvertConnectURL(url);
            HttpPut put = new HttpPut(connectURL);

            Map<String, Object> headerMap = apiService.getDefaultHeaders(true);
            headerMap.remove("Content-Type");

            // 지정된 헤더 삽입 정보가 있는 경우 추가
            if (headerMap.size() > 0) {
                for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                    put.setHeader(entry.getKey(), entry.getValue().toString());
                }
            }

            FileBody fileParam = new FileBody(imageFile);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileParam)
                    .build();

            put.setEntity(reqEntity);

            httpclient = HttpClients.custom().setSSLSocketFactory(SSLConnectService.getInstance().getSSLFactory()).build();
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
