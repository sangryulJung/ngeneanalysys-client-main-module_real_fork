package ngeneanalysys.model.paged;

import ngeneanalysys.model.SampleAnalysisJobCount;
import ngeneanalysys.model.SampleView;

import java.util.List;

public class PagedSample {
    private  Integer count;
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
        return "PagedSample{" +
                "count=" + count +
                ", result=" + result +
                ", sampleAnalysisJobCount=" + sampleAnalysisJobCount +
                '}';
    }
}
