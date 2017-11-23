package ngeneanalysys.util.httpclient;

import java.util.List;

import org.apache.http.HttpEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import javafx.collections.FXCollections;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.StringUtils;

/**
 * HttpClient 응답 결과 정보 Value Object 클래스
 * 
 * @author gjyoo
 * @since 2016. 5. 5. 오후 12:00:36
 */
public class HttpClientResponse {

	/** http status */
	private int status;
	/** 오류 메시지 [서버측] */
	private String errorDetail;
	/** httpclient response entity */
	private HttpEntity entity;
	/** 응답 결과 */
	private String contentString;
	
	/**
	 * 응답결과 JSON 객체로 변환 후 지정 클래스 객체로 반환
	 * @param valueType
	 * @return
	 */
	public <T> T getObjectBeforeConvertResponseToJSON(Class<T> valueType) {
		try {
			String response = getContentString();
			if(!StringUtils.isEmpty(response)) {
				return JsonUtil.fromJson(response, valueType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 응답결과 JSON 객체로 변환 후 지정 클래스의 목록 객체 형태로 반환
	 * @param valueType 객체 클래스
	 * @param isConvertObservableList ObservableList 객체 형태로 변환 여부
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMultiObjectBeforeConvertResponseToJSON(Class<T> valueType, boolean isConvertObservableList) {
		try {
			String response = getContentString();
			if(!StringUtils.isEmpty(response)) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JodaModule());
				List<Class<T>> list = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
				if(list != null && !list.isEmpty()) {
					if(isConvertObservableList) {
						return (T) FXCollections.observableArrayList(list);
					} else {
						return (T) list;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the errorDetail
	 */
	public String getErrorDetail() {
		return errorDetail;
	}
	/**
	 * @param errorDetail the errorDetail to set
	 */
	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}
	/**
	 * @return the entity
	 */
	public HttpEntity getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}
	/**
	 * @return the contentString
	 */
	public String getContentString() {
		return contentString;
	}
	/**
	 * @param contentString the contentString to set
	 */
	public void setContentString(String contentString) {
		this.contentString = contentString;
	}
}
