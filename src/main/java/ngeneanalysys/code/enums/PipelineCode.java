package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.CommonConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 파이프라인 기초 설정
 * 
 * @author gjyoo
 * @since 2016. 5. 31. 오전 9:28:11
 */
public enum PipelineCode {
	BRCA_ACCUTEST_DNA(CommonConstants.BRCA_ACCUTEST_DNA_PIPELINE, "BRCAaccuTest DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.AMPLICON_BASED, CommonConstants.ANALYSIS_TARGET_DNA),
	HEME_ACCUTEST_DNA(CommonConstants.HEME_ACCUTEST_DNA_PIPELINE, "HEMEaccuTest DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	SOLID_ACCUTEST_DNA(CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE, "SOLIDaccuTest DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	TST170_DNA(CommonConstants.TST_170_DNA, "TruSight Tumor 170 DNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA),
	TST170_RNA(CommonConstants.TST_170_RNA, "TruSight Tumor 170 RNA", AnalysisTypeCode.SOMATIC, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_RNA),
	HERED_ACCUTEST_DNA(CommonConstants.HERED_ACCUTEST_PIPELINE, "HEREDaccuTest DNA", AnalysisTypeCode.GERMLINE, LibraryTypeCode.HYBRIDIZATION_CAPTURE, CommonConstants.ANALYSIS_TARGET_DNA);


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
	 * @param pipelineCode
	 * @return
	 */
	public static List<SampleSourceCode> getSampleSource(PipelineCode pipelineCode) {
		List<SampleSourceCode> list = new ArrayList<>();
		switch (pipelineCode.getCode()) {
			case CommonConstants.BRCA_ACCUTEST_DNA_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				list.add(SampleSourceCode.FFPE);
				break;
			case CommonConstants.HEME_ACCUTEST_DNA_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				list.add(SampleSourceCode.BLOODCRYO);
				list.add(SampleSourceCode.BONEMARROW);
				list.add(SampleSourceCode.BONEMARROWCRYO);
				list.add(SampleSourceCode.DNA);
				list.add(SampleSourceCode.ETC);
				break;
			case CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE:
				list.add(SampleSourceCode.FFPE);
				break;
			case CommonConstants.HERED_ACCUTEST_PIPELINE:
				list.add(SampleSourceCode.BLOOD);
				break;
		}
		return list;
	}

	public static List<String> getLowConfidences(PipelineCode pipelineCode) {
		List<String> list = new ArrayList<>();
		switch (pipelineCode.getCode()) {
			case CommonConstants.HEME_ACCUTEST_DNA_PIPELINE:
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
			case CommonConstants.SOLID_ACCUTEST_DNA_PIPELINE:
				list.add("mapping_quality");
				list.add("strand_artifact");
				list.add("panel_of_normal");
				list.add("fragment_length");
				list.add("orientation_bias");
				list.add("sequencing_error");
				list.add("mapping_error");
				list.add("snp_candidate");
				break;
			case CommonConstants.HERED_ACCUTEST_PIPELINE:
				list.add("homopolymer");
				list.add("repeat_sequence");
				list.add("sequencing_error");
				list.add("mapping_error");
				list.add("snp_candidate");
				list.add("low_vaf");
				list.add("snp_for_cnv");
				list.add("cnv_probe_region");
				break;
		}
		return list;
	}
}
