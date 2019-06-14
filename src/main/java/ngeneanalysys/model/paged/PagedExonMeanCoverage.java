package ngeneanalysys.model.paged;

import ngeneanalysys.model.ExonMeanCoverage;

import java.util.List;

/**
 * @author Jang
 * @since 2019-06-13
 */
public class PagedExonMeanCoverage {
    private Integer count;
    private List<ExonMeanCoverage> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<ExonMeanCoverage> getResult() {
        return result;
    }
}
