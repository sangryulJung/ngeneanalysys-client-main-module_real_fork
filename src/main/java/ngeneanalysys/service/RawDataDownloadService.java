package ngeneanalysys.service;

import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.task.RawDataDownloadTask;
import ngeneanalysys.util.LoggerUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Jang
 * @since 2018-06-25
 */
public class RawDataDownloadService {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    private SSLConnectService sslConnectService;

    private RawDataDownloadService() {
        apiService = APIService.getInstance();
        sslConnectService = SSLConnectService.getInstance();
    }

    private static class RawDataDownloadServiceHelper{
        private RawDataDownloadServiceHelper() {}
        private static final RawDataDownloadService INSTANCE = new RawDataDownloadService();
    }

    public static RawDataDownloadService getInstance() {
        return RawDataDownloadService.RawDataDownloadServiceHelper.INSTANCE;
    }

    public boolean downloadFile(AnalysisFile analysisResultFile, File folder,
                                RawDataDownloadTask task, Long start, Long end) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        if(analysisResultFile != null && folder != null) {
            String downloadUrl = "/analysisFiles/" + analysisResultFile.getSampleId() + "/" +
                    analysisResultFile.getName();

            File file = new File(folder + File.separator + analysisResultFile.getName());

            OutputStream os = null;

            String connectURL = apiService.getConvertConnectURL(downloadUrl);
            try {
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

                    os = Files.newOutputStream(Paths.get(file.toURI()));

                    long nread = 0L;
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = content.read(buf)) > 0) {
                        os.write(buf, 0, n);
                        nread += n;
                        task.updateProgress(start + nread , end);
                    }
                    content.close();
                    os.flush();
                    if (httpclient != null) httpclient.close();
                    if (response != null) response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if(os != null) {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean downloadRunExcel(Integer runId, List<SampleView> sampleViews, String field, File folder) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        if(PipelineCode.isBRCAPipeline(sampleViews.get(0).getPanel().getCode())) {
            field = "sampleName,enigmaPathogenicity,acmg,acmgCriteria," + field;
        } else if(PipelineCode.isHeredPipeline(sampleViews.get(0).getPanel().getCode())) {
            field = "sampleName,acmg,acmgCriteria," + field;
        } else {
            field = "sampleName,evidence," + field;
        }
        if(sampleViews != null && folder != null) {
            String downloadUrl = "runSnpInDels";

            File file = new File(folder + File.separator +
                    sampleViews.get(0).getRun().getName().replace("\\p{Z}", "_") + "_variant.xlsx");

            OutputStream os = null;

            String connectURL = apiService.getConvertConnectURL(downloadUrl);
            try {
                URIBuilder builder = new URIBuilder(connectURL);
                builder.addParameter("runId", runId.toString());
                builder.addParameter("fields", field);
                try {
                    // 헤더 삽입 정보 설정
                    Map<String,Object> headerMap = apiService.getDefaultHeaders(true);

                    HttpGet get = new HttpGet(builder.build());
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

                        os = Files.newOutputStream(Paths.get(file.toURI()));

                        long nread = 0L;
                        byte[] buf = new byte[8192];
                        int n;
                        while ((n = content.read(buf)) > 0) {
                            os.write(buf, 0, n);
                            nread += n;
                        }
                        content.close();
                        os.flush();
                        if (httpclient != null) httpclient.close();
                        if (response != null) response.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    if(os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        return true;
    }

    public ByteArrayInputStream getImageStream(AnalysisFile analysisResultFile) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = null;
        if(analysisResultFile != null) {
            String downloadUrl = "/analysisFiles/" + analysisResultFile.getSampleId() + "/" +
                    analysisResultFile.getName();

            String connectURL = apiService.getConvertConnectURL(downloadUrl);
            try {
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

                    long nread = 0L;
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = content.read(buf)) > 0) {
                        os.write(buf, 0, n);
                        nread += n;
                    }
                    content.close();
                    os.flush();
                    inputStream = new ByteArrayInputStream(os.toByteArray());
                    httpclient.close();
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return inputStream;
            } finally {
                if(os != null) {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return inputStream;
                    }
                }
            }
        } else {
            return inputStream;
        }

        return inputStream;
    }

}
