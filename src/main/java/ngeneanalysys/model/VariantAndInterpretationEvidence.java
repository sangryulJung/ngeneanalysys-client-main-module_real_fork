package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-06
 */
public class VariantAndInterpretationEvidence {
    private SnpInDel snpInDel;
    private Interpretation interpretationEvidence;

    /**
     * @param snpInDel
     */
    public void setSnpInDel(SnpInDel snpInDel) {
        this.snpInDel = snpInDel;
    }

    /**
     * @param interpretationEvidence
     */
    public void setInterpretationEvidence(Interpretation interpretationEvidence) {
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
    public Interpretation getInterpretationEvidence() {
        return interpretationEvidence;
    }
}
