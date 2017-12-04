package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class FusionGene {
    private Integer id;
    private Integer sampleId;
    private String name;
    private Integer junctionReadCount;
    private Integer spanningFragCount;
    private String leftBreakPoint;
    private String leftTxExon;
    private String rightTxExon;
    private String protFusionType;
    private String spliceType;
    private String largeAnchorSupport;
    private String leftBreakDinuc;
    private String rightBreakDinuc;
    private String annots;
    private String cdsLeftId;
    private String cdsLeftRange;
    private String cdsRightId;
    private String cdsRightRange;
    private String fusionModel;
    private String fusionCds;
    private String fusionTransl;
    private String pfamLeft;
    private String pfamRight;

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
     * @return leftBreakPoint
     */
    public String getLeftBreakPoint() {
        return leftBreakPoint;
    }

    /**
     * @return leftTxExon
     */
    public String getLeftTxExon() {
        return leftTxExon;
    }

    /**
     * @return rightTxExon
     */
    public String getRightTxExon() {
        return rightTxExon;
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
     * @return leftBreakDinuc
     */
    public String getLeftBreakDinuc() {
        return leftBreakDinuc;
    }

    /**
     * @return rightBreakDinuc
     */
    public String getRightBreakDinuc() {
        return rightBreakDinuc;
    }

    /**
     * @return annots
     */
    public String getAnnots() {
        return annots;
    }

    /**
     * @return cdsLeftId
     */
    public String getCdsLeftId() {
        return cdsLeftId;
    }

    /**
     * @return cdsLeftRange
     */
    public String getCdsLeftRange() {
        return cdsLeftRange;
    }

    /**
     * @return cdsRightId
     */
    public String getCdsRightId() {
        return cdsRightId;
    }

    /**
     * @return cdsRightRange
     */
    public String getCdsRightRange() {
        return cdsRightRange;
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
     * @return pfamLeft
     */
    public String getPfamLeft() {
        return pfamLeft;
    }

    /**
     * @return pfamRight
     */
    public String getPfamRight() {
        return pfamRight;
    }
}
