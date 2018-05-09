package ngeneanalysys.model.paged;

import ngeneanalysys.model.SpliceVariant;

import java.util.List;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class PagedSpliceVariant {
    private Integer count;
    private List<SpliceVariant> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<SpliceVariant> getResult() {
        return result;
    }
}
