package ngeneanalysys.model;

import com.sun.media.jfxmedia.logging.Logger;

import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.StringUtils;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class SnpInDelExpression {
    private String variantType;
    private String variantTypeExtension;
    private String transcript;
    private String codingConsequence;
    private String ntChange;
    private String ntChangeBic;
    private String aaChange;
    private String aaChangeSingleLetter;
    private String zygosity;
    private String aachangeSingleLetter;

    /**
     * @return aachangeSingleLetter
     */
    public String getAachangeSingleLetter() {
        return aachangeSingleLetter;
    }

    /**
     * @param aachangeSingleLetter
     */
    public void setAachangeSingleLetter(String aachangeSingleLetter) {
        this.aachangeSingleLetter = aachangeSingleLetter;
    }

    /**
     * @return variantType
     */
    public String getVariantType() {
    	//System.out.println(variantType);
    	if (variantType.equals("snp")) {
    		return "SNP";
    	}else if(variantType.equals("ins")) {
    		return "Ins";
    	}else if(variantType.equals("del")) {
    		return "Del";
    	}else if(variantType.equals("complex")) {
    		return "Complex";
    	}else if(variantType.equals("mnp")) {
    		return "MNP";
    	}else {
    		return variantType;
    	}
    }

    /**
     * @param variantType
     */
    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public String getVariantTypeExtension() { return variantTypeExtension; }

    public void setVariantTypeExtension(String variantTypeExtension) {
        this.variantTypeExtension = variantTypeExtension;
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

    public String getAaChangeSingleLetter() {
        return aaChangeSingleLetter;
    }

    public void setAaChangeSingleLetter(String aaChangeSingleLetter) {
        this.aaChangeSingleLetter = aaChangeSingleLetter;
    }

    public String getAaChangeConversion() {
        return ConvertUtil.getAminoAcid(aaChange);
    }

    @Override
    public String toString() {
        return "SnpInDelExpression{" +
                "variantType='" + variantType + '\'' +
                ", variantTypeExtension='" + variantTypeExtension + '\'' +
                ", transcript='" + transcript + '\'' +
                ", codingConsequence='" + codingConsequence + '\'' +
                ", ntChange='" + ntChange + '\'' +
                ", ntChangeBic='" + ntChangeBic + '\'' +
                ", aaChange='" + aaChange + '\'' +
                ", aaChangeSingleLetter='" + aaChangeSingleLetter + '\'' +
                ", zygosity='" + zygosity + '\'' +
                ", aachangeSingleLetter='" + aachangeSingleLetter + '\'' +
                '}';
    }
}
