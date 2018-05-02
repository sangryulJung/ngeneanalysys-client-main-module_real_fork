package ngeneanalysys.code.enums;

/**
 * SampleSheet Source 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 24. 오전 10:31:15
 */
public enum SampleSourceCode {
	BLOOD("Blood"),
	BONEMARROW("Bone marrow"),
	FFPE("FFPE"),
	DNA("DNA");
	
	private String description;
	
	SampleSourceCode(String name) {
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
