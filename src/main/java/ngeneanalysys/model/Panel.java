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
    private String sampleSource;
    private DateTime createdAt;
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

    public String getSampleSource() {
        return sampleSource;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Panel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", target='" + target + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", sampleSource='" + sampleSource + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
