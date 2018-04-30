package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-02-19
 */
public class ReportComponent {
    private Integer id;
    private Integer reportTemplateId;
    private String name;
    private Long size;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return reportTemplateId
     */
    public Integer getReportTemplateId() {
        return reportTemplateId;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return size
     */
    public Long getSize() {
        return size;
    }
}
