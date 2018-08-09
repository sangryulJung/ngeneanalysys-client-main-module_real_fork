package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-12-28
 */
public class GenomicCoordinateClinicalVariant {
    private Integer id;
    private Integer diseaseId;
    private String tier;
    private String clinicalVariantVersion;
    private String chr;
    private String gene;
    private String transcript;
    private String hgvsc;
    private String hgvsp;
    private Integer startPosition;
    private Integer endPosition;
    private String codingConsequence;
    private String clinicalVariantType;
    private String clinVarExistence;
    private ClinicalDatabaseExistence clinicalDatabaseExistence;
    private ClinicalEvidence therapeuticEvidence;
    private ClinicalEvidence prognosisEvidence;
    private ClinicalEvidence diagnosisEvidence;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;

    /**
     * @return clinVarExistence
     */
    public String getClinVarExistence() {
        return clinVarExistence;
    }

    /**
     * @param clinVarExistence
     */
    public void setClinVarExistence(String clinVarExistence) {
        this.clinVarExistence = clinVarExistence;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Integer diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getClinicalVariantVersion() {
        return clinicalVariantVersion;
    }

    public void setClinicalVariantVersion(String clinicalVariantVersion) {
        this.clinicalVariantVersion = clinicalVariantVersion;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getHgvsc() {
        return hgvsc;
    }

    public void setHgvsc(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    public String getHgvsp() {
        return hgvsp;
    }

    public void setHgvsp(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    public Integer getStartPosition() { return startPosition; }

    public void setStartPosition(Integer startPosition) { this.startPosition = startPosition; }

    public Integer getEndPosition() { return endPosition; }

    public void setEndPosition(Integer endPosition) { this.endPosition = endPosition; }

    public String getCodingConsequence() {
        return codingConsequence;
    }

    public void setCodingConsequence(String codingConsequence) {
        this.codingConsequence = codingConsequence;
    }

    public String getClinicalVariantType() {
        return clinicalVariantType;
    }

    public void setClinicalVariantType(String clinicalVariantType) {
        this.clinicalVariantType = clinicalVariantType;
    }

    public ClinicalDatabaseExistence getClinicalDatabaseExistence() {
        return clinicalDatabaseExistence;
    }

    public void setClinicalDatabaseExistence(ClinicalDatabaseExistence clinicalDatabaseExistence) {
        this.clinicalDatabaseExistence = clinicalDatabaseExistence;
    }

    public ClinicalEvidence getTherapeuticEvidence() {
        return therapeuticEvidence;
    }

    public void setTherapeuticEvidence(ClinicalEvidence therapeuticEvidence) {
        this.therapeuticEvidence = therapeuticEvidence;
    }

    public ClinicalEvidence getPrognosisEvidence() {
        return prognosisEvidence;
    }

    public void setPrognosisEvidence(ClinicalEvidence prognosisEvidence) {
        this.prognosisEvidence = prognosisEvidence;
    }

    public ClinicalEvidence getDiagnosisEvidence() {
        return diagnosisEvidence;
    }

    public void setDiagnosisEvidence(ClinicalEvidence diagnosisEvidence) {
        this.diagnosisEvidence = diagnosisEvidence;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(DateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
