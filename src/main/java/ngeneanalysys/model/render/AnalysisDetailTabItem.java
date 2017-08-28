package ngeneanalysys.model.render;

import java.io.Serializable;

/**
 * 분석 상세 상단 탭메뉴 정보
 * 
 * @author gjyoo
 * @since 2016. 5. 28. 오후 1:42:30
 */
public class AnalysisDetailTabItem implements Serializable {
	private static final long serialVersionUID = 4204972020646072811L;
	
	/** 탬 Node id */
	private String nodeId;
	/** 탭메뉴 출력 메뉴명 */
	private String tabName;
	/** 해당탭 FXML 경로 */
	private String fxmlPath;
	/** 출력 권한 정보 and 계산 */
	private int displayAuth;
	
	public AnalysisDetailTabItem(String nodeId, String tabName, String fxmlPath, int displayAuth) {
		this.nodeId = nodeId;
		this.tabName = tabName;
		this.fxmlPath = fxmlPath;
		this.displayAuth = displayAuth;
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

	/**
	 * @return the displayAuth
	 */
	public int getDisplayAuth() {
		return displayAuth;
	}

	/**
	 * @param displayAuth the displayAuth to set
	 */
	public void setDisplayAuth(int displayAuth) {
		this.displayAuth = displayAuth;
	}
}
