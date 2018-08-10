package ngeneanalysys.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class Panel {
    private Integer id;
    private String name;
    private String code;
    private String target;
    private String analysisType;
    private String libraryType;
    private String defaultSampleSource;
    private Integer defaultDiseaseId;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;
    private Boolean isDefault;
    private Integer warningReadDepth;
    private BigDecimal warningMAF;
    private VariantFilter variantFilter;
    private String canonicalTranscripts;

    private QCPassConfig qcPassConfig;

    private Integer reportTemplateId;

    /**
     * @return canonicalTranscripts
     */
    public String getCanonicalTranscripts() {
        return canonicalTranscripts;
    }

    /**
     * @return variantFilter
     */
    public VariantFilter getVariantFilter() {
        return variantFilter;
    }

    /**
     * @return isDefault
     */
    public Boolean getDefault() {
        return isDefault;
    }

    /**
     * @return warningReadDepth
     */
    public Integer getWarningReadDepth() {
        return warningReadDepth;
    }

    /**
     * @return warningMAF
     */
    public BigDecimal getWarningMAF() {
        return warningMAF;
    }

    /**
     * @param defaultDiseaseId
     */
    public void setDefaultDiseaseId(Integer defaultDiseaseId) {
        this.defaultDiseaseId = defaultDiseaseId;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return qcPassConfig
     */
    public QCPassConfig getQcPassConfig() {
        return qcPassConfig;
    }

    public String getDefaultSampleSource() {
        return defaultSampleSource;
    }

    public Integer getDefaultDiseaseId() {
        return defaultDiseaseId;
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
                ", defaultSampleSource='" + defaultSampleSource + '\'' +
                ", defaultDiseaseId=" + defaultDiseaseId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                ", isDefault=" + isDefault +
                ", warningReadDepth=" + warningReadDepth +
                ", warningMAF=" + warningMAF +
                ", variantFilter=" + variantFilter +
                ", qcPassConfig=" + qcPassConfig +
                ", reportTemplateId=" + reportTemplateId +
                '}';
    }
}
