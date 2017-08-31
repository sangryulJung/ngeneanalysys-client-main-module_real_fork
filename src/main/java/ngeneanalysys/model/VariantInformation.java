package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class VariantInformation {

    private Integer tier;

    private String Gene;

    private String variant;

    private String therapeutic;

    public VariantInformation(Integer tier, String gene, String variant, String therapeutic) {
        this.tier = tier;
        Gene = gene;
        this.variant = variant;
        this.therapeutic = therapeutic;
    }

    /**
     * @return tier
     */
    public Integer getTier() {
        return tier;
    }

    /**
     * @param tier
     */
    public void setTier(Integer tier) {
        this.tier = tier;
    }

    /**
     * @return Gene
     */
    public String getGene() {
        return Gene;
    }

    /**
     * @param gene
     */
    public void setGene(String gene) {
        Gene = gene;
    }

    /**
     * @return variant
     */
    public String getVariant() {
        return variant;
    }

    /**
     * @param variant
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * @return therapeutic
     */
    public String getTherapeutic() {
        return therapeutic;
    }

    /**
     * @param therapeutic
     */
    public void setTherapeutic(String therapeutic) {
        this.therapeutic = therapeutic;
    }
}
