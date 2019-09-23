package ngeneanalysys.service;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * @author Jang
 * @since 2019-06-27
 */
public class SSLConnectService {

    private SSLConnectionSocketFactory factory;

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class SSLConnectHelper {
        private SSLConnectHelper() {}
        private static final SSLConnectService INSTANCE = new SSLConnectService();
    }

    public static SSLConnectService getInstance() { return SSLConnectHelper.INSTANCE; }

    private SSLConnectService() {
        SSLConnectionSocketFactory factory = null;
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    } };

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new SecureRandom());
            factory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {

        }
        this.factory = factory;
    }

    public SSLConnectionSocketFactory getSSLFactory() { return factory; }

}
