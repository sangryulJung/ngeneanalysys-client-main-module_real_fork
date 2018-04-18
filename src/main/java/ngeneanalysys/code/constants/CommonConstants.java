package ngeneanalysys.code.constants;

import java.io.File;

public class CommonConstants {
	
	private CommonConstants() { throw new IllegalAccessError("CommonConstants class"); }

	/** OS가 윈도우인지 여부 */
	public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("window");
	
	/** OS가 맥OS인지 여부 */
	public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
	
	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_PROPERTIES_PATH = "/properties/application.properties";
	
	/** 최소 메인화면 높이 */
	public static final int MIN_MAIN_SCENE_HEIGHT = 600;

	/** 기본 메인화면 높이 */
	public static final int DEFAULT_MAIN_SCENE_HEIGHT = 710;
	
	/** 로그인 세션 캐시메모리셋키명 */
	public static final String SESSION_CACHE_SET_NAME = "LOGIN_SESSION";
	
	/** 로그인 세션 캐시메모리셋 엔티티명 */
	public static final String SESSION_CACHE_KEY_NAME = "NGENEBIO_ANALYSYS_LOGIN_USER";
	
	/** 로컬 환경 기본 경로 */
	public static final String BASE_PATH = "ngenebio_analysys_gui";
	
	/** 로컬 환경 기본 전체 경로 */
	public static final String BASE_FULL_PATH = System.getProperty("user.home") + File.separator + CommonConstants.BASE_PATH;
	
	/** 로컬 환경 설정 프로퍼티 파일명 */
	public static final String CONFIG_PROPERTIES = "config.properties";
	
	/** API 서버 URL 프로퍼티 키 */
	public static final String DEFAULT_SERVER_HOST_KEY = "default.server.host";
	
	/** 시스템명 */
	public static final String SYSTEM_NAME = "NGeneAnalySys";
	
	/** 시스템 Favicon Image 경로 */
	public static final String SYSTEM_FAVICON_PATH = "/layout/images/renewal/ngeneanalysis_48.png";
	
	/** 임시 디렉토리 경로 반환 */
	public static final String TEMP_PATH = System.getProperty("java.io.tmpdir") + File.separator + "ngenebio_analysis" + File.separator;

	/** 파일업로드 재시도 횟수 */
	public static final int FILE_UPLOAD_RETRY_COUNT = 3;
	
	/** 자동 새로고침 시간 간격 */
	public static final String[] AUTO_REPRESH_SECOND_PERIOD = new String[]{"10", "20", "30" ,"45", "60"};
	
	/** 윈도우테마 */
	public static final String[] WINDOW_THEME = new String[] {"Default", "Dark","Red","Ice","Mountain"};

	/** 홈페이지 URL */
	public static final String HOMEPAGE_URL = "http://www.ngenebio.com";
	
	/** 시스템 사용 메뉴얼 문서 경로 */
	public static final String MANUAL_DOC_PATH_OPERATION = "NGene_AnalySys_User_Manual_v1.1.pdf";
	
	/** 프로그램 릴리즈 노트 페이지 url */
	public static final String RELEASE_NOTE_URL = "http://ngenebio.com/releasenote";
	
	/** 프록시 서버 포트 */
	public static final int HTTP_PROXY_SERVER_PORT = 19799;
	
	/** IGV File name */
	public static final String IGV_FILE_NAME = "IGV_2.4.1.zip";

	public static final String IGV_PATH = "IGV_2.4.1";
	
	/** [Windows] JRE Package File name */
	public static final String JRE_FILE_NAME_FOR_WIN = "jre-8u144-windows-i586.tar.gz";
	
	/** [Windows] JRE Package Path */
	public static final String JRE_PATH_FOR_WIN = "jre1.8.0_144";
	
	/** [Windows] JRE Package Path */
	public static final String JRE_BIN_PATH_FOR_WIN = JRE_PATH_FOR_WIN + "/bin";
	
	/** [MAC OS] JRE Package File name */
	public static final String JRE_FILE_NAME_FOR_MAC = "jre-8u144-macosx-x64.tar.gz";
	
	/** [MAC OS] JRE Package Path */
	public static final String JRE_PATH_FOR_MAC = "jre1.8.0_144.jre";
	
	/** [MAC OS] JRE Package Path */
	public static final String JRE_BIN_PATH_FOR_MAC = JRE_PATH_FOR_MAC + "/Contents/Home/bin";
	
	public static final String BIRTHDAY_PATTERN = "^(18|19|20|21)[0-9][0-9]-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

	public static final String EHCACHE_PATH =  "/config/ehcache.xml";

}
