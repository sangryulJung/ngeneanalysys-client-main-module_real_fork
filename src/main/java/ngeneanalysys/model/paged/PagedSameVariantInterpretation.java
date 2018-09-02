package ngeneanalysys.model.paged;

import ngeneanalysys.model.SameVariantInterpretation;

import java.util.List;

/**
 * @author Jang
 * @since 2018-04-11
 */
public class PagedSameVariantInterpretation {
    private Integer count;
    private List<SameVariantInterpretation> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<SameVariantInterpretation> getResult() {
        return result;
    }
}
