package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class PertinentNegatives {
    private String gene;

    private String variant;

    private String variantType;

    private String cause;

    public PertinentNegatives(String gene, String variant, String variantType, String cause) {
        this.gene = gene;
        this.variant = variant;
        this.variantType = variantType;
        this.cause = cause;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @param gene
     */
    public void setGene(String gene) {
        this.gene = gene;
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
     * @return variantType
     */
    public String getVariantType() {
        return variantType;
    }

    /**
     * @param variantType
     */
    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    /**
     * @return cause
     */
    public String getCause() {
        return cause;
    }

    /**
     * @param cause
     */
    public void setCause(String cause) {
        this.cause = cause;
    }
}
