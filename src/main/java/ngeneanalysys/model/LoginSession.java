package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @author Jang
 * @since 2017-08-07
 */
public class LoginSession implements Serializable{
    private static final long serialVersionUID = 6956912584120897585L;

    @JsonProperty("token")
    private String token;

    @JsonProperty("tokenExpiredAt")
    private DateTime tokenExpiredAt;

    @JsonProperty("tokenCreatedAt")
    private DateTime tokenCreatedAt;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("role")
    private String role;

    @JsonProperty("name")
    private String name;

    @JsonProperty("memberGroupId")
    private Integer memberGroupId;

    private String loginId;

    /**
     * @return loginId
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * @param loginId
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return tokenExpiredAt
     */
    public DateTime getTokenExpiredAt() {
        return tokenExpiredAt;
    }

    /**
     * @param tokenExpiredAt
     */
    public void setTokenExpiredAt(DateTime tokenExpiredAt) {
        this.tokenExpiredAt = tokenExpiredAt;
    }

    /**
     * @return tokenCreatedAt
     */
    public DateTime getTokenCreatedAt() {
        return tokenCreatedAt;
    }

    /**
     * @param tokenCreatedAt
     */
    public void setTokenCreatedAt(DateTime tokenCreatedAt) {
        this.tokenCreatedAt = tokenCreatedAt;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return memberGroupId
     */
    public Integer getMemberGroupId() {
        return memberGroupId;
    }

    /**
     * @param memberGroupId
     */
    public void setMemberGroupId(Integer memberGroupId) {
        this.memberGroupId = memberGroupId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
