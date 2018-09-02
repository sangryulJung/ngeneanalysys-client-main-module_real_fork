package ngeneanalysys.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import javafx.stage.Stage;

/**
 * 입력값 체크 유틸 클래스
 * 
 * @author gjyoo
 * @since 2016. 9. 28. 오전 9:32:35
 */
public class ValidationUtil {

	private ValidationUtil() {throw new IllegalAccessError("Utility class");}
	
	/**
	 * 문자열 유효성 체크 [존재 여부 기본 체크]
	 * @param value String
	 * @param name String
	 * @param minByte int
	 * @param maxByte int
	 * @param pattern String
	 * @param guideMessage String
	 * @param alertValid String
	 * @param ownerStage stage
	 * @return int
	 */
	public static int text(String value, String name, int minByte, int maxByte, String pattern, String guideMessage, boolean alertValid, Stage ownerStage) {
		return text(value, name, minByte, maxByte, pattern, true, guideMessage, alertValid, ownerStage);
	}
	
	/**
	 * 문자열 유효성 체크 [존재 여부 미체크]
	 * @param value String
	 * @param name String
	 * @param minByte int
	 * @param maxByte int
	 * @param pattern String
	 * @param guideMessage String
	 * @param alertValid boolean
	 * @param ownerStage stage
	 * @return int
	 */
	public static int textDoNotEmptyCheck(String value, String name, int minByte, int maxByte, String pattern, String guideMessage, boolean alertValid, Stage ownerStage) {
		return text(value, name, minByte, maxByte, pattern, false, guideMessage, alertValid, ownerStage);
	}

	/**
	 * 문자열 유효성 체크
	 * @param value String
	 * @param name String
	 * @param minLength int
	 * @param maxLength int
	 * @param pattern String
	 * @param checkEmpty boolean
	 * @param alertValid boolean
	 * @param ownerStage stage
	 * @return int
	 */
	public static int text(String value, String name, int minLength, int maxLength, String pattern, boolean checkEmpty, String guideMessage, boolean alertValid, Stage ownerStage) {
		boolean waitAfterShow = ownerStage != null;
		if(StringUtils.isEmpty(guideMessage)){
			guideMessage = "";
		}
		// 입력값 체크
		if(checkEmpty && StringUtils.isEmpty(value)) {
			if(alertValid) {
				DialogUtil.warning("Empty Data", String.format("please enter the value of '%s'\n" + guideMessage , name), ownerStage, waitAfterShow);
			}
			return 1;
		}
		// 정규식 패턴이 지정된 경우
		if(StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(pattern)) {
			//문자, 숫자, 특수문자의 조합인지 확인
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(value);
			if (!m.find()){
				if(alertValid) {
					DialogUtil.warning("Invalid Format", String.format("'%s' is an invalid format.\n" + guideMessage, name), ownerStage, waitAfterShow);
				}
				return 4;
			}
		}
		// 최소 입력, 최대 입력 체크값이 존재하는 경우 진행
		if(minLength > 0 || maxLength > -1) {
			if(StringUtils.isEmpty(value)) {
				value = "";
			}
			
			// 입력 최소값 체크
			if(minLength > 0 && value.length() < minLength) {
				if(alertValid) {
					DialogUtil.warning("Invalid Data", String.format("'%s', The minimum length is %s.\n" + guideMessage, name, minLength), ownerStage, waitAfterShow);
				}
				return 2;
			}
			
			// 입력 최대값 체크
			if(maxLength > -1 && value.length() > maxLength) {
				if(alertValid) {
					DialogUtil.warning("Invalid Data", String.format("'%s', The maximum length is %s\n" + guideMessage, name, maxLength), ownerStage, waitAfterShow);
				}
				return 3;
			}
		}
		
		return 0;
	}
}
