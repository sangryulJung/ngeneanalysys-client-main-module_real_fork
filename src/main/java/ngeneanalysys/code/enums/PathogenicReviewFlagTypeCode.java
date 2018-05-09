package ngeneanalysys.code.enums;

/**
 * Pathogenicity flag type Code
 * 
 * @author gjyoo
 * @since 2016. 6. 23. 오후 12:15:01
 */
public enum PathogenicReviewFlagTypeCode {

	PATHOGENICITY("Pathogenicity"),
	REPORTED("Reported"),
	FALSE("Set to false");
	
	private String description;
	
	PathogenicReviewFlagTypeCode(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
