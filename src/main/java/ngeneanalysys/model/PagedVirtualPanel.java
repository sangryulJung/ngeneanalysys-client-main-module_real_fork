package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2018-01-08
 */
public class PagedVirtualPanel {

    private Integer count;
    private List<VirtualPanel> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<VirtualPanel> getResult() {
        return result;
    }
}
