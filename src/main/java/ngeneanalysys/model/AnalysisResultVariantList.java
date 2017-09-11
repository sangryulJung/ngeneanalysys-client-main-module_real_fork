package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class AnalysisResultVariantList {
    private Integer count;
    private List<AnalysisResultVariant> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * @return result
     */
    public List<AnalysisResultVariant> getResult() {
        return result;
    }

    /**
     * @param result
     */
    public void setResult(List<AnalysisResultVariant> result) {
        this.result = result;
    }
}
