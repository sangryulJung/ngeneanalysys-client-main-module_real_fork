package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-08-16
 */
public class TableColumnInfo {
    private Integer order;
    private String columnName;
    private boolean visible;

    /**
     * @return order
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * @return columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }
}
