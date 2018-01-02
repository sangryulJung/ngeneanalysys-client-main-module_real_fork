package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-01-02
 */
public class NgsGenomicCoordinateClinicalVariant {
    private String ngsChr;
    private String ngsGene;
    private Integer ngsPosition;
    private String ngsRef;
    private String ngsAlt;

    /**
     * @param ngsChr
     */
    public void setNgsChr(String ngsChr) {
        this.ngsChr = ngsChr;
    }

    /**
     * @param ngsGene
     */
    public void setNgsGene(String ngsGene) {
        this.ngsGene = ngsGene;
    }

    /**
     * @param ngsPosition
     */
    public void setNgsPosition(Integer ngsPosition) {
        this.ngsPosition = ngsPosition;
    }

    /**
     * @param ngsRef
     */
    public void setNgsRef(String ngsRef) {
        this.ngsRef = ngsRef;
    }

    /**
     * @param ngsAlt
     */
    public void setNgsAlt(String ngsAlt) {
        this.ngsAlt = ngsAlt;
    }

    /**
     * @return ngsChr
     */
    public String getNgsChr() {
        return ngsChr;
    }

    /**
     * @return ngsGene
     */
    public String getNgsGene() {
        return ngsGene;
    }

    /**
     * @return ngsPosition
     */
    public Integer getNgsPosition() {
        return ngsPosition;
    }

    /**
     * @return ngsRef
     */
    public String getNgsRef() {
        return ngsRef;
    }

    /**
     * @return ngsAlt
     */
    public String getNgsAlt() {
        return ngsAlt;
    }
}
