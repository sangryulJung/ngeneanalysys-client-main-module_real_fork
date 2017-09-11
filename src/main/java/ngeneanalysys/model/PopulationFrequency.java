package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class PopulationFrequency {
    private String dbsnpRsId;
    private BigDecimal g1000;
    private BigDecimal exac;
    private BigDecimal esp6500;
    private BigDecimal korean;
    private BigDecimal kohbraFreq;

    /**
     * @return dbsnpRsId
     */
    public String getDbsnpRsId() {
        return dbsnpRsId;
    }

    /**
     * @param dbsnpRsId
     */
    public void setDbsnpRsId(String dbsnpRsId) {
        this.dbsnpRsId = dbsnpRsId;
    }

    /**
     * @return g1000
     */
    public BigDecimal getG1000() {
        return g1000;
    }

    /**
     * @param g1000
     */
    public void setG1000(BigDecimal g1000) {
        this.g1000 = g1000;
    }

    /**
     * @return exac
     */
    public BigDecimal getExac() {
        return exac;
    }

    /**
     * @param exac
     */
    public void setExac(BigDecimal exac) {
        this.exac = exac;
    }

    /**
     * @return esp6500
     */
    public BigDecimal getEsp6500() {
        return esp6500;
    }

    /**
     * @param esp6500
     */
    public void setEsp6500(BigDecimal esp6500) {
        this.esp6500 = esp6500;
    }

    /**
     * @return korean
     */
    public BigDecimal getKorean() {
        return korean;
    }

    /**
     * @param korean
     */
    public void setKorean(BigDecimal korean) {
        this.korean = korean;
    }

    /**
     * @return kohbraFreq
     */
    public BigDecimal getKohbraFreq() {
        return kohbraFreq;
    }

    /**
     * @param kohbraFreq
     */
    public void setKohbraFreq(BigDecimal kohbraFreq) {
        this.kohbraFreq = kohbraFreq;
    }
}
