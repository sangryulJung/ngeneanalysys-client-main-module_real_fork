package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-01-04
 */
public class GenomicCoordinateForCV {
    private String chr;
    private String gene;
    private Integer position;
    private String ngsRef;
    private String ngsAlt;
    private String dbRef;
    private String dbAlt;

    /**
     * @return chr
     */
    public String getChr() {
        return chr;
    }

    /**
     * @param chr
     */
    public void setChr(String chr) {
        this.chr = chr;
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
     * @return position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * @return ngsRef
     */
    public String getNgsRef() {
        return ngsRef;
    }

    /**
     * @param ngsRef
     */
    public void setNgsRef(String ngsRef) {
        this.ngsRef = ngsRef;
    }

    /**
     * @return ngsAlt
     */
    public String getNgsAlt() {
        return ngsAlt;
    }

    /**
     * @param ngsAlt
     */
    public void setNgsAlt(String ngsAlt) {
        this.ngsAlt = ngsAlt;
    }

    /**
     * @return dbRef
     */
    public String getDbRef() {
        return dbRef;
    }

    /**
     * @param dbRef
     */
    public void setDbRef(String dbRef) {
        this.dbRef = dbRef;
    }

    /**
     * @return dbAlt
     */
    public String getDbAlt() {
        return dbAlt;
    }

    /**
     * @param dbAlt
     */
    public void setDbAlt(String dbAlt) {
        this.dbAlt = dbAlt;
    }
}
