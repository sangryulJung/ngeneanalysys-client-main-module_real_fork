package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class ReadInfo {
    private Integer refReadNum;
    private Integer altReadNum;
    private Integer readDepth;
    private BigDecimal alleleFraction;

    /**
     * @return refReadNum
     */
    public Integer getRefReadNum() {
        return refReadNum;
    }

    /**
     * @param refReadNum
     */
    public void setRefReadNum(Integer refReadNum) {
        this.refReadNum = refReadNum;
    }

    /**
     * @return altReadNum
     */
    public Integer getAltReadNum() {
        return altReadNum;
    }

    /**
     * @param altReadNum
     */
    public void setAltReadNum(Integer altReadNum) {
        this.altReadNum = altReadNum;
    }

    /**
     * @return readDepth
     */
    public Integer getReadDepth() {
        return readDepth;
    }

    /**
     * @param readDepth
     */
    public void setReadDepth(Integer readDepth) {
        this.readDepth = readDepth;
    }

    /**
     * @return alleleFraction
     */
    public BigDecimal getAlleleFraction() {
        return alleleFraction;
    }

    /**
     * @param alleleFraction
     */
    public void setAlleleFraction(BigDecimal alleleFraction) {
        this.alleleFraction = alleleFraction;
    }
}
