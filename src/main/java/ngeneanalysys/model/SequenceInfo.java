package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class SequenceInfo {
    private String chromosome;
    private String gene;
    private String refSequence;
    private String altSequence;
    private String leftSequence;
    private String rightSequence;
    private Integer genomicCoordinate;
    private String exonNum;
    private String exonNumBic;
    private String strand;
    private String refGenomeVer;

    /**
     * @return chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
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
     * @return refSequence
     */
    public String getRefSequence() {
        return refSequence;
    }

    /**
     * @param refSequence
     */
    public void setRefSequence(String refSequence) {
        this.refSequence = refSequence;
    }

    /**
     * @return altSequence
     */
    public String getAltSequence() {
        return altSequence;
    }

    /**
     * @param altSequence
     */
    public void setAltSequence(String altSequence) {
        this.altSequence = altSequence;
    }

    /**
     * @return leftSequence
     */
    public String getLeftSequence() {
        return leftSequence;
    }

    /**
     * @param leftSequence
     */
    public void setLeftSequence(String leftSequence) {
        this.leftSequence = leftSequence;
    }

    /**
     * @return rightSequence
     */
    public String getRightSequence() {
        return rightSequence;
    }

    /**
     * @param rightSequence
     */
    public void setRightSequence(String rightSequence) {
        this.rightSequence = rightSequence;
    }

    /**
     * @return genomicCoordinate
     */
    public Integer getGenomicCoordinate() {
        return genomicCoordinate;
    }

    /**
     * @param genomicCoordinate
     */
    public void setGenomicCoordinate(Integer genomicCoordinate) {
        this.genomicCoordinate = genomicCoordinate;
    }

    /**
     * @return exonNum
     */
    public String getExonNum() {
        return exonNum;
    }

    /**
     * @param exonNum
     */
    public void setExonNum(String exonNum) {
        this.exonNum = exonNum;
    }

    /**
     * @return exonNumBic
     */
    public String getExonNumBic() {
        return exonNumBic;
    }

    /**
     * @param exonNumBic
     */
    public void setExonNumBic(String exonNumBic) {
        this.exonNumBic = exonNumBic;
    }

    /**
     * @return strand
     */
    public String getStrand() {
        return strand;
    }

    /**
     * @param strand
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }

    /**
     * @return refGenomeVer
     */
    public String getRefGenomeVer() {
        return refGenomeVer;
    }

    /**
     * @param refGenomeVer
     */
    public void setRefGenomeVer(String refGenomeVer) {
        this.refGenomeVer = refGenomeVer;
    }
}
