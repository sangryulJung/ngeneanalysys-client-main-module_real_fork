package ngeneanalysys.task;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.NullResponseException;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.service.SSLConnectService;
import ngeneanalysys.util.CompressUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class IGVInstallTask extends Task<Void> {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private MainController controller;

    /** IGV 서비스 클래스 */
    private IGVService igvService;

    private APIService apiService;

    private File userBasePath = new File(CommonConstants.BASE_FULL_PATH);

    /** JRE 설치 여부 */
    private boolean jreInstall;

    /** IGV 설치 여부 */
    private boolean igvInstall;

    /** 진행상태 박스 id */
    private String progressBoxId;

    public IGVInstallTask(IGVService igvService, boolean jreInstall, boolean igvInstall, String progressBoxId) {
        this.igvService = igvService;
        this.controller = igvService.getMainController();
        this.jreInstall = jreInstall;
        this.igvInstall = igvInstall;
        this.progressBoxId = progressBoxId;
    }

    @Override
    protected Void call() {
        apiService = APIService.getInstance();
        apiService.setStage(controller.getPrimaryStage());

        try {
            // IGV 설치
            if(igvInstall) {
                File igvFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.IGV_FILE_NAME);
                // 파일 다운로드
                updateTitle("Download IGV Application");
                download(igvFile);
                // 압축해제
                updateTitle("Install IGV Application");
                CompressUtil.uncompressZip(igvFile, userBasePath);
                // 압축파일 삭제
                igvFile.delete();
            }
            // JRE 설치
            if(jreInstall) {
                File jreFile = new File(CommonConstants.BASE_FULL_PATH, (CommonConstants.IS_WINDOWS) ? CommonConstants.JRE_FILE_NAME_FOR_WIN : CommonConstants.JRE_FILE_NAME_FOR_MAC);
                // 파일 다운로드
                updateTitle("Download JRE Application");
                download(jreFile);
                // 압축해제
                updateTitle("Install JRE Application");
                CompressUtil.uncompressTarGZ(jreFile, userBasePath);
                // 압축파일 삭제
                jreFile.delete();
                // windows가 아닌 경우 읽기권한 퍼미션 부여함.
                if(!CommonConstants.IS_WINDOWS) {
                    logger.debug("JRE directory set permission [775]");
                    updateTitle("Set Permission to JRE Application Directory");
                    List<String> cmdArray = new ArrayList<>();
                    cmdArray.add("chmod");
                    cmdArray.add("-R");
                    cmdArray.add("775");
                    cmdArray.add(new File(CommonConstants.BASE_FULL_PATH, CommonConstants.JRE_PATH_FOR_MAC).getAbsolutePath());
                    ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
                    processBuilder.redirectErrorStream(true);
                    processBuilder.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 다운로드
     * @param saveFile
     */
    public void download(File saveFile) {

        if(saveFile != null) {
            CloseableHttpClient httpclient = null;
            CloseableHttpResponse response = null;
            try {
                String connectURL = apiService.getConvertConnectURL(String.format("/clientTools/%s", saveFile.getName()));

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

                httpclient = HttpClients.custom().setSSLSocketFactory(SSLConnectService.getInstance().getSSLFactory()).build();
                if (httpclient != null) {
                    response = httpclient.execute(get);
                } else {
                    throw new NullResponseException("httpclient is null");
                }
                if (response == null) {
                    throw new NullResponseException("httpclient's response is null");
                }
                int status = response.getStatusLine().getStatusCode();

                if(status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    long fileLength = entity.getContentLength();
                    logger.debug("파일 크기 = " + fileLength);
                    try (InputStream is = content; OutputStream os = Files.newOutputStream(Paths.get(saveFile.toURI()))) {
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
                        os.flush();
                    }
                }
            } catch (Exception e) {
                logger.error("file download", e);
            } finally {
                if (httpclient != null)
                    try {
                        httpclient.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        logger.error(String.format("igv library install fail!!"));
        DialogUtil.error("Install Fail.", "An error occurred during igv running environment install.", controller.getPrimaryStage(), false);
        controller.removeProgressTaskItemById(progressBoxId);
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        logger.debug("igv install task complete");
        controller.removeProgressTaskItemById(progressBoxId);
        // igv 실행
        try {
            igvService.launchIGV();
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    controller.getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch\n" + e.getMessage(),
                    controller.getPrimaryStage(), true);
        }
    }

}
