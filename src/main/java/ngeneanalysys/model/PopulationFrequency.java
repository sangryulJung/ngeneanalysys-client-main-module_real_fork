package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class PopulationFrequency {
    private String dbsnpRsId;
    private G1000 g1000;
    private BigDecimal exac;
    private ESP6500 esp6500;
    private GenomeAD genomAD;
    private BigDecimal koreanExomInformationDatabase;
    private BigDecimal kohbraFreq;
    private BigDecimal koreanReferenceGenomeDatabase;

    /**
     * @return dbsnpRsId
     */
    public String getDbsnpRsId() {
        return dbsnpRsId;
    }

    /**
     * @return g1000
     */
    public G1000 getG1000() {
        return g1000;
    }

    /**
     * @return exac
     */
    public BigDecimal getExac() {
        return exac;
    }

    /**
     * @return esp6500
     */
    public ESP6500 getEsp6500() {
        return esp6500;
    }

    /**
     * @return genomAD
     */
    public GenomeAD getGenomAD() {
        return genomAD;
    }

    /**
     * @return koreanExomInformationDatabase
     */
    public BigDecimal getKoreanExomInformationDatabase() {
        return koreanExomInformationDatabase;
    }

    /**
     * @return kohbraFreq
     */
    public BigDecimal getKohbraFreq() {
        return kohbraFreq;
    }

    /**
     * @return koreanReferenceGenomeDatabase
     */
    public BigDecimal getKoreanReferenceGenomeDatabase() {
        return koreanReferenceGenomeDatabase;
    }
}
