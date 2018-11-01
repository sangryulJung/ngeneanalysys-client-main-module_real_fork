package ngeneanalysys.model.paged;

import ngeneanalysys.model.BrcaCNVAmplicon;

import java.util.List;

/**
 * @author Jang
 * @since 2018-10-23
 */
public class PagedBrcaCNV {
    private Integer count;
    private List<BrcaCNVAmplicon> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<BrcaCNVAmplicon> getResult() {
        return result;
    }
}
