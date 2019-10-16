package ngeneanalysys.model.paged;

import ngeneanalysys.model.TargetROI;

import java.util.List;

public class PagedTargetROI {
    private List<TargetROI> result;
    private Integer count;

    public List<TargetROI> getResult() {
        return result;
    }

    public Integer getCount() {
        return count;
    }
}
