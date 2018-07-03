package ngeneanalysys.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import ngeneanalysys.model.SnpInDelEvidence;
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
		if(StringUtils.isNotEmpty(numberStr)) {
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
		if(StringUtils.isNotEmpty(numberStr)) {
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
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "" + units[digitGroups];
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
			StringBuilder buf = new StringBuilder();
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

	public static String tierConvert(String tier) {
		String convertTier = "";
		if(tier != null) {
			if (tier.equalsIgnoreCase("T1")) convertTier = "Tier I";
			else if (tier.equalsIgnoreCase("T2")) convertTier = "Tier II";
			else if (tier.equalsIgnoreCase("T3")) convertTier = "Tier III";
			else if (tier.equalsIgnoreCase("T4")) convertTier = "Tier IV";
			else if (tier.equalsIgnoreCase("TN")) convertTier = "Negative";
		}

		return convertTier;
	}

	public static String convertButtonId(String tier) {
		String convertTier = "";
		if(tier != null) {
			if (tier.equalsIgnoreCase("T1")) convertTier = "tierOne";
			else if (tier.equalsIgnoreCase("T2")) convertTier = "tierTwo";
			else if (tier.equalsIgnoreCase("T3")) convertTier = "tierThree";
			else if (tier.equalsIgnoreCase("T4")) convertTier = "tierFour";
		}

		return convertTier;
	}

	public static <T> Map<String, Object> getMapToModel(T item) {
		Field[] fields = item.getClass().getDeclaredFields();
		Map<String, Object> params = new HashMap<>();
		for(Field field : fields) {
			field.setAccessible(true);
			try {
				params.put(field.getName(), field.get(item));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return params;
	}

	public static BigDecimal removeZero(BigDecimal bigDecimal) {
		return (bigDecimal != null) ? bigDecimal.stripTrailingZeros() : null;
	}

	public static SnpInDelEvidence findPrimaryEvidence(List<SnpInDelEvidence> snpInDelEvidenceList) {
		if (snpInDelEvidenceList == null || snpInDelEvidenceList.isEmpty()) return null;
		for(SnpInDelEvidence snpInDelEvidence : snpInDelEvidenceList) {
			if(snpInDelEvidence.getPrimaryEvidence()) return snpInDelEvidence;
		}
		return null;
	}

	public static String getAminoAcid(String aminoAcid) {
		if(ngeneanalysys.util.StringUtils.isEmpty(aminoAcid)) return null;

		String[] pattern1 = {"Ala", "Arg", "Asn", "Asp", "Cys", "Glu", "Gln", "Gly", "His",
				"Ile", "Leu", "Lys", "Met","Phe", "Pro", "Ser", "Thr", "Trp", "Tyr", "Val"};
		String[] pattern2 = {"A","R","N","D","C","E","Q","G","H","I","L","K","M","F","P","S","T","W","Y","V"};

		/*if(aminoAcid.startsWith("p."))
			aminoAcid = aminoAcid.replaceFirst("p.", "");*/
		return StringUtils.replaceEach(aminoAcid, pattern1, pattern2);
	}
}
