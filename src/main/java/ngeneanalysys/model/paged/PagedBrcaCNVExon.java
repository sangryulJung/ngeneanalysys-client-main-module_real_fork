package ngeneanalysys.model.paged;

import ngeneanalysys.model.BrcaCnvExon;

import java.util.List;

/**
 * @author Jang
 * @since 2018-10-30
 */
public class PagedBrcaCNVExon {
    private Integer count;
    private List<BrcaCnvExon> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<BrcaCnvExon> getResult() {
        return result;
    }
}
