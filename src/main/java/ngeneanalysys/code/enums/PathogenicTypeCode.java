package ngeneanalysys.code.enums;

import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;


/**
 * Pathogenicity Type code Enum
 * 
 * @author gjyoo
 * @since 2016. 10. 6. 오전 11:06:45
 */
public enum PathogenicTypeCode {
	PATHOGENIC_5("Pathogenic", "Pathogenic", "5", "P"),
	PATHOGENIC_4("Likely Pathogenic", "Likely Pathogenic", "4", "LP"),
	PATHOGENIC_3("Uncertain Significance", "Uncertain Significance", "3", "US"),
	PATHOGENIC_2("Likely Benign", "Likely Benign", "2", "LB"),
	PATHOGENIC_1("Benign", "Benign", "1", "B");

	private static Logger logger = LoggerUtil.getLogger();
	
	private String description;
	private String detail;
	private String code;
	private String alias;
	
	PathogenicTypeCode(String name, String detail, String code, String alias) {
		this.description = name;
		this.detail = detail;
		this.code = code;
		this.alias = alias;
	}
	
	public static PathogenicTypeCode getByCode(String code) {
		PathogenicTypeCode typeCode = null;
		try {
			typeCode = PathogenicTypeCode.valueOf(String.format("PATHOGENIC_%s", code));
		} catch (Exception e) {
			logger.error("Not Exist Prediction Code");
		}
		return typeCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	public static String getAliasFromCode(String code) {
		String aliasValue = null;
		if (code == null) {
			aliasValue = "NONE";
		} else if (code.equals(PATHOGENIC_5.getCode())) {
			aliasValue = PATHOGENIC_5.getAlias();
		} else if (code.equals(PATHOGENIC_4.getCode())) {
			aliasValue = PATHOGENIC_4.getAlias();
		} else if (code.equals(PATHOGENIC_3.getCode())) {
			aliasValue = PATHOGENIC_3.getAlias();
		} else if (code.equals(PATHOGENIC_2.getCode())) {
			aliasValue = PATHOGENIC_2.getAlias();
		} else if (code.equals(PATHOGENIC_1.getCode())) {
			aliasValue = PATHOGENIC_1.getAlias();
		} else {
			aliasValue = "NONE";
		}
		return aliasValue;
	}
	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
