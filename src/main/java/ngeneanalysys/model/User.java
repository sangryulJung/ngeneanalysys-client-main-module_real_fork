package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @author Jang
 * @since 2017-08-07
 */
public class User implements Serializable {
    private static final long serialVersionUID = -7540753200161977775L;

    /** 사용자 시스템 아이디 */
    @JsonProperty("id")
    private Integer id;
    /** 아이디 */
    @JsonProperty("login_id")
    private String loginId;
    /** 비밀번호 */
    @JsonProperty("login_password")
    private String loginPassword;
    /** 사용자명 */
    @JsonProperty("name")
    private String name;
    /** 사용자 구분 [리뷰어, 분석자] */
    @JsonProperty("user_type")
    private String userType;
    /** 사용자 그룹 고유번호 */
    @JsonProperty("user_group")
    private Integer userGroup;
    /** organization */
    @JsonProperty("organization")
    private String organization;
    /** department */
    @JsonProperty("department")
    private String department;
    /** address */
    @JsonProperty("address")
    private String address;
    /** phone */
    @JsonProperty("phone")
    private String phone;
    /** email */
    @JsonProperty("email")
    private String email;
    /** 생성일시 */
    @JsonProperty("created_at")
    private DateTime createdAt;
    /** 수정일시 */
    @JsonProperty("updated_at")
    private DateTime updatedAt;
    /** 삭제일시 */
    @JsonProperty("deleted_at")
    private DateTime deletedAt;
    /** 삭제여부 [0:미삭제, 1:삭제] */
    @JsonProperty("deleted")
    private Integer deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Integer userGroup) {
        this.userGroup = userGroup;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(DateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
