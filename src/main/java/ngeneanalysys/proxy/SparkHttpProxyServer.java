package ngeneanalysys.proxy;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.stop;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Jang
 * @since 2017-08-07
 */
public class SparkHttpProxyServer {
    private static final Logger logger = LoggerUtil.getLogger();

    private int proxyServerPort = CommonConstants.HTTP_PROXY_SERVER_PORT;
    private String apiServerHost;
    private String authToken;

    public String getApiServerHost() {
        return apiServerHost;
    }

    public void setApiServerHost(String apiServerHost) {
        this.apiServerHost = apiServerHost;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @SuppressWarnings("satic-access")
    public void start() {

        //포트 설정
        port(proxyServerPort);

        get("/ping", (req, res) -> {
            logger.debug(String.format("api host : %s / token : %s", getApiServerHost(), getAuthToken()));
            return "Hello..";
        });

        get("/analysisFiles/:sample/:name", (request, response) -> {
                String sample = request.params(":sample");
                String name = request.params(":name");
                String url = String.format("%s/analysisFiles/%s/%s", getApiServerHost(), sample, name);
                logger.debug(String.format("URL = %s", url));
                HttpsURLConnection conn = null;
                try {
                    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    } };

                    SSLContext sc = SSLContext.getDefault().getInstance("SSL");
                    sc.init(null, trustAllCerts, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    URL obj = new URL(url);
                    conn = (HttpsURLConnection) obj.openConnection();
                    conn.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });
                    conn.setDefaultUseCaches(false);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");

                    Long bytes = calculateBytes(request.headers("Range"));

                    /**
                     Accept : text/plain
                     Cache-Control : no-cache
                     Connection : keep-alive
                     Host : 127.0.0.1:9090
                     Pragma : no-cache
                     Range : bytes=0-10
                     User-Agent : IGV Version 2.3.74 (111)05/25/2016 04:14 PM
                     */
                    if(request.headers() != null) {
                        for(String key : request.headers()) {
                            if(key.equalsIgnoreCase("range")) {
                                conn.setRequestProperty("Range", request.headers(key));
                            }
                            else if(key.equalsIgnoreCase("User-Agent")) {
                                //Akka-Http 서버가 기본 User-Agent 헤더값을 잘못된 헤더값으로 인식함.
                                conn.setRequestProperty("User-Agent", "NGeneAnalySys-IGV");
                            }
                            else if(key.equalsIgnoreCase("accept")) {
                                // 기본 Accept 헤더를 사용하면 Akka-Http 서버에서 거부함.
                            } else {
                                conn.setRequestProperty(key, request.headers(key));
                            }
                        }
                    }

                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    response.header("Transfer-Encoding", "chunked");
                    logger.debug(String.format("request bam data [%s][%s][range:%s][total:%s]", conn.getResponseCode(), url, request.headers("Range"), bytes));

                    if(conn.getResponseCode() >= 400) {
                        response.status(206);
                    } else {
                        response.status(conn.getResponseCode());
                        try (OutputStream outputStream = new BufferedOutputStream(response.raw().getOutputStream());
                             BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream())) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bufferedInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, len);
                            }
                            outputStream.flush();
                            outputStream.close();
                        }
                    }
                    response.type("application/octet-stream;charset=UTF-8");
                    response.header("Content-Transfer-Encoding", "binary");
                    response.header("Content-Length", conn.getHeaderField("Content-Length"));
                    response.header("Content-Range", conn.getHeaderField("Content-Range"));
                    response.header("Range", (String) conn.getRequestProperty("Range"));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                return response;
        });

    }

    public boolean isRunning() {
        boolean isRunning = false;
        try {
            URL url = new URL(String.format("http://localhost:%s/ping", proxyServerPort));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                isRunning = true;
            }
            connection.disconnect();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return isRunning;
    }

    /**
     *  서버 실행 중지
     */
    public void stopServer() {
        logger.debug("spark server stop...");
        stop();
    }

    /**
     * 바이트 계산
     * @param string
     * @return
     */
    private Long calculateBytes(String string) {
        if(string == null) {
            return Long.valueOf(0L);
        } else {
            String[] bits = string.replace("bytes=", "").split("-");
            Long bytes = Long.valueOf(Long.parseLong(bits[1]) - Long.parseLong(bits[0]));
            return bytes;
        }
    }
}
