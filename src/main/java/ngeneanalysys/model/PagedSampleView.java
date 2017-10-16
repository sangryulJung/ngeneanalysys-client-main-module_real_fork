package ngeneanalysys.model;

import java.util.List;

public class PagedSampleView {
    private Integer count;
    private List<SampleView> result;
    private SampleAnalysisJobCount sampleAnalysisJobCount;

    public Integer getCount() {
        return count;
    }

    public List<SampleView> getResult() {
        return result;
    }

    public SampleAnalysisJobCount getSampleAnalysisJobCount() {
        return sampleAnalysisJobCount;
    }

    @Override
    public String toString() {
        return "PagedSampleView{" +
                "count=" + count +
                ", result=" + result +
                ", sampleAnalysisJobCount=" + sampleAnalysisJobCount +
                '}';
    }
}
