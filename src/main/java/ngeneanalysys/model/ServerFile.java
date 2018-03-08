package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-03-08
 */
public class ServerFile {
    private String name;
    private String isFile;
    private String isEmpty;

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return isFile
     */
    public String getIsFile() {
        return isFile;
    }

    /**
     * @return isEmpty
     */
    public String getIsEmpty() {
        return isEmpty;
    }

    @Override
    public String toString() {
        return name;
    }
}
