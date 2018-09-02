package ngeneanalysys.model.paged;

import ngeneanalysys.model.TSTCNV;

import java.util.List;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class PagedTSTCNV {

    private Integer count;
    private List<TSTCNV> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<TSTCNV> getResult() {
        return result;
    }
}
