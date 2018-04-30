package ngeneanalysys.model.paged;

import ngeneanalysys.model.CNV;

import java.util.List;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class PagedCNV {

    private Integer count;
    private List<CNV> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<CNV> getResult() {
        return result;
    }
}
