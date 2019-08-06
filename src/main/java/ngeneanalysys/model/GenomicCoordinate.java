package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class GenomicCoordinate {
    private String chromosome;
    private String gene;
    private Integer startPosition;
    private String exonNum;
    private String exonNumBic;
    private String strand;
    private String refGenomeVer;
    private String inheritance;

    /**
     * @return inheritance
     */
    public String getInheritance() {
        return inheritance;
    }

    /**
     * @return startPosition
     */
    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition
     */
    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

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
