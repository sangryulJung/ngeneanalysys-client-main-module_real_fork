package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-11-29
 */
public class PagedAnalysisFile {
    private Integer count;
    private List<AnalysisFile> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<AnalysisFile> getResult() {
        return result;
    }
}
