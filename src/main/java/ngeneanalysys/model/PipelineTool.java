package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-17
 */
public class PipelineTool {
    private Integer pipelineVersionId;
    private String name;
    private String version;
    private String releaseDate;
    private String license;
    private String description;
    private String source;
    private String sampleCount;

    /**
     * @return pipelineVersionId
     */
    public Integer getPipelineVersionId() {
        return pipelineVersionId;
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
     * @return license
     */
    public String getLicense() {
        return license;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public String getSource() { return source; }

    public String getSampleCount() { return sampleCount; }
}
