package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-03-22
 */
public class NoticeView extends Notice {
    private String loginId;

    private String memberName;

    /**
     * @return memberName
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * @return loginId
     */
    public String getLoginId() {
        return loginId;
    }
}
