package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-03-08
 */
public class ServerFile {
    private String name;
    private Boolean isFile;
    private Boolean isEmpty;

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return isFile
     */
    public Boolean getIsFile() {
        return isFile;
    }

    /**
     * @return isEmpty
     */
    public Boolean getIsEmpty() {
        return isEmpty;
    }

    @Override
    public String toString() {
        return name;
    }
}
