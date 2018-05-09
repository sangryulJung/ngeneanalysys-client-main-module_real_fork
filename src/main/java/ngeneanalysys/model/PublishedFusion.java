package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-30
 */
public class PublishedFusion {
    private Integer sampleId;
    private String fusionGene;
    private String mitelmanIds;
    private String observed;
    private String breakpoint1;
    private String breakpoint2;
    private String fusionSupportingReads;
    private String gene1ReferenceReads;
    private String gene2ReferenceReads;
    private String score;
    private String filter;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return fusionGene
     */
    public String getFusionGene() {
        return fusionGene;
    }

    /**
     * @return mitelmanIds
     */
    public String getMitelmanIds() {
        return mitelmanIds;
    }

    /**
     * @return observed
     */
    public String getObserved() {
        return observed;
    }

    /**
     * @return breakpoint1
     */
    public String getBreakpoint1() {
        return breakpoint1;
    }

    /**
     * @return breakpoint2
     */
    public String getBreakpoint2() {
        return breakpoint2;
    }

    /**
     * @return fusionSupportingReads
     */
    public String getFusionSupportingReads() {
        return fusionSupportingReads;
    }

    /**
     * @return gene1ReferenceReads
     */
    public String getGene1ReferenceReads() {
        return gene1ReferenceReads;
    }

    /**
     * @return gene2ReferenceReads
     */
    public String getGene2ReferenceReads() {
        return gene2ReferenceReads;
    }

    /**
     * @return score
     */
    public String getScore() {
        return score;
    }

    /**
     * @return filter
     */
    public String getFilter() {
        return filter;
    }
}
