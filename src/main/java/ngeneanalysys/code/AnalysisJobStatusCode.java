package ngeneanalysys.code;

/**
 * 분석 작업 관련 상태값 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 31. 오후 2:11:18
 */
public class AnalysisJobStatusCode {
	private AnalysisJobStatusCode() {
		throw new IllegalStateException("Utility class");
	}

	/** job run group status : QUEUED */
	public static final String JOB_RUN_GROUP_QUEUED = "QUEUED";
	/** job run group status : UPLOADED */
	public static final String JOB_RUN_GROUP_UPLOADED = "UPLOADED";
	/** job run group status : PIPELINE */
	public static final String JOB_RUN_GROUP_PIPELINE = "PIPELINE";
	/** job run group status : COMPLETE */
	public static final String JOB_RUN_GROUP_COMPLETE = "COMPLETE";
	/** job run group status : FAIL */
	public static final String JOB_RUN_GROUP_FAIL = "FAIL";
	
	/** sample status : NONE */
	public static final String SAMPLE_ANALYSIS_STATUS_NONE = "NONE";
	/** sample status : QUEUED */
	public static final String SAMPLE_ANALYSIS_STATUS_QUEUED = "QUEUED";
	/** sample status : RUNNING */
	public static final String SAMPLE_ANALYSIS_STATUS_RUNNING = "RUNNING";
	/** sample status : COMPLETE */
	public static final String SAMPLE_ANALYSIS_STATUS_COMPLETE = "COMPLETE";
	/** sample status : FAIL */
	public static final String SAMPLE_ANALYSIS_STATUS_FAIL = "FAIL";
	
	/** sample file meta status : UPLOAD */
	public static final String SAMPLE_FILE_META_UPLOAD = "UPLOAD";
	/** sample file meta status : ACTIVE */
	public static final String SAMPLE_FILE_META_ACTIVE = "ACTIVE";

	public static final String SAMPLE_ANALYSIS_STEP_UPLOAD = "UPLOAD";
	public static final String SAMPLE_ANALYSIS_STEP_PIPELINE = "PIPELINE";
}
