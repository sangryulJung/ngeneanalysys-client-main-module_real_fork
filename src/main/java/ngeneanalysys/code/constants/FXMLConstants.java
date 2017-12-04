/**
 * 
 */
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
	
	/** System Menu > Public Databases 정보 화면 */
	public static final String SYSTEM_MENU_PUBLIC_DATABASES = "/layout/fxml/SystemMenu-PublicDatabases.fxml";

	/** 로그인 화면 */
	public static final String LOGIN = "/layout/fxml/Login.fxml";

	/** 메인 화면 */
	public static final String MAIN = "/layout/fxml/Main.fxml";

	/** 분석자(실험자) Home 화면 */
	public static final String HOME = "/layout/fxml/Home.fxml";

	/** 분석자(실험자) Past Results (최근 완료 분석건) 화면 */
	public static final String PAST_RESULTS = "/layout/fxml/PastResults.fxml";

	/** 환자 DB 화면 */
	public static final String PATIENT_DB = "/layout/fxml/PatientDB.fxml";

	/** 환자 DB 검색 Dialog 화면 */
	public static final String PATIENT_SEARCH = "/layout/fxml/PatientSearch.fxml";

	/** FAST 파일 업로드 창 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_MAIN = "/layout/fxml/SampleUpload.fxml";

	/** FAST 파일 업로드 창  1 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_SECOND = "/layout/fxml/SampleUploadScreenSecond.fxml";

	/** FAST 파일 업로드 창 2 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_THIRD = "/layout/fxml/SampleUploadScreenThird.fxml";

	/** FAST 파일 업로드 창 3 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_FIRST = "/layout/fxml/SampleUploadScreenFirst.fxml";
	
	/** FAST 파일 업로드 창 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_PROGRESS_TASK = "/layout/fxml/AnalysisSampleUploadProgressTask.fxml";
	
	/** FAST 파일 업로드 상세 정보 창 */
	public static final String ANALYSIS_SAMPLE_UPLOAD_PROGRESS_DETAIL = "/layout/fxml/AnalysisSampleUploadProgressDetail.fxml";
	
	/** 분석 상세 레이아웃 화면 */
	public static final String ANALYSIS_DETAIL_LAYOUT = "/layout/fxml/AnalysisDetailLayout.fxml";
	
	/** 분석 상세 > Read Statistics 화면 */
	public static final String ANALYSIS_DETAIL_READ_STATISTICS = "/layout/fxml/AnalysisDetail-ReadStatistics.fxml";
	
	/** 분석 상세 > Assay Target 화면 */
	public static final String ANALYSIS_DETAIL_ASSAY_TARGET = "/layout/fxml/AnalysisDetail-AssayTarget.fxml";
	
	/** 분석 상세 > Variant Summary 화면 */
	public static final String ANALYSIS_DETAIL_VARIANT_SUMMARY = "/layout/fxml/AnalysisDetail-VariantSummary.fxml";
	
	/** 분석 상세 > SNPs-INDELs 레이아웃 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_LAYOUT = "/layout/fxml/AnalysisDetail-SNPs-INDELs.fxml";
	
	/** 분석 상세 > SNPs-INDELs > Flagging Dialog 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_FLAGGING_DIALOG = "/layout/fxml/AnalysisDetail-SNPs-INDELs-Flagging.fxml";
	
	/** 분석 상세 > SNPs-INDELs > FUSIONGENE TAB 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW = "/layout/fxml/AnalysisDetail-SNPs-INDELs-Overview.fxml";
	
	/** 분석 상세 > SNPs-INDELs > FUSIONGENE TAB 화면 > BRCA 파이프라인 Link 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_BRCA = "/layout/fxml/AnalysisDetail-SNPs-INDELs-Overview-Link-BRCA.fxml";

	/** 분석 상세 > SNPs-INDELs > FUSIONGENE TAB 화면 > BRCA 파이프라인 Link 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_SOMATIC = "/layout/fxml/AnalysisDetail-SNPs-INDELs-Overview-Link-SOMATIC.fxml";
	
	/** 분석 상세 > SNPs-INDELs > DATA SUMMARY TAB 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_DATA_SUMMARY = "/layout/fxml/AnalysisDetail-SNPs-INDELs-DataSummary.fxml";
	
	/** 분석 상세 > SNPs-INDELs > Comment TAB 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_MEMO = "/layout/fxml/AnalysisDetail-SNPs-INDELs-Memo.fxml";
	
	/** 분석 상세 > SNPs-INDELs > VIEWER TAB 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_VIEWER = "/layout/fxml/AnalysisDetail-SNPs-INDELs-Viewer.fxml";
	
	/** 분석 상세 > SNPs-INDELs > WARNINGS TAB 화면 */
	public static final String ANALYSIS_DETAIL_SNPS_INDELS_LOW_CONFIDENCE = "/layout/fxml/AnalysisDetail-SNPs-INDELs-LowConfidence.fxml";

	/** 분석 상세 > SNPs-INDELs > WARNINGS TAB 화면 */
	public static final String ANALYSIS_DETAIL_FUSION = "/layout/fxml/AnalysisDetail-Fusion.fxml";
	
	/** 분석 상세 > Genes 화면 */
	public static final String ANALYSIS_DETAIL_GENES = "/layout/fxml/AnalysisDetail-Genes.fxml";
	
	/** 분석 상세 > Warnings 화면 */
	public static final String ANALYSIS_DETAIL_WARNINGS = "/layout/fxml/AnalysisDetail-Warnings.fxml";
	
	/** 분석 상세 > Raw data 화면 */
	public static final String ANALYSIS_DETAIL_RAW_DATA = "/layout/fxml/AnalysisDetail-RawData.fxml";
	
	/** 분석 상세 > Report 화면 */
	public static final String ANALYSIS_DETAIL_REPORT = "/layout/fxml/AnalysisDetail-Report.fxml";
	
	/** 분석 요청 그룹 검색 Dialog 화면 */
	public static final String ANALYSIS_JOB_RUN_GROUP_SEARCH_DIALOG = "/layout/fxml/AnalysisJobRunGroupSearch.fxml";

	/** 분석 상세 > OverView 화면 */
	public static final String ANALYSIS_DETAIL_OVERVIEW = "/layout/fxml/AnalysisDetail-Overview.fxml";

	/** 분석 상세 > Variants 화면 */
	public static final String ANALYSIS_DETAIL_TARGET = "/layout/fxml/AnalysisDetail-Target.fxml";

	/** 시스템 매니저 화면 */
	public static final String SYSTEM_MANAGER_HOME = "/layout/fxml/SystemManagerHome.fxml";

	/** 시스템 매니저 사용자 정보 화면 */
	public static final String SYSTEM_MANAGER_USER_ACCOUNT = "/layout/fxml/systemManager/SystemManagerUserAccount.fxml";

	/** 시스템 매니저 서버 정보 화면 */
	public static final String SYSTEM_MANAGER_SERVER_STATUS = "/layout/fxml/systemManager/SystemManagerServerStatus.fxml";

	/** 시스템 매니저 분석 상태 화면 */
	public static final String SYSTEM_MANAGER_ANALYSIS_STATUS = "/layout/fxml/systemManager/SystemManagerAnalysisStatus.fxml";

	/** 시스템 매니저 로그 정보 화면 */
	public static final String SYSTEM_MANAGER_LOG_LIST = "/layout/fxml/systemManager/SystemManagerSystemLogs.fxml";

	/** 시스템 매니저 패널 관리 */
	public static final String SYSTEM_MANAGER_PANEL = "/layout/fxml/systemManager/SystemManagerPanel.fxml";

	/** 시스템 매니저 리포트 양식 관리 */
	public static final String SYSTEM_MANAGER_REPORT_TEMPLATE = "/layout/fxml/systemManager/SystemManagerReportTemplate.fxml";

	/** 사용자 추가/수정 화면 */
	public static final String USER_ACCOUNT = "/layout/fxml/systemManager/UserAccount.fxml";

	/** 사용자 추가/수정 화면 */
	public static final String GROUP_ADD = "/layout/fxml/systemManager/GroupAdd.fxml";

	public static final String EXCLUDE_REPORT = "/layout/fxml/ExcludeReportDialog.fxml";

	public static final String CHANGE_TIER = "/layout/fxml/ChangeTierDialog.fxml";

	public static final String ANALYSIS_DETAIL_FUSION_MAIN = "/layout/fxml/AnalysisDetail-FusionMain.fxml";

	public static final String ANALYSIS_DETAIL_FUSION_GENE = "/layout/fxml/AnalysisDetail-FusionGene.fxml";

	public static final String ANALYSIS_DETAIL_GENE_EXPRESSION = "/layout/fxml/AnalysisDetail-GeneExpression.fxml";

}
