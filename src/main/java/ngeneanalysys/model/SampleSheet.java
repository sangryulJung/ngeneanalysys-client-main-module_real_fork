package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-08-11
 */
public class SampleSheet {
    private String sampleId;
    private String sampleName;
    private String samplePlate;
    private String sampleWell;
    private String i7IndexId;
    private String sampleIndex;
    private String sampleProject;
    private String description;

    public SampleSheet() {    }

    public SampleSheet(String sampleId, String sampleName, String samplePlate, String sampleWell, String i7IndexId, String sampleIndex, String sampleProject, String description) {
        this.sampleId = sampleId;

        this.sampleName = sampleName;
        this.samplePlate = samplePlate;
        this.sampleWell = sampleWell;
        this.i7IndexId = i7IndexId;
        this.sampleIndex = sampleIndex;
        this.sampleProject = sampleProject;
        this.description = description;
    }

    /**
     * @return sampleId
     */
    public String getSampleId() {
        return sampleId;
    }

    /**
     * @param sampleId
     */
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
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
     * @return i7IndexId
     */
    public String getI7IndexId() {
        return i7IndexId;
    }

    /**
     * @param i7IndexId
     */
    public void setI7IndexId(String i7IndexId) {
        this.i7IndexId = i7IndexId;
    }

    /**
     * @return sampleIndex
     */
    public String getSampleIndex() {
        return sampleIndex;
    }

    /**
     * @param sampleIndex
     */
    public void setSampleIndex(String sampleIndex) {
        this.sampleIndex = sampleIndex;
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

    @Override
    public String toString() {
        return "SampleSheet{" +
                "sampleId='" + sampleId + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", samplePlate='" + samplePlate + '\'' +
                ", sampleWell='" + sampleWell + '\'' +
                ", i7IndexId='" + i7IndexId + '\'' +
                ", index='" + sampleIndex + '\'' +
                ", sampleProject='" + sampleProject + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
