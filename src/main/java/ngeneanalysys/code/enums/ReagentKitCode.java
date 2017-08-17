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
	 * @param name
	 * @param numberOfReads
	 * @param kitSize
	 * @param outputMax
	 * @param _2x75bpOutput
	 * @param _2x150bpOutput
	 * @param _2x250bpOutput
	 */
	ReagentKitCode(String description, SequencerCode sequencerCode, String numberOfReads, String kitSize,
			String outputMax, String _2x75bpOutput, String _2x150bpOutput, String _2x250bpOutput) {
		this.setSequencerCode(sequencerCode);
		this.setDescription(description);
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
	 * @param numberOfReads
	 *            the numberOfReads to set
	 */
	public void setNumberOfReads(String numberOfReads) {
		this.numberOfReads = numberOfReads;
	}

	/**
	 * @return the kitSize
	 */
	public String getKitSize() {
		return kitSize;
	}

	/**
	 * @param kitSize
	 *            the kitSize to set
	 */
	public void setKitSize(String kitSize) {
		this.kitSize = kitSize;
	}

	/**
	 * @return the outputMax
	 */
	public String getOutputMax() {
		return outputMax;
	}

	/**
	 * @param outputMax
	 *            the outputMax to set
	 */
	public void setOutputMax(String outputMax) {
		this.outputMax = outputMax;
	}

	/**
	 * @return the _2x75bpOutput
	 */
	public String get_2x75bpOutput() {
		return _2x75bpOutput;
	}

	/**
	 * @param _2x75bpOutput
	 *            the _2x75bpOutput to set
	 */
	public void set_2x75bpOutput(String _2x75bpOutput) {
		this._2x75bpOutput = _2x75bpOutput;
	}

	/**
	 * @return the _2x150bpOutput
	 */
	public String get_2x150bpOutput() {
		return _2x150bpOutput;
	}

	/**
	 * @param _2x150bpOutput
	 *            the _2x150bpOutput to set
	 */
	public void set_2x150bpOutput(String _2x150bpOutput) {
		this._2x150bpOutput = _2x150bpOutput;
	}

	/**
	 * @return the _2x250bpOutput
	 */
	public String get_2x250bpOutput() {
		return _2x250bpOutput;
	}

	/**
	 * @param _2x250bpOutput
	 *            the _2x250bpOutput to set
	 */
	public void set_2x250bpOutput(String _2x250bpOutput) {
		this._2x250bpOutput = _2x250bpOutput;
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
	 * @param sequencerCode
	 *            the sequencerCode to set
	 */
	public void setSequencerCode(SequencerCode sequencerCode) {
		this.sequencerCode = sequencerCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
