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

    private String name;

    private String sequencingPlatform;

    private String status;

   private String statusMsg;

    private Timestamp createdAt;

    private Timestamp startedAt;

    private Timestamp completedAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

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
