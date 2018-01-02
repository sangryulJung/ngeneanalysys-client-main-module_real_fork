package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-01-02
 */
public class DbGenomicCoordinateClinicalVariant {
    private String dbChr;
    private String dbGene;
    private Integer dbPosition;
    private String dbRef;
    private String dbAlt;

    /**
     * @param dbChr
     */
    public void setDbChr(String dbChr) {
        this.dbChr = dbChr;
    }

    /**
     * @param dbGene
     */
    public void setDbGene(String dbGene) {
        this.dbGene = dbGene;
    }

    /**
     * @param dbPosition
     */
    public void setDbPosition(Integer dbPosition) {
        this.dbPosition = dbPosition;
    }

    /**
     * @param dbRef
     */
    public void setDbRef(String dbRef) {
        this.dbRef = dbRef;
    }

    /**
     * @param dbAlt
     */
    public void setDbAlt(String dbAlt) {
        this.dbAlt = dbAlt;
    }

    /**
     * @return dbChr
     */
    public String getDbChr() {
        return dbChr;
    }

    /**
     * @return dbGene
     */
    public String getDbGene() {
        return dbGene;
    }

    /**
     * @return dbPosition
     */
    public Integer getDbPosition() {
        return dbPosition;
    }

    /**
     * @return dbRef
     */
    public String getDbRef() {
        return dbRef;
    }

    /**
     * @return dbAlt
     */
    public String getDbAlt() {
        return dbAlt;
    }
}
