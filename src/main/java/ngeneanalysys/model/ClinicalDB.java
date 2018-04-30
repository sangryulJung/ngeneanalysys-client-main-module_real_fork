package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class ClinicalDB {
    private ClinVar clinVar;
    private CatalogueOfSomaticMutationsInCancer cosmic;
    private BrcaCancerInformationCore bic;
    private String kohbraPatient;
    private String enigma;
    private BrcaExchange be;
    private OncoKB oncoKB;
    private DBNSFP dbNSFP;

    /**
     * @return dbNSFP
     */
    public DBNSFP getDbNSFP() {
        return dbNSFP;
    }

    /**
     * @return oncoKB
     */
    public OncoKB getOncoKB() {
        return oncoKB;
    }

    /**
     * @return cosmic
     */
    public CatalogueOfSomaticMutationsInCancer getCosmic() {
        return cosmic;
    }

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
