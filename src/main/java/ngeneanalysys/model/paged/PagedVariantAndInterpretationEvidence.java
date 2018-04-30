package ngeneanalysys.model.paged;

import ngeneanalysys.model.VariantAndInterpretationEvidence;

import java.util.List;

/**
 * @author Jang
 * @since 2017-12-06
 */
public class PagedVariantAndInterpretationEvidence {
    private Integer count;

    private List<VariantAndInterpretationEvidence> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<VariantAndInterpretationEvidence> getResult() {
        return result;
    }
}
