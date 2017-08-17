package ngeneanalysys.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 상단 메뉴 Entity 정보 Class
 * 
 * @author gjyoo
 * @since 2016. 5. 9. 오후 7:28:58
 */
public class TopMenu implements Serializable {
	private static final long serialVersionUID = -4018789034720168299L;
	
	/** 메뉴 아이디 */
	private String id;
	/** 메뉴명 */
	private String menuName;
	/** fxml 경로 */
	private String fxmlPath;
	/** 화면 출력 파라미터 */
	private Map<String,Object> paramMap;
	/** 메뉴 출력 순서 */
	private int displayOrder;
	/** 메뉴 선택 여부 */
	private boolean selected;
	/** 고정 메뉴 여부 */
	private boolean staticMenu;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the paramMap
	 */
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	/**
	 * @param paramMap the paramMap to set
	 */
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	/**
	 * @return the displayOrder
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}
	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	/**
	 * @return the staticMenu
	 */
	public boolean isStaticMenu() {
		return staticMenu;
	}
	/**
	 * @param staticMenu the staticMenu to set
	 */
	public void setStaticMenu(boolean staticMenu) {
		this.staticMenu = staticMenu;
	}
}
