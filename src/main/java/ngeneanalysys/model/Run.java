package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;

public class Run implements Serializable {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("memberId")
    private Integer memberId;

    @JsonProperty(value = "memberGroupId")
    private Integer memberGroupId;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "sequencingPlatform")
    private String sequencingPlatform;

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "statusMsg")
   private String statusMsg;

    @JsonProperty(value = "createdAt")
    private Timestamp createdAt;

    @JsonProperty(value = "startedAt")
    private Timestamp startedAt;

    @JsonProperty(value = "completedAt")
    private Timestamp completedAt;

    @JsonProperty(value = "updatedAt")
    private Timestamp updatedAt;

    @JsonProperty(value = "deletedAt")
    private Timestamp deletedAt;

    @JsonProperty(value = "deleted")
    private Integer deleted;

    public Integer getId() {
        return id;
    }

    public Integer getMemberGroupId() {
        return memberGroupId;
    }

    public String getName() {
        return name;
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

    public String getStatus() {
        return status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Timestamp getDeletedAt() {
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
                ", memberGroupId=" + memberGroupId +
                ", name='" + name + '\'' +
                ", sequencingPlatform='" + sequencingPlatform + '\'' +
                ", status='" + status + '\'' +
                ", statusMsg='" + statusMsg + '\'' +
                ", createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                '}';
    }
}
