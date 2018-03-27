package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.model.render.SystemManagerTabItem;

public enum SystemManagerTabMenuCode {
	
	TAB_SYSTEM_MANAGER_ANALYSIS_STATUS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_ANALYSIS_STATUS", "Analysis Status", FXMLConstants.SYSTEM_MANAGER_ANALYSIS_STATUS)),
	TAB_SYSTEM_MANAGER_USER_ACCOUNT(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_USER_ACCOUNT", "User Account", FXMLConstants.SYSTEM_MANAGER_USER_ACCOUNT)),
	//TAB_SYSTEM_MANAGER_SERVER_STATUS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_SERVER_STATUS", "Server Status", FXMLConstants.SYSTEM_MANAGER_SERVER_STATUS)),
	TAB_SYSTEM_MANAGER_PANEL(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_PANEL", "Panel", FXMLConstants.SYSTEM_MANAGER_PANEL)),
	TAB_SYSTEM_MANAGER_REPORT_TEMPLATE(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_REPORT_TEMPLATE", "Report", FXMLConstants.SYSTEM_MANAGER_REPORT_TEMPLATE)),
	TAB_SYSTEM_MANAGER_NEWS_AND_TIPS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_NEWS_AND_TIPS", "News&Tips", FXMLConstants.SYSTEM_MANAGER_NEWS_AND_TIPS)),
	TAB_SYSTEM_MANAGER_SYSTEM_LOGS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_SYSTEM_LOGS", "System Logs", FXMLConstants.SYSTEM_MANAGER_LOG_LIST)),
	TAB_SYSTEM_MANAGER_INTERPRETATION_DATABASE(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_INTERPRETATION_DATABASE", "Clinical Variant DB", FXMLConstants.SYSTEM_MANAGER_INTERPRETATION_DATABASE));
	
	private SystemManagerTabItem item;

	private SystemManagerTabMenuCode(SystemManagerTabItem item) {
		this.item = item;
	}

	public SystemManagerTabItem getItem() {
		return item;
	}

	public void setItem(SystemManagerTabItem item) {
		this.item = item;
	}

}
