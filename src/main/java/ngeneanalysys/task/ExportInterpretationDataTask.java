package ngeneanalysys.task;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import ngeneanalysys.MainApp;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.SSLConnectService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Variant Data Export Task
 * 
 * @author Joonsik,Jang
 * @since 2017. 2. 13.
 */
public class ExportInterpretationDataTask extends Task<Void> {

	private static Logger logger = LoggerUtil.getLogger();
	private MainApp mainApp;
	private String fileType;
	private File file;
	private Map<String, Object> params;
	/** API Service */
	private APIService apiService;

	public ExportInterpretationDataTask(MainApp mainApp, String fileType, File file, Map<String, Object> params) {
		this.fileType = fileType;
		this.file = file;
		this.params = params;
		this.mainApp = mainApp;
		// api service init..
		apiService = APIService.getInstance();
		apiService.setStage(mainApp.getPrimaryStage());
	}

	@Override
	protected Void call() throws Exception {
		updateProgress(0, 1);
		updateMessage("");
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		String downloadUrl = "/admin/clinicalVariant/genomicCoordinate/exportData";

		OutputStream os = null;
		InputStream is = null;
		try {
			String connectURL = apiService.getConvertConnectURL(downloadUrl);
			URIBuilder builder = new URIBuilder(connectURL);
			if(params != null) {
				Set<Map.Entry<String, Object>> entrySet = params.entrySet();
				for (Map.Entry<String, Object> entry : entrySet) {
					builder.setParameter(entry.getKey(), entry.getValue().toString());
				}
			}

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

			httpclient = HttpClients.custom().setSSLSocketFactory(SSLConnectService.getInstance().getSSLFactory()).build();
			if (httpclient != null)
				response = httpclient.execute(get);
			if (response == null){
				logger.error("httpclient response is null");
				throw new NullPointerException();
			}
			int status = response.getStatusLine().getStatusCode();

			if(status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				long fileLength = entity.getContentLength();
				os = Files.newOutputStream(Paths.get(file.toURI()));

				long nread = 0L;
				byte[] buf = new byte[8192];
				int n;
				while ((n = is.read(buf)) > 0) {
					if (isCancelled()) {
						break;
					}
					os.write(buf, 0, n);
					nread += n;
					updateProgress(nread, fileLength);
					updateMessage(String.valueOf(Math.round(((double) nread / (double) fileLength) * 100)) + "%");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e){}

			}
			if(os != null) {
				try {
					os.close();
				} catch (Exception e) {}
			}
			if(httpclient != null) {
				try {
					httpclient.close();
				} catch (Exception e){}
			}
		}
		return null;
	}

	/**
	 * 작업 종료시 처리
	 */
	@Override
	protected void done() {
		logger.debug("done");
	}
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#succeeded()
	 */
	@Override
	protected void succeeded() {
		logger.debug("success");
		super.succeeded();
		if(this.isCancelled()) {
			return;
		}
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			DialogUtil.setIcon(alert);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("");
			alert.setHeaderText("");
			alert.setContentText("Creating the " + fileType + " document was completed. Do you want to check the " + fileType + " document?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				this.mainApp.getHostServices().showDocument(file.toURI().toURL().toExternalForm());
			} else {
				alert.close();
			}
		} catch (Exception e){
			e.printStackTrace();
			DialogUtil.error("Save Fail.",
					"An error occurred during the creation of the " + fileType + " document.\n" + e.getMessage(),
					this.mainApp.getPrimaryStage(), false);
		}
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#failed()
	 */
	@Override
	protected void failed() {
		logger.debug("failed");
		super.failed();
		DialogUtil.error("Variant Data Export Fail.", getException().getMessage(), this.mainApp.getPrimaryStage(), false);
	}

}
