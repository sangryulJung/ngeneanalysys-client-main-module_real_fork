package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-19
 */
public class VariantTranscript {
    private Integer variantId;
    private String transcriptId;
    private String geneSymbol;
    private String codingDna;
    private String protein;
    private String genomicDna;
    private String isDefault;

    /**
     * @return variantId
     */
    public Integer getVariantId() {
        return variantId;
    }

    /**
     * @param variantId
     */
    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    /**
     * @return transcriptId
     */
    public String getTranscriptId() {
        return transcriptId;
    }

    /**
     * @param transcriptId
     */
    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

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
     * @return codingDna
     */
    public String getCodingDna() {
        return codingDna;
    }

    /**
     * @param codingDna
     */
    public void setCodingDna(String codingDna) {
        this.codingDna = codingDna;
    }

    /**
     * @return protein
     */
    public String getProtein() {
        return protein;
    }

    /**
     * @param protein
     */
    public void setProtein(String protein) {
        this.protein = protein;
    }

    /**
     * @return genomicDna
     */
    public String getGenomicDna() {
        return genomicDna;
    }

    /**
     * @param genomicDna
     */
    public void setGenomicDna(String genomicDna) {
        this.genomicDna = genomicDna;
    }

    /**
     * @return isDefault
     */
    public String getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault
     */
    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
