package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-11-12
 */
public class BrcaCnvResult {
    private String gene;
    private String cnv;
    private String result;

    public BrcaCnvResult(String gene, String cnv, String result) {
        this.gene = gene;
        this.cnv = cnv;
        this.result = result;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return cnv
     */
    public String getCnv() {
        return cnv;
    }

    /**
     * @return result
     */
    public String getResult() {
        return result;
    }
}
