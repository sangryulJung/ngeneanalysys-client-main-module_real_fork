package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-08-17
 */
public class QCData {

    private String dnaQC;

    private String libraryQC;

    private String seqClusterDensity;

    private String seqQ30;

    private String seqClusterPF;

    private String seqIndexingPFCV;

    /**
     * @return dnaQC
     */
    public String getDnaQC() {
        return dnaQC;
    }

    /**
     * @param dnaQC
     */
    public void setDnaQC(String dnaQC) {
        this.dnaQC = dnaQC;
    }

    /**
     * @return libraryQC
     */
    public String getLibraryQC() {
        return libraryQC;
    }

    /**
     * @param libraryQC
     */
    public void setLibraryQC(String libraryQC) {
        this.libraryQC = libraryQC;
    }

    /**
     * @return seqClusterDensity
     */
    public String getSeqClusterDensity() {
        return seqClusterDensity;
    }

    /**
     * @param seqClusterDensity
     */
    public void setSeqClusterDensity(String seqClusterDensity) {
        this.seqClusterDensity = seqClusterDensity;
    }

    /**
     * @return seqQ30
     */
    public String getSeqQ30() {
        return seqQ30;
    }

    /**
     * @param seqQ30
     */
    public void setSeqQ30(String seqQ30) {
        this.seqQ30 = seqQ30;
    }

    /**
     * @return seqClusterPF
     */
    public String getSeqClusterPF() {
        return seqClusterPF;
    }

    /**
     * @param seqClusterPF
     */
    public void setSeqClusterPF(String seqClusterPF) {
        this.seqClusterPF = seqClusterPF;
    }

    /**
     * @return seqIndexingPFCV
     */
    public String getSeqIndexingPFCV() {
        return seqIndexingPFCV;
    }

    /**
     * @param seqIndexingPFCV
     */
    public void setSeqIndexingPFCV(String seqIndexingPFCV) {
        this.seqIndexingPFCV = seqIndexingPFCV;
    }
}
