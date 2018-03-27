package ngeneanalysys.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class Panel {
    private Integer id;
    private String name;
    private String code;
    private String target;
    private String analysisType;
    private String libraryType;

    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;
    private Boolean isDefault;

    private QCPassConfig qcPassConfig;

    private VariantConfig variantConfig;

    private Integer reportTemplateId;

    /**
     * @return qcPassConfig
     */
    public QCPassConfig getQcPassConfig() {
        return qcPassConfig;
    }

    /**
     * @return variantConfig
     */
    public VariantConfig getVariantConfig() {
        return variantConfig;
    }

    /**
     * @return reportTemplateId
     */
    public Integer getReportTemplateId() {
        return reportTemplateId;
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

    public Boolean getIsDefault() { return isDefault; }

    @Override
    public String toString() {
        return "Panel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", target='" + target + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", libraryType='" + libraryType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                ", isDefault=" + isDefault +
                ", qcPassConfig=" + qcPassConfig +
                ", variantConfig=" + variantConfig +
                ", reportTemplateId=" + reportTemplateId +
                '}';
    }
}
