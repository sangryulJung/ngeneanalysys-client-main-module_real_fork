package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-10
 */
public class SameVariantInterpretation {
    private Integer runId;
    private String runName;
    private Integer sampleId;
    private String sampleName;
    private String diseaseName;
    private String panelName;
    private String tier;
    private String pathogenicity;
    private SnpInDelInterpretation snpInDelInterpretation;

    /**
     * @return runid
     */
    public Integer getRunId() {
        return runId;
    }

    /**
     * @return runName
     */
    public String getRunName() {
        return runName;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return sampleName
     */
    public String getSampleName() {
        return sampleName;
    }

    /**
     * @return diseaseName
     */
    public String getDiseaseName() {
        return diseaseName;
    }

    /**
     * @return panelName
     */
    public String getPanelName() {
        return panelName;
    }

    /**
     * @return tier
     */
    public String getTier() {
        return tier;
    }

    /**
     * @return pathogenicity
     */
    public String getPathogenicity() {
        return pathogenicity;
    }

    /**
     * @return snpInDelInterpretation
     */
    public SnpInDelInterpretation getSnpInDelInterpretation() {
        return snpInDelInterpretation;
    }
}
