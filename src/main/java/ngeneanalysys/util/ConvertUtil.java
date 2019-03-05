package ngeneanalysys.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import ngeneanalysys.model.SnpInDelEvidence;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 값 변환 Util Class
 * 
 * @author gjyoo
 * @since 2016. 6. 21. 오후 9:00:21
 */
public class ConvertUtil {

	private ConvertUtil() {}

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

	public static String returnQCTitle(String value) {
		if(value.equals("Median_BinCount_CNV_Targets")) {
			return "Median BinCount";
		} else if(value.equals("PCT_ExonBases_100X")) {
			return "PCT ExonBases";
		} else if(value.equals("Q30_score_read1")) {
			return "Q30+ Read2";
		} else if(value.equals("Q30_score_read2")) {
			return "Q30+ Read1";
		} else if(value.equals("roi_coverage")) {
			return "ROI Coverage";
		}

		return WordUtils.capitalize(value.replaceAll("_", " "));
	}

	public static String convertBrcaCnvRegion(List<String> list, final String gene) {
		final StringBuilder sb = new StringBuilder();
		LinkedList<String> tempList = new LinkedList<>();
		list.remove("Promoter");
		/** MLPA 처리방식 논의중 **/
		list.remove("MLPA");
		try {
			list.forEach(item -> {
				if (item.equals("Promoter")) {
					tempList.add(item);
				} else {
					if (tempList.isEmpty()) {
						tempList.add(item);
					} else {
						String last = tempList.getLast();
						if (last.equals("Promoter") && item.equals("1")) {
							tempList.add(item);
						} else if (last.equals("Promoter")) {
							sb.append(last).append(", ");
							tempList.clear();
						} else {
							int lastInt = Integer.parseInt(last);
							int currentInt = Integer.parseInt(item);
							if (lastInt == currentInt - 1/* ||
                                    ("BRCA1".equals(gene) && lastInt == 3 && currentInt == 5)*/) {
								tempList.add(item);
							} else if (tempList.size() > 1) {
								sb.append(tempList.getFirst()).append(" ~ ").append(tempList.getLast()).append(", ");
								tempList.clear();
								tempList.add(item);
							} else {
								sb.append(last).append(", ");
								tempList.clear();
								tempList.add(item);
							}
						}
					}
				}
			});

			if(!tempList.isEmpty()) {
				if(tempList.size() > 1) {
					sb.append(tempList.getFirst()).append(" ~ ").append(tempList.getLast());
				} else {
					sb.append(tempList.getLast());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
