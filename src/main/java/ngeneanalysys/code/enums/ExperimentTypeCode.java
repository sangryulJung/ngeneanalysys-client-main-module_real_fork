package ngeneanalysys.code.enums;

/**
 * 실험 방식 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 24. 오전 10:32:36
 */
public enum ExperimentTypeCode {
	GERMLINE("GERMLINE"),
	SOMATIC("SOMATIC"),
	SOMATIC_AND_GERMLINE("SOMATIC_AND_GERMLINE");
	
	private String description;
	
	ExperimentTypeCode(String name) {
		this.description = name;
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
}
