package ngeneanalysys.model.paged;

import ngeneanalysys.model.Panel;

import java.util.List;

/**
 * @author Jang
 * @since 2017-11-02
 */
public class PagedPanel {
    private Integer count;
    private List<Panel> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<Panel> getResult() {
        return result;
    }
}
