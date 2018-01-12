package ngeneanalysys.model;

import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.StringUtils;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class SnpInDelExpression {
    private String variantType;
    private String transcript;
    private String codingConsequence;
    private String ntChange;
    private String ntChangeBic;
    private String aaChange;
    private String zygosity;

    /**
     * @return variantType
     */
    public String getVariantType() {
        return variantType;
    }

    /**
     * @param variantType
     */
    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    /**
     * @return transcript
     */
    public String getTranscript() {
        return transcript;
    }

    /**
     * @param transcript
     */
    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    /**
     * @return codingConsequence
     */
    public String getCodingConsequence() {
        return codingConsequence;
    }

    /**
     * @param codingConsequence
     */
    public void setCodingConsequence(String codingConsequence) {
        this.codingConsequence = codingConsequence;
    }

    /**
     * @return ntChange
     */
    public String getNtChange() {
        return ntChange;
    }

    /**
     * @param ntChange
     */
    public void setNtChange(String ntChange) {
        this.ntChange = ntChange;
    }

    /**
     * @return ntChangeBic
     */
    public String getNtChangeBic() {
        return ntChangeBic;
    }

    /**
     * @param ntChangeBic
     */
    public void setNtChangeBic(String ntChangeBic) {
        this.ntChangeBic = ntChangeBic;
    }

    /**
     * @return aaChange
     */
    public String getAaChange() {
        return aaChange;
    }

    /**
     * @param aaChange
     */
    public void setAaChange(String aaChange) {
        this.aaChange = aaChange;
    }

    /**
     * @return zygosity
     */
    public String getZygosity() {
        return zygosity;
    }

    /**
     * @param zygosity
     */
    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public String getAaChangeConversion() {
        return ConvertUtil.getAminoAcid(aaChange);
    }
}
