package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.FXMLConstants;

/**
 * 분석 결과 > SNPs-INDELs 하단 탭 메뉴 정보
 * 
 * @author gjyoo
 * @since 2016. 6. 25. 오후 4:26:13
 */
public enum SNPsINDELsTabMenuCode {
	
	OVERVIEW("OVERVIEW", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW, 15),
	DATA_SUMMARY("DATA SUMMARY", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_DATA_SUMMARY, 15),
	COMMENTS("COMMENTS", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_COMMENT, 15),
	VIEWER("VIEWER", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_VIEWER, 0),
	WARNINGS("WARNINGS", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_WARNINGS, 15);
	
	private String menuName;
	private String fxmlPath;
	private Integer authBit;
	
	SNPsINDELsTabMenuCode(String menu, String fxml, Integer auth) {
		this.menuName = menu;
		this.fxmlPath = fxml;
		this.authBit = auth;
	}
	
	/**
	 * @return the menuName
	 */
	public String getMenuName() {
		return menuName;
	}
	/**
	 * @param menuName the menuName to set
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	/**
	 * @return the authBit
	 */
	public Integer getAuthBit() {
		return authBit;
	}
	/**
	 * @param authBit the authBit to set
	 */
	public void setAuthBit(Integer authBit) {
		this.authBit = authBit;
	}

	/**
	 * @return the fxmlPath
	 */
	public String getFxmlPath() {
		return fxmlPath;
	}

	/**
	 * @param fxmlPath the fxmlPath to set
	 */
	public void setFxmlPath(String fxmlPath) {
		this.fxmlPath = fxmlPath;
	}
}
