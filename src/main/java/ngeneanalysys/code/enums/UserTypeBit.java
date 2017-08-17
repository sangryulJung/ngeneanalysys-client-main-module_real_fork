package ngeneanalysys.code.enums;

/**
 * 사용자 권한 정보
 * 
 * @author gjyoo
 * @since 2016. 5. 28. 오후 1:58:42
 */
public enum UserTypeBit {

	ADMIN(1),			// 관리자
	EXPERIMENTER(2),	// 분석자
	DOCTOR(4),			// 리뷰어
	RESEARCHER(8);		// 연구원
	
	private int auth;
	
	UserTypeBit(int auth) {
		this.auth = auth;
	}

	/**
	 * @return the auth
	 */
	public int getAuth() {
		return auth;
	}

	/**
	 * @param auth the auth to set
	 */
	public void setAuth(int auth) {
		this.auth = auth;
	}
}
