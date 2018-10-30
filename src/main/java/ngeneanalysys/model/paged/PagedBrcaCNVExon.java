package ngeneanalysys.model.paged;

import ngeneanalysys.model.BrcaCNVExon;

import java.util.List;

/**
 * @author Jang
 * @since 2018-10-30
 */
public class PagedBrcaCNVExon {
    private Integer count;
    private List<BrcaCNVExon> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<BrcaCNVExon> getResult() {
        return result;
    }
}
