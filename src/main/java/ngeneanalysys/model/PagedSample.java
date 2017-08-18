package ngeneanalysys.model;

import java.util.List;

public class PagedSample {
    private  Integer count;
    private List<Sample> result;

    public Integer getCount() {
        return count;
    }
    public List<Sample> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "PagedSample{" +
                "count=" + count +
                ", result=" + result +
                '}';
    }
}
