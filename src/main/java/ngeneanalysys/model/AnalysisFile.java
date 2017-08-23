package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-08-22
 */
public class AnalysisFile {

    private Integer id;

    private Integer sampleId;

    private String name;

    private Long size;

    private String fileType;

    private String status;

    private DateTime createdAt;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @param sampleId
     */
    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
