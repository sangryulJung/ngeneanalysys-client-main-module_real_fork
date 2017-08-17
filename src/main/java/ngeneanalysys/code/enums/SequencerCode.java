package ngeneanalysys.code.enums;

/**
 * 시퀀서 장비 타입 코드
 * 
 * @author gjyoo
 * @since 2016. 5. 24. 오전 10:30:47
 */
public enum SequencerCode {
	MISEQ("Illumina MiSeq"),
	MISEQ_DX("Illumina MiSeq Dx");
	
	private String description;
	
	SequencerCode(String name) {
		this.description = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
