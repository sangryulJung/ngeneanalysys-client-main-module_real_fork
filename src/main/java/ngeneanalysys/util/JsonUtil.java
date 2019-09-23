package ngeneanalysys.util;

import java.util.HashMap;
import java.util.List;
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
	 * @param jsonElement T
	 * @return String
	 */
	public static <T> String toJsonIncludeNullValue(T jsonElement) {
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		String result = null;

		try {
			result = objectMapper.writeValueAsString(jsonElement);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			objectMapper.setSerializationInclusion(Include.NON_NULL);
		}

		return result;
	}
	
	/**
	 * 지정 객체를 Json String으로 변환하여 반환
	 * @param jsonElement T
	 * @return String
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
	 * @param jsonString String
	 * @param cls Class<T>
	 * @return T
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
	 * @param jsonString String
	 * @return Map<String, T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> fromJsonToMap(String jsonString) {
		
		Map<String, T> map = new HashMap<>();
		
		try {
			map = objectMapper.readValue(jsonString, map.getClass());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return map;
	}

	public static <T> T getObjectList(String str,Class<T> valueType) {
		try {
			if(StringUtils.isNotEmpty(str)) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JodaModule());
				List<Class<T>> list = mapper.readValue(str, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
				if(list != null && !list.isEmpty()) {
					return (T) list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
