package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-09-20
 */
public class SnpInDelInterpretationLogsList {
    private Integer count;
    private List<SnpInDelInterpretationLogs> result;

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
    public List<SnpInDelInterpretationLogs> getResult() {
        return result;
    }

    /**
     * @param result
     */
    public void setResult(List<SnpInDelInterpretationLogs> result) {
        this.result = result;
    }
}
