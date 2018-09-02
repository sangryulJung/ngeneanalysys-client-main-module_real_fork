package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-30
 */
public class HighConfidenceSpliceVariant {
    private Integer sampleId;
    private String gene;
    private String affectedExons;
    private String transcript;
    private String breakpointStart;
    private String breakpointEnd;
    private String spliceSupportingReads;
    private String referenceReads;
    private String score;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return affectedExons
     */
    public String getAffectedExons() {
        return affectedExons;
    }

    /**
     * @return transcript
     */
    public String getTranscript() {
        return transcript;
    }

    /**
     * @return breakpointStart
     */
    public String getBreakpointStart() {
        return breakpointStart;
    }

    /**
     * @return breakpointEnd
     */
    public String getBreakpointEnd() {
        return breakpointEnd;
    }

    /**
     * @return spliceSupportingReads
     */
    public String getSpliceSupportingReads() {
        return spliceSupportingReads;
    }

    /**
     * @return referenceReads
     */
    public String getReferenceReads() {
        return referenceReads;
    }

    /**
     * @return score
     */
    public String getScore() {
        return score;
    }
}
