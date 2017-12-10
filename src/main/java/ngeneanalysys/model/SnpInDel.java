package ngeneanalysys.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * 분석 결과 변이 목록
 * 
 * @author gjyoo
 * @since 2016. 6. 15. 오후 7:37:59
 */
public class SnpInDel implements Serializable {
	private static final long serialVersionUID = -5728637480602616382L;

	private Integer id;
	private Integer sampleId;
	private String serialNumber;
	private String swPathogenicityLevel;
	private String expertPathogenicityLevel;
	private String swTier;
	private String expertTier;
	private String includedInReport;
	private String hasWarning;
	private String warningReason;
	private Integer variantNum;
	private String comment;

	private SnpInDelExpression snpInDelExpression;

	private ClinicalDB clinicalDB;

	private SequenceInfo sequenceInfo;

	private ReadInfo readInfo;

	private PopulationFrequency populationFrequency;

	private Integer interpretationEvidenceId;


	/**
	 * @return sequenceInfo
	 */
	public SequenceInfo getSequenceInfo() {
		return sequenceInfo;
	}

	/**
	 * @param sequenceInfo
	 */
	public void setSequenceInfo(SequenceInfo sequenceInfo) {
		this.sequenceInfo = sequenceInfo;
	}

	/**
	 * @return readInfo
	 */
	public ReadInfo getReadInfo() {
		return readInfo;
	}

	/**
	 * @param readInfo
	 */
	public void setReadInfo(ReadInfo readInfo) {
		this.readInfo = readInfo;
	}

	/**
	 * @return populationFrequency
	 */
	public PopulationFrequency getPopulationFrequency() {
		return populationFrequency;
	}

	/**
	 * @param populationFrequency
	 */
	public void setPopulationFrequency(PopulationFrequency populationFrequency) {
		this.populationFrequency = populationFrequency;
	}

	/**
	 * @return clinicalSignificant
	 */
	public ClinicalDB getClinicalDB() {
		return clinicalDB;
	}

	/**
	 * @param clinicalDB
	 */
	public void setClinicalDB(ClinicalDB clinicalDB) {
		this.clinicalDB = clinicalDB;
	}

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return sampleId
	 */
	public Integer getSampleId() {
		return sampleId;
	}

	/**
	 * @param sampleId
	 */
	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
	 * @return swPathogenicityLevel
	 */
	public String getSwPathogenicityLevel() {
		return swPathogenicityLevel;
	}

	/**
	 * @param swPathogenicityLevel
	 */
	public void setSwPathogenicityLevel(String swPathogenicityLevel) {
		this.swPathogenicityLevel = swPathogenicityLevel;
	}

	/**
	 * @return expertPathogenicityLevel
	 */
	public String getExpertPathogenicityLevel() {
		return expertPathogenicityLevel;
	}

	/**
	 * @param expertPathogenicityLevel
	 */
	public void setExpertPathogenicityLevel(String expertPathogenicityLevel) {
		this.expertPathogenicityLevel = expertPathogenicityLevel;
	}

	/**
	 * @return swTier
	 */
	public String getSwTier() {
		return swTier;
	}

	/**
	 * @param swTier
	 */
	public void setSwTier(String swTier) {
		this.swTier = swTier;
	}

	/**
	 * @return expertTier
	 */
	public String getExpertTier() {
		return expertTier;
	}

	/**
	 * @param expertTier
	 */
	public void setExpertTier(String expertTier) {
		this.expertTier = expertTier;
	}

	/**
	 * @return includedInReport
	 */
	public String getIncludedInReport() {
		return includedInReport;
	}

	/**
	 * @param includedInReport
	 */
	public void setIncludedInReport(String includedInReport) {
		this.includedInReport = includedInReport;
	}

	/**
	 * @return hasWarning
	 */
	public String getHasWarning() {
		if("Y".equalsIgnoreCase(this.hasWarning)) {
			return (!StringUtils.isEmpty(this.warningReason)) ? this.warningReason : "NONE";
		}
		return null;
	}

	/**
	 * @param hasWarning
	 */
	public void setHasWarning(String hasWarning) {
		this.hasWarning = hasWarning;
	}

	/**
	 * @return warningReason
	 */
	public String getWarningReason() {
		return warningReason;
	}

	/**
	 * @param warningReason
	 */
	public void setWarningReason(String warningReason) {
		this.warningReason = warningReason;
	}

	/**
	 * @return variantNum
	 */
	public Integer getVariantNum() {
		return variantNum;
	}

	/**
	 * @param variantNum
	 */
	public void setVariantNum(Integer variantNum) {
		this.variantNum = variantNum;
	}

	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return snpInDelExpression
	 */
	public SnpInDelExpression getSnpInDelExpression() {
		return snpInDelExpression;
	}

	/**
	 * @param snpInDelExpression
	 */
	public void setSnpInDelExpression(SnpInDelExpression snpInDelExpression) {
		this.snpInDelExpression = snpInDelExpression;
	}

    /**
     * @return interpretationEvidenceId
     */
    public Integer getInterpretationEvidenceId() {
        return interpretationEvidenceId;
    }

    /**
     * @param interpretationEvidenceId
     */
    public void setInterpretationEvidenceId(Integer interpretationEvidenceId) {
        this.interpretationEvidenceId = interpretationEvidenceId;
    }
}
