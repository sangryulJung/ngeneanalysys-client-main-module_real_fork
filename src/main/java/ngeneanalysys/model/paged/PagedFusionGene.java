package ngeneanalysys.model.paged;

import ngeneanalysys.model.FusionGene;

import java.util.List;

/**
 * @author Jang
 * @since 2017-12-26
 */
public class PagedFusionGene {
    private Integer count;
    private List<FusionGene> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<FusionGene> getResult() {
        return result;
    }
}
