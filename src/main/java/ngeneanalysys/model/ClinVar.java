package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class ClinVar {
    private String clinVarAcc;
    private String clinVarDisease;
    private String clinVarClass;
    private String clinVarTraitOMIM;
    private String clinVarReviewStatus;

    /**
     * @return clinVarAcc
     */
    public String getClinVarAcc() {
        return clinVarAcc;
    }

    /**
     * @param clinVarAcc
     */
    public void setClinVarAcc(String clinVarAcc) {
        this.clinVarAcc = clinVarAcc;
    }

    /**
     * @return clinVarDisease
     */
    public String getClinVarDisease() {
        return clinVarDisease;
    }

    /**
     * @param clinVarDisease
     */
    public void setClinVarDisease(String clinVarDisease) {
        this.clinVarDisease = clinVarDisease;
    }

    /**
     * @return clinVarClass
     */
    public String getClinVarClass() {
        return clinVarClass;
    }

    /**
     * @param clinVarClass
     */
    public void setClinVarClass(String clinVarClass) {
        this.clinVarClass = clinVarClass;
    }

    public String getClinVarTraitOMIM() {
        return clinVarTraitOMIM;
    }

    public void setClinVarTraitOMIM(String clinVarTraitOMIM) {
        this.clinVarTraitOMIM = clinVarTraitOMIM;
    }

    public String getClinVarReviewStatus() {
        return clinVarReviewStatus;
    }
}
