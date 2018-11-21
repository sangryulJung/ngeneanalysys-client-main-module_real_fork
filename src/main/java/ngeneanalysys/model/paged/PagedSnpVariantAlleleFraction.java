package ngeneanalysys.model.paged;

import ngeneanalysys.model.SnpVariantAlleleFraction;

import java.util.List;

/**
 * @author Jang
 * @since 2018-11-20
 */
public class PagedSnpVariantAlleleFraction {
    private Integer count;
    private List<SnpVariantAlleleFraction> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<SnpVariantAlleleFraction> getResult() {
        return result;
    }
}
