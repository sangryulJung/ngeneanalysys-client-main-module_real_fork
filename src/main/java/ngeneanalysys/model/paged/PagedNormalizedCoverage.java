package ngeneanalysys.model.paged;

import ngeneanalysys.model.NormalizedCoverage;

import java.util.List;

/**
 * @author Jang
 * @since 2018-11-20
 */
public class PagedNormalizedCoverage {
    private Integer count;
    private List<NormalizedCoverage> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<NormalizedCoverage> getResult() {
        return result;
    }
}
