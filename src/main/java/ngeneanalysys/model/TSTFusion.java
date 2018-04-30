package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class TSTFusion {
    private Integer sampleId;
    private String caller;
    private String geneA;
    private String geneB;
    private String geneABreakpoint;
    private String geneBBreakpoint;
    private String score;
    private String filter;
    private String preciseImprecise;
    private String intragenicCall;
    private String refASplit;
    private String refAPair;
    private String refBSplit;
    private String refBPair;
    private String altSplit;
    private String altPair;
    private String candidateAlt;
    private String contig;
    private String contigAlign1;
    private String contigAlign2;
    private String keepFusion;


    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return caller
     */
    public String getCaller() {
        return caller;
    }

    /**
     * @return geneA
     */
    public String getGeneA() {
        return geneA;
    }

    /**
     * @return geneB
     */
    public String getGeneB() {
        return geneB;
    }

    /**
     * @return geneABreakpoint
     */
    public String getGeneABreakpoint() {
        return geneABreakpoint;
    }

    /**
     * @return geneBBreakpoint
     */
    public String getGeneBBreakpoint() {
        return geneBBreakpoint;
    }

    /**
     * @return score
     */
    public String getScore() {
        return score;
    }

    /**
     * @return filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @return preciseImprecise
     */
    public String getPreciseImprecise() {
        return preciseImprecise;
    }

    /**
     * @return intragenicCall
     */
    public String getIntragenicCall() {
        return intragenicCall;
    }

    /**
     * @return refASplit
     */
    public String getRefASplit() {
        return refASplit;
    }

    /**
     * @return refAPair
     */
    public String getRefAPair() {
        return refAPair;
    }

    /**
     * @return refBSplit
     */
    public String getRefBSplit() {
        return refBSplit;
    }

    /**
     * @return refBPair
     */
    public String getRefBPair() {
        return refBPair;
    }

    /**
     * @return altSplit
     */
    public String getAltSplit() {
        return altSplit;
    }

    /**
     * @return altPair
     */
    public String getAltPair() {
        return altPair;
    }

    /**
     * @return candidateAlt
     */
    public String getCandidateAlt() {
        return candidateAlt;
    }

    /**
     * @return contig
     */
    public String getContig() {
        return contig;
    }

    /**
     * @return contigAlign1
     */
    public String getContigAlign1() {
        return contigAlign1;
    }

    /**
     * @return contigAlign2
     */
    public String getContigAlign2() {
        return contigAlign2;
    }

    /**
     * @return keepFusion
     */
    public String getKeepFusion() {
        return keepFusion;
    }
}
