package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class ClinicalSignificant {
    private ClinVar clinVar;
    private BrcaCancerInformationCore bic;
    private String kohbraPatient;
    private String enigma;
    private BigDecimal polyphen2;
    private BigDecimal sift;
    private String mutationTaster;
    private BrcaExchange be;

    /**
     * @return be
     */
    public BrcaExchange getBe() {
        return be;
    }

    /**
     * @param be
     */
    public void setBe(BrcaExchange be) {
        this.be = be;
    }

    /**
     * @return clinVar
     */
    public ClinVar getClinVar() {
        return clinVar;
    }

    /**
     * @return bic
     */
    public BrcaCancerInformationCore getBic() {
        return bic;
    }

    /**
     * @return kohbraPatient
     */
    public String getKohbraPatient() {
        return kohbraPatient;
    }

    /**
     * @param kohbraPatient
     */
    public void setKohbraPatient(String kohbraPatient) {
        this.kohbraPatient = kohbraPatient;
    }

    /**
     * @return enigma
     */
    public String getEnigma() {
        return enigma;
    }

    /**
     * @param enigma
     */
    public void setEnigma(String enigma) {
        this.enigma = enigma;
    }

    /**
     * @return polyphen2
     */
    public BigDecimal getPolyphen2() {
        return polyphen2;
    }

    /**
     * @param polyphen2
     */
    public void setPolyphen2(BigDecimal polyphen2) {
        this.polyphen2 = polyphen2;
    }

    /**
     * @return sift
     */
    public BigDecimal getSift() {
        return sift;
    }

    /**
     * @param sift
     */
    public void setSift(BigDecimal sift) {
        this.sift = sift;
    }

    /**
     * @return mutationTaster
     */
    public String getMutationTaster() {
        return mutationTaster;
    }

    /**
     * @param mutationTaster
     */
    public void setMutationTaster(String mutationTaster) {
        this.mutationTaster = mutationTaster;
    }

    /**
     * @param clinVar
     */
    public void setClinVar(ClinVar clinVar) {
        this.clinVar = clinVar;
    }

    /**
     * @param bic
     */
    public void setBic(BrcaCancerInformationCore bic) {
        this.bic = bic;
    }
}
