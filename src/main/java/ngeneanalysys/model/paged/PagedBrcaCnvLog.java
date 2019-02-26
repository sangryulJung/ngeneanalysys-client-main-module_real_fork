package ngeneanalysys.model.paged;

import ngeneanalysys.model.BrcaCnvLog;

import java.util.List;

/**
 * @author Jang
 * @since 2019-02-25
 */
public class PagedBrcaCnvLog {
    Integer count;
    List<BrcaCnvLog> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<BrcaCnvLog> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "PagedBrcaCnvLog{" +
                "count=" + count +
                ", result=" + result +
                '}';
    }
}
