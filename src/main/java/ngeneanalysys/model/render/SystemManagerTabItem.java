package ngeneanalysys.model.render;

public class SystemManagerTabItem {
	
	
	/** 탭 Node id */
	private String nodeId;
	/** 탭메뉴 출력 메뉴명 */
	private String tabName;
	/** 해당탭 FXML 경로 */
	private String fxmlPath;
	
	public SystemManagerTabItem(String nodeId, String tabName, String fxmlPath) {
		this.nodeId = nodeId;
		this.tabName = tabName;
		this.fxmlPath = fxmlPath;
	}

	/**
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the tabName
	 */
	public String getTabName() {
		return tabName;
	}

	/**
	 * @param tabName the tabName to set
	 */
	public void setTabName(String tabName) {
		this.tabName = tabName;
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
