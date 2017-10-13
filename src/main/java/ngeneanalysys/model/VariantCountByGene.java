package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class VariantCountByGene {
    private String geneSymbol;
    private Integer tier1SnpCount;
    private Integer tier1IndelCount;
    private Integer tier2SnpCount;
    private Integer tier2IndelCount;
    private Integer tier3SnpCount;
    private Integer tier3IndelCount;
    private Integer tier4SnpCount;
    private Integer tier4IndelCount;
    private Integer tierNSnpCount;
    private Integer tierNIndelCount;

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public Integer getTier1SnpCount() {
        return tier1SnpCount;
    }

    public void setTier1SnpCount(Integer tier1SnpCount) {
        this.tier1SnpCount = tier1SnpCount;
    }

    public Integer getTier1IndelCount() {
        return tier1IndelCount;
    }

    public void setTier1IndelCount(Integer tier1IndelCount) {
        this.tier1IndelCount = tier1IndelCount;
    }

    public Integer getTier2SnpCount() {
        return tier2SnpCount;
    }

    public void setTier2SnpCount(Integer tier2SnpCount) {
        this.tier2SnpCount = tier2SnpCount;
    }

    public Integer getTier2IndelCount() {
        return tier2IndelCount;
    }

    public void setTier2IndelCount(Integer tier2IndelCount) {
        this.tier2IndelCount = tier2IndelCount;
    }

    public Integer getTier3SnpCount() {
        return tier3SnpCount;
    }

    public void setTier3SnpCount(Integer tier3SnpCount) {
        this.tier3SnpCount = tier3SnpCount;
    }

    public Integer getTier3IndelCount() {
        return tier3IndelCount;
    }

    public void setTier3IndelCount(Integer tier3IndelCount) {
        this.tier3IndelCount = tier3IndelCount;
    }

    public Integer getTier4SnpCount() {
        return tier4SnpCount;
    }

    public void setTier4SnpCount(Integer tier4SnpCount) {
        this.tier4SnpCount = tier4SnpCount;
    }

    public Integer getTier4IndelCount() {
        return tier4IndelCount;
    }

    public void setTier4IndelCount(Integer tier4IndelCount) {
        this.tier4IndelCount = tier4IndelCount;
    }

    public Integer getTierNSnpCount() {
        return tierNSnpCount;
    }

    public void setTierNSnpCount(Integer tierNSnpCount) {
        this.tierNSnpCount = tierNSnpCount;
    }

    public Integer getTierNIndelCount() {
        return tierNIndelCount;
    }

    public void setTierNIndelCount(Integer tierNIndelCount) {
        this.tierNIndelCount = tierNIndelCount;
    }
}
