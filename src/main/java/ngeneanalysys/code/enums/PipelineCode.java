package ngeneanalysys.code.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 파이프라인 기초 설정
 * 
 * @author gjyoo
 * @since 2016. 5. 31. 오전 9:28:11
 */
public enum PipelineCode {
	BRCA_445_MISEQ(PanelKitCode.BRCA_445, SequencerCode.MISEQ, ExperimentTypeCode.GERMLINE),
	BRCA_446_MISEQ(PanelKitCode.BRCA_446, SequencerCode.MISEQ, ExperimentTypeCode.GERMLINE),
	BRCA_447_MISEQ(PanelKitCode.BRCA_447, SequencerCode.MISEQ, ExperimentTypeCode.GERMLINE),
	BRCA_444_MISEQ(PanelKitCode.BRCA_444, SequencerCode.MISEQ, ExperimentTypeCode.GERMLINE),	
	BRCA_445_MISEQ_DX(PanelKitCode.BRCA_445, SequencerCode.MISEQ_DX, ExperimentTypeCode.GERMLINE),
	BRCA_446_MISEQ_DX(PanelKitCode.BRCA_446, SequencerCode.MISEQ_DX, ExperimentTypeCode.GERMLINE),
	BRCA_447_MISEQ_DX(PanelKitCode.BRCA_447, SequencerCode.MISEQ_DX, ExperimentTypeCode.GERMLINE),
	BRCA_444_MISEQ_DX(PanelKitCode.BRCA_444, SequencerCode.MISEQ_DX, ExperimentTypeCode.GERMLINE);

	/** 패널 키트 정보 */
	private PanelKitCode panelKit;
	/** 시퀀서 장비 정보 */
	private SequencerCode sequencer;
	/** 실험 구분 정보 */
	private ExperimentTypeCode experimentType;

	PipelineCode(PanelKitCode panelKit, SequencerCode sequencer, ExperimentTypeCode experimentType) {
		this.panelKit = panelKit;
		this.sequencer = sequencer;
		this.experimentType = experimentType;
	}
	
	/**
	 * 지정 시퀀서 장비에 해당하는 패널 키트 목록 반환
	 * @param sequencer
	 * @return
	 */
	public static List<PanelKitCode> getPanelKitListFromSequencer(SequencerCode sequencer) {
		List<PanelKitCode> list = new ArrayList<PanelKitCode>();
		for (PipelineCode pipeline : PipelineCode.values()) {
			if(pipeline.getSequencer() == sequencer) {
				list.add(pipeline.getPanelKit());
			}
		}
		return list;
	}
	
	/**
	 * 지정 시퀀서 장비에 해당하는 파이프라인 코드명 목록 반환
	 * @param sequencer
	 * @return
	 */
	public static List<String> getPipelineNameListFromSequencer(SequencerCode sequencer) {
		List<String> list = new ArrayList<String>();
		for (PipelineCode pipeline : PipelineCode.values()) {
			if(pipeline.equals(PipelineCode.BRCA_444_MISEQ)
				|| pipeline.equals(PipelineCode.BRCA_444_MISEQ_DX)) {
				continue;
			}
			if(pipeline.getSequencer() == sequencer) {
				list.add(pipeline.name());
			}
		}
		return list;
	}
	
	/**
	 * 지정 시퀀서 장비 타입과 패널키트 정보에 해당하는 실험 구분 정보 반환
	 * @param panelKit
	 * @param sequencer
	 * @return
	 */
	public static ExperimentTypeCode getExperimentTypeFromSequencerAndPanelKit(PanelKitCode panelKit, SequencerCode sequencer) {
		ExperimentTypeCode experimentType = null;
		for (PipelineCode pipeline : PipelineCode.values()) {
			if(pipeline.getSequencer() == sequencer && pipeline.getPanelKit() == panelKit) {
				experimentType = pipeline.getExperimentType();
			}
		}
		return experimentType;
	}

	/**
	 * @return the panelKit
	 */
	public PanelKitCode getPanelKit() {
		return panelKit;
	}

	/**
	 * @return the sequencer
	 */
	public SequencerCode getSequencer() {
		return sequencer;
	}

	/**
	 * @return the experimentType
	 */
	public ExperimentTypeCode getExperimentType() {
		return experimentType;
	}
}
