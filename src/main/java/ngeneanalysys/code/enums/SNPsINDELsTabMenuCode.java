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
	MEMO("MEMO", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_MEMO, 15),
	LOWCONFIDENCE("LOW CONFIDENCE", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LOW_CONFIDENCE, 15);
	
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
	 * @return the authBit
	 */
	public Integer getAuthBit() {
		return authBit;
	}
	/**
	 * @return the fxmlPath
	 */
	public String getFxmlPath() {
		return fxmlPath;
	}
}
