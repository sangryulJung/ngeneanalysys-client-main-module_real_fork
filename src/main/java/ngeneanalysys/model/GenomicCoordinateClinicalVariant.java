package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-12-28
 */
public class GenomicCoordinateClinicalVariant {
    private Integer id;
    private Integer diseaseId;
    private String clinicalVariantVersion;
    private NgsGenomicCoordinateClinicalVariant ngs;
    private DbGenomicCoordinateClinicalVariant db;
    private String clinicalVariantType;
    private String evidenceLevelA;
    private String evidenceLevelB;
    private String evidenceLevelC;
    private String evidenceLevelD;
    private String evidenceLevelBenign;
    private String evidencePertinentNegative;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;

    /**
     * @param clinicalVariantType
     */
    public void setClinicalVariantType(String clinicalVariantType) {
        this.clinicalVariantType = clinicalVariantType;
    }

    /**
     * @param evidenceLevelA
     */
    public void setEvidenceLevelA(String evidenceLevelA) {
        this.evidenceLevelA = evidenceLevelA;
    }

    /**
     * @param evidenceLevelB
     */
    public void setEvidenceLevelB(String evidenceLevelB) {
        this.evidenceLevelB = evidenceLevelB;
    }

    /**
     * @param evidenceLevelC
     */
    public void setEvidenceLevelC(String evidenceLevelC) {
        this.evidenceLevelC = evidenceLevelC;
    }

    /**
     * @param evidenceLevelD
     */
    public void setEvidenceLevelD(String evidenceLevelD) {
        this.evidenceLevelD = evidenceLevelD;
    }

    /**
     * @param evidenceLevelBenign
     */
    public void setEvidenceLevelBenign(String evidenceLevelBenign) {
        this.evidenceLevelBenign = evidenceLevelBenign;
    }

    /**
     * @param evidencePertinentNegative
     */
    public void setEvidencePertinentNegative(String evidencePertinentNegative) {
        this.evidencePertinentNegative = evidencePertinentNegative;
    }

    /**
     * @param ngs
     */
    public void setNgs(NgsGenomicCoordinateClinicalVariant ngs) {
        this.ngs = ngs;
    }

    /**
     * @param db
     */
    public void setDb(DbGenomicCoordinateClinicalVariant db) {
        this.db = db;
    }

    /**
     * @param diseaseId
     */
    public void setDiseaseId(Integer diseaseId) {
        this.diseaseId = diseaseId;
    }

    /**
     * @param clinicalVariantVersion
     */
    public void setClinicalVariantVersion(String clinicalVariantVersion) {
        this.clinicalVariantVersion = clinicalVariantVersion;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return diseaseId
     */
    public Integer getDiseaseId() {
        return diseaseId;
    }

    /**
     * @return clinicalVariantVersion
     */
    public String getClinicalVariantVersion() {
        return clinicalVariantVersion;
    }

    /**
     * @return ngs
     */
    public NgsGenomicCoordinateClinicalVariant getNgs() {
        return ngs;
    }

    /**
     * @return db
     */
    public DbGenomicCoordinateClinicalVariant getDb() {
        return db;
    }

    /**
     * @return clinicalVariantType
     */
    public String getClinicalVariantType() {
        return clinicalVariantType;
    }

    /**
     * @return evidenceLevelA
     */
    public String getEvidenceLevelA() {
        return evidenceLevelA;
    }

    /**
     * @return evidenceLevelB
     */
    public String getEvidenceLevelB() {
        return evidenceLevelB;
    }

    /**
     * @return evidenceLevelC
     */
    public String getEvidenceLevelC() {
        return evidenceLevelC;
    }

    /**
     * @return evidenceLevelD
     */
    public String getEvidenceLevelD() {
        return evidenceLevelD;
    }

    /**
     * @return evidenceLevelBenign
     */
    public String getEvidenceLevelBenign() {
        return evidenceLevelBenign;
    }

    /**
     * @return evidencePertinentNegative
     */
    public String getEvidencePertinentNegative() {
        return evidencePertinentNegative;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @return deletedAt
     */
    public DateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * @return deleted
     */
    public Integer getDeleted() {
        return deleted;
    }
}
