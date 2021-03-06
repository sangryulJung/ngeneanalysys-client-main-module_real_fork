package ngeneanalysys.code.enums;

/**
 * 실험 방식 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 24. 오전 10:32:36
 */
public enum AnalysisTypeCode {
	SOMATIC("SOMATIC"),
	GERMLINE("GERMLINE");

	private String description;
	
	AnalysisTypeCode(String name) {
		this.description = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
