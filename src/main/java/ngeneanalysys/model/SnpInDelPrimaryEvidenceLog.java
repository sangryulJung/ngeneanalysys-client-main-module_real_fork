package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2018-04-11
 */
public class SnpInDelPrimaryEvidenceLog {
    private Integer id;
    private Integer snpInDelId;
    private String evidenceType;
    private String evidenceLevel;
    private String evidence;
    private DateTime createdAt;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return snpInDelId
     */
    public Integer getSnpInDelId() {
        return snpInDelId;
    }

    /**
     * @return evidenceType
     */
    public String getEvidenceType() {
        return evidenceType;
    }

    /**
     * @return evidenceLevel
     */
    public String getEvidenceLevel() {
        return evidenceLevel;
    }

    /**
     * @return evidence
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }
}
