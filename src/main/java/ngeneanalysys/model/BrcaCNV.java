package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-10-22
 */
public class BrcaCNV {
    private Integer sampleId;
    private String gene;
    private String exon;
    private String amplicon;
    private BigDecimal normalRangeMin;
    private BigDecimal normalRangeMax;
    private BigDecimal cnvValue;
    private String prediction;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return exon
     */
    public String getExon() {
        return exon;
    }

    /**
     * @return amplicon
     */
    public String getAmplicon() {
        return amplicon;
    }

    /**
     * @return normalRangeMin
     */
    public BigDecimal getNormalRangeMin() {
        return normalRangeMin;
    }

    /**
     * @return normalRangeMax
     */
    public BigDecimal getNormalRangeMax() {
        return normalRangeMax;
    }

    /**
     * @return cnvValue
     */
    public BigDecimal getCnvValue() {
        return cnvValue;
    }

    /**
     * @return prediction
     */
    public String getPrediction() {
        return prediction;
    }
}

