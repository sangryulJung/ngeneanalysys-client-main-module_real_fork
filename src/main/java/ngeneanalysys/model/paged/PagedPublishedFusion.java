package ngeneanalysys.model.paged;

import ngeneanalysys.model.PublishedFusion;

import java.util.List;

/**
 * @author Jang
 * @since 2018-04-30
 */
public class PagedPublishedFusion {
    private Integer count;
    private List<PublishedFusion> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<PublishedFusion> getResult() {
        return result;
    }
}
