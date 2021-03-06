package ngeneanalysys.code.constants;

/**
 * @author Jang
 * @since 2017. 8. 1.
 *
 */
public class FXMLConstants {

	private FXMLConstants() { throw new IllegalAccessError("FXMLConstants class"); }

	/** 서버 설정 화면 */
	public static final String SERVER_URL_SETTING = "/layout/fxml/ServerURLSetting.fxml";

	/** System Menu > 사용자 정보 변경 화면 */
	public static final String SYSTEM_MENU_EDIT = "/layout/fxml/SystemMenu-Edit.fxml";

	/** System Menu > 시스템 설정 화면 */
	public static final String SYSTEM_MENU_SETTING = "/layout/fxml/SystemMenu-Setting.fxml";

	/** System Menu > Support 화면 */
	public static final String SYSTEM_MENU_SUPPORT = "/layout/fxml/SystemMenu-Support.fxml";

	/** System Menu > License 화면 */
	public static final String SYSTEM_MENU_LICENSE = "/layout/fxml/SystemMenu-License.fxml";

	/** System Menu > License 화면 */
	public static final String SYSTEM_MENU_SOFTWARE_VERSION = "/layout/fxml/SystemMenu-SoftwareVersion.fxml";

	/** System Menu > Public Databases 정보 화면 */
	public static final String SYSTEM_MENU_PUBLIC_DATABASES = "/layout/fxml/SystemMenu-PublicDatabases.fxml";

	/** Public Databases 정보 화면 */
	public static final String SYSTEM_MENU_DEFAULT_PUBLIC_DATABASE = "/layout/fxml/SystemMenu-PublicDatabases1.fxml";

	/** Tools 정보 화면 */
	public static final String SYSTEM_MENU_TOOLS = "/layout/fxml/SystemMenu-PublicTools1.fxml";

	/** 로그인 화면 */
	public static final String LOGIN = "/layout/fxml/Login.fxml";

	/** 메인 화면 */
	public static final String MAIN = "/layout/fxml/Main.fxml";

	/** 분석자(실험자) Home 화면 */
	public static final String HOME = "/layout/fxml/Home.fxml";

	/** 분석자(실험자) Past Results (최근 완료 분석건) 화면 */
	public static final String PAST_RESULTS = "/layout/fxml/PastResults.fxml";

	/** FAST 파일 업로드 창 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_MAIN = "/layout/fxml/SampleUpload.fxml";

	/** FAST 파일 업로드 창 3 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_FIRST = "/layout/fxml/SampleUploadScreenFirst.fxml";

	/** FAST 파일 업로드 창 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_PROGRESS_TASK = "/layout/fxml/AnalysisSampleUploadProgressTask.fxml";

	/** 분석 상세 레이아웃 화면 */
	public static final String ANALYSIS_DETAIL_LAYOUT = "/layout/fxml/AnalysisDetailLayout.fxml";

	/** 분석 상세 > Raw data 화면 */
	public static final String ANALYSIS_DETAIL_RAW_DATA = "/layout/fxml/AnalysisDetail-RawData.fxml";

	/** 분석 상세 > Report 화면 */
	public static final String ANALYSIS_DETAIL_REPORT = "/layout/fxml/AnalysisDetail-Report.fxml";

	/** 분석 상세 > Report 화면 */
	public static final String ANALYSIS_DETAIL_REPORT_GERMLINE = "/layout/fxml/AnalysisDetail-Report-Germline.fxml";

	/** 분석 상세 > Report 화면 */
	public static final String ANALYSIS_DETAIL_TST_RNA_REPORT = "/layout/fxml/AnalysisDetail-TSTRNAReport.fxml";

	/** 분석 상세 > OverView 화면 */
	public static final String ANALYSIS_DETAIL_OVERVIEW = "/layout/fxml/AnalysisDetail-Overview.fxml";

	/** 분석 상세 > OverView 화면 */
	public static final String ANALYSIS_DETAIL_OVERVIEW_GERMLINE = "/layout/fxml/AnalysisDetail-Overview-Germline.fxml";

	/** 분석 상세 > OverView 화면 */
	public static final String ANALYSIS_DETAIL_OVERVIEW_TST_RNA = "/layout/fxml/AnalysisDetail-Overview-TST-RNA.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_CNV = "/layout/fxml/AnalysisDetail-CNV.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_BRCA_CNV = "/layout/fxml/AnalysisDetail-BRCA-CNV.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_TST_CNV = "/layout/fxml/AnalysisDetail-TST-CNV.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_TST_FUSION = "/layout/fxml/AnalysisDetail-TST170Fusion.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_TST_SPLICE_VARIANT = "/layout/fxml/AnalysisDetail-TSTSpliceVariant.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_TST_PUBLISHED_FUSION = "/layout/fxml/AnalysisDetail-TSTPublishedFusion.fxml";

	/** 시스템 매니저 화면 */
	public static final String SYSTEM_MANAGER_HOME = "/layout/fxml/SystemManagerHome.fxml";

	/** 시스템 매니저 사용자 정보 화면 */
	public static final String SYSTEM_MANAGER_USER_ACCOUNT = "/layout/fxml/manager/SystemManagerUserAccount.fxml";

	/** 시스템 매니저 분석 상태 화면 */
	public static final String SYSTEM_MANAGER_ANALYSIS_STATUS = "/layout/fxml/manager/SystemManagerAnalysisStatus.fxml";

	/** 시스템 매니저 로그 정보 화면 */
	public static final String SYSTEM_MANAGER_LOG_LIST = "/layout/fxml/manager/SystemManagerSystemLogs.fxml";

	/** 시스템 매니저 패널 관리 */
	public static final String SYSTEM_MANAGER_PANEL = "/layout/fxml/manager/SystemManagerPanel.fxml";

