package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-20
 */
public class ReportImageFileUploadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    private List<File> list = null;

    private int templateId = -1;

    private MainController mainController;

    private APIService apiService = null;

    public ReportImageFileUploadTask(List<File> list, int templateId, MainController mainController) {
        apiService = APIService.getInstance();
        this.list = list;
        this.templateId = templateId;
        this.mainController = mainController;
    }

    @Override
    protected Object call() throws Exception {

        String url = "/admin/reportTemplate/" + templateId + "/reportImage";

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpClientResponse result = null;

        for(File imageFile : list) {
            try {
                String connectURL = apiService.getConvertConnectURL(url);
                HttpPost post = new HttpPost(connectURL);
                //HttpPut put = new HttpPut(connectURL);

                Map<String, Object> headerMap = apiService.getDefaultHeaders(true);
                headerMap.remove("Content-Type");

                // 지정된 헤더 삽입 정보가 있는 경우 추가
                if (headerMap != null && headerMap.size() > 0) {
                    Iterator<String> keys = headerMap.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        post.setHeader(key, headerMap.get(key).toString());
                    }
                }

                FileBody fileParam = new FileBody(imageFile);

                //HttpEntity reqEntity = EntityBuilder.create()
                //        .setFile(imageFile).build();
                HttpEntity reqEntity = MultipartEntityBuilder.create()
                        .addPart("file", fileParam)
                        .build();

                post.setEntity(reqEntity);

                httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
                if (httpclient != null) response = httpclient.execute(post);
                if (response == null) return null;
                result = HttpClientUtil.getHttpClientResponse(response);

            } catch (IOException e) {
                e.printStackTrace();
                logger.error("upload file", e);
            }
        }

        return null;
    }
}
