package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PagedRun {
    private  Integer count;
    private List<Run> result;

    public Integer getCount() {
        return count;
    }
    public List<Run> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "PagedRun{" +
                "count=" + count +
                ", result=" + result +
                '}';
    }
}
