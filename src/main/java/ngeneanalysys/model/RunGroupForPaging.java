package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class RunGroupForPaging extends CommonPagination implements Serializable {
    private static final long serialVersionUID = -1285015411935459072L;

    /** 목록 */
    @JsonProperty("results")
    private List<Run> list;

    /**
     * @return the list
     */
    public List<Run> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<Run> list) {
        this.list = list;
    }
}
