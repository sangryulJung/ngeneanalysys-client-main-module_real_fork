package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-09-20
 */
public class SnpInDelInterpretationLogs {
    private Integer id;
    private Integer variantId;
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
     * @return snpInDelId
     */
    public Integer getSnpInDelId() {
        return snpInDelId;
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
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return variantId
     */
    public Integer getVariantId() {
        return variantId;
    }

    /**
     * @return memberLoginId
     */
    public String getMemberLoginId() {
        return memberLoginId;
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
}
