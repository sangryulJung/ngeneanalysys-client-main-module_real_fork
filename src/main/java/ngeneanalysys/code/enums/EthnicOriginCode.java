package ngeneanalysys.code.enums;

/**
 * 민족 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 28. 오전 10:43:04
 */
public enum EthnicOriginCode {

	ARAB_LEAGUE("Arab League"),
	EUROPE("Europe"),
	AUSTRALIAN_INDIGENOUS("Australian(indigenous)"),
	INDIGENOUS_OCEANIAN("Indigenous Oceanian"),
	EUROPEANS_IN_OCEANIA("Europeans in Oceania"),
	INDIGENOUS("Indigenous"),
	CANADA("Canada"),
	MEXICO("Mexico"),
	UNITED_STATES("United States"),
	CENTRAL_AMERICA("Central America"),
	SOUTH_AMERICA("South America"),
	ASIA_CENTRAL("Asia-Central"),
	ASIA_EAST("Asia-East"),
	ASIA_NORTHERN("Asia-Northern"),
	ASIA_SOUTH("Asia-South"),
	ASIA_SOUTHEAST("Asia-Southeast"),
	ASIA_WEST("Asia-West");
	
	private String description;
	
	EthnicOriginCode(String name) {
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
