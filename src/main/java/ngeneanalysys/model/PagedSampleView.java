package ngeneanalysys.model;

import java.util.List;

public class PagedSampleView {
    private Integer count;
    private List<SampleView> result;

    public Integer getCount() {
        return count;
    }

    public List<SampleView> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "PagedSampleView{" +
                "count=" + count +
                ", result=" + result +
                '}';
    }
}
