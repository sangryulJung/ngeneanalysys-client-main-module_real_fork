package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-19
 */
public class SnpInDelTranscript {
    private Integer snpInDelId;
    private String transcriptId;
    private String geneSymbol;
    private String codingDna;
    private String protein;
    private String genomicDna;
    private Boolean isDefault;
    private String leftSequence;
    private String rightSequence;
    private String refSequence;
    private String altSequence;

    /**
     * @return isDefault
     */
    public Boolean getDefault() {
        return isDefault;
    }

    /**
     * @return leftSequence
     */
    public String getLeftSequence() {
        return leftSequence;
    }

    /**
     * @return rightSequence
     */
    public String getRightSequence() {
        return rightSequence;
    }

    /**
     * @return refSequence
     */
    public String getRefSequence() {
        return refSequence;
    }

    /**
     * @return altSequence
     */
    public String getAltSequence() {
        return altSequence;
    }

    /**
     * @return snpInDelId
     */
    public Integer getSnpInDelId() {
        return snpInDelId;
    }

    /**
     * @param snpInDelId
     */
    public void setSnpInDelId(Integer snpInDelId) {
        this.snpInDelId = snpInDelId;
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
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}