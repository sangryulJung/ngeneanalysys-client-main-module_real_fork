package ngeneanalysys.model.paged;

import ngeneanalysys.model.GeneExpression;

import java.util.List;

/**
 * @author Jang
 * @since 2018-01-12
 */
public class PagedGeneExpression {
    private Integer count;
    private List<GeneExpression> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<GeneExpression> getResult() {
        return result;
    }
}
