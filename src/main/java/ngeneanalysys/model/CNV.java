package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-05-23
 */
public class CNV {
    private Integer sampleId;
    private String gene;
    private BigDecimal cnvValue;
    private String tier;
    private String includedInReport;

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
    }

    /**
     * @return tier
     */
    public String getTier() {
        return tier;
    }

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
     * @return cnvValue
     */
    public BigDecimal getCnvValue() {
        return cnvValue;
    }
}
