package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2018-01-08
 */
public class PagedPanelView {
    private Integer count;
    private List<PanelView> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<PanelView> getResult() {
        return result;
    }
}
