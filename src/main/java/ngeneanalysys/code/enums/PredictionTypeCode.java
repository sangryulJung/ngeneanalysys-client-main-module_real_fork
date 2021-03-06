package ngeneanalysys.code.enums;

import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;


/**
 * Prediction Type code Enum
 * 
 * @author gjyoo
 * @since 2016. 10. 10. 오전 10:53:03
 */
public enum PredictionTypeCode {
	PREDICTION_A("Pathogenic", "P : Pathogenic", "A", "P"),
	PREDICTION_B("Likely Pathogenic", "LP : Likely Pathogenic", "B", "LP"),
	PREDICTION_C("Uncertain Significance", "US : Uncertain Significance", "C", "US"),
	PREDICTION_D("Likely Benign", "LB : Likely Benign", "D", "LB"),
	PREDICTION_E("Benign", "B : Benign", "E", "B");

	private static Logger logger = LoggerUtil.getLogger();
	
	private String description;
	private String detail;
	private String code;
	private String alias;
	
	PredictionTypeCode(String name, String detail, String code, String alias) {
		this.description = name;
		this.detail = detail;
		this.code = code;
		this.alias = alias;
	}
	
	public static PredictionTypeCode getByCode(String code) {
		PredictionTypeCode typeCode = null;
		try {
			typeCode = PredictionTypeCode.valueOf(String.format("PREDICTION_%s", code));
		} catch (Exception e) {
			logger.error("Not Exist Prediction Code");
		}
		return typeCode;
	}
	public static String getAliasFromCode(String code) {
		String aliasValue = null;
		if (code == null) {
			aliasValue = "NONE";
		} else if (code.equals(PREDICTION_A.getCode())) {		
			aliasValue = PREDICTION_A.getAlias();
		} else if (code.equals(PREDICTION_B.getCode())) {
			aliasValue = PREDICTION_B.getAlias();
		} else if (code.equals(PREDICTION_C.getCode())) {
			aliasValue = PREDICTION_C.getAlias();
		} else if (code.equals(PREDICTION_D.getCode())) {
			aliasValue = PREDICTION_D.getAlias();
		} else if (code.equals(PREDICTION_E.getCode())) {
			aliasValue = PREDICTION_E.getAlias();
		} else {
			aliasValue = "NONE";
		}
		return aliasValue;
	}

	public static String getNameFromAlias(String alias) {
		String aliasValue = null;
		if (alias == null) {
			aliasValue = "NONE";
		} else if (alias.equals(PREDICTION_A.getAlias())) {
			aliasValue = PREDICTION_A.getDescription();
		} else if (alias.equals(PREDICTION_B.getAlias())) {
			aliasValue = PREDICTION_B.getDescription();
		} else if (alias.equals(PREDICTION_C.getAlias())) {
			aliasValue = PREDICTION_C.getDescription();
		} else if (alias.equals(PREDICTION_D.getAlias())) {
			aliasValue = PREDICTION_D.getDescription();
		} else if (alias.equals(PREDICTION_E.getAlias())) {
			aliasValue = PREDICTION_E.getDescription();
		} else {
			aliasValue = "NONE";
		}
		return aliasValue;
	}

	public static String getCodeFromAlias(String alias) {
		String codeValue = null;
		if (alias == null) {
			codeValue = "NONE";
		} else if (alias.equals(PREDICTION_A.getAlias())) {
			codeValue = PREDICTION_A.getCode();
		} else if (alias.equals(PREDICTION_B.getAlias())) {
			codeValue = PREDICTION_B.getCode();
		} else if (alias.equals(PREDICTION_C.getAlias())) {
			codeValue = PREDICTION_C.getCode();
		} else if (alias.equals(PREDICTION_D.getAlias())) {
			codeValue = PREDICTION_D.getCode();
		} else if (alias.equals(PREDICTION_E.getAlias())) {
			codeValue = PREDICTION_E.getCode();
		} else {
			codeValue = "NONE";
		}
		return codeValue;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}
}
