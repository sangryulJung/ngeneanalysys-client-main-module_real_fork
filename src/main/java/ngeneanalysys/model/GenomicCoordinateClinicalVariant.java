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
    private String Tier;
    private GenomicCoordinateForCV genomicCoordinateForCV;
    private String dbNtChange;
    private String dbTranscript;
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
     * @return dbTranscript
     */
    public String getDbTranscript() {
        return dbTranscript;
    }

    /**
     * @param dbTranscript
     */
    public void setDbTranscript(String dbTranscript) {
        this.dbTranscript = dbTranscript;
    }

    /**
     * @return Tier
     */
    public String getTier() {
        return Tier;
    }

    /**
     * @param tier
     */
    public void setTier(String tier) {
        Tier = tier;
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
     * @return diseaseId
     */
    public Integer getDiseaseId() {
        return diseaseId;
    }

    /**
     * @param diseaseId
     */
    public void setDiseaseId(Integer diseaseId) {
        this.diseaseId = diseaseId;
    }

    /**
     * @return clinicalVariantVersion
     */
    public String getClinicalVariantVersion() {
        return clinicalVariantVersion;
    }

    /**
     * @param clinicalVariantVersion
     */
    public void setClinicalVariantVersion(String clinicalVariantVersion) {
        this.clinicalVariantVersion = clinicalVariantVersion;
    }

    /**
     * @return genomicCoordinateForCV
     */
    public GenomicCoordinateForCV getGenomicCoordinateForCV() {
        return genomicCoordinateForCV;
    }

    /**
     * @param genomicCoordinateForCV
     */
    public void setGenomicCoordinateForCV(GenomicCoordinateForCV genomicCoordinateForCV) {
        this.genomicCoordinateForCV = genomicCoordinateForCV;
    }

    /**
     * @return dbNtChange
     */
    public String getDbNtChange() {
        return dbNtChange;
    }

    /**
     * @param dbNtChange
     */
    public void setDbNtChange(String dbNtChange) {
        this.dbNtChange = dbNtChange;
    }

    /**
     * @return clinicalVariantType
     */
    public String getClinicalVariantType() {
        return clinicalVariantType;
    }

    /**
     * @param clinicalVariantType
     */
    public void setClinicalVariantType(String clinicalVariantType) {
        this.clinicalVariantType = clinicalVariantType;
    }

    /**
     * @return evidenceLevelA
     */
    public String getEvidenceLevelA() {
        return evidenceLevelA;
    }

    /**
     * @param evidenceLevelA
     */
    public void setEvidenceLevelA(String evidenceLevelA) {
        this.evidenceLevelA = evidenceLevelA;
    }

    /**
     * @return evidenceLevelB
     */
    public String getEvidenceLevelB() {
        return evidenceLevelB;
    }

    /**
     * @param evidenceLevelB
     */
    public void setEvidenceLevelB(String evidenceLevelB) {
        this.evidenceLevelB = evidenceLevelB;
    }

    /**
     * @return evidenceLevelC
     */
    public String getEvidenceLevelC() {
        return evidenceLevelC;
    }

    /**
     * @param evidenceLevelC
     */
    public void setEvidenceLevelC(String evidenceLevelC) {
        this.evidenceLevelC = evidenceLevelC;
    }

    /**
     * @return evidenceLevelD
     */
    public String getEvidenceLevelD() {
        return evidenceLevelD;
    }

    /**
     * @param evidenceLevelD
     */
    public void setEvidenceLevelD(String evidenceLevelD) {
        this.evidenceLevelD = evidenceLevelD;
    }

    /**
     * @return evidenceLevelBenign
     */
    public String getEvidenceLevelBenign() {
        return evidenceLevelBenign;
    }

    /**
     * @param evidenceLevelBenign
     */
    public void setEvidenceLevelBenign(String evidenceLevelBenign) {
        this.evidenceLevelBenign = evidenceLevelBenign;
    }

    /**
     * @return evidencePertinentNegative
     */
    public String getEvidencePertinentNegative() {
        return evidencePertinentNegative;
    }

    /**
     * @param evidencePertinentNegative
     */
    public void setEvidencePertinentNegative(String evidencePertinentNegative) {
        this.evidencePertinentNegative = evidencePertinentNegative;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return deletedAt
     */
    public DateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * @param deletedAt
     */
    public void setDeletedAt(DateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * @return deleted
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * @param deleted
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
