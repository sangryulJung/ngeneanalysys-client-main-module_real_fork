package ngeneanalysys.model.paged;

import ngeneanalysys.model.Cnv;

import java.util.List;

/**
 * @author Jang
 * @since 2018-05-23
 */
public class PagedCnv {
    private Integer count;
    private List<Cnv> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<Cnv> getResult() {
        return result;
    }
}