	/** 시스템 매니저 리포트 양식 관리 */
	public static final String SYSTEM_MANAGER_REPORT_TEMPLATE = "/layout/fxml/manager/SystemManagerReportTemplate.fxml";

	/** 시스템 매니저 Evidence Database 관리 */
	public static final String SYSTEM_MANAGER_NEWS_AND_TIPS = "/layout/fxml/manager/SystemManagerNewsAndTips.fxml";

	/** 시스템 매니저 Evidence Database 관리 */
	public static final String SYSTEM_MANAGER_INTERPRETATION_DATABASE = "/layout/fxml/manager/SystemManagerInterpretationDatabase.fxml";

	/** 사용자 추가/수정 화면 */
	public static final String USER_ACCOUNT = "/layout/fxml/manager/UserAccount.fxml";

	/** 사용자 추가/수정 화면 */
	public static final String GROUP_ADD = "/layout/fxml/manager/GroupAdd.fxml";

	public static final String EXCLUDE_REPORT = "/layout/fxml/ExcludeReportDialog.fxml";

	public static final String BATCH_EXCLUDE_REPORT = "/layout/fxml/BatchExcludeReportDialog.fxml";

	public static final String BATCH_CHANGE_TIER = "/layout/fxml/BatchChangeTierDialog.fxml";

	public static final String BATCH_CHANGE_PATHOGENICITY = "/layout/fxml/BatchChangePathogenicityDialog.fxml";

	public static final String BATCH_FALSE_POSITIVE = "/layout/fxml/BatchFalsePositiveDialog.fxml";

	public static final String CHANGE_PATHOGENICITY = "/layout/fxml/ChangePathogenicityDialog.fxml";

	public static final String VIRTUAL_PANEL_EDIT = "/layout/fxml/VirtualPanelEdit.fxml";

	public static final String SERVER_DIRECTORY_VIEW = "/layout/fxml/ServerDirectoryView.fxml";

	public static final String ANALYSIS_DETAIL_VARIANTS = "/layout/fxml/AnalysisDetail-Variants.fxml";

	public static final String ANALYSIS_DETAIL_VARIANTS_SNV = "/layout/fxml/AnalysisDetail-SNV.fxml";

	public static final String ANALYSIS_DETAIL_INTERPRETATION_LOGS = "/layout/fxml/detail/AnalysisDetail-SNPs-INDELs-Memo.fxml";

	public static final String ANALYSIS_DETAIL_INTERPRETATION = "/layout/fxml/detail/AnalysisDetail-Interpretation.fxml";

	public static final String ANALYSIS_DETAIL_VARIANT_STATISTICS = "/layout/fxml/detail/VariantStatistics.fxml";

	public static final String ANALYSIS_DETAIL_CLINICAL_SIGNIFICANT = "/layout/fxml/detail/ClinicalSignificant.fxml";

	public static final String ANALYSIS_DETAIL_VARIANT_DETAIL = "/layout/fxml/detail/VariantDetail.fxml";

	public static final String ANALYSIS_DETAIL_READ_DEPTH = "/layout/fxml/detail/ReadDepthVariantFraction.fxml";

	public static final String ANALYSIS_DETAIL_VARIANT_NOMENCLATURE = "/layout/fxml/detail/VariantNomenclature.fxml";

	public static final String ANALYSIS_DETAIL_POPULATION_FREQUENCIES = "/layout/fxml/detail/PopulationFrequencies.fxml";

	public static final String ANALYSIS_DETAIL_DETAIL_SUB_INFO = "/layout/fxml/detail/DetailSubInfo.fxml";

	public static final String ANALYSIS_DETAIL_IN_SILICO_PREDICTIONS = "/layout/fxml/detail/InSilicoPredictions.fxml";

	public static final String ANALYSIS_DETAIL_VARIANT_FILTER = "/layout/fxml/VariantFilter.fxml";

	public static final String RUN_RAW_DATA_DOWNLOAD = "/layout/fxml/RunRawDataDownload.fxml";

	public static final String RAW_DATA_DOWNLOAD_TASK = "/layout/fxml/AnalysisSampleDownloadProgressTask.fxml";

	public static final String WORK_PROGRESS = "/layout/fxml/WorkProgress.fxml";

	public static final String ANALYSIS_DETAIL_BRCA_CNV_OVERVIEW = "/layout/fxml/AnalysisDetail-Overview-BRCA-CNV.fxml";

	public static final String ANALYSIS_DETAIL_BRCA_CNV_REPORT = "/layout/fxml/AnalysisDetail-Report-Germline-CNV-Report.fxml";

	public static final String ANALYSIS_DETAIL_HERED_CNV = "/layout/fxml/AnalysisDetail-HERED-CNV.fxml";

	public static final String ANALYSIS_DETAIL_HERED_AMC_CNV_REPORT = "/layout/fxml/AnalysisDetail-Report-Germline-AMC-CNV-Report.fxml";

	public static final String ANALYSIS_DETAIL_SOLID_AMC_CNV_REPORT = "/layout/fxml/AnalysisDetail-Report-Solid-CNV-Report.fxml";

	public static final String ANALYSIS_DETAIL_HERED_AMC_OVERVIEW = "/layout/fxml/AnalysisDetail-Overview-Hered-CNV.fxml";

	public static final String ANALYSIS_DETAIL_SOLID_CNV_OVERVIEW = "/layout/fxml/AnalysisDetail-Overview-Solid-CNV.fxml";

	public static final String BATCH_BRCA_CNV = "/layout/fxml/BatchChangeBrcaCnvDialog.fxml";

	public static final String SYSTEM_MANAGER_CUSTOM_DATABASE = "/layout/fxml/manager/customDatabase.fxml";
}
