package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-12-28
 */
public class GenomicCoordinateClinicalVariant {
    private Integer id;
    private Integer diseaseId;
    private String dbChr;
    private String dbGene;
    private Integer dbPosition;
    private String dbbRef;
    private String dbAlt;
    private String ngsChr;
    private String ngsGene;
    private Integer ngsPosition;
    private String ngsRef;
    private String ngsAlt;
    private String clinicalVariantType;
    private String evidenceLevelA;
    private String evidenceLevelB;
    private String evidenceLevelC;
    private String evidenceLevelD;
    private String evidenceLevelBenign;
    private String pertinentNegativeEvidence;
    private DateTime createdAt;
    private DateTime deletedAt;
    private Integer deleted;

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
     * @return dbChr
     */
    public String getDbChr() {
        return dbChr;
    }

    /**
     * @param dbChr
     */
    public void setDbChr(String dbChr) {
        this.dbChr = dbChr;
    }

    /**
     * @return dbGene
     */
    public String getDbGene() {
        return dbGene;
    }

    /**
     * @param dbGene
     */
    public void setDbGene(String dbGene) {
        this.dbGene = dbGene;
    }

    /**
     * @return dbPosition
     */
    public Integer getDbPosition() {
        return dbPosition;
    }

    /**
     * @param dbPosition
     */
    public void setDbPosition(Integer dbPosition) {
        this.dbPosition = dbPosition;
    }

    /**
     * @return dbbRef
     */
    public String getDbbRef() {
        return dbbRef;
    }

    /**
     * @param dbbRef
     */
    public void setDbbRef(String dbbRef) {
        this.dbbRef = dbbRef;
    }

    /**
     * @return dbAlt
     */
    public String getDbAlt() {
        return dbAlt;
    }

    /**
     * @param dbAlt
     */
    public void setDbAlt(String dbAlt) {
        this.dbAlt = dbAlt;
    }

    /**
     * @return ngsChr
     */
    public String getNgsChr() {
        return ngsChr;
    }

    /**
     * @param ngsChr
     */
    public void setNgsChr(String ngsChr) {
        this.ngsChr = ngsChr;
    }

    /**
     * @return ngsGene
     */
    public String getNgsGene() {
        return ngsGene;
    }

    /**
     * @param ngsGene
     */
    public void setNgsGene(String ngsGene) {
        this.ngsGene = ngsGene;
    }

    /**
     * @return ngsPosition
     */
    public Integer getNgsPosition() {
        return ngsPosition;
    }

    /**
     * @param ngsPosition
     */
    public void setNgsPosition(Integer ngsPosition) {
        this.ngsPosition = ngsPosition;
    }

    /**
     * @return ngsRef
     */
    public String getNgsRef() {
        return ngsRef;
    }

    /**
     * @param ngsRef
     */
    public void setNgsRef(String ngsRef) {
        this.ngsRef = ngsRef;
    }

    /**
     * @return ngsAlt
     */
    public String getNgsAlt() {
        return ngsAlt;
    }

    /**
     * @param ngsAlt
     */
    public void setNgsAlt(String ngsAlt) {
        this.ngsAlt = ngsAlt;
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
     * @return pertinentNegativeEvidence
     */
    public String getPertinentNegativeEvidence() {
        return pertinentNegativeEvidence;
    }

    /**
     * @param pertinentNegativeEvidence
     */
    public void setPertinentNegativeEvidence(String pertinentNegativeEvidence) {
        this.pertinentNegativeEvidence = pertinentNegativeEvidence;
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
