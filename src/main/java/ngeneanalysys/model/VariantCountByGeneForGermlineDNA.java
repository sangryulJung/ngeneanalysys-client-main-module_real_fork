package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-19
 */
public class VariantCountByGeneForGermlineDNA {
    private String geneSymbol;
    private Integer pathogenicSnpCount;
    private Integer likelyPathogenicSnpCount;
    private Integer uncertainSignificanceSnpCount;
    private Integer likelyBenignSnpCount;
    private Integer benignSnpCount;
    private Integer pathogenicInDelCount;
    private Integer likelyPathogenicInDelCount;
    private Integer uncertainSignificanceInDelCount;
    private Integer likelyBenignInDelCount;
    private Integer benignInDelCount;

    /**
     * @return geneSymbol
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @return pathogenicSnpCount
     */
    public Integer getPathogenicSnpCount() {
        return pathogenicSnpCount;
    }

    /**
     * @return likelyPathogenicSnpCount
     */
    public Integer getLikelyPathogenicSnpCount() {
        return likelyPathogenicSnpCount;
    }

    /**
     * @return uncertainSignificanceSnpCount
     */
    public Integer getUncertainSignificanceSnpCount() {
        return uncertainSignificanceSnpCount;
    }

    /**
     * @return likelyBenignSnpCount
     */
    public Integer getLikelyBenignSnpCount() {
        return likelyBenignSnpCount;
    }

    /**
     * @return benignSnpCount
     */
    public Integer getBenignSnpCount() {
        return benignSnpCount;
    }

    /**
     * @return pathogenicInDelCount
     */
    public Integer getPathogenicInDelCount() {
        return pathogenicInDelCount;
    }

    /**
     * @return likelyPathogenicInDelCount
     */
    public Integer getLikelyPathogenicInDelCount() {
        return likelyPathogenicInDelCount;
    }

    /**
     * @return uncertainSignificanceInDelCount
     */
    public Integer getUncertainSignificanceInDelCount() {
        return uncertainSignificanceInDelCount;
    }

    /**
     * @return likelyBenignInDelCount
     */
    public Integer getLikelyBenignInDelCount() {
        return likelyBenignInDelCount;
    }

    /**
     * @return benignInDelCount
     */
    public Integer getBenignInDelCount() {
        return benignInDelCount;
    }
}
