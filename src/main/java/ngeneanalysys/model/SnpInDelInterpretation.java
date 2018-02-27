package ngeneanalysys.model;

public class SnpInDelInterpretation {
    private Integer snpInDelId;
    private String clinicalVariantVersion;
    private String clinicalVariantType;
    private ClinicalEvidence therapeuticEvidence;
    private ClinicalEvidence diagnosisEvidence;
    private ClinicalEvidence prognosisEvidence;


    public Integer getSnpInDelId() {
        return snpInDelId;
    }

    public void setSnpInDelId(Integer snpInDelId) {
        this.snpInDelId = snpInDelId;
    }

    public String getClinicalVariantVersion() {
        return clinicalVariantVersion;
    }

    public void setClinicalVariantVersion(String clinicalVariantVersion) {
        this.clinicalVariantVersion = clinicalVariantVersion;
    }

    public String getClinicalVariantType() {
        return clinicalVariantType;
    }

    public void setClinicalVariantType(String clinicalVariantType) {
        this.clinicalVariantType = clinicalVariantType;
    }

    public ClinicalEvidence getTherapeuticEvidence() {
        return therapeuticEvidence;
    }

    public void setTherapeuticEvidence(ClinicalEvidence therapeuticEvidence) {
        this.therapeuticEvidence = therapeuticEvidence;
    }

    public ClinicalEvidence getDiagnosisEvidence() {
        return diagnosisEvidence;
    }

    public void setDiagnosisEvidence(ClinicalEvidence diagnosisEvidence) {
        this.diagnosisEvidence = diagnosisEvidence;
    }

    public ClinicalEvidence getPrognosisEvidence() {
        return prognosisEvidence;
    }

    public void setPrognosisEvidence(ClinicalEvidence prognosisEvidence) {
        this.prognosisEvidence = prognosisEvidence;
    }

    @Override
    public String toString() {
        return "SnpInDelInterpretation{" +
                "snpInDelId=" + snpInDelId +
                ", clinicalVariantVersion='" + clinicalVariantVersion + '\'' +
                ", clinicalVariantType='" + clinicalVariantType + '\'' +
                ", therapeuticEvidence=" + therapeuticEvidence +
                ", diagnosisEvidence=" + diagnosisEvidence +
                ", prognosisEvidence=" + prognosisEvidence +
                '}';
    }
}
