package ngeneanalysys.code.enums;

/**
 * SampleSheet Source 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 24. 오전 10:31:15
 */
public enum SampleSourceCode {
	BLOOD("Peripheral Blood"),
	BLOODCRYO("Peripheral Blood (Cryo)"),
	BONEMARROW("Bone marrow"),
	BONEMARROWCRYO("Bone marrow (Cryo)"),
	FFPE("FFPE"),
	DNA("DNA"),
	ETC("Etc");
	
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

	@Override
	public String toString() {
		return description;
	}
}
