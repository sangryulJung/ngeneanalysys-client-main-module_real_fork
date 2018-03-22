package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2018-03-22
 */
public class Notice {
    private Integer id;
    private Integer memberId;
    private String title;
    private String contents;
    private DateTime createAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return memberId
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * @return createAt
     */
    public DateTime getCreateAt() {
        return createAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @return deletedAt
     */
    public DateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * @return deleted
     */
    public Integer getDeleted() {
        return deleted;
    }
}
