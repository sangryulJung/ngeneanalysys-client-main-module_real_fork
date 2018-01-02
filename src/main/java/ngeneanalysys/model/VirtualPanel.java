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
    private String essentialGenes;
    private String optionalGenes;
    private DateTime createdAt;
    private DateTime deletedAt;
    private Integer deleted;

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
