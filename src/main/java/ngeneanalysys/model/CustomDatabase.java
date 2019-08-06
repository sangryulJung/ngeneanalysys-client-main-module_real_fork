package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2019-06-21
 */
public class CustomDatabase {
    private Integer id;
    private Integer panelId;
    private String title;
    private String description;
    private String contents;
    private DateTime createdAt;
    private DateTime updatedAt;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return panelId
     */
    public Integer getPanelId() {
        return panelId;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }
}
