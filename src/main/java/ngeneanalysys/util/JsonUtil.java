package ngeneanalysys.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Json Data Util
 * 
 * @author gjyoo
 * @since 2016. 4. 28. 오후 3:18:22
 */
public class JsonUtil {

	private static Logger logger = LoggerUtil.getLogger();
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.registerModule(new JodaModule());
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	private JsonUtil() { throw new IllegalAccessError("JsonUtil class"); }
	
	/**
	 * 지정 객체를 Json String으로 변환하여 반환
	 * @param jsonElement
	 * @return
	 */
	public static <T> String toJson(T jsonElement) {
		
		String result = null;
		
		try {
			result = objectMapper.writeValueAsString(jsonElement);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Json String을 지정 Object Class로 변환하여 반환
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> T fromJson(String jsonString, Class<T> cls) {
		
		T result = null;
		
		try {
			result = objectMapper.readValue(jsonString, cls);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Json String을 Map 객체로 변환하여 반환
	 * @param jsonString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> fromJsonToMap(String jsonString) {
		
		Map<String, T> map = new HashMap<String, T>();
		
		try {
			map = objectMapper.readValue(jsonString, map.getClass());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return map;
	}
}
