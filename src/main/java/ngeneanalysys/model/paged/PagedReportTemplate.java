package ngeneanalysys.model.paged;

import ngeneanalysys.model.ReportTemplate;

import java.util.List;

/**
 * @author Jang
 * @since 2017-11-14
 */
public class PagedReportTemplate {
    Integer count;
    private List<ReportTemplate> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<ReportTemplate> getResult() {
        return result;
    }
}
