package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-11-14
 */
public class ReportTemplate {

    private Integer id;
    private String name;
    private String contents;
    private String customFields;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;

    /**
     * @return customFields
     */
    public String getCustomFields() {
        return customFields;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @return deletedAt
     */
    public DateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * @return deleted
     */
    public Integer getDeleted() {
        return deleted;
    }

    /*
    * def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def contents = column[String]("contents")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Option[Timestamp]]("updated_at")
  def deletedAt = column[Option[Timestamp]]("deleted_at")
  def deleted = column[Int]("deleted", O.Default(0))
    * */

}
