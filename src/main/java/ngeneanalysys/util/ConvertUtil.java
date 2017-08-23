package ngeneanalysys.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * 값 변환 Util Class
 * 
 * @author gjyoo
 * @since 2016. 6. 21. 오후 9:00:21
 */
public class ConvertUtil {

	/**
	 * 숫자 문자열 지정 형식 문자열로 변환
	 * @param format
	 * @param numberStr
	 * @return
	 */
	public static String convertFormatNumber(String format, String numberStr) {
		return convertFormatNumber(format, numberStr, null);
	}

	/**
	 * 숫자 문자열 지정 형식 문자열로 변환
	 * @param format
	 * @param numberStr
	 * @param defaultValue
	 * @return
	 */
	public static String convertFormatNumber(String format, String numberStr, String defaultValue) {
		if(!StringUtils.isEmpty(numberStr)) {
			return String.format(format, Double.parseDouble(numberStr));
		}
		return defaultValue;
	}
	
	/**
	 * 지정 숫자 문자열 퍼센트 단위로 변환하여 지정 형식 문자열로 변환 (*100)
	 * @param format
	 * @param numberStr
	 * @return
	 */
	public static String convertFormatPercentageNumber(String format, String numberStr) {
		if(!StringUtils.isEmpty(numberStr)) {
			Double number = Double.parseDouble(numberStr) * 100;
			return String.format(format, number);
		}
		return null;
	}
	
	/**
	 * 파일 용량 단위 형식의 문자열로 반환
	 * @param size
	 * @return
	 */
	public static String convertFileSizeFormat(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	/**
	 * 표준시로 변환하여 반환
	 * @param inputDateTime
	 * @return
	 */
	public static String convertLocalTimeToUTC(String inputDateTime, String dateFormat, String utcDateFormat) {
		if(dateFormat == null) dateFormat = "yyyy-MM-dd";
		if(utcDateFormat == null) utcDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
		
		String utcTime = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		SimpleDateFormat dateFormatForUTC = new SimpleDateFormat(utcDateFormat);
		TimeZone tz = TimeZone.getDefault();
		
		try {
			Date parseDate = sdf.parse(inputDateTime);
			long milliseconds = parseDate.getTime();
			int offset = tz.getOffset(milliseconds);
			utcTime = dateFormatForUTC.format(milliseconds - offset);
			utcTime = utcTime.replace("+0000", "");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return utcTime;
	}
	public static String insertTextAtFixedPosition(String text, int splitLength, String insertText){
		if(text == null) {
			return "";
		} else if( text.length() > splitLength) {
			StringBuffer buf = new StringBuffer();
			for(int i = 0; i < text.length(); i += splitLength){
				if(i + splitLength > text.length()) {
					buf.append(text.substring(i, text.length()));
					break;
				} else {
					buf.append(text.substring(i, i+splitLength)).append(insertText);
					
				}
			}
			return buf.toString();
		} else {
			return text;
		}		
	}
}
