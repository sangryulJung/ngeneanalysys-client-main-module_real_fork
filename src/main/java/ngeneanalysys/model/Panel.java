package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

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
