package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class VariantCountByGene implements Comparable<VariantCountByGene> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariantCountByGene that = (VariantCountByGene) o;

        if (geneSymbol != null ? !geneSymbol.equals(that.geneSymbol) : that.geneSymbol != null) return false;
        if (tier1SnpCount != null ? !tier1SnpCount.equals(that.tier1SnpCount) : that.tier1SnpCount != null)
            return false;
        if (tier1IndelCount != null ? !tier1IndelCount.equals(that.tier1IndelCount) : that.tier1IndelCount != null)
            return false;
        if (tier2SnpCount != null ? !tier2SnpCount.equals(that.tier2SnpCount) : that.tier2SnpCount != null)
            return false;
        if (tier2IndelCount != null ? !tier2IndelCount.equals(that.tier2IndelCount) : that.tier2IndelCount != null)
            return false;
        if (tier3SnpCount != null ? !tier3SnpCount.equals(that.tier3SnpCount) : that.tier3SnpCount != null)
            return false;
        if (tier3IndelCount != null ? !tier3IndelCount.equals(that.tier3IndelCount) : that.tier3IndelCount != null)
            return false;
        if (tier4SnpCount != null ? !tier4SnpCount.equals(that.tier4SnpCount) : that.tier4SnpCount != null)
            return false;
        if (tier4IndelCount != null ? !tier4IndelCount.equals(that.tier4IndelCount) : that.tier4IndelCount != null)
            return false;
        if (tierNSnpCount != null ? !tierNSnpCount.equals(that.tierNSnpCount) : that.tierNSnpCount != null)
            return false;
        return tierNIndelCount != null ? tierNIndelCount.equals(that.tierNIndelCount) : that.tierNIndelCount == null;
    }

    @Override
    public int hashCode() {
        int result = geneSymbol != null ? geneSymbol.hashCode() : 0;
        result = 31 * result + (tier1SnpCount != null ? tier1SnpCount.hashCode() : 0);
        result = 31 * result + (tier1IndelCount != null ? tier1IndelCount.hashCode() : 0);
        result = 31 * result + (tier2SnpCount != null ? tier2SnpCount.hashCode() : 0);
        result = 31 * result + (tier2IndelCount != null ? tier2IndelCount.hashCode() : 0);
        result = 31 * result + (tier3SnpCount != null ? tier3SnpCount.hashCode() : 0);
        result = 31 * result + (tier3IndelCount != null ? tier3IndelCount.hashCode() : 0);
        result = 31 * result + (tier4SnpCount != null ? tier4SnpCount.hashCode() : 0);
        result = 31 * result + (tier4IndelCount != null ? tier4IndelCount.hashCode() : 0);
        result = 31 * result + (tierNSnpCount != null ? tierNSnpCount.hashCode() : 0);
        result = 31 * result + (tierNIndelCount != null ? tierNIndelCount.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(VariantCountByGene o) {
        if(this.tier1SnpCount + this.tier1IndelCount > o.tier1SnpCount + o.tier1IndelCount) {
            return 1;
        } else if(this.tier1SnpCount + this.tier1IndelCount < o.tier1SnpCount + o.tier1IndelCount) {
            return -1;
        } else {
            if (this.tier2SnpCount + this.tier2IndelCount > o.tier2SnpCount + o.tier2IndelCount) {
                return 1;
            } else if (this.tier2SnpCount + this.tier2IndelCount < o.tier2SnpCount + o.tier2IndelCount) {
                return -1;
            } else {
                if (this.tier3SnpCount + this.tier3IndelCount > o.tier3SnpCount + o.tier3IndelCount) {
                    return 1;
                } else if (this.tier3SnpCount + this.tier3IndelCount < o.tier3SnpCount + o.tier3IndelCount) {
                    return -1;
                } else {
                    if (this.tier4SnpCount + this.tier4IndelCount > o.tier4SnpCount + o.tier4IndelCount) {
                        return 1;
                    } else if (this.tier4SnpCount + this.tier4IndelCount < o.tier4SnpCount + o.tier4IndelCount) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}
