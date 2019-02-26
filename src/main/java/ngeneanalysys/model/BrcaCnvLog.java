package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2019-02-25
 */
public class BrcaCnvLog {
    private Integer id;
    private Integer brcaCnvExonId;
    private Integer memberId;
    private String oldValue;
    private String newValue;
    private String comment;
    private DateTime createdAt;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return brcaCnvExonId
     */
    public Integer getBrcaCnvExonId() {
        return brcaCnvExonId;
    }

    /**
     * @return memberId
     */
    public Integer getMemberId() {
        return memberId;
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

    @Override
    public String toString() {
        return "BrcaCnvLog{" +
                "id=" + id +
                ", brcaCnvExonId=" + brcaCnvExonId +
                ", memberId=" + memberId +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
