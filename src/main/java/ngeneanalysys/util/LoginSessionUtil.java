package ngeneanalysys.util;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.model.LoginSession;
import ngeneanalysys.service.CacheMemoryService;

/**
 * 로그인 정보 UTIL
 * 
 * @author gjyoo
 * @since 2016. 5. 19. 오후 8:33:37
 */
public class LoginSessionUtil {
	
	/**
	 * 현재 로그인된 세션 반환
	 * @param cacheMemoryService CacheMemoryService
	 * @return LoginSession
	 */
	public static LoginSession getCurrentLoginSession(CacheMemoryService cacheMemoryService) {
		return (LoginSession) cacheMemoryService.getCacheObject(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME);
	}

	/**
	 * 현재 로그인된 세션 반환
	 * @return LoginSession
	 */
	public static LoginSession getCurrentLoginSession() {
		CacheMemoryService cacheMemoryService = CacheMemoryService.getInstance();
		return getCurrentLoginSession(cacheMemoryService);
	}
	
	/**
	 * 현재 로그인한 사용자 아이디 반환
	 * @return String
	 */
	public static String getAccessLoginId() {
		String loginId = null;
		LoginSession session = getCurrentLoginSession();
		if(session == null) {
			return null;
		} else {
			loginId = session.getLoginId();
		}
		return loginId;
	}
}
