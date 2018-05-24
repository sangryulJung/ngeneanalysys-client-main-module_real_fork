package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-05-23
 */
public class CNV {
    private Integer sampleId;
    private String gene;
    private Double cnvValue;

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
    public Double getCnvValue() {
        return cnvValue;
    }
}
