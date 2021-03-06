package ngeneanalysys.code.constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonConstants {
	
	private CommonConstants() { throw new IllegalAccessError("CommonConstants class"); }

	/** OS가 윈도우인지 여부 */
	public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("window");
	
	/** OS가 맥OS인지 여부 */
	public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
	
	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_PROPERTIES_PATH = "/properties/application.properties";

	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_HEME_COLUMN_ORDER_PATH = "/layout/column/heme";

	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_BRCA_COLUMN_ORDER_PATH = "/layout/column/brca";

	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_BRCA_SNU_COLUMN_ORDER_PATH = "/layout/column/brcaSnu";

	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_HERED_COLUMN_ORDER_PATH = "/layout/column/hered";

	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_SOLID_COLUMN_ORDER_PATH = "/layout/column/solid";

	/** 기본 프로퍼티 파일 경로 */
	public static final String BASE_TSTDNA_COLUMN_ORDER_PATH = "/layout/column/tstdna";
	
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
	
	/** 자동 새로고침 시간 간격 */
	public static final List<String> AUTO_REFRESH_SECOND_PERIOD = Collections.unmodifiableList(
			new ArrayList<String>() {{add("10"); add("20"); add("30"); add("45"); add("60");}});

	/** 윈도우테마 */
	public static final List<String> WINDOW_THEME =  Collections.unmodifiableList(
			new ArrayList<String>(){{add("Default"); add("Dark"); add("Red"); add("Ice"); add("Mountain"); add("DNA");}});

	/** 홈페이지 URL */
	public static final String HOMEPAGE_URL = "http://www.ngenebio.com";
	
	/** 시스템 사용 메뉴얼 문서 경로 */
	public static final String MANUAL_DOC_PATH_OPERATION = "NGene_AnalySys_User_Manual_v1.4.4.0.pdf";
	
	/** 프로그램 릴리즈 노트 페이지 url */
	public static final String RELEASE_NOTE_URL = "http://ngenebio.com/releasenote";
	
	/** 프록시 서버 포트 */
	public static final int HTTP_PROXY_SERVER_PORT = 19799;
	
	/** IGV File name */
	public static final String IGV_FILE_NAME = "IGV_2.4.16.zip";

	public static final String IGV_PATH = "IGV_2.4.16";
	
	/** [Windows] JRE Package File name */
	public static final String JRE_FILE_NAME_FOR_WIN = "jre-8u192-windows-i586.tar.gz";
	
	/** [Windows] JRE Package Path */
	public static final String JRE_PATH_FOR_WIN = "jre1.8.0_192";
	
	/** [Windows] JRE Package Path */
	public static final String JRE_BIN_PATH_FOR_WIN = JRE_PATH_FOR_WIN + "/bin";
	
	/** [MAC OS] JRE Package File name */
	public static final String JRE_FILE_NAME_FOR_MAC = "jre-8u192-macosx-x64.tar.gz";
	
	/** [MAC OS] JRE Package Path */
	public static final String JRE_PATH_FOR_MAC = "jre1.8.0_192.jre";
	
	/** [MAC OS] JRE Package Path */
	public static final String JRE_BIN_PATH_FOR_MAC = JRE_PATH_FOR_MAC + "/Contents/Home/bin";
	
	public static final String BIRTHDAY_PATTERN = "^(18|19|20|21)[0-9][0-9]-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

	public static final String DEFAULT_DAY_FORMAT = "yyyy-MM-dd";

	public static final String NUMBER_PATTERN = "[0-9]*";

	public static final String DEFAULT_WARNING_MGS = "Unknown Error";

	public static final String ENCODING_TYPE_UTF = "UTF-8";

	public static final String EHCACHE_PATH =  "/config/ehcache.xml";

	public static final String BRCA_ACCUTEST_DNA_PIPELINE = "BRCAaccuTest_DNA_Pipeline";
	public static final String BRCA_ACCUTEST_DNA_CNV_PIPELINE = "BRCAaccuTest_DNA_CNV_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_PIPELINE = "BRCAaccuTest_PLUS_DNA_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_CNV_PIPELINE = "BRCAaccuTest_PLUS_DNA_CNV_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_V2_PIPELINE = "BRCAaccuTest_PLUS_DNA_V2_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_CNV_V2_PIPELINE = "BRCAaccuTest_PLUS_DNA_CNV_V2_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_CMC_PIPELINE = "BRCAaccuTest_PLUS_DNA_CMC_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_V3_PIPELINE = "BRCAaccuTest_PLUS_DNA_V3_Pipeline";
	public static final String BRCA_ACCUTEST_PLUS_DNA_CNV_V2_SNU_PIPELINE = "BRCAaccuTest_PLUS_DNA_CNV_V2_SNU_Pipeline";
	public static final String HEME_ACCUTEST_DNA_PIPELINE = "HEMEaccuTest_DNA_Pipeline";
	public static final String HEME_ACCUTEST_DNA_CNV_PIPELINE = "HEMEaccuTest_DNA_CNV_Pipeline";
	public static final String SOLID_ACCUTEST_DNA_PIPELINE = "SOLIDaccuTest_DNA_Pipeline";
	public static final String SOLID_ACCUTEST_DNA_CNV_PIPELINE = "SOLIDaccuTest_DNA_CNV_Pipeline";
	public static final String TST170_DNA_PIPELINE = "TST170_DNA_Pipeline";
	public static final String TST170_RNA_PIPELINE = "TST170_RNA_Pipeline";
	public static final String HERED_ACCUTEST_PIPELINE = "HEREDaccuTest_DNA_Pipeline";
	public static final String HERED_ACCUTEST_CNV_PIPELINE = "HEREDaccuTest_DNA_CNV_Pipeline";
	public static final String HERED_ACCUTEST_AMC_CNV_PIPELINE = "HEREDaccuTest_DNA_AMC_CNV_Pipeline";
	public static final String ANALYSIS_TARGET_DNA = "DNA";
	public static final String ANALYSIS_TARGET_RNA = "RNA";
}
