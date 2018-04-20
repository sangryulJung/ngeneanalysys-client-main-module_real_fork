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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @FXML
    private TextField endFractionTextField;

    @FXML
    private TextField startFractionTextField;

    @FXML
    private CheckBox cosmicidCheckBox;

    @FXML
    private CheckBox dbSNPidCheckBox;


    private List<Object> currentFilter;

    private String currentFilerName;

    /**
     * @param currentFilter List<Object>
     */
    public void setCurrentFilter(List<Object> currentFilter) {
        this.currentFilter = currentFilter;
    }

    /**
     * @param currentFilerName String
     */
    public void setCurrentFilerName(String currentFilerName) {
        this.currentFilerName = currentFilerName;
    }

    private AnalysisDetailSNVController analysisDetailSNVController;

    /**
     * @param analysisDetailSNVController AnalysisDetailSNVController
     */
    public void setAnalysisDetailSNVController(AnalysisDetailSNVController analysisDetailSNVController) {
        this.analysisDetailSNVController = analysisDetailSNVController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
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

        startFractionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) startFractionTextField.setText(oldValue);
        });

        startFractionTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv) {
                if(!StringUtils.isEmpty(startFractionTextField.getText())
                        && !StringUtils.isEmpty(endFractionTextField.getText())) {
                    if(Integer.parseInt(startFractionTextField.getText()) > Integer.parseInt(endFractionTextField.getText())) {
                        startFractionTextField.setText("");
                    }
                }
            }
        });

        endFractionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) endFractionTextField.setText(oldValue);
        });

        endFractionTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv) {
                if(!StringUtils.isEmpty(endFractionTextField.getText())
                        && !StringUtils.isEmpty(startFractionTextField.getText())) {
                    if(Integer.parseInt(startFractionTextField.getText()) > Integer.parseInt(endFractionTextField.getText())) {
                        endFractionTextField.setText("");
                    }
                }
            }
        });

        if(currentFilter != null) {
            setCurrentOption();
        }

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setCurrentOption() {
        String currentKey = "";
        for(Object obj : currentFilter) {
            String option = obj.toString();
            if(obj.toString().contains(" ")) {
                String key = option.substring(0, option.indexOf(' '));
                String value = option.substring(option.indexOf(' ') + 1);
                setKeyValue(key, value);
            } else {
                if(option.equalsIgnoreCase("dbsnpRsId")) {
                    dbSNPidCheckBox.setSelected(true);
                }
                if(option.equalsIgnoreCase("cosmicIds")) {
                    cosmicidCheckBox.setSelected(true);
                }
            }
        }
    }

    private void setKeyValue(String key, String value) {
        if(key.equalsIgnoreCase("codingConsequence")) {
            codingConsequenceCheck(value);
        } else if(key.equalsIgnoreCase("gene")) {
            setTextArray(value, geneTextField);
        } else if(key.equalsIgnoreCase("chromosome")) {
            setTextArray(value, chromosomeTextField);
        } else if(key.equalsIgnoreCase("alleleFraction")) {
            alleSet(value);
        } else if(key.equalsIgnoreCase("variantType")) {
            if(value.equalsIgnoreCase("snp")) {
                snvCheckBox.setSelected(true);
            } else if(value.equalsIgnoreCase("ins")) {
                indelCheckBox.setSelected(true);
            }
        }
    }

    private void alleSet(String value) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m;
        List<String> values = new ArrayList<>();
        m = p.matcher(value);
        while (m.find()) {
            values.add(m.group());
        }
        if(value.contains("and")) {
            setTextField(values.get(0), startFractionTextField);
            setTextField(values.get(1), endFractionTextField);
        } else if(value.contains("gt")) {
            setTextField(values.get(0), startFractionTextField);
        } else {
            setTextField(values.get(1), endFractionTextField);
        }

    }

    private void setTextArray(String option, TextField textField) {
        if(!StringUtils.isEmpty(textField.getText())) {
            textField.setText(textField.getText() + ", " + option);
        } else {
            setTextField(option, textField);
        }
    }

    private void setTextField(String option, TextField textField) {
        textField.setText(option);
    }

    private void codingConsequenceCheck(String option) {
        if(option.equalsIgnoreCase("5_prime_UTR_variant")) {
            fivePrimeUTRCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("conservative_inframe_deletion")) {
            cidCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("conservative_inframe_deletion")) {
            cidCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("disruptive_inframe_deletion")) {
            didCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("frameshift_variant")) {
            frameshiftCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("intron_variant")) {
            intronCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("missense_variant")) {
            missenseCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("splice_region_variant")) {
            spliceRegionCheckbox.setSelected(true);
        } else if(option.equalsIgnoreCase("splice_acceptor_variant")) {
            spliceAcceptorCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("synonymous_variant")) {
            synonymousCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("stop_gained")) {
            stopGainedCheckBox.setSelected(true);
        }
    }

    @FXML
    public void filterSave() {
        List<Object> list = new ArrayList<>();

        variantTabSave(list);

        consequenceSave(list);

        populationFrequencySave(list);

        if(!list.isEmpty()) {
            analysisDetailSNVController.saveFilter(list, currentFilerName);
            closeFilter();
        }
    }

    private void populationFrequencySave(List<Object> list) {

    }

    private void consequenceSave(List<Object> list) {
        if(fivePrimeUTRCheckBox.isSelected()) {
            list.add("codingConsequence 5_prime_UTR_variant");
        }
        if(cidCheckBox.isSelected()) {
            list.add("codingConsequence conservative_inframe_deletion");
        }
        if(didCheckBox.isSelected()) {
            list.add("codingConsequence disruptive_inframe_deletion");
        }
        if(frameshiftCheckBox.isSelected()) {
            list.add("codingConsequence frameshift_variant");
        }
        if(intronCheckBox.isSelected()) {
            list.add("codingConsequence intron_variant");
        }
        if(missenseCheckBox.isSelected()) {
            list.add("codingConsequence missense_variant");
        }
        if(spliceRegionCheckbox.isSelected()) {
            list.add("codingConsequence splice_region_variant");
        }
        if(spliceAcceptorCheckBox.isSelected()) {
            list.add("codingConsequence splice_acceptor_variant");
        }
        if(synonymousCheckBox.isSelected()) {
            list.add("codingConsequence synonymous_variant");
        }
        if(stopGainedCheckBox.isSelected()) {
            list.add("codingConsequence stop_gained");
        }
    }

    private void variantTabSave(List<Object> list) {
        if(!StringUtils.isEmpty(geneTextField.getText())) {

            String[] geneList = geneTextField.getText().replaceAll(" ", "").split(",");

            for(String gene : geneList) {
                list.add("gene " + gene);
            }
        }

        if(!StringUtils.isEmpty(chromosomeTextField.getText())) {

            String[] chrList = chromosomeTextField.getText().replaceAll(" ", "").split(",");

            for(String chr : chrList) {
                list.add("chromosome " + chr);
            }
        }

        if(snvCheckBox.isSelected()) {
            list.add("variantType snp");
        }

        if(dbSNPidCheckBox.isSelected()) {
            list.add("dbsnpRsId");
        }

        if(cosmicidCheckBox.isSelected()) {
            list.add("cosmicIds");
        }

        if(indelCheckBox.isSelected()) {
            list.add("variantType ins");
            list.add("variantType del");
        }

        if(StringUtils.isEmpty(endFractionTextField.getText()) && !StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction gt:" + startFractionTextField.getText());
        } else if(!StringUtils.isEmpty(endFractionTextField.getText()) && StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction lt:" + endFractionTextField.getText());
        } else if(!StringUtils.isEmpty(endFractionTextField.getText()) && !StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction gt:" + startFractionTextField.getText() + ",lt:" + endFractionTextField.getText() + ",and");
        }
    }

    @FXML
    public void closeFilter() {
        dialogStage.close();
    }
}
