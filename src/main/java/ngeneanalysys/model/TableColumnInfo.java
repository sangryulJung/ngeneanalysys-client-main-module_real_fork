package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-08-16
 */
public class TableColumnInfo {
    private Integer order;
    private String columnName;
    private boolean visible;

    public TableColumnInfo(Integer order, String columnName, boolean visible) {
        this.order = order;
        this.columnName = columnName;
        this.visible = visible;
    }

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
