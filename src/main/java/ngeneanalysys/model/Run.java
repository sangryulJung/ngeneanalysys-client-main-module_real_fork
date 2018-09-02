package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;


public class Run{
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("memberId")
    private Integer memberId;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "sequencingPlatform")
    private String sequencingPlatform;

    @JsonProperty(value = "serverRunDir")
    private String serverRunDir;

    @JsonProperty(value = "createdAt")
    private DateTime createdAt;

    @JsonProperty(value = "startedAt")
    private DateTime startedAt;

    @JsonProperty(value = "completedAt")
    private DateTime completedAt;

    @JsonProperty(value = "updatedAt")
    private DateTime updatedAt;

    @JsonProperty(value = "deletedAt")
    private DateTime deletedAt;

    @JsonProperty(value = "deleted")
    private Integer deleted;

    @JsonProperty(value = "memberName")
    private String memberName;

    private String loginId;

    private String memberGroupName;

    private RunStatus runStatus;

    private String panelName;

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return panelName
     */
    public String getPanelName() {
        return panelName;
    }

    /**
     * @return runStatus
     */
    public RunStatus getRunStatus() {
        return runStatus;
    }

    /**
     * @return memberGroupName
     */
    public String getMemberGroupName() {
        return memberGroupName;
    }

    /**
     * @return loginId
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * @return memberName
     */
    public String getMemberName() {
        return memberName;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSequencingPlatform() {
        return sequencingPlatform;
    }

    public void setSequencingPlatform(String sequencingPlatform) {
        this.sequencingPlatform = sequencingPlatform;
    }

    public String getServerRunDir() { return serverRunDir; }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getStartedAt() {
        return startedAt;
    }

    public DateTime getCompletedAt() {
        return completedAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public DateTime getDeletedAt() {
        return deletedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return "Run{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", name='" + name + '\'' +
                ", sequencingPlatform='" + sequencingPlatform + '\'' +
                ", serverRunDir='" + serverRunDir + '\'' +
                ", createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                ", memberName='" + memberName + '\'' +
                ", loginId='" + loginId + '\'' +
                ", memberGroupName='" + memberGroupName + '\'' +
                '}';
    }
}
