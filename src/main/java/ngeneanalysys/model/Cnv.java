package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-05-23
 */
public class Cnv {
    private Integer id;
    private Integer sampleId;
    private String gene;
    private BigDecimal cnvValue;
    private String swTier;
    private String expertTier;
    private String includedInReport;
    private String comment;

    public String getComment() {
        return comment;
    }

    public Integer getId() {
        return id;
    }

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
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

    public String getSwTier() {
        return swTier;
    }

    public String getExpertTier() {
        return expertTier;
    }
}
