package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class AnalysisDetailSNPsINDELsLowConfidenceController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;


    /** low Variant Coverage Depth */
    @FXML
    private Label lowVariantCoverageDepthLabel;
    /** low Variant Fraction */
    @FXML
    private Label lowVariantFractionLabel;
    /** homopolymer Region */
    @FXML
    private Label homopolymerRegionLabel;
    /** soft Clipped Amplicon */
    @FXML
    private Label softClippedAmpliconLabel;
    /** primer Region Deletion */
    @FXML
    private Label primerRegionDeletionLabel;


    /**
     * @return the analysisDetailSNPsINDELsController
     */
    public AnalysisDetailSNPsINDELsController getAnalysisDetailSNPsINDELsController() {
        return analysisDetailSNPsINDELsController;
    }
    /**
     * @param analysisDetailSNPsINDELsController the analysisDetailSNPsINDELsController to set
     */
    public void setAnalysisDetailSNPsINDELsController(
            AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController) {
        this.analysisDetailSNPsINDELsController = analysisDetailSNPsINDELsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        String na = "N/A";
        if(getParamMap() != null && !getParamMap().isEmpty() && getParamMap().size() > 0) {
            String lowVariantCoverageDepth = (String) getParamMap().get("low_variant_coverage_depth");
            String lowVariantFraction = (String) getParamMap().get("low_variant_fraction");
            String softClippedAmplicon = (String) getParamMap().get("soft_clipped_amplicon");
            String homopolymerRegion = (String) getParamMap().get("homopolymer_region");
            String primerRegionDeletion = (String) getParamMap().get("primer_deletion");

            if(!StringUtils.isEmpty(lowVariantCoverageDepth)) {
                lowVariantCoverageDepthLabel.setText(lowVariantCoverageDepth.toUpperCase());
                if(lowVariantCoverageDepth.toUpperCase().equals("YES")) {
                    lowVariantCoverageDepthLabel.getStyleClass().add("txt_green");
                } else {
                    lowVariantCoverageDepthLabel.getStyleClass().add("txt_red");
                }
            } else {
                lowVariantCoverageDepthLabel.setText(na);
            }

            if(!StringUtils.isEmpty(lowVariantFraction)) {
                lowVariantFractionLabel.setText(lowVariantFraction.toUpperCase());
                if(lowVariantFraction.toUpperCase().equals("YES")) {
                    lowVariantFractionLabel.getStyleClass().add("txt_green");
                } else {
                    lowVariantFractionLabel.getStyleClass().add("txt_red");
                }
            } else {
                lowVariantFractionLabel.setText(na);
            }

            if(!StringUtils.isEmpty(softClippedAmplicon)) {
                softClippedAmpliconLabel.setText(softClippedAmplicon.toUpperCase());
                if(softClippedAmplicon.toUpperCase().equals("YES")) {
                    softClippedAmpliconLabel.getStyleClass().add("txt_green");
                } else {
                    softClippedAmpliconLabel.getStyleClass().add("txt_red");
                }
            } else {
                softClippedAmpliconLabel.setText(na);
            }

            if(!StringUtils.isEmpty(homopolymerRegion)) {
                homopolymerRegionLabel.setText(homopolymerRegion.toUpperCase());
                if(homopolymerRegion.toUpperCase().equals("YES")) {
                    homopolymerRegionLabel.getStyleClass().add("txt_green");
                } else {
                    homopolymerRegionLabel.getStyleClass().add("txt_red");
                }
            } else {
                homopolymerRegionLabel.setText(na);
            }

            if(!StringUtils.isEmpty(primerRegionDeletion)) {
                primerRegionDeletionLabel.setText(primerRegionDeletion.toUpperCase());
                if(primerRegionDeletion.toUpperCase().equals("YES")) {
                    primerRegionDeletionLabel.getStyleClass().add("txt_green");
                } else {
                    primerRegionDeletionLabel.getStyleClass().add("txt_red");
                }
            } else {
                primerRegionDeletionLabel.setText(na);
            }
        }

        analysisDetailSNPsINDELsController.subTabLowConfidence.setContent(root);
    }
}
