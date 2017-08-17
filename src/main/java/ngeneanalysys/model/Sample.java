package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-08-11
 */
public class Sample {
    private String sampleID;
    private String sampleName;
    private String samplePlate;
    private String sampleWell;
    private String i7IndexID;
    private String index;
    private String sampleProject;
    private String description;

    public Sample() {    }

    public Sample(String sampleID, String sampleName, String samplePlate, String sampleWell, String i7IndexID, String index, String sampleProject, String description) {
        this.sampleID = sampleID;
        this.sampleName = sampleName;
        this.samplePlate = samplePlate;
        this.sampleWell = sampleWell;
        this.i7IndexID = i7IndexID;
        this.index = index;
        this.sampleProject = sampleProject;
        this.description = description;
    }

    /**
     * @return sampleID
     */
    public String getSampleID() {
        return sampleID;
    }

    /**
     * @param sampleID
     */
    public void setSampleID(String sampleID) {
        this.sampleID = sampleID;
    }

    /**
     * @return sampleName
     */
    public String getSampleName() {
        return sampleName;
    }

    /**
     * @param sampleName
     */
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    /**
     * @return samplePlate
     */
    public String getSamplePlate() {
        return samplePlate;
    }

    /**
     * @param samplePlate
     */
    public void setSamplePlate(String samplePlate) {
        this.samplePlate = samplePlate;
    }

    /**
     * @return sampleWell
     */
    public String getSampleWell() {
        return sampleWell;
    }

    /**
     * @param sampleWell
     */
    public void setSampleWell(String sampleWell) {
        this.sampleWell = sampleWell;
    }

    /**
     * @return i7IndexID
     */
    public String getI7IndexID() {
        return i7IndexID;
    }

    /**
     * @param i7IndexID
     */
    public void setI7IndexID(String i7IndexID) {
        this.i7IndexID = i7IndexID;
    }

    /**
     * @return index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return sampleProject
     */
    public String getSampleProject() {
        return sampleProject;
    }

    /**
     * @param sampleProject
     */
    public void setSampleProject(String sampleProject) {
        this.sampleProject = sampleProject;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
