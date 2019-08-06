package ngeneanalysys.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
	 * @param path String
	 * @return Properties
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

	public static String getJsonString(String path) {
		try {
			ResourceUtil resourceUtil = new ResourceUtil();
			InputStream input = resourceUtil.getResourceAsStream(path);
			if(input != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(inputStreamReader);

				StringBuilder stringBuilder = new StringBuilder();
				for (String line; (line = reader.readLine()) != null;) {
					stringBuilder.append(line);
				}

				reader.close();
				return stringBuilder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 지정 프로퍼티 파일에 프로퍼티 저장
	 * @param propertiesFile File
	 * @param map Map<String,String>
	 */
	public static void saveProperties(File propertiesFile, Map<String,String> map) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(propertiesFile), CommonConstants.ENCODING_TYPE_UTF))){
			// 기존 설정 파일 내용
			String fileContent = FileUtils.readFileToString(propertiesFile);
			
			if(StringUtils.isEmpty(fileContent)) {
				FileUtils.write(propertiesFile, "", CommonConstants.ENCODING_TYPE_UTF);
			}	
			Properties properties = new Properties();
			properties.load(reader);
				
			for(Map.Entry<String, String> entry : map.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
				
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesFile, false), CommonConstants.ENCODING_TYPE_UTF));
			properties.store(writer, "Update Settings");
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시스템 기본 프로퍼티객체 반환 : 사용자 디렉토리 설정 정보 포함하여 반환함.
	 * @return Properties
	 */
	public static Properties getSystemDefaultProperties() {
		Properties config = getPropertiesByPath(CommonConstants.BASE_PROPERTIES_PATH);
		File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), CommonConstants.ENCODING_TYPE_UTF))){
			// 서버 정보 설정 파일에 입력 받은 내용을 기록
			Properties properties = new Properties();
			properties.load(reader);

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				// 설정값이 존재하는 경우 추가
				if(StringUtils.isNotEmpty(value) && config != null) {
					config.setProperty(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
}
