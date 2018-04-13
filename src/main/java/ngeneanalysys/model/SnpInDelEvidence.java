package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2018-04-11
 */
public class SnpInDelEvidence {
    private Integer id;
    private Integer snpInDelId;
    private String provider;
    private Boolean primaryEvidence;
    private String evidenceType;
    private String evidenceLevel;
    private String evidence;
    private String status;
    private DateTime createdAt;
    private DateTime updatedAt;

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
     * @return snpInDelId
     */
    public Integer getSnpInDelId() {
        return snpInDelId;
    }

    /**
     * @param snpInDelId
     */
    public void setSnpInDelId(Integer snpInDelId) {
        this.snpInDelId = snpInDelId;
    }

    /**
     * @return provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return primaryEvidence
     */
    public Boolean getPrimaryEvidence() {
        return primaryEvidence;
    }

    /**
     * @param primaryEvidence
     */
    public void setPrimaryEvidence(Boolean primaryEvidence) {
        this.primaryEvidence = primaryEvidence;
    }

    /**
     * @return evidenceType
     */
    public String getEvidenceType() {
        return evidenceType;
    }

    /**
     * @param evidenceType
     */
    public void setEvidenceType(String evidenceType) {
        this.evidenceType = evidenceType;
    }

    /**
     * @return evidenceLevel
     */
    public String getEvidenceLevel() {
        return evidenceLevel;
    }

    /**
     * @param evidenceLevel
     */
    public void setEvidenceLevel(String evidenceLevel) {
        this.evidenceLevel = evidenceLevel;
    }

    /**
     * @return evidence
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * @param evidence
     */
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
