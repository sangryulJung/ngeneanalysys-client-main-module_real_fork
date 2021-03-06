package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2017-11-17
 */
public class ReportContents {
    private ReportTemplate reportTemplate;
    private List<ReportImage> reportImages;
    private List<ReportComponent> reportComponents;

    /**
     * @return reportComponents
     */
    public List<ReportComponent> getReportComponents() {
        return reportComponents;
    }

    /**
     * @return reportTemplate
     */
    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    /**
     * @return reportImages
     */
    public List<ReportImage> getReportImages() {
        return reportImages;
    }
}
