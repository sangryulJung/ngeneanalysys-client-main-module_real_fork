package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2018-01-02
 */
public class VirtualPanel {
    private Integer id;
    private Integer panelId;
    private String name;
    private String description;
    private String essentialGenes;
    private String optionalGenes;
    private DateTime createdAt;
    private DateTime deletedAt;
    private DateTime updatedAt;
    private Integer deleted;

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return essentialGenes
     */
    public String getEssentialGenes() {
        return essentialGenes;
    }

    /**
     * @return optionalGenes
     */
    public String getOptionalGenes() {
        return optionalGenes;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
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
