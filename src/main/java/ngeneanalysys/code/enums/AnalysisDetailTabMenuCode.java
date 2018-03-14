package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.model.render.AnalysisDetailTabItem;

/**
 * 분석 상세 상단 탭메뉴 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 28. 오후 2:45:40
 */
public enum AnalysisDetailTabMenuCode {
	TAB_OVERVIEW(new AnalysisDetailTabItem("TAB_OVERVIEW", "OVERVIEW", FXMLConstants.ANALYSIS_DETAIL_OVERVIEW, 13)),
	TAB_OVERVIEW_GERMLINE(new AnalysisDetailTabItem("TAB_OVERVIEW_GERMLINE", "OVERVIEW", FXMLConstants.ANALYSIS_DETAIL_OVERVIEW_GERMLINE, 13)),
	TAB_SNPS_INDELS(new AnalysisDetailTabItem("TAB_SNPS_INDELS", "SNPs-INDELs", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LAYOUT, 13)),
	TAB_REPORT_GERMLINE(new AnalysisDetailTabItem("TAB_REPORT_GERMLINE", "REPORT", FXMLConstants.ANALYSIS_DETAIL_REPORT_GERMLINE, 13)),
	TAB_TARGET_GERMLINE(new AnalysisDetailTabItem("TAB_TARGET_GERMLINE", "TARGET GENES", FXMLConstants.ANALYSIS_DETAIL_TARGET_GERMLINE, 13)),
	TAB_REPORT(new AnalysisDetailTabItem("TAB_REPORT", "REPORT", FXMLConstants.ANALYSIS_DETAIL_REPORT, 13));
	//TAB_TARGET(new AnalysisDetailTabItem("TAB_TARGET", "TARGET GENES", FXMLConstants.ANALYSIS_DETAIL_TARGET, 13));
	//TAB_RAW_DATA(new AnalysisDetailTabItem("TAB_RAW_DATA", "RAW Data", FXMLConstants.ANALYSIS_DETAIL_RAW_DATA, 13));
	
	private AnalysisDetailTabItem item;

	/**
	 *
 	 * @param item
	 */
	AnalysisDetailTabMenuCode(AnalysisDetailTabItem item) {
		this.item = item;
	}

	/**
	 *
	 * @return
	 */
	public AnalysisDetailTabItem getItem() {
		return item;
	}

	/**
	 *
	 * @param item
	 */
	public void setItem(AnalysisDetailTabItem item) {
		this.item = item;
	}
}
