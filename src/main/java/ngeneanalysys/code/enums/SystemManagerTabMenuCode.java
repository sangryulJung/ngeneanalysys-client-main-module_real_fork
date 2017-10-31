package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.model.render.SystemManagerTabItem;

public enum SystemManagerTabMenuCode {
	
	TAB_SYSTEM_MANAGER_ANALYSIS_STATUS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_ANALYSIS_STATUS", "Analysis Status", FXMLConstants.SYSTEM_MANAGER_ANALYSIS_STATUS)),
	TAB_SYSTEM_MANAGER_USER_ACCOUNT(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_USER_ACCOUNT", "User Account", FXMLConstants.SYSTEM_MANAGER_USER_ACCOUNT)),
	//TAB_SYSTEM_MANAGER_SERVER_STATUS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_SERVER_STATUS", "Server Status", FXMLConstants.SYSTEM_MANAGER_SERVER_STATUS)),
	TAB_SYSTEM_MANAGER_PANEL(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_PANEL", "Panel", FXMLConstants.SYSTEM_MANAGER_PANEL)),
	TAB_SYSTEM_MANAGER_SYSTEM_LOGS(new SystemManagerTabItem("TAB_SYSTEM_MANAGER_SYSTEM_LOGS", "System Logs", FXMLConstants.SYSTEM_MANAGER_LOG_LIST));
	
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
