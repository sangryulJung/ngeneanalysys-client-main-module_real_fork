package ngeneanalysys.model;

import org.joda.time.DateTime;

import java.util.List;

public class Panel {
    private Integer id;
    private String name;
    private String code;
    private String target;
    private String analysisType;
    private String libraryType;
    private String sampleSource;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;

    private Integer reportTemplateId;

    private List<Integer> memberGroupIds;

    private List<Integer> diseaseIds;

    /**
     * @return reportTemplateId
     */
    public Integer getReportTemplateId() {
        return reportTemplateId;
    }

    /**
     * @return memberGroupIds
     */
    public List<Integer> getMemberGroupIds() {
        return memberGroupIds;
    }

    /**
     * @return diseaseIds
     */
    public List<Integer> getDiseaseIds() {
        return diseaseIds;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getTarget() {
        return target;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getLibraryType() { return libraryType; }

    public String getSampleSource() {
        return sampleSource;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
    public DateTime getUpdatedAt() {
        return updatedAt;
    }
    public DateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public String toString() {
        return "Panel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", target='" + target + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", libraryType='" + libraryType + '\'' +
                ", sampleSource='" + sampleSource + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                '}';
    }
}
