package ngeneanalysys.code.enums;

/**
 * Reagent Kit Code
 * 
 * @author Joonsik,Jang
 * @since 2016. 12. 27.
 *        http://www.illumina.com/products/by-type/sequencing-kits/cluster-gen-sequencing-reagents/miseq-reagent-kit-v2.html
 */
public enum ReagentKitCode {
	MiSeq_Reagent_Kit_v2("MiSeq Reagent Kit v2", SequencerCode.MISEQ, "15 M", "50, 300, 500", "7.5 Gb", "2.25 Gb",
			"4.5 Gb", "7.5 Gb"), MiSeq_Reagent_Micro_Kit_v2("MiSeq Reagent Micro Kit v2", SequencerCode.MISEQ, "15 M",
					"50, 300, 500", "7.5 Gb", "2.25 Gb", "4.5 Gb",
					"7.5 Gb"), MiSeq_Reagent_Nano_Kit_v2("MiSeq Reagent Nano Kit v2", SequencerCode.MISEQ, "15 M",
							"50, 300, 500", "7.5 Gb", "2.25 Gb", "4.5 Gb", "7.5 Gb");
	private String description;
	private SequencerCode sequencerCode;
	private String numberOfReads;
	private String kitSize;
	private String outputMax;
	private String _2x75bpOutput;
	private String _2x150bpOutput;
	private String _2x250bpOutput;

	/**
	 * MiSeqReagentKitCode Constructor
	 * 
	 * @param sequencerCode
	 * @param numberOfReads
	 * @param kitSize
	 * @param outputMax
	 * @param _2x75bpOutput
	 * @param _2x150bpOutput
	 * @param _2x250bpOutput
	 */
	ReagentKitCode(String description, SequencerCode sequencerCode, String numberOfReads, String kitSize,
			String outputMax, String _2x75bpOutput, String _2x150bpOutput, String _2x250bpOutput) {
		this.sequencerCode = sequencerCode;
		this.description = description;
		this.numberOfReads = numberOfReads;
		this.kitSize = kitSize;
		this.outputMax = outputMax;
		this._2x75bpOutput = _2x75bpOutput;
		this._2x150bpOutput = _2x150bpOutput;
		this._2x250bpOutput = _2x250bpOutput;
	}

	/**
	 * @return the numberOfReads
	 */
	public String getNumberOfReads() {
		return numberOfReads;
	}

	/**
	 * @return the kitSize
	 */
	public String getKitSize() {
		return kitSize;
	}

	/**
	 * @return the outputMax
	 */
	public String getOutputMax() {
		return outputMax;
	}

	/**
	 * @return the _2x75bpOutput
	 */
	public String get_2x75bpOutput() {
		return _2x75bpOutput;
	}

	/**
	 * @return the _2x150bpOutput
	 */
	public String get_2x150bpOutput() {
		return _2x150bpOutput;
	}

	/**
	 * @return the _2x250bpOutput
	 */
	public String get_2x250bpOutput() {
		return _2x250bpOutput;
	}

	/**
	 * 라이브러리 사이즈별 생산량
	 * 
	 * @param librarySizeCode
	 * @return
	 */
	public String getOutput(SequencerCode sequencerCode, LibrarySizeCode librarySizeCode) {
		String output = null;
		if (sequencerCode.equals(SequencerCode.MISEQ) || sequencerCode.equals(SequencerCode.MISEQ_DX)) {
			switch (librarySizeCode) {
			case MiSeq2X75:
				output = this._2x75bpOutput;
				break;
			case MiSeq2X150:
				output = this._2x150bpOutput;
				break;
			case MiSeq2X250:
				output = this._2x250bpOutput;
				break;
			default:
				output = this._2x150bpOutput;
			}
		}		
		return output;
	}

	/**
	 * @return the sequencerCode
	 */
	public SequencerCode getSequencerCode() {
		return sequencerCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
