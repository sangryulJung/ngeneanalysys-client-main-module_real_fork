package ngeneanalysys.model;

import java.util.List;

public class PagedSample {
    private  Integer count;
    private List<Sample> result;
    private SampleAnalysisJobCount sampleAnalysisJobCount;

    public Integer getCount() {
        return count;
    }
    public List<Sample> getResult() {
        return result;
    }

    public SampleAnalysisJobCount getSampleAnalysisJobCount() {
        return sampleAnalysisJobCount;
    }

    @Override
    public String toString() {
        return "PagedSample{" +
                "count=" + count +
                ", result=" + result +
                ", sampleAnalysisJobCount=" + sampleAnalysisJobCount +
                '}';
    }
}
