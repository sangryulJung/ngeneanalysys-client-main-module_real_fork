package ngeneanalysys.code.enums;

import ngeneanalysys.code.constants.FXMLConstants;

/**
 * @author Jang
 * @since 2017-12-04
 */
public enum FusionTabMenuCode {

    FUSIONGENE("FUSION GENE", FXMLConstants.ANALYSIS_DETAIL_FUSION_GENE, 15),
    GENEEXPRESSION("GENE EXPRESSION", FXMLConstants.ANALYSIS_DETAIL_GENE_EXPRESSION, 15),
    EXONSKIPPING("EXON SKIPPING", FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LOW_CONFIDENCE, 15);

    private String menuName;
    private String fxmlPath;
    private Integer authBit;

    FusionTabMenuCode(String menu, String fxml, Integer auth) {
        this.menuName = menu;
        this.fxmlPath = fxml;
        this.authBit = auth;
    }

    /**
     * @return the menuName
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * @return the authBit
     */
    public Integer getAuthBit() {
        return authBit;
    }

    /**
     * @return the fxmlPath
     */
    public String getFxmlPath() {
        return fxmlPath;
    }
}
