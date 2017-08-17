package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PagedRun {
    @JsonProperty("count")
    private  Integer count;
    @JsonProperty("result")
    private List<Run> result;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Run> getResult() {
        return result;
    }
}
