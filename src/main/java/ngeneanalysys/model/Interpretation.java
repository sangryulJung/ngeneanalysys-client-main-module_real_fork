package ngeneanalysys.model;

public class Interpretation {
    private Integer id;
    private Integer diseaseId;
    private String identificationMethod;
    private String identificationKey;
    private String description;
    private String interpretationVersion;
    private String evidenceLevelA;
    private String evidenceLevelB;
    private String evidenceLevelC;
    private String evidenceLevelD;
    private String evidencePersistentNegative;
    private String version;

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
     * @return identificationMethod
     */
    public String getIdentificationMethod() {
        return identificationMethod;
    }

    /**
     * @return identificationKey
     */
    public String getIdentificationKey() {
        return identificationKey;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return interpretationVersion
     */
    public String getInterpretationVersion() {
        return interpretationVersion;
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
     * @return evidencePersistentNegative
     */
    public String getEvidencePersistentNegative() {
        return evidencePersistentNegative;
    }

    /**
     * @return version
     */
    public String getVersion() {
        return version;
    }
}
