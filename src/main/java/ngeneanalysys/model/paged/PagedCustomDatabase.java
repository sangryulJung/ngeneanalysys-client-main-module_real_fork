package ngeneanalysys.model.paged;

import ngeneanalysys.model.CustomDatabase;

import java.util.List;

/**
 * @author Jang
 * @since 2019-06-21
 */
public class PagedCustomDatabase {
    private Integer count;
    private List<CustomDatabase> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<CustomDatabase> getResult() {
        return result;
    }
}
