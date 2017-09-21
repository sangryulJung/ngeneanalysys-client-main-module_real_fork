package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-09-20
 */
public class VariantInterpretationLogsList {
    private Integer count;
    private List<VariantInterpretationLogs> result;

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
    public List<VariantInterpretationLogs> getResult() {
        return result;
    }

    /**
     * @param result
     */
    public void setResult(List<VariantInterpretationLogs> result) {
        this.result = result;
    }
}
