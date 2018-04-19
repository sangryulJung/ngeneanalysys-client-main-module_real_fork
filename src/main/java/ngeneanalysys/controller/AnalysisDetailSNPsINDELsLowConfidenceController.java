package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class AnalysisDetailSNPsINDELsLowConfidenceController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;

    @FXML
    private VBox vbox;

    /** low Variant Coverage Depth *//*
    @FXML
    private Label lowVariantCoverageDepthLabel;
    *//** low Variant Fraction *//*
    @FXML
    private Label lowVariantFractionLabel;
    *//** homopolymer Region *//*
    @FXML
    private Label homopolymerRegionLabel;
    *//** soft Clipped Amplicon *//*
    @FXML
    private Label softClippedAmpliconLabel;
    *//** primer Region Deletion *//*
    @FXML
    private Label primerRegionDeletionLabel;*/


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
        logger.debug("show..");
        String na = "N/A";

        if (getParamMap() != null && !getParamMap().isEmpty() && getParamMap().size() > 0) {
            Set<String> keySet = getParamMap().keySet();

            for(String key : keySet) {

                //if(key.equalsIgnoreCase("WARNING")) continue;

                if (key.equalsIgnoreCase("low_variant_coverage_depth") ||
                        key.equalsIgnoreCase("low_variant_fraction") ||
                        key.equalsIgnoreCase("homopolymer_region") ||
                        key.equalsIgnoreCase("soft_clipped_amplicon") ||
                        key.equalsIgnoreCase("primer_deletion") ||
                        key.equalsIgnoreCase("low_read_depth") ||
                        key.equalsIgnoreCase("low_allele_fraction") ||
                        key.equalsIgnoreCase("low_confidence")) {

                    HBox box = new HBox();

                    Label titleLabel = new Label();

                    titleLabel.setText(WordUtils.capitalize(key.replaceAll("_", " ")) + " : ");
                    titleLabel.getStyleClass().add("font_size_12");

                    String warningString = (String) getParamMap().get(key);
                    Label warningLabel = new Label();
                    warningLabel.getStyleClass().add("font_size_12");
                    warningLabel.setText(warningString.toUpperCase());

                    if (!StringUtils.isEmpty(warningString)) {
                        if (warningString.equalsIgnoreCase("YES")) {
                            warningLabel.getStyleClass().add("txt_green");
                        } else {
                            warningLabel.getStyleClass().add("txt_red");
                        }
                    } else {
                        warningLabel.setText(na);
                    }

                    box.getChildren().add(titleLabel);
                    box.getChildren().add(warningLabel);

                    vbox.getChildren().add(box);
                }
            }
        }

        /*if(getParamMap() != null && !getParamMap().isEmpty() && getParamMap().size() > 0) {
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
        }*/

        analysisDetailSNPsINDELsController.subTabLowConfidence.setContent(root);
    }
}
