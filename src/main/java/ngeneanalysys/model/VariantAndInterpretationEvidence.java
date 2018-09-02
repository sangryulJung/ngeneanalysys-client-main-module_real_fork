package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-12-06
 */
public class VariantAndInterpretationEvidence {
    private SnpInDel snpInDel;
    private List<SnpInDelEvidence> snpInDelEvidences;
    private Boolean checkItem = false;

    /**
     * @return checkItem
     */
    public Boolean getCheckItem() {
        return checkItem;
    }

    /**
     * @param checkItem
     */
    public void setCheckItem(Boolean checkItem) {
        this.checkItem = checkItem;
    }

    /**
     * @return snpInDel
     */
    public SnpInDel getSnpInDel() {
        return snpInDel;
    }

    /**
     * @param snpInDel
     */
    public void setSnpInDel(SnpInDel snpInDel) {
        this.snpInDel = snpInDel;
    }

    /**
     * @return snpInDelEvidences
     */
    public List<SnpInDelEvidence> getSnpInDelEvidences() {
        return snpInDelEvidences;
    }

    /**
     * @param snpInDelEvidences
     */
    public void setSnpInDelEvidences(List<SnpInDelEvidence> snpInDelEvidences) {
        this.snpInDelEvidences = snpInDelEvidences;
    }
}
