package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-01
 */
public class VariantPerGene {
    private Integer id;
    private Integer sampleId;
    private String isRoi;
    private String gene;
    private Integer t1SnpCount;
    private Integer t1IndelCount;
    private Integer t1FusionCount;
    private Integer t1CnvCount;
    private Integer t2SnpCount;
    private Integer t2IndelCount;
    private Integer t2FusionCount;
    private Integer t2CnvCount;
    private Integer t3SnpCount;
    private Integer t3IndelCount;
    private Integer t3FusionCount;
    private Integer t3CnvCount;
    private Integer t4SnpCount;
    private Integer t4IndelCount;
    private Integer t4FusionCount;
    private Integer t4CnvCount;
    private Integer pCount;
    private Integer lpCount;
    private Integer usCount;
    private Integer lbCount;
    private Integer bCount;

    public VariantPerGene(Integer id, Integer sampleId, String isRoi, String gene, Integer t1SnpCount, Integer t1indelCount, Integer t1fusionCount, Integer t1cnvCount, Integer t2SnpCount, Integer t2indelCount, Integer t2fusionCount, Integer t2cnvCount, Integer t3SnpCount, Integer t3indelCount, Integer t3fusionCount, Integer t3cnvCount, Integer t4SnpCount, Integer t4indelCount, Integer t4fusionCount, Integer t4cnvCount) {
        this.id = id;
        this.sampleId = sampleId;
        this.isRoi = isRoi;
        this.gene = gene;
        this.t1SnpCount = t1SnpCount;
        this.t1IndelCount = t1indelCount;
        this.t1FusionCount = t1fusionCount;
        this.t1CnvCount = t1cnvCount;
        this.t2SnpCount = t2SnpCount;
        this.t2IndelCount = t2indelCount;
        this.t2FusionCount = t2fusionCount;
        this.t2CnvCount = t2cnvCount;
        this.t3SnpCount = t3SnpCount;
        this.t3IndelCount = t3indelCount;
        this.t3FusionCount = t3fusionCount;
        this.t3CnvCount = t3cnvCount;
        this.t4SnpCount = t4SnpCount;
        this.t4IndelCount = t4indelCount;
        this.t4FusionCount = t4fusionCount;
        this.t4CnvCount = t4cnvCount;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @param sampleId
     */
    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * @return isRoi
     */
    public String getIsRoi() {
        return isRoi;
    }

    /**
     * @param isRoi
     */
    public void setIsRoi(String isRoi) {
        this.isRoi = isRoi;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @param gene
     */
    public void setGene(String gene) {
        this.gene = gene;
    }

    /**
     * @return t1SnpCount
     */
    public Integer getT1SnpCount() {
        return t1SnpCount;
    }

    /**
     * @param t1SnpCount
     */
    public void setT1SnpCount(Integer t1SnpCount) {
        this.t1SnpCount = t1SnpCount;
    }

    /**
     * @return t1IndelCount
     */
    public Integer getT1IndelCount() {
        return t1IndelCount;
    }

    /**
     * @param t1IndelCount
     */
    public void setT1IndelCount(Integer t1IndelCount) {
        this.t1IndelCount = t1IndelCount;
    }

    /**
     * @return t1FusionCount
     */
    public Integer getT1FusionCount() {
        return t1FusionCount;
    }

    /**
     * @param t1FusionCount
     */
    public void setT1FusionCount(Integer t1FusionCount) {
        this.t1FusionCount = t1FusionCount;
    }

    /**
     * @return t1CnvCount
     */
    public Integer getT1CnvCount() {
        return t1CnvCount;
    }

    /**
     * @param t1CnvCount
     */
    public void setT1CnvCount(Integer t1CnvCount) {
        this.t1CnvCount = t1CnvCount;
    }

    /**
     * @return t2SnpCount
     */
    public Integer getT2SnpCount() {
        return t2SnpCount;
    }

    /**
     * @param t2SnpCount
     */
    public void setT2SnpCount(Integer t2SnpCount) {
        this.t2SnpCount = t2SnpCount;
    }

    /**
     * @return t2IndelCount
     */
    public Integer getT2IndelCount() {
        return t2IndelCount;
    }

    /**
     * @param t2IndelCount
     */
    public void setT2IndelCount(Integer t2IndelCount) {
        this.t2IndelCount = t2IndelCount;
    }

    /**
     * @return t2FusionCount
     */
    public Integer getT2FusionCount() {
        return t2FusionCount;
    }

    /**
     * @param t2FusionCount
     */
    public void setT2FusionCount(Integer t2FusionCount) {
        this.t2FusionCount = t2FusionCount;
    }

    /**
     * @return t2CnvCount
     */
    public Integer getT2CnvCount() {
        return t2CnvCount;
    }

    /**
     * @param t2CnvCount
     */
    public void setT2CnvCount(Integer t2CnvCount) {
        this.t2CnvCount = t2CnvCount;
    }

    /**
     * @return t3SnpCount
     */
    public Integer getT3SnpCount() {
        return t3SnpCount;
    }

    /**
     * @param t3SnpCount
     */
    public void setT3SnpCount(Integer t3SnpCount) {
        this.t3SnpCount = t3SnpCount;
    }

    /**
     * @return t3IndelCount
     */
    public Integer getT3IndelCount() {
        return t3IndelCount;
    }

    /**
     * @param t3IndelCount
     */
    public void setT3IndelCount(Integer t3IndelCount) {
        this.t3IndelCount = t3IndelCount;
    }

    /**
     * @return t3FusionCount
     */
    public Integer getT3FusionCount() {
        return t3FusionCount;
    }

    /**
     * @param t3FusionCount
     */
    public void setT3FusionCount(Integer t3FusionCount) {
        this.t3FusionCount = t3FusionCount;
    }

    /**
     * @return t3CnvCount
     */
    public Integer getT3CnvCount() {
        return t3CnvCount;
    }

    /**
     * @param t3CnvCount
     */
    public void setT3CnvCount(Integer t3CnvCount) {
        this.t3CnvCount = t3CnvCount;
    }

    /**
     * @return t4SnpCount
     */
    public Integer getT4SnpCount() {
        return t4SnpCount;
    }

    /**
     * @param t4SnpCount
     */
    public void setT4SnpCount(Integer t4SnpCount) {
        this.t4SnpCount = t4SnpCount;
    }

    /**
     * @return t4IndelCount
     */
    public Integer getT4IndelCount() {
        return t4IndelCount;
    }

    /**
     * @param t4IndelCount
     */
    public void setT4IndelCount(Integer t4IndelCount) {
        this.t4IndelCount = t4IndelCount;
    }

    /**
     * @return t4FusionCount
     */
    public Integer getT4FusionCount() {
        return t4FusionCount;
    }

    /**
     * @param t4FusionCount
     */
    public void setT4FusionCount(Integer t4FusionCount) {
        this.t4FusionCount = t4FusionCount;
    }

    /**
     * @return t4CnvCount
     */
    public Integer getT4CnvCount() {
        return t4CnvCount;
    }

    /**
     * @param t4CnvCount
     */
    public void setT4CnvCount(Integer t4CnvCount) {
        this.t4CnvCount = t4CnvCount;
    }

    /**
     * @return pCount
     */
    public Integer getpCount() {
        return pCount;
    }

    /**
     * @param pCount
     */
    public void setpCount(Integer pCount) {
        this.pCount = pCount;
    }

    /**
     * @return lpCount
     */
    public Integer getLpCount() {
        return lpCount;
    }

    /**
     * @param lpCount
     */
    public void setLpCount(Integer lpCount) {
        this.lpCount = lpCount;
    }

    /**
     * @return usCount
     */
    public Integer getUsCount() {
        return usCount;
    }

    /**
     * @param usCount
     */
    public void setUsCount(Integer usCount) {
        this.usCount = usCount;
    }

    /**
     * @return lbCount
     */
    public Integer getLbCount() {
        return lbCount;
    }

    /**
     * @param lbCount
     */
    public void setLbCount(Integer lbCount) {
        this.lbCount = lbCount;
    }

    /**
     * @return bCount
     */
    public Integer getbCount() {
        return bCount;
    }

    /**
     * @param bCount
     */
    public void setbCount(Integer bCount) {
        this.bCount = bCount;
    }
}
