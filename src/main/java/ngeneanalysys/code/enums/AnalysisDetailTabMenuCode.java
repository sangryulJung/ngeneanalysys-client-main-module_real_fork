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
	TAB_OVERVIEW_SOMATIC(new AnalysisDetailTabItem("TAB_OVERVIEW_SOMATIC", "OVERVIEW", FXMLConstants.ANALYSIS_DETAIL_OVERVIEW, 13)),
	TAB_OVERVIEW_GERMLINE(new AnalysisDetailTabItem("TAB_OVERVIEW_GERMLINE", "OVERVIEW", FXMLConstants.ANALYSIS_DETAIL_OVERVIEW_GERMLINE, 13)),
	TAB_OVERVIEW_TST_RNA(new AnalysisDetailTabItem("TAB_OVERVIEW_TST_RNA", "OVERVIEW", FXMLConstants.ANALYSIS_DETAIL_OVERVIEW_TST_RNA, 13)),
	TAB_VARIANTS(new AnalysisDetailTabItem("TAB_VARIANTS", "VARIANTS", FXMLConstants.ANALYSIS_DETAIL_VARIANTS, 13)),
	TAB_REPORT_SOMATIC(new AnalysisDetailTabItem("TAB_REPORT_SOMATIC", "REPORT", FXMLConstants.ANALYSIS_DETAIL_REPORT, 13)),
	TAB_REPORT_GERMLINE(new AnalysisDetailTabItem("TAB_REPORT_GERMLINE", "REPORT", FXMLConstants.ANALYSIS_DETAIL_REPORT_GERMLINE, 13)),
	TAB_REPORT_TST_RNA(new AnalysisDetailTabItem("TAB_REPORT_TST_RNA", "REPORT", FXMLConstants.ANALYSIS_DETAIL_TST_RNA_REPORT, 13));
	
	private AnalysisDetailTabItem item;

	/**
	 *
 	 * @param item item
	 */
	AnalysisDetailTabMenuCode(AnalysisDetailTabItem item) {
		this.item = item;
	}

	/**
	 *
	 * @return AnalysisDetailTabItem
	 */
	public AnalysisDetailTabItem getItem() {
		return item;
	}

	/**
	 *
	 * @param item item
	 */
	public void setItem(AnalysisDetailTabItem item) {
		this.item = item;
	}
}
