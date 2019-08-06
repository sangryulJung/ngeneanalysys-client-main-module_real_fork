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
    private String domain;
    private Integer copyNumber;
    private String swCnv;
    private String expertCnv;
    private Double copyNumberOneAmpliconPercentage;
    private Double copyNumberTwoAmpliconPercentage;
    private Double copyNumberThreeAmpliconPercentage;
    private String comment;
    private String includedInReport;

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
    }

    /**
     * @return comment
     */
    public String getComment() {
        return comment;
    }

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
     * @return swCnv
     */
    public String getSwCnv() {
        return swCnv;
    }

    /**
     * @return expertCnv
     */
    public String getExpertCnv() {
        return expertCnv;
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
