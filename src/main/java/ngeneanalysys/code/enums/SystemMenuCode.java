package ngeneanalysys.code.enums;

/**
 * 메인화면 상단 사용자 메뉴 > 메뉴 코드 정보
 * 
 * @author gjyoo
 * @since 2016. 6. 25. 오후 12:06:39
 */
public enum SystemMenuCode {

	EDIT("User Profile", 15),
	SETTINGS("Settings", 15),
	SUPPORT("Support", 15),
	LICENSE("License", 0),
	/*PUBLIC_DATABASES("Public Databases", 15),*/
	LOGOUT("Logout", 15);
	
	private String menuName;
	private Integer authBit;
	
	SystemMenuCode(String menu, Integer auth) {
		this.menuName = menu;
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
}
