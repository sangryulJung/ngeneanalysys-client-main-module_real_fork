package ngeneanalysys.model.paged;

import ngeneanalysys.model.NoticeView;

import java.util.List;

/**
 * @author Jang
 * @since 2018-03-22
 */
public class PagedNotice {
    private Integer count;
    private List<NoticeView> result;

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @return result
     */
    public List<NoticeView> getResult() {
        return result;
    }
}
