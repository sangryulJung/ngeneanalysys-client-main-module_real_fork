package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2018-01-15
 */
public class PagedExonSkip {
    private Integer count;
    private List<ExonSkip> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<ExonSkip> getResult() {
        return result;
    }
}
