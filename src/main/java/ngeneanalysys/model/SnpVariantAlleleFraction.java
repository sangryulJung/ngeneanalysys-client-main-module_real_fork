package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-11-15
 */
public class SnpVariantAlleleFraction {
    private Integer sampleId;
    private Integer number;
    private String gene;
    private String dbSnpId;
    private BigDecimal minReferenceHeteroRange;
    private BigDecimal maxReferenceHeteroRange;
    private BigDecimal vaf;
    private BigDecimal sampleRatio;
    private BigDecimal controlRatio;
    private String zygosity;
    private String prediction;
    private BigDecimal g1000EastAsian;
    private BigDecimal gnomADeastAsian;
    private BigDecimal koreanExomInformationDatabase;
    private BigDecimal koreanReferenceGenomeDatabase;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return dbSnpId
     */
    public String getDbSnpId() {
        return dbSnpId;
    }

    /**
     * @return minReferenceHeteroRange
     */
    public BigDecimal getMinReferenceHeteroRange() {
        return minReferenceHeteroRange;
    }

    /**
     * @return maxReferenceHeteroRange
     */
    public BigDecimal getMaxReferenceHeteroRange() {
        return maxReferenceHeteroRange;
    }

    /**
     * @return vaf
     */
    public BigDecimal getVaf() {
        return vaf;
    }

    /**
     * @return sampleRatio
     */
    public BigDecimal getSampleRatio() {
        return sampleRatio;
    }

    /**
     * @return controlRatio
     */
    public BigDecimal getControlRatio() {
        return controlRatio;
    }

    /**
     * @return zygosity
     */
    public String getZygosity() {
        return zygosity;
    }

    /**
     * @return prediction
     */
    public String getPrediction() {
        return prediction;
    }

    /**
     * @return g1000EastAsian
     */
    public BigDecimal getG1000EastAsian() {
        return g1000EastAsian;
    }

    /**
     * @return gnomADeastAsian
     */
    public BigDecimal getGnomADeastAsian() {
        return gnomADeastAsian;
    }

    /**
     * @return koreanExomInformationDatabase
     */
    public BigDecimal getKoreanExomInformationDatabase() {
        return koreanExomInformationDatabase;
    }

    /**
     * @return koreanReferenceGenomeDatabase
     */
    public BigDecimal getKoreanReferenceGenomeDatabase() {
        return koreanReferenceGenomeDatabase;
    }
}
