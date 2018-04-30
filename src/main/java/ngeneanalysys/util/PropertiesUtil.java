package ngeneanalysys.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import ngeneanalysys.code.constants.CommonConstants;


/**
 * 프로퍼티 Util
 * 
 * @author gjyoo
 * @since 2016. 6. 10. 오후 10:51:06
 */
public class PropertiesUtil {
	
	private PropertiesUtil() { throw new IllegalAccessError("PropertiesUtil class"); }
	
	/**
	 * 프로퍼티 반환
	 * @param path
	 * @return
	 */
	public static Properties getPropertiesByPath(String path) {
		try {
			ResourceUtil resourceUtil = new ResourceUtil();
			InputStream input = resourceUtil.getResourceAsStream(path);
			if(input != null) {
				Properties properties = new Properties();
				properties.load(input);
				input.close();
				return properties;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 지정 프로퍼티 파일에 프로퍼티 저장
	 * @param propertiesFile
	 * @param map
	 */
	public static void saveProperties(File propertiesFile, Map<String,String> map) {
		
		try (FileReader reader = new FileReader(propertiesFile)){
			// 기존 설정 파일 내용
			String fileContent = FileUtils.readFileToString(propertiesFile);
			
			if(StringUtils.isEmpty(fileContent)) {
				FileUtils.write(propertiesFile, "", "UTF-8");
			}	
			Properties properties = new Properties();
			properties.load(reader);
				
			for(String key : map.keySet()) {
				properties.setProperty(key, map.get(key));
			}
				
			FileWriter writer = new FileWriter(propertiesFile);
			properties.store(writer, "Update Settings");
			writer.close();
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시스템 기본 프로퍼티객체 반환 : 사용자 디렉토리 설정 정보 포함하여 반환함.
	 * @return
	 */
	public static Properties getSystemDefaultProperties() {
		Properties config = getPropertiesByPath(CommonConstants.BASE_PROPERTIES_PATH);
		File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);
		try (FileReader reader = new FileReader(configFile)){
			// 서버 정보 설정 파일에 입력 받은 내용을 기록
			
			Properties properties = new Properties();
			properties.load(reader);

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				// 설정값이 존재하는 경우 추가
				if(!StringUtils.isEmpty(value)) {
					config.setProperty(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
}
