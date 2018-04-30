package ngeneanalysys.model.paged;

import ngeneanalysys.model.GenomicCoordinateClinicalVariant;

import java.util.List;

/**
 * @author Jang
 * @since 2018-01-03
 */
public class PagedGenomicCoordinateClinicalVariant {
    private Integer count;
    private List<GenomicCoordinateClinicalVariant> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<GenomicCoordinateClinicalVariant> getResult() {
        return result;
    }
}
