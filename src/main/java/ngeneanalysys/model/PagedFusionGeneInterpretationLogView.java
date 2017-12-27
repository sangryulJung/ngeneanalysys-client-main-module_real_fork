package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-12-26
 */
public class PagedFusionGeneInterpretationLogView {
    private Integer count;
    private List<FusionGeneInterpretationLogView> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<FusionGeneInterpretationLogView> getResult() {
        return result;
    }
}
