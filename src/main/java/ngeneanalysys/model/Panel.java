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
    private String sampleSource;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;
    private Boolean isDefault;
    private Integer warningReadDepth;
    private BigDecimal warningMAF;
    private String essentialGenes;
    private String geneRepresentativeTranscript;

    private ReportCutOffParams reportCutOffParams;

    /**
     * @return essentialGenes
     */
    public String getEssentialGenes() {
        return essentialGenes;
    }

    /**
     * @return geneRepresentativeTranscript
     */
    public String getGeneRepresentativeTranscript() {
        return geneRepresentativeTranscript;
    }

    private Integer reportTemplateId;

    public ReportCutOffParams getReportCutOffParams() {
        return reportCutOffParams;
    }

    public void setReportCutOffParams(ReportCutOffParams reportCutOffParams) {
        this.reportCutOffParams = reportCutOffParams;
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

    public Boolean getIsDefault() { return isDefault; }

    public Integer getWarningReadDepth() { return warningReadDepth; }

    public BigDecimal getWarningMAF() { return warningMAF; }

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
                ", isDefault=" + isDefault +
                ", warningReadDepth=" + warningReadDepth +
                ", warningMAF=" + warningMAF +
                ", reportCutOffParams=" + reportCutOffParams +
                ", reportTemplateId=" + reportTemplateId +
                '}';
    }
}
