package ngeneanalysys.service;

import javafx.stage.Stage;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.LoginSession;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * API 서버 통신 서비스 클래스
 * @author Jang
 * @since 2017-08-04
 */
public class APIService {
    private static final Logger logger = LoggerUtil.getLogger();

    private CacheMemoryService cacheMemoryService = CacheMemoryService.getInstance();

    private Properties config = PropertiesService.getInstance().getConfig();

    private Stage stage;


    /**
     * 인스턴스 생성 제한
     */
    private APIService() {}

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class APIServiceHelper{
        private APIServiceHelper() {}
        private static final APIService INSTANCE = new APIService();
    }

    /**
     * 싱글톤 객체 반환
     * @return
     */
    public static APIService getInstance() {
        return APIServiceHelper.INSTANCE;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @param isJsonRequest
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse get(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException {
        logger.debug("APIService.get..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        //헤더정보 삽입
        Map<String, Object> headerMap = getDefaultHeaders(true);
        /*if(headers.isEmpty()) {
            Iterator<String> keys = headers.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headerMap.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        //HttpClientResponse res = HttpClientUtil.get(connectURL, params, headerMap, isJsonRequest);
        //return res;
        return HttpClientUtil.get(connectURL, params, headerMap, isJsonRequest);
    }

    /**
     * GET 방식 조회, 인증토큰 헤더 기본 삽입하지 않음
     * @param url
     * @param params
     * @param headers
     * @param isJsonRequest
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse getNotContainToken(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException {
        logger.debug("APIService.getNotContainToken..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        // 헤더 삽입 정보 설정
        Map<String,Object> headerMap = getDefaultHeaders(false);
        /*if(headers != null && headers.size() > 0) {
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headers.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        return HttpClientUtil.get(connectURL, params, headerMap, isJsonRequest);
    }

    public HttpClientResponse post(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException, IOException {
        logger.debug("APIService.post..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        // 헤더 삽입 정보 설정
        Map<String,Object> headerMap = getDefaultHeaders(true);
        /*if(headers != null && headers.size() > 0) {
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headers.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        return HttpClientUtil.post(connectURL, params, headerMap, isJsonRequest);
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @param isJsonRequest
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse postNotContainToken(String url, Map<String, Object> params, Map<String, Object> headers, boolean isJsonRequest) throws WebAPIException, IOException {
        logger.debug("APIService.postNotContainToken..[" + url + "]");
        String connectURL = getConvertConnectURL(url);

        //헤더 삽입 정보 설정
        Map<String, Object> headerMap = getDefaultHeaders(false);

        if(headers != null && headers.size() > 0)  headerMap.putAll(headers);

        return HttpClientUtil.post(connectURL, params, headerMap, isJsonRequest);
    }


    /**
     * [METHOD : POST] 파일 업로드
     * @param url
     * @param uploadFile
     * @param headers
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse postUploadFile(String url, File uploadFile, Map<String,Object> headers) throws WebAPIException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpClientResponse result = null;

        try {
            String connectURL = getConvertConnectURL(url);

            // 헤더 삽입 정보 설정
            Map<String,Object> headerMap = getDefaultHeaders(true);
            /*if(headers != null && headers.size() > 0) {
                Iterator<String> keys = headers.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    headerMap.put(key, headers.get(key).toString());
                }
            }*/
            if(headers != null && headers.size() > 0) headerMap.putAll(headers);

                HttpPost post = new HttpPost(connectURL);

            // 지정된 헤더 삽입 정보가 있는 경우 추가
            if(headerMap != null && headerMap.size() > 0) {
                Iterator<String> keys = headerMap.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    post.setHeader(key, headerMap.get(key).toString());
                }
            }

            // 파일 삽입
            FileEntity reqEntity = new FileEntity(uploadFile);
//			reqEntity.setContentType(ContentType.WILDCARD.getMimeType());
//			reqEntity.setChunked(false);
            post.setEntity(reqEntity);

            logger.info("POST:" + post.getURI());

            httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
            try {
                response = httpclient.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
            result = HttpClientUtil.getHttpClientResponse(response);
            return result;
        } catch (WebAPIException e) {
            throw e;
        } finally {
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (response != null){
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * PUT 방식, 인증토큰 헤더 기본 삽입
      * @param url
     * @param params
     * @param headers
     * @param isJsonRequest
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse put(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException {
        logger.debug("APIService.put..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        // 헤더 삽입 정보 설정
        Map<String,Object> headerMap = getDefaultHeaders(true);
        /*if(headers != null && headers.size() > 0) {
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headers.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        return HttpClientUtil.put(connectURL, params, headerMap, isJsonRequest);
    }

    public HttpClientResponse putNotContainToken(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException {
        logger.debug("APIService.putNotContainToken..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        // 헤더 삽입 정보 설정
        Map<String,Object> headerMap = getDefaultHeaders(false);
        /*if(headers != null && headers.size() > 0) {
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headers.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        return HttpClientUtil.put(connectURL, params, headerMap, isJsonRequest);
    }

    /**
     * PATCH 방식, 인증토큰 헤더 기본 삽입
     * @param url
     * @param params
     * @param headers
     * @param isJsonRequest
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse patch(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException {
        logger.debug("APIService.patch..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        // 헤더 삽입 정보 설정
        Map<String,Object> headerMap = getDefaultHeaders(true);
        /*if(headers != null && headers.size() > 0) {
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headers.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        return HttpClientUtil.patch(connectURL, params, headerMap, isJsonRequest);
    }

    /**
     * PATCH 방식, 인증토큰 헤더 기본 삽입하지 않음
     * @param url
     * @param params
     * @param headers
     * @param isJsonRequest
     * @return
     * @throws WebAPIException
     */
    public HttpClientResponse patchNotContainToken(String url, Map<String,Object> params, Map<String,Object> headers, boolean isJsonRequest) throws WebAPIException {
        logger.debug("APIService.patchNotContainToken..[" + url + "]");
        String connectURL = getConvertConnectURL(url);
        // 헤더 삽입 정보 설정
        Map<String,Object> headerMap = getDefaultHeaders(false);
        /*if(headers != null && headers.size() > 0) {
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headerMap.put(key, headers.get(key).toString());
            }
        }*/
        if(headers != null && headers.size() > 0) headerMap.putAll(headers);
        return HttpClientUtil.patch(connectURL, params, headerMap, isJsonRequest);
    }

    public HttpClientResponse delete(String url) throws WebAPIException {
        logger.debug("APIServcie.delete..[" + url + "]");
        String connectURL = getConvertConnectURL(url);

        Map<String, Object> headerMap = getDefaultHeaders(true);
        return HttpClientUtil.delete(connectURL, headerMap);
    }

    public Map<String, Object> getDefaultHeaders(boolean tokenContain) {
        logger.info("call getDefaultHeaders");
        Map<String,Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        if(tokenContain) {
            LoginSession loginSession = (LoginSession) cacheMemoryService.getCacheObject(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME);
            headers.put("authorization", "Bearer " + loginSession.getToken());
        }
        return headers;
    }

    public String getConvertConnectURL(String url) {
        String serverHost = config.getProperty(CommonConstants.DEFAULT_SERVER_HOST_KEY);
        if(!StringUtils.isEmpty(serverHost) && config.getProperty(CommonConstants.DEFAULT_SERVER_HOST_KEY).endsWith("/")) {
            serverHost = serverHost.substring(0, serverHost.lastIndexOf("/"));
        }
        return (!url.startsWith("/")) ? serverHost + "/" + url : serverHost + url;
    }
}
