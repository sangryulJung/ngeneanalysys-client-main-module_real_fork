package ngeneanalysys.model.paged;

import ngeneanalysys.model.FusionGeneView;

import java.util.List;

/**
 * @author Jang
 * @since 2017-12-26
 */
public class PagedFusionGene {
    private Integer count;
    private List<FusionGeneView> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<FusionGeneView> getResult() {
        return result;
    }
}
