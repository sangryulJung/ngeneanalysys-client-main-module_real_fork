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
     * @param order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * @param columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @param visible
     */
    public void setVisible(boolean visible) {
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
