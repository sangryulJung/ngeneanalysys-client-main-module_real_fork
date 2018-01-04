package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-06
 */
public class VariantAndInterpretationEvidence {
    private SnpInDel snpInDel;
    private SnpInDelInterpretation interpretationEvidence;

    /**
     * @param snpInDel
     */
    public void setSnpInDel(SnpInDel snpInDel) {
        this.snpInDel = snpInDel;
    }

    /**
     * @param interpretationEvidence
     */
    public void setInterpretationEvidence(SnpInDelInterpretation interpretationEvidence) {
        this.interpretationEvidence = interpretationEvidence;
    }

    /**
     * @return snpInDel
     */
    public SnpInDel getSnpInDel() {
        return snpInDel;
    }

    /**
     * @return interpretationEvidence
     */
    public SnpInDelInterpretation getInterpretationEvidence() {
        return interpretationEvidence;
    }
}
