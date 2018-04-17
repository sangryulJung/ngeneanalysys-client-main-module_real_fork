package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-17
 */
public class PipelineAnnotationDatabase {
    private Integer pipelineVersionId;
    private String category;
    private String name;
    private String version;
    private String releaseDate;
    private String description;

    /**
     * @return pipelineVersionId
     */
    public Integer getPipelineVersionId() {
        return pipelineVersionId;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
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
     * @return description
     */
    public String getDescription() {
        return description;
    }
}
