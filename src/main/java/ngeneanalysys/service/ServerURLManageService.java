package ngeneanalysys.service;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.PropertiesUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Jang
 * @since 2017-08-07
 */
public class ServerURLManageService {
    private static final Logger logger = LoggerUtil.getLogger();

    /**
     * 인스턴스 생성 제한
     */
    private ServerURLManageService() {}

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class ServerURLManageServiceHelper{
        private ServerURLManageServiceHelper() {}
        private static final ServerURLManageService INSTANCE = new ServerURLManageService();
    }

    /**
     * 싱글톤 객체 반환
     * @return ServerURLManageService
     */
    public static ServerURLManageService getInstance() {
        return ServerURLManageServiceHelper.INSTANCE;
    }

    /**
     * 입력된 URL 정상적인 URL인지 체크하여 반환
     * @param inputURL
     * @return
     */
    public int isValidURL(String inputURL) {
        String regex = "^(http|https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(inputURL).matches()) {
            HttpClientResponse response = null;
            try {
                response = HttpClientUtil.get(inputURL, null, null, false);
                if(response != null) {
                    int status = response.getStatus();
                    logger.debug("HTTP Status : " + status);
                    return status;
                }
            } catch (WebAPIException e) {
                logger.debug(e.getMessage());
            }
        } else {
            return 0;
        }
        return -1;
    }

    /**
     * 저장
     * @param inputURL
     * @throws IOException
     */
    public void save(String inputURL) throws IOException {
        //서버 정보 설정 파일에 입력 받은 내용을 기록
        File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);
        File configDir = new File(CommonConstants.BASE_FULL_PATH);
        if(!configDir.exists()) {
            configDir.mkdirs();
        }

        if(!configFile.exists()) {
            configFile.createNewFile();
        }

        Map<String, String> map = new HashMap<>();
        map.put("default.server.host", inputURL);
        PropertiesUtil.saveProperties(configFile, map);
    }
}
