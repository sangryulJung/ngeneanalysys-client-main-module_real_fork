package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-10-30
 */
public class BrcaCNVExon {
    private Boolean checkItem;
    private Integer id;
    private Integer sampleId;
    private String gene;
    private String cnv;
    private String exon;
    private String includedInReport;
    private String domain;
    private Integer copyNumber;
    private String ampliconRatio;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
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
     * @return checkItem
     */
    public Boolean getCheckItem() {
        return checkItem;
    }

    /**
     * @return cnvValue
     */
    public String getCnv() {
        return cnv;
    }

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
    }

    /**
     * @return exon
     */
    public String getExon() {
        return exon;
    }

    /**
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return copyNumber
     */
    public Integer getCopyNumber() {
        return copyNumber;
    }

    /**
     * @return ampliconRatio
     */
    public String getAmpliconRatio() {
        return ampliconRatio;
    }
}
