package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-10-30
 */
public class BrcaCnvExon {
    private Boolean checkItem = false;
    private Integer id;
    private Integer sampleId;
    private String gene;
    private String exon;
    private String warning;
    private String includedInReport;
    private String domain;
    private Integer swCopyNumber;
    private Integer expertCopyNumber;
    private Double copyNumberOneAmpliconPercentage;
    private Double copyNumberTwoAmpliconPercentage;
    private Double copyNumberThreeAmpliconPercentage;

    /**
     * @param checkItem
     */
    public void setCheckItem(Boolean checkItem) {
        this.checkItem = checkItem;
    }

    /**
     * @return checkItem
     */
    public Boolean getCheckItem() {
        return checkItem;
    }

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
     * @return exon
     */
    public String getExon() {
        return exon;
    }

    /**
     * @return warning
     */
    public String getWarning() {
        return warning;
    }

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
    }

    /**
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return swCopyNumber
     */
    public Integer getSwCopyNumber() {
        return swCopyNumber;
    }

    /**
     * @return expertCopyNumber
     */
    public Integer getExpertCopyNumber() {
        return expertCopyNumber;
    }

    /**
     * @return copyNumberOneAmpliconPercentage
     */
    public Double getCopyNumberOneAmpliconPercentage() {
        return copyNumberOneAmpliconPercentage;
    }

    /**
     * @return copyNumberTwoAmpliconPercentage
     */
    public Double getCopyNumberTwoAmpliconPercentage() {
        return copyNumberTwoAmpliconPercentage;
    }

    /**
     * @return copyNumberThreeAmpliconPercentage
     */
    public Double getCopyNumberThreeAmpliconPercentage() {
        return copyNumberThreeAmpliconPercentage;
    }
}
