package ngeneanalysys.model.paged;

import ngeneanalysys.model.TSTFusion;

import java.util.List;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class PagedTSTFusion {
    private Integer count;
    private List<TSTFusion> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<TSTFusion> getResult() {
        return result;
    }
}
