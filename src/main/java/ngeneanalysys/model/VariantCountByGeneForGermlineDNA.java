package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-19
 */
public class VariantCountByGeneForGermlineDNA implements Comparable<VariantCountByGeneForGermlineDNA> {
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
    private Integer falseCount;

    /**
     * @return falsePositiveCount
     */
    public Integer getFalseCount() {
        return falseCount;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariantCountByGeneForGermlineDNA that = (VariantCountByGeneForGermlineDNA) o;

        if (geneSymbol != null ? !geneSymbol.equals(that.geneSymbol) : that.geneSymbol != null) return false;
        if (pathogenicSnpCount != null ? !pathogenicSnpCount.equals(that.pathogenicSnpCount) : that.pathogenicSnpCount != null)
            return false;
        if (pathogenicInDelCount != null ? !pathogenicInDelCount.equals(that.pathogenicInDelCount) : that.pathogenicInDelCount != null)
            return false;
        if (likelyPathogenicSnpCount != null ? !likelyPathogenicSnpCount.equals(that.likelyPathogenicSnpCount) : that.likelyPathogenicSnpCount != null)
            return false;
        if (likelyPathogenicInDelCount != null ? !likelyPathogenicInDelCount.equals(that.likelyPathogenicInDelCount) : that.likelyPathogenicInDelCount != null)
            return false;
        if (uncertainSignificanceSnpCount != null ? !uncertainSignificanceSnpCount.equals(that.uncertainSignificanceSnpCount) : that.uncertainSignificanceSnpCount != null)
            return false;
        if (uncertainSignificanceInDelCount != null ? !uncertainSignificanceInDelCount.equals(that.uncertainSignificanceInDelCount) : that.uncertainSignificanceInDelCount != null)
            return false;
        if (likelyBenignSnpCount != null ? !likelyBenignSnpCount.equals(that.likelyBenignSnpCount) : that.likelyBenignSnpCount != null)
            return false;
        if (likelyBenignInDelCount != null ? !likelyBenignInDelCount.equals(that.likelyBenignInDelCount) : that.likelyBenignInDelCount != null)
            return false;
        if (benignSnpCount != null ? !benignSnpCount.equals(that.benignSnpCount) : that.benignSnpCount != null)
            return false;
        return benignInDelCount != null ? benignInDelCount.equals(that.benignInDelCount) : that.benignInDelCount == null;
    }

    @Override
    public int hashCode() {
        int result = geneSymbol != null ? geneSymbol.hashCode() : 0;
        result = 31 * result + (pathogenicSnpCount != null ? pathogenicSnpCount.hashCode() : 0);
        result = 31 * result + (pathogenicInDelCount != null ? pathogenicInDelCount.hashCode() : 0);
        result = 31 * result + (likelyPathogenicSnpCount != null ? likelyPathogenicSnpCount.hashCode() : 0);
        result = 31 * result + (likelyPathogenicInDelCount != null ? likelyPathogenicInDelCount.hashCode() : 0);
        result = 31 * result + (uncertainSignificanceSnpCount != null ? uncertainSignificanceSnpCount.hashCode() : 0);
        result = 31 * result + (uncertainSignificanceInDelCount != null ? uncertainSignificanceInDelCount.hashCode() : 0);
        result = 31 * result + (likelyBenignSnpCount != null ? likelyBenignSnpCount.hashCode() : 0);
        result = 31 * result + (likelyBenignInDelCount != null ? likelyBenignInDelCount.hashCode() : 0);
        result = 31 * result + (benignSnpCount != null ? benignSnpCount.hashCode() : 0);
        result = 31 * result + (benignInDelCount != null ? benignInDelCount.hashCode() : 0);
        result = 31 * result + (falseCount != null ? falseCount.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(VariantCountByGeneForGermlineDNA o) {
        if(this.pathogenicSnpCount + this.pathogenicInDelCount > o.pathogenicSnpCount + o.pathogenicInDelCount) {
            return 1;
        } else if(this.pathogenicSnpCount + this.pathogenicInDelCount < o.pathogenicSnpCount + o.pathogenicInDelCount) {
            return -1;
        } else {
            if (this.likelyPathogenicSnpCount + this.likelyPathogenicInDelCount > o.likelyPathogenicSnpCount + o.likelyPathogenicInDelCount) {
                return 1;
            } else if (this.likelyPathogenicSnpCount + this.likelyPathogenicInDelCount < o.likelyPathogenicSnpCount + o.likelyPathogenicInDelCount) {
                return -1;
            } else {
                if (this.uncertainSignificanceSnpCount + this.uncertainSignificanceInDelCount > o.uncertainSignificanceSnpCount + o.uncertainSignificanceInDelCount) {
                    return 1;
                } else if (this.uncertainSignificanceSnpCount + this.uncertainSignificanceInDelCount < o.uncertainSignificanceSnpCount + o.uncertainSignificanceInDelCount) {
                    return -1;
                } else {
                    if (this.likelyBenignSnpCount + this.likelyBenignInDelCount > o.likelyBenignSnpCount + o.likelyBenignInDelCount) {
                        return 1;
                    } else if (this.likelyBenignSnpCount + this.likelyBenignInDelCount < o.likelyBenignSnpCount + o.likelyBenignInDelCount) {
                        return -1;
                    } else {
                        if (this.benignSnpCount + this.benignInDelCount > o.benignSnpCount + o.benignInDelCount) {
                            return 1;
                        } else if (this.benignSnpCount + this.benignInDelCount < o.benignSnpCount + o.benignInDelCount) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    }
}
