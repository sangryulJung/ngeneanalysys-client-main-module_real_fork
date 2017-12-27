package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-12-26
 */
public class FusionGeneInterpretationLogView {
    private Integer id;
    private String memberLoginId;
    private Integer memberId;
    private String memberName;
    private String interpretationType;
    private String oldValue;
    private String newValue;
    private String comment;
    private DateTime createdAt;
    private Integer snpInDelId;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return memberLoginId
     */
    public String getMemberLoginId() {
        return memberLoginId;
    }

    /**
     * @return memberId
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * @return memberName
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * @return interpretationType
     */
    public String getInterpretationType() {
        return interpretationType;
    }

    /**
     * @return oldValue
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * @return newValue
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return snpInDelId
     */
    public Integer getSnpInDelId() {
        return snpInDelId;
    }
}
