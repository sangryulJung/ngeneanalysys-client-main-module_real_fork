package ngeneanalysys.code.enums;

/**
 * 성별 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 28. 오전 10:55:11
 */
public enum GenderCode {
	
	M("Male"),
	F("Female");
	
	private String description;
	
	GenderCode(String name) {
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
