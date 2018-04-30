package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class FusionGene {
    private Integer id;
    private Integer sampleId;
    private String swTier;
    private String expertTier;
    private String includedInReport;
    private String comment;
    private String name;
    private Integer junctionReadCount;
    private Integer spanningFragCount;
    private String protFusionType;
    private String spliceType;
    private String largeAnchorSupport;
    private String annots;
    private String fusionModel;
    private String fusionCds;
    private String fusionTransl;
    private Integer interpretationEvidenceId;
    private FusionRight fusionRight;
    private FusionLeft fusionLeft;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return swTier
     */
    public String getSwTier() {
        return swTier;
    }

    /**
     * @return expertTier
     */
    public String getExpertTier() {
        return expertTier;
    }

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
    }

    /**
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return junctionReadCount
     */
    public Integer getJunctionReadCount() {
        return junctionReadCount;
    }

    /**
     * @return spanningFragCount
     */
    public Integer getSpanningFragCount() {
        return spanningFragCount;
    }

    /**
     * @return protFusionType
     */
    public String getProtFusionType() {
        return protFusionType;
    }

    /**
     * @return spliceType
     */
    public String getSpliceType() {
        return spliceType;
    }

    /**
     * @return largeAnchorSupport
     */
    public String getLargeAnchorSupport() {
        return largeAnchorSupport;
    }

    /**
     * @return annots
     */
    public String getAnnots() {
        return annots;
    }

    /**
     * @return fusionModel
     */
    public String getFusionModel() {
        return fusionModel;
    }

    /**
     * @return fusionCds
     */
    public String getFusionCds() {
        return fusionCds;
    }

    /**
     * @return fusionTransl
     */
    public String getFusionTransl() {
        return fusionTransl;
    }

    /**
     * @return interpretationEvidenceId
     */
    public Integer getInterpretationEvidenceId() {
        return interpretationEvidenceId;
    }

    /**
     * @return fusionRight
     */
    public FusionRight getFusionRight() {
        return fusionRight;
    }

    /**
     * @return fusionLeft
     */
    public FusionLeft getFusionLeft() {
        return fusionLeft;
    }
}
