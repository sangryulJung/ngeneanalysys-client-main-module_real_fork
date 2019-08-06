package ngeneanalysys.model;

import org.joda.time.DateTime;

public class SystemLogView {
    private Integer id;
    private Integer memberId;
    private String loginId;
    private String memberName;
    private String memberGroupName;
    private String memberRole;
    private String logType;
    private String logMessage;
    private DateTime createdAt;

    public Integer getId() {
        return id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberGroupName() {
        return memberGroupName;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public String getLogType() {
        return logType;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
