package ngeneanalysys.model.paged;

import ngeneanalysys.model.RunSampleView;

import java.util.List;

/**
 * @author Jang
 * @since 2018-03-28
 */
public class PagedRunSampleView {
    private Integer count;
    private List<RunSampleView> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<RunSampleView> getResult() {
        return result;
    }
}
