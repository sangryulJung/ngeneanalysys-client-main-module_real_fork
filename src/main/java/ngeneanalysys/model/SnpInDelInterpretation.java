package ngeneanalysys.model;

public class SnpInDelInterpretation {
    private Integer snpInDelId;
    private Integer diseaseId;
    private String clinicalVariantVersion;
    private String dbRef;
    private String dbAlt;
    private String clinicalVariantType;
    private String dbNtChange;
    private String evidenceLevelA;
    private String evidenceLevelB;
    private String evidenceLevelC;
    private String evidenceLevelD;
    private String evidenceBenign;
    private String dbTranscript;

    /**
     * @return dbTranscript
     */
    public String getDbTranscript() {
        return dbTranscript;
    }

    /**
     * @return snpInDelId
     */
    public Integer getSnpInDelId() {
        return snpInDelId;
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
     * @return dbRef
     */
    public String getDbRef() {
        return dbRef;
    }

    /**
     * @return dbAlt
     */
    public String getDbAlt() {
        return dbAlt;
    }

    /**
     * @return clinicalVariantType
     */
    public String getClinicalVariantType() {
        return clinicalVariantType;
    }

    /**
     * @return dbNtChange
     */
    public String getDbNtChange() {
        return dbNtChange;
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
     * @return evidenceBenign
     */
    public String getEvidenceBenign() {
        return evidenceBenign;
    }
}
