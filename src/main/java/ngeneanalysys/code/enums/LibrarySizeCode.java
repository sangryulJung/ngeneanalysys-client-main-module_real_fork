package ngeneanalysys.code.enums;

/**
 * Library Size Code
 * 
 * @author Joonsik,Jang
 * @since 2016. 12. 27.
 */
public enum LibrarySizeCode {
	MiSeq2X75(SequencerCode.MISEQ, "2 x 75 bp"), MiSeq2X150(SequencerCode.MISEQ, "2 x 150 bp"), MiSeq2X250(SequencerCode.MISEQ, "2 x 250 bp");

	private String description;
	private SequencerCode sequencerCode;
	
	LibrarySizeCode(SequencerCode sequencerCode, String description) {
		this.description = description;
		this.sequencerCode = sequencerCode;
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
