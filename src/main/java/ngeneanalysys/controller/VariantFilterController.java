package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jang
 * @since 2018-04-16
 */
public class VariantFilterController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private Stage dialogStage;

    @FXML
    private TextField geneTextField;

    @FXML
    private TextField chromosomeTextField;

    @FXML
    private CheckBox snvCheckBox;

    @FXML
    private CheckBox indelCheckBox;

    @FXML
    private CheckBox fivePrimeUTRCheckBox;

    @FXML
    private CheckBox cidCheckBox;

    @FXML
    private CheckBox didCheckBox;

    @FXML
    private CheckBox frameshiftCheckBox;

    @FXML
    private CheckBox intronCheckBox;

    @FXML
    private CheckBox missenseCheckBox;

    @FXML
    private CheckBox spliceRegionCheckbox;

    @FXML
    private CheckBox synonymousCheckBox;

    @FXML
    private CheckBox spliceAcceptorCheckBox;

    @FXML
    private CheckBox stopGainedCheckBox;

    private AnalysisDetailSNVController analysisDetailSNVController;

    /**
     * @param analysisDetailSNVController AnalysisDetailSNVController
     */
    public void setAnalysisDetailSNVController(AnalysisDetailSNVController analysisDetailSNVController) {
        this.analysisDetailSNVController = analysisDetailSNVController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        // Create the dialog Stage
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Variant Filter");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        this.dialogStage = dialogStage;

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void filterSave() {
        List<Object> list = new ArrayList<>();

        variantTabSave(list);

        consequenceSave(list);

        if(!list.isEmpty()) {
            analysisDetailSNVController.saveFilter(list);
            closeFilter();
        }
    }

    private void consequenceSave(List<Object> list) {
        if(fivePrimeUTRCheckBox.isSelected()) {
            list.add("consequence 5_prime_UTR_variant");
        }
        if(cidCheckBox.isSelected()) {
            list.add("consequence conservative_inframe_deletion");
        }
        if(didCheckBox.isSelected()) {
            list.add("consequence disruptive_inframe_deletion");
        }
        if(frameshiftCheckBox.isSelected()) {
            list.add("consequence frameshift_variant");
        }
        if(intronCheckBox.isSelected()) {
            list.add("consequence intron_variant");
        }
        if(missenseCheckBox.isSelected()) {
            list.add("consequence missense_variant");
        }
        if(spliceRegionCheckbox.isSelected()) {
            list.add("consequence splice_region_variant");
        }
        if(spliceAcceptorCheckBox.isSelected()) {
            list.add("consequence splice_acceptor_variant");
        }
        if(synonymousCheckBox.isSelected()) {
            list.add("consequence synonymous_variant");
        }
        if(stopGainedCheckBox.isSelected()) {
            list.add("consequence stop_gained");
        }
    }

    private void variantTabSave(List<Object> list) {
        if(!StringUtils.isEmpty(geneTextField.getText())) {
            list.add("gene " + geneTextField.getText());
        }

        if(!StringUtils.isEmpty(chromosomeTextField.getText())) {
            list.add("chromosome " + chromosomeTextField.getText());
        }

        if(snvCheckBox.isSelected()) {
            list.add("variantType snv");
        }

        if(indelCheckBox.isSelected()) {
            list.add("variantType indel");
        }
    }

    @FXML
    public void closeFilter() {
        dialogStage.close();
    }
}
