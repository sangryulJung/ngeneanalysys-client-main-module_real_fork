package ngeneanalysys.util.httpclient;

import java.io.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javafx.scene.control.Alert;
import ngeneanalysys.exceptions.WebAPIException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ngeneanalysys.util.LoggerUtil;

/**
 * HttpClient 통신 Util
 * v4.5
 * @author gjyoo
 *
 */
public class HttpClientUtil {
	private final static Logger logger = LoggerUtil.getLogger();

	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * SSL 연결 커넥션 설정 반환
	 * @return
	 */
	public static SSLConnectionSocketFactory getSSLSocketFactory() {
		SSLConnectionSocketFactory factory = null;
		try {
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			TrustManager[] trustAllCerts = new TrustManager[] {
					new X509TrustManager() {
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
			factory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
		} catch (Exception e) {
			logger.error("HttpClient SSL Connection Socket Factory Create Fail!!!");
		}
		return factory;
	}

	/**
	 * post 설정 반환
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 */
	public static HttpPost initPost(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) {
		HttpPost post = null;
		try {
			post = new HttpPost(url);
			// 지정된 헤더 삽입 정보가 있는 경우 추가
			if(headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					post.addHeader(key, headers.get(key).toString());
				}
			}

			logger.info("POST:" + post.getURI());

			if(!isJsonRequest) {
				List<NameValuePair> paramList = convertParam(params);
				post.setEntity(new UrlEncodedFormEntity(paramList, encoding));
			} else {
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonStr = objectMapper.writeValueAsString(params);
				StringEntity stringEntity = new StringEntity(jsonStr, HttpClientUtil.DEFAULT_ENCODING);
				stringEntity.setContentType("application/json");
				post.setEntity(stringEntity);
			}
		} catch (Exception e) {
			logger.error("post setting fail", e);
		}
		return post;
	}

	/**
	 * HTTPS POST
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @throws WebAPIException
	 */
	public static HttpClientResponse post(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) throws IOException, WebAPIException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpClientResponse result = null;

		try {
			HttpPost post = initPost(url, params, headers, encoding, isJsonRequest);
			httpclient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).build();
			try {
				response = httpclient.execute(post);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = getHttpClientResponse(response);
			return result;
		} catch (WebAPIException wae) {
			throw wae;
		} finally {
			if(httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * HTTPS POST
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws WebAPIException
	 */
	public static HttpClientResponse post(String url, Map<String, Object> params, Map<String, Object> headers, boolean isJsonRequest) throws IOException, WebAPIException {
		return post(url, params, headers, HttpClientUtil.DEFAULT_ENCODING, isJsonRequest);
	}

	/**
	 * get 설정 반환
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 */
	public static HttpGet initGet(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) {
		HttpGet get = null;
		try {
			String requstURL = null;
			if(!isJsonRequest) {
				// 파라미터 List 객체로 변환
				List<NameValuePair> paramList = convertParam(params);
				requstURL = url + "?" + URLEncodedUtils.format(paramList, encoding);
			} else {
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonStr = objectMapper.writeValueAsString(params);
				requstURL = url + "?" + jsonStr;
			}

			get = new HttpGet(requstURL);
			// 지정된 헤더 삽입 정보가 있는 경우 추가
			if(headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					get.addHeader(key, headers.get(key).toString());
				}
			}
			logger.info("GET:" + get.getURI());
		} catch (Exception e) {
			logger.error("get setting fail", e);
		}
		return get;
	}

	/**
	 * HTTPS GET
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 * @throws WebAPIException
	 */
	public static HttpClientResponse get(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) throws WebAPIException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpClientResponse result = null;

		try {
			HttpGet get = initGet(url, params, headers, encoding, isJsonRequest);
			httpclient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).build();
			try {
				response = httpclient.execute(get);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = getHttpClientResponse(response);
			return result;
		} catch (WebAPIException wae) {
			throw wae;
		} finally {
			if(httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static HttpClientResponse getHttpClientResponse(CloseableHttpResponse response) throws WebAPIException {
		HttpClientResponse result = null;
		String errorHeader = "Server Communication Error";
		String errorMessage = null;
		if(response != null) {
			int status = response.getStatusLine().getStatusCode();
			result = new HttpClientResponse();
			result.setStatus(response.getStatusLine().getStatusCode());

			// 응답 결과가 존재하는 경우
			if (response.getEntity() != null) {
				result.setEntity(response.getEntity());
				try {
					result.setContentString(convertReponseContent(response.getEntity().getContent()));
				} catch (UnsupportedOperationException | IOException e) {
					e.printStackTrace();
					errorMessage = String.format("Server API ERROR(%s)\n\nContents is none.", status);
					throw new WebAPIException(result, Alert.AlertType.ERROR, errorHeader, errorMessage, false);
				}
			}
			logger.debug("http client connection status : " + status);
			logger.debug(String.format("RESULT [%s]", result.getContentString()));
			if (status >= 200 && status <= 300) {
				return result;
			} else {
				logger.error(String.format("Web API RESULT STATUS = %s, CONTENTS = %s", status ,result.getContentString()));
				errorMessage = String.format("Server API ERROR(%s)\n\n%s", status, result.getContentString());
				throw new WebAPIException(result, Alert.AlertType.ERROR, errorHeader, errorMessage, false);
			}
		} else {
			errorMessage = "Server API ERROR\n\nAPI Server Connection Fail.";
			throw new WebAPIException(result, Alert.AlertType.ERROR, errorHeader, errorMessage, false);
		}
	}

	/**
	 * HTTPS GET
	 * @param url
	 * @param params
	 * @param headers
	 * @param isJsonRequest
	 * @return
	 * @throws WebAPIException
	 */
	public static HttpClientResponse get(String url, Map<String, Object> params, Map<String, Object> headers, boolean isJsonRequest) throws WebAPIException {
		return get(url, params, headers, HttpClientUtil.DEFAULT_ENCODING, isJsonRequest);
	}

	/**
	 * PUT 설정 반환
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 */
	public static HttpPut initPut(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) {
		logger.info("method : [put]");
		HttpPut httpPut = null;
		try {
			httpPut = new HttpPut(url);

			// 지정된 헤더 삽입 정보가 있는 경우 추가
			if(headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					httpPut.addHeader(key, headers.get(key).toString());
				}
			}
			logger.info("PUT:" + httpPut.getURI());

			if(!isJsonRequest) {
				List<NameValuePair> paramList = convertParam(params);
				httpPut.setEntity(new UrlEncodedFormEntity(paramList, encoding));
			} else {
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonStr = objectMapper.writeValueAsString(params);
				StringEntity stringEntity = new StringEntity(jsonStr, HttpClientUtil.DEFAULT_ENCODING);
				stringEntity.setContentType("application/json");
				httpPut.setEntity(stringEntity);
			}
		} catch (Exception e) {
			logger.error("put setting fail", e);
		}
		return httpPut;
	}

	/**
	 * HTTPS PUT
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 * @throws WebAPIException
	 */
	public static HttpClientResponse put(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) throws WebAPIException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpClientResponse result = null;

		try {
			HttpPut httpPut = initPut(url, params, headers, encoding, isJsonRequest);
			httpclient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).build();
			try {
				response = httpclient.execute(httpPut);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = getHttpClientResponse(response);
			return result;
		} catch (WebAPIException wae) {
			throw wae;
		} finally {
			if(httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * HTTPS PUT
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws WebAPIException
	 */
	public static HttpClientResponse put(String url, Map<String, Object> params, Map<String, Object> headers, boolean isJsonRequest) throws WebAPIException {
		return put(url, params, headers, HttpClientUtil.DEFAULT_ENCODING, isJsonRequest);
	}

	/**
	 * PATCH 설정 반환
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 */
	public static HttpPatch initPatch(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) {
		logger.info("method : [patch]");
		HttpPatch httpPatch = null;
		try {
			httpPatch = new HttpPatch(url);

			// 지정된 헤더 삽입 정보가 있는 경우 추가
			if(headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					httpPatch.addHeader(key, headers.get(key).toString());
				}
			}
			logger.info("PATCH:" + httpPatch.getURI());

			if(!isJsonRequest) {
				List<NameValuePair> paramList = convertParam(params);
				httpPatch.setEntity(new UrlEncodedFormEntity(paramList, encoding));
			} else {
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonStr = objectMapper.writeValueAsString(params);
				StringEntity stringEntity = new StringEntity(jsonStr, HttpClientUtil.DEFAULT_ENCODING);
				stringEntity.setContentType("application/json");
				httpPatch.setEntity(stringEntity);
			}
		} catch (Exception e) {
			logger.error("patch setting fail", e);
		}
		return httpPatch;
	}

	/**
	 * HTTPS PATCH
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 * @throws WebAPIException
	 */
	public static HttpClientResponse patch(String url, Map<String, Object> params, Map<String, Object> headers, String encoding, boolean isJsonRequest) throws WebAPIException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpClientResponse result = null;

		try {
			HttpPatch httpPatch = initPatch(url, params, headers, encoding, isJsonRequest);
			httpclient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).build();
			try {
				response = httpclient.execute(httpPatch);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = getHttpClientResponse(response);
			return result;
		} catch (WebAPIException wae) {
			throw wae;
		} finally {
			if(httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * HTTPS PATCH
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws WebAPIException
	 */
	public static HttpClientResponse patch(String url, Map<String, Object> params, Map<String, Object> headers, boolean isJsonRequest) throws WebAPIException {
		return patch(url, params, headers, HttpClientUtil.DEFAULT_ENCODING, isJsonRequest);
	}

	public static HttpDelete initDelete(String url, Map<String, Object> headers) {
		HttpDelete delete = null;
		try {
			delete = new HttpDelete(url);
			// 지정된 헤더 삽입 정보가 있는 경우 추가
			if(headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					delete.addHeader(key, headers.get(key).toString());
				}
			}
			logger.info("DELETE:" + delete.getURI());
		} catch (Exception e) {
			logger.error("delete setting fail", e);
		}
		return delete;
	}

	public static HttpClientResponse delete(String url, Map<String, Object> headers) throws WebAPIException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpClientResponse result = null;

		try {
			HttpDelete delete = initDelete(url, headers);
			httpclient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).build();
			try {
				response = httpclient.execute(delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = getHttpClientResponse(response);
			return result;
		} catch (WebAPIException wae) {
			throw wae;
		} finally {
			if(httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Convert Parameter Map to List
	 * @param params
	 * @return
	 */
	public static List<NameValuePair> convertParam(Map<String, Object> params) {
		List<NameValuePair> paramList = new ArrayList<>();
		if (params != null && params.size() > 0) {
			Iterator<String> keys = params.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
		}
		return paramList;
	}

	/**
	 * 응답 결과 문자열로 변환
	 * @param content
	 * @return
	 */
	public static String convertReponseContent(InputStream content) {
		if (content == null) return null;
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(content));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			return sb.toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
