package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2018-04-17
 */
public class PipelineVersion {
    private Integer id;
    private Integer panelId;
    private String version;
    private String releaseDate;
    private String releaseNote;
    private DateTime createdAt;

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
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @return releaseNote
     */
    public String getReleaseNote() {
        return releaseNote;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }
}
