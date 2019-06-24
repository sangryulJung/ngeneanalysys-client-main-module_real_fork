package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 파이프라인 기초 설정
 * 
 * @author gjyoo
 * @since 2016. 5. 31. 오전 9:28:11
 */
public enum PipelineCode {
	HEME_ACCUTEST_DNA(CommonConstants.HEME_ACCUTEST_DNA_PIPELINE, "HEMEaccuTest DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	HEME_ACCUTEST_CNV_DNA(CommonConstants.HEME_ACCUTEST_DNA_CNV_PIPELINE, "HEMEaccuTest CNV DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	SOLID_ACCUTEST_DNA(CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE, "SOLIDaccuTest DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	SOLID_ACCUTEST_CNV_DNA(CommonConstants.SOLID_ACCUTEST_DNA_CNV_PIPELINE, "SOLIDaccuTest CNV DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	HERED_ACCUTEST_DNA(CommonConstants.HERED_ACCUTEST_PIPELINE, "HEREDaccuTest DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	HERED_ACCUTEST_CNV_DNA(CommonConstants.HERED_ACCUTEST_CNV_PIPELINE, "HEREDaccuTest CNV DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	HERED_ACCUTEST_AMC_CNV_DNA(CommonConstants.HERED_ACCUTEST_AMC_CNV_PIPELINE, "HEREDaccuTest AMC CNV DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_PLUS_DNA(CommonConstants.BRCA_ACCUTEST_PLUS_DNA_PIPELINE, "BRCAaccuTest PLUS DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_PLUS_CMC_DNA(CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CMC_PIPELINE, "BRCAaccuTest PLUS CMC DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_PLUS_DNA_V3(CommonConstants.BRCA_ACCUTEST_PLUS_DNA_V3_PIPELINE, "BRCAaccuTest PLUS DNA V3", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_PLUS_DNA_V2(CommonConstants.BRCA_ACCUTEST_PLUS_DNA_V2_PIPELINE, "BRCAaccuTest PLUS DNA V2", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_PLUS_CNV_DNA_V2(CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CNV_V2_PIPELINE, "BRCAaccuTest PLUS DNA CNV V2", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_PLUS_CNV_DNA(CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CNV_PIPELINE, "BRCAaccuTest PLUS CNV DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_CNV_DNA(CommonConstants.BRCA_ACCUTEST_DNA_CNV_PIPELINE, "BRCAaccuTest CNV DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	BRCA_ACCUTEST_DNA(CommonConstants.BRCA_ACCUTEST_DNA_PIPELINE, "BRCAaccuTest DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	TST170_DNA(CommonConstants.TST170_DNA_PIPELINE, "TruSight Tumor 170 DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	TST170_RNA(CommonConstants.TST170_RNA_PIPELINE, "TruSight Tumor 170 RNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_RNA);

	private String code;
	/** 패널 키트 정보 */
	private String description;

	/** 실험 구분 정보 */
	private AnalysisTypeCode analysisType;

	private LibraryTypeCode libraryType;

	private String analysisTarget;

	PipelineCode(String code, String description, AnalysisTypeCode analysisType, LibraryTypeCode libraryType, String analysisTarget) {
		this.code = code;
		this.description = description;
		this.libraryType = libraryType;
		this.analysisType = analysisType;
		this.analysisTarget = analysisTarget;
	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return description;
	}

	public AnalysisTypeCode getAnalysisType() {
		return this.analysisType;
	}
	public LibraryTypeCode getLibraryType() {
		return this.libraryType;
	}
	public String getAnalysisTarget() {
		return this.analysisTarget;
	}
	/**
	 * 지정 시퀀서 장비에 해당하는 패널 키트 목록 반환
	 * @param pipelineCode String
	 * @return List<SampleSourceCode>
	 */
	public static List<SampleSourceCode> getSampleSource(String pipelineCode) {
		List<SampleSourceCode> list = new ArrayList<>();
		switch (pipelineCode) {
			case CommonConstants.BRCA_ACCUTEST_DNA_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_V3_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CMC_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_V2_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_DNA_CNV_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CNV_V2_PIPELINE:
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CNV_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				list.add(SampleSourceCode.FFPE);
				break;
			case CommonConstants.HEME_ACCUTEST_DNA_PIPELINE:
			case CommonConstants.HEME_ACCUTEST_DNA_CNV_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				list.add(SampleSourceCode.BLOODCRYO);
				list.add(SampleSourceCode.BONEMARROW);
				list.add(SampleSourceCode.BONEMARROWCRYO);
				list.add(SampleSourceCode.DNA);
				list.add(SampleSourceCode.ETC);
				break;
			case CommonConstants.TST170_DNA_PIPELINE:
			case CommonConstants.TST170_RNA_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				list.add(SampleSourceCode.BLOODCRYO);
				list.add(SampleSourceCode.BONEMARROW);
				list.add(SampleSourceCode.BONEMARROWCRYO);
				list.add(SampleSourceCode.FFPE);
				list.add(SampleSourceCode.DNA);
				list.add(SampleSourceCode.ETC);
				break;
			case CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE:
			case CommonConstants.SOLID_ACCUTEST_DNA_CNV_PIPELINE:
				list.add(SampleSourceCode.FFPE);
				break;
			case CommonConstants.HERED_ACCUTEST_PIPELINE:
			case CommonConstants.HERED_ACCUTEST_CNV_PIPELINE:
			case CommonConstants.HERED_ACCUTEST_AMC_CNV_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				break;
			default:
		}
		return list;
	}

	public static PipelineCode getPipelineCode(String pipelineCode) {
		PipelineCode returnPipeline;
		switch (pipelineCode) {
			case CommonConstants.BRCA_ACCUTEST_DNA_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_DNA;
				break;
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_PLUS_DNA;
				break;
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CMC_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_PLUS_CMC_DNA;
				break;
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_V3_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_PLUS_DNA_V3;
				break;
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_V2_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_PLUS_DNA_V2;
				break;
			case CommonConstants.HEME_ACCUTEST_DNA_PIPELINE:
				returnPipeline = HEME_ACCUTEST_DNA;
			break;
			case CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE:
				returnPipeline = SOLID_ACCUTEST_DNA;
				break;
			case CommonConstants.TST170_DNA_PIPELINE:
				returnPipeline = TST170_DNA;
				break;
			case CommonConstants.TST170_RNA_PIPELINE:
				returnPipeline = TST170_RNA;
				break;
			case CommonConstants.HERED_ACCUTEST_PIPELINE:
				returnPipeline = HERED_ACCUTEST_DNA;
				break;
			case CommonConstants.HERED_ACCUTEST_CNV_PIPELINE:
				returnPipeline = HERED_ACCUTEST_CNV_DNA;
				break;
			case CommonConstants.HERED_ACCUTEST_AMC_CNV_PIPELINE:
				returnPipeline = HERED_ACCUTEST_AMC_CNV_DNA;
				break;
			case CommonConstants.HEME_ACCUTEST_DNA_CNV_PIPELINE:
				returnPipeline = HEME_ACCUTEST_CNV_DNA;
				break;
			case CommonConstants.SOLID_ACCUTEST_DNA_CNV_PIPELINE:
				returnPipeline = SOLID_ACCUTEST_CNV_DNA;
				break;
			case CommonConstants.BRCA_ACCUTEST_DNA_CNV_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_CNV_DNA;
				break;
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CNV_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_PLUS_CNV_DNA;
				break;
			case CommonConstants.BRCA_ACCUTEST_PLUS_DNA_CNV_V2_PIPELINE:
				returnPipeline = BRCA_ACCUTEST_PLUS_CNV_DNA_V2;
				break;
			default:
				returnPipeline = null;
		}
		return returnPipeline;
	}

	public static List<String> getLowConfidences(String pipelineCode) {
		List<String> list = new ArrayList<>();
		switch (pipelineCode) {
			case CommonConstants.HEME_ACCUTEST_DNA_PIPELINE:
			case CommonConstants.HEME_ACCUTEST_DNA_CNV_PIPELINE:
			case CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE:
			case CommonConstants.SOLID_ACCUTEST_DNA_CNV_PIPELINE:
				list.add("mapping_quality");
				list.add("strand_artifact");
				list.add("panel_of_normal");
				list.add("fragment_length");
				list.add("orientation_bias");
				list.add("sequencing_error");
				list.add("mapping_error");
				list.add("snp_candidate");
				list.add("t_lod");
				break;
			case CommonConstants.HERED_ACCUTEST_PIPELINE:
			case CommonConstants.HERED_ACCUTEST_CNV_PIPELINE:
			case CommonConstants.HERED_ACCUTEST_AMC_CNV_PIPELINE:
				list.add("homopolymer");
				list.add("repeat_sequence");
				list.add("sequencing_error");
				list.add("mapping_error");
				break;
			default:
		}
		return list;
	}

	public static boolean isBRCAPipeline(String code) {
		return StringUtils.isNotEmpty(code) && (code.equals(BRCA_ACCUTEST_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_CNV_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_CNV_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_CMC_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_DNA_V3.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_DNA_V2.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_CNV_DNA_V2.getCode()));
	}

	public static boolean isBRCACNVPipeline(String code) {
		return StringUtils.isNotEmpty(code) && (code.equals(BRCA_ACCUTEST_PLUS_CNV_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_CNV_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_CMC_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_CNV_DNA_V2.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_DNA_V3.getCode()));
	}

	public static boolean isHeredPipeline(String code) {
		return StringUtils.isNotEmpty(code) && (code.equals(HERED_ACCUTEST_DNA.getCode())
				|| code.equals(HERED_ACCUTEST_CNV_DNA.getCode())
				|| code.equals(HERED_ACCUTEST_AMC_CNV_DNA.getCode()));
	}

	public static boolean isHemePipeline(String code) {
		return StringUtils.isNotEmpty(code) && (code.equals(HEME_ACCUTEST_CNV_DNA.getCode())
				|| code.equals(HEME_ACCUTEST_DNA.getCode()));
	}

	public static boolean isSolidPipeline(String code) {
		return StringUtils.isNotEmpty(code) && (code.equals(SOLID_ACCUTEST_DNA.getCode())
				|| code.equals(SOLID_ACCUTEST_CNV_DNA.getCode()));
	}

	public static boolean isNotCnvPanel(String code) {
		return StringUtils.isNotEmpty(code) && (code.equals(SOLID_ACCUTEST_DNA.getCode())
				|| code.equals(HEME_ACCUTEST_DNA.getCode())
				|| code.equals(HERED_ACCUTEST_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_DNA.getCode())
				|| code.equals(BRCA_ACCUTEST_PLUS_DNA_V2.getCode())
				|| code.equals(TST170_DNA.getCode())
				|| code.equals(TST170_RNA.getCode()));
	}
}
