package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-08-01
 */
public class NGeneAnalySysVersion {
    private String system;
    private String pipelineDocker;
    private String apiServer;

    private PipelineSoftwareVersion pipelines;

    /**
     * @return system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @return pipelineDocker
     */
    public String getPipelineDocker() {
        return pipelineDocker;
    }

    /**
     * @return apiServer
     */
    public String getApiServer() {
        return apiServer;
    }

    /**
     * @return pipelines
     */
    public PipelineSoftwareVersion getPipelines() {
        return pipelines;
    }
}
