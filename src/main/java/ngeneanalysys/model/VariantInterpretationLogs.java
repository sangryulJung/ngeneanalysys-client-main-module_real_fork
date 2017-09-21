package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-09-20
 */
public class VariantInterpretationLogs {
    private Integer id;
    private Integer variant_id;
    private Integer member_id;
    private String interpretation_type;
    private String old_value;
    private String new_value;
    private String comment;
    private DateTime created_at;

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
     * @return variant_id
     */
    public Integer getVariant_id() {
        return variant_id;
    }

    /**
     * @param variant_id
     */
    public void setVariant_id(Integer variant_id) {
        this.variant_id = variant_id;
    }

    /**
     * @return member_id
     */
    public Integer getMember_id() {
        return member_id;
    }

    /**
     * @param member_id
     */
    public void setMember_id(Integer member_id) {
        this.member_id = member_id;
    }

    /**
     * @return interpretation_type
     */
    public String getInterpretation_type() {
        return interpretation_type;
    }

    /**
     * @param interpretation_type
     */
    public void setInterpretation_type(String interpretation_type) {
        this.interpretation_type = interpretation_type;
    }

    /**
     * @return old_value
     */
    public String getOld_value() {
        return old_value;
    }

    /**
     * @param old_value
     */
    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    /**
     * @return new_value
     */
    public String getNew_value() {
        return new_value;
    }

    /**
     * @param new_value
     */
    public void setNew_value(String new_value) {
        this.new_value = new_value;
    }

    /**
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return created_at
     */
    public DateTime getCreated_at() {
        return created_at;
    }

    /**
     * @param created_at
     */
    public void setCreated_at(DateTime created_at) {
        this.created_at = created_at;
    }
}
