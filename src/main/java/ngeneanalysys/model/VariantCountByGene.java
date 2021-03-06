package ngeneanalysys.model;

import java.util.Objects;

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
    private Integer falseCount;

    /**
     * @return geneSymbol
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @param geneSymbol
     */
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
     * @return tier1SnpCount
     */
    public Integer getTier1SnpCount() {
        return tier1SnpCount;
    }

    /**
     * @param tier1SnpCount
     */
    public void setTier1SnpCount(Integer tier1SnpCount) {
        this.tier1SnpCount = tier1SnpCount;
    }

    /**
     * @return tier1IndelCount
     */
    public Integer getTier1IndelCount() {
        return tier1IndelCount;
    }

    /**
     * @param tier1IndelCount
     */
    public void setTier1IndelCount(Integer tier1IndelCount) {
        this.tier1IndelCount = tier1IndelCount;
    }

    /**
     * @return tier2SnpCount
     */
    public Integer getTier2SnpCount() {
        return tier2SnpCount;
    }

    /**
     * @param tier2SnpCount
     */
    public void setTier2SnpCount(Integer tier2SnpCount) {
        this.tier2SnpCount = tier2SnpCount;
    }

    /**
     * @return tier2IndelCount
     */
    public Integer getTier2IndelCount() {
        return tier2IndelCount;
    }

    /**
     * @param tier2IndelCount
     */
    public void setTier2IndelCount(Integer tier2IndelCount) {
        this.tier2IndelCount = tier2IndelCount;
    }

    /**
     * @return tier3SnpCount
     */
    public Integer getTier3SnpCount() {
        return tier3SnpCount;
    }

    /**
     * @param tier3SnpCount
     */
    public void setTier3SnpCount(Integer tier3SnpCount) {
        this.tier3SnpCount = tier3SnpCount;
    }

    /**
     * @return tier3IndelCount
     */
    public Integer getTier3IndelCount() {
        return tier3IndelCount;
    }

    /**
     * @param tier3IndelCount
     */
    public void setTier3IndelCount(Integer tier3IndelCount) {
        this.tier3IndelCount = tier3IndelCount;
    }

    /**
     * @return tier4SnpCount
     */
    public Integer getTier4SnpCount() {
        return tier4SnpCount;
    }

    /**
     * @param tier4SnpCount
     */
    public void setTier4SnpCount(Integer tier4SnpCount) {
        this.tier4SnpCount = tier4SnpCount;
    }

    /**
     * @return tier4IndelCount
     */
    public Integer getTier4IndelCount() {
        return tier4IndelCount;
    }

    /**
     * @param tier4IndelCount
     */
    public void setTier4IndelCount(Integer tier4IndelCount) {
        this.tier4IndelCount = tier4IndelCount;
    }

    /**
     * @return falseCount
     */
    public Integer getFalseCount() {
        return falseCount;
    }

    /**
     * @param falseCount
     */
    public void setFalseCount(Integer falseCount) {
        this.falseCount = falseCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantCountByGene that = (VariantCountByGene) o;
        return Objects.equals(geneSymbol, that.geneSymbol) &&
                Objects.equals(tier1SnpCount, that.tier1SnpCount) &&
                Objects.equals(tier1IndelCount, that.tier1IndelCount) &&
                Objects.equals(tier2SnpCount, that.tier2SnpCount) &&
                Objects.equals(tier2IndelCount, that.tier2IndelCount) &&
                Objects.equals(tier3SnpCount, that.tier3SnpCount) &&
                Objects.equals(tier3IndelCount, that.tier3IndelCount) &&
                Objects.equals(tier4SnpCount, that.tier4SnpCount) &&
                Objects.equals(tier4IndelCount, that.tier4IndelCount) &&
                Objects.equals(falseCount, that.falseCount);
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
        result = 31 * result + (falseCount != null ? falseCount.hashCode() : 0);
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
