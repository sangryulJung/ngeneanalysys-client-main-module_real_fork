package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Panel {
    private Integer id;
    private String name;
    private String code;
    private String panelType;
    private Integer deleted;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPanelType() {
        return panelType;
    }

    public String getCode() {
        return code;
    }
    @JsonIgnore
    public Integer getDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return "Panel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", panelType='" + panelType + '\'' +
                '}';
    }
}
