package ngeneanalysys.code.enums;

/**
 * 분석 결과 ACMG Filter 코드
 * 
 * @author gjyoo
 * @since 2016. 6. 15. 오후 7:07:58
 */
public enum VariantLevelCode {
	PREDICTION_A("Pathogenic", "Pathogenic", "A", "P"),
	PREDICTION_B("Likely Pathogenic", "Likely Pathogenic", "B", "LP"),
	PREDICTION_C("Uncertain Significance", "Uncertain Significance", "C", "US"),
	PREDICTION_D("Likely Benign", "Likely Benign", "D", "LB"),
	PREDICTION_E("Benign", "Benign", "E", "B"),
	//임시
	TIER_ONE("Tier I", "Tier I", "1", "T1"),
	TIER_TWO("Tier II", "Tier II", "2", "T2"),
	TIER_THREE("Tier III", "Tier III", "3", "T3"),
	TIER_FOUR("Tier IV", "Tier IV", "4", "T4");

	private String description;
	private String detail;
	private String code;
	private String alias;

	VariantLevelCode(String name, String detail, String code, String alias) {
		this.description = name;
		this.detail = detail;
		this.code = code;
		this.alias = alias;
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
		}
		return aliasValue;
	}

	public static String getCodeFromAlias(String alias) {
		String codeValue = null;
		if (alias == null) {
			codeValue = "NONE";
		} else if (alias.equals(TIER_ONE.getAlias())) {
			codeValue = TIER_ONE.getCode();
		} else if (alias.equals(TIER_TWO.getAlias())) {
			codeValue = TIER_TWO.getCode();
		} else if (alias.equals(TIER_THREE.getAlias())) {
			codeValue = TIER_THREE.getCode();
		} else if (alias.equals(TIER_FOUR.getAlias())) {
			codeValue = TIER_FOUR.getCode();
		}
		return codeValue;
	}
}
