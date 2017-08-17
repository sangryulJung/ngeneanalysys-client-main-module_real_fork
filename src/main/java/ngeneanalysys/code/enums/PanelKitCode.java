package ngeneanalysys.code.enums;

/**
 * 패널 키트 코드 정보 클래스
 * 
 * @author gjyoo
 * @since 2016. 5. 24. 오전 10:06:02
 */
public enum PanelKitCode {
	BRCA_445("NGeneBio BRCAaccuTest\u00AE", "445", true),
	BRCA_446("SNUH BRCA TruSeq", "446", false),
	BRCA_447("NGeneBio BRCAaccuTest Plus", "447", false),
	BRCA_444("NGeneBio BRCAaccuTest", "444", false),
	BRCA_001("Bioo BRCA (ligation)", "001", false),
	BRCA_002("Multiplicom BRCA MASTR v2.1", "002", false),
	BRCA_998("Qiagen NGHS-102x Human BRCA1 and BRCA2", "998", false);
	
	private String description;
	private String primer;
	private boolean available;
	
	/**
	 * @return the available
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * @param available the available to set
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}

	PanelKitCode(String name, String primer, boolean available) {
		this.description = name;
		this.primer = primer;
		this.available = available;
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
	 * @return the primer
	 */
	public String getPrimer() {
		return primer;
	}

	/**
	 * @param primer the primer to set
	 */
	public void setPrimer(String primer) {
		this.primer = primer;
	}
}
