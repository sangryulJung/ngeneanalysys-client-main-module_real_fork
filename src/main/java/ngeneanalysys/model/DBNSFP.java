package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-12-28
 */
public class DBNSFP {
    private Sift sift;
    private MutationTaster mutationTaster;
    private GERP gerp;
    private LRT lrt;
    private FATHMM fathmm;
    private MutationAssessor mutationAssessor;

    /**
     * @return mutationAssessor
     */
    public MutationAssessor getMutationAssessor() {
        return mutationAssessor;
    }

    /**
     * @return fathmm
     */
    public FATHMM getFathmm() {
        return fathmm;
    }

    /**
     * @return sift
     */
    public Sift getSift() {
        return sift;
    }

    /**
     * @return mutationTaster
     */
    public MutationTaster getMutationTaster() {
        return mutationTaster;
    }

    /**
     * @return gerp
     */
    public GERP getGerp() {
        return gerp;
    }

    /**
     * @return lrt
     */
    public LRT getLrt() {
        return lrt;
    }
}
