package ngeneanalysys.model;

import java.util.List;

public class PagedSystemLogView {
    private Integer count;
    private List<SystemLogView> result;

    public Integer getCount() {
        return count;
    }

    public List<SystemLogView> getResult() {
        return result;
    }
}
