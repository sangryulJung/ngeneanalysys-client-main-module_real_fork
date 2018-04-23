package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
import java.util.function.UnaryOperator;
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

    @FXML
    private TextField tgAllTextField;

    @FXML
    private TextField tgafrTextField;

    @FXML
    private TextField tgamrTextField;

    @FXML
    private TextField tgeasTextField;

    @FXML
    private TextField tgeurTextField;

    @FXML
    private TextField tgsasTextField;

    @FXML
    private TextField espallTextField;

    @FXML
    private TextField espaaTextField;

    @FXML
    private TextField espeaTextField;

    @FXML
    private TextField keidTextField;

    @FXML
    private TextField krgdTextField;

    @FXML
    private TextField kohbraTextField;

    @FXML
    private TextField genomADAllTextField;

    @FXML
    private TextField genomADmaTextField;

    @FXML
    private TextField genomADaaaTextField;

    @FXML
    private TextField genomADajgenomAD;

    @FXML
    private TextField genomADeaTextField;

    @FXML
    private TextField genomADfinTextField;

    @FXML
    private TextField genomADnfeTextField;

    @FXML
    private TextField genomADotherTextField;

    @FXML
    private TextField genomADsaTextField;

    @FXML
    private TextField exacTextField;

    @FXML
    private CheckBox caseACheckBox;

    @FXML
    private CheckBox caseBCheckBox;

    @FXML
    private CheckBox caseCCheckBox;

    @FXML
    private CheckBox caseDCheckBox;

    @FXML
    private CheckBox caseECheckBox;

    @FXML
    private CheckBox clinVarACheckBox;

    @FXML
    private CheckBox clinVarBCheckBox;

    @FXML
    private CheckBox clinVarCCheckBox;

    @FXML
    private CheckBox clinVarDCheckBox;

    @FXML
    private CheckBox clinVarECheckBox;

    @FXML
    private CheckBox haltCheckBox;
    @FXML
    private Label caseLabel;

    private List<Object> currentFilter;

    private String currentFilerName;

    private String analysisType;

    /**
     * @param analysisType String
     */
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

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

        setFormat();

        setPathogenicity();

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

    private void setPathogenicity() {
        if("SOMATIC".equalsIgnoreCase(analysisType)) {
            caseLabel.setText("Tier");
            caseECheckBox.setVisible(false);
            clinVarECheckBox.setVisible(false);
            caseACheckBox.setText("T1");
            caseBCheckBox.setText("T2");
            caseCCheckBox.setText("T3");
            caseDCheckBox.setText("T4");

            clinVarACheckBox.setText("T1");
            clinVarBCheckBox.setText("T2");
            clinVarCCheckBox.setText("T3");
            clinVarDCheckBox.setText("T4");
        } else {
            caseLabel.setText("Pathogenicity");
            caseACheckBox.setText("P");
            caseBCheckBox.setText("LP");
            caseCCheckBox.setText("US");
            caseDCheckBox.setText("LB");
            caseECheckBox.setText("B");

            clinVarACheckBox.setText("P");
            clinVarBCheckBox.setText("LP");
            clinVarCCheckBox.setText("US");
            clinVarDCheckBox.setText("LB");
            clinVarECheckBox.setText("B");
        }
    }

    private void setFormat() {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
            pattern.matcher(change.getControlNewText()).matches() ? change : null);
        genomADAllTextField.setTextFormatter(formatter);
    }

    private void setCurrentOption() {
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

    private void setCase(String value) {
        if(value.equalsIgnoreCase("T1") || value.equalsIgnoreCase("P")) {
            caseACheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T2") || value.equalsIgnoreCase("LP")) {
            caseBCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T3") || value.equalsIgnoreCase("US")) {
            caseCCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T4") || value.equalsIgnoreCase("LB")) {
            caseDCheckBox.setSelected(true);
        } else {
            caseECheckBox.setSelected(true);
        }
    }

    private void setClinVar(String value) {
        if(value.equalsIgnoreCase("T1") || value.equalsIgnoreCase("Pathogenic")) {
            clinVarACheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T2") || value.equalsIgnoreCase("Likely Pathogenic")) {
            clinVarBCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T3") || value.equalsIgnoreCase("Uncertain Significance")) {
            clinVarCCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T4") || value.equalsIgnoreCase("Likely Benign")) {
            clinVarDCheckBox.setSelected(true);
        } else {
            clinVarECheckBox.setSelected(true);
        }
    }

    private void setKeyValue(String key, String value) {
        if(key.equalsIgnoreCase("tier") || key.equalsIgnoreCase("pathogenicity")) {
            setCase(value);
        } else if(key.equalsIgnoreCase("clinVarClasss")) {
            setClinVar(value);
        } else if(key.equalsIgnoreCase("codingConsequence")) {
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
        } else if(key.equalsIgnoreCase("g1000All")) {
            setFeqTextField(value, tgAllTextField);
        }else if(key.equalsIgnoreCase("g1000African")) {
            setFeqTextField(value, tgafrTextField);
        }else if(key.equalsIgnoreCase("g1000American")) {
            setFeqTextField(value, tgamrTextField);
        }else if(key.equalsIgnoreCase("g1000EastAsian")) {
            setFeqTextField(value, tgeasTextField);
        }else if(key.equalsIgnoreCase("g1000European")) {
            setFeqTextField(value, tgeurTextField);
        }else if(key.equalsIgnoreCase("g1000SouthAsian")) {
            setFeqTextField(value, tgsasTextField);
        }else if(key.equalsIgnoreCase("esp6500All")) {
            setFeqTextField(value, espallTextField);
        }else if(key.equalsIgnoreCase("esp6500aa")) {
            setFeqTextField(value, espaaTextField);
        }else if(key.equalsIgnoreCase("koreanExomInformationDatabase")) {
            setFeqTextField(value, keidTextField);
        }else if(key.equalsIgnoreCase("koreanReferenceGenomeDatabase")) {
            setFeqTextField(value, krgdTextField);
        }else if(key.equalsIgnoreCase("kohbraFreq")) {
            setFeqTextField(value, espeaTextField);
        }else if(key.equalsIgnoreCase("exac")) {
            setFeqTextField(value, exacTextField);
        }else if(key.equalsIgnoreCase("genomADall")) {
            setFeqTextField(value, genomADAllTextField);
        }else if(key.equalsIgnoreCase("genomADadmixedAmerican")) {
            setFeqTextField(value, genomADmaTextField);
        }else if(key.equalsIgnoreCase("genomADafricanAfricanAmerican")) {
            setFeqTextField(value, genomADaaaTextField);
        }else if(key.equalsIgnoreCase("genomADashkenaziJewish")) {
            setFeqTextField(value, genomADajgenomAD);
        }else if(key.equalsIgnoreCase("genomADeastAsian")) {
            setFeqTextField(value, genomADeaTextField);
        }else if(key.equalsIgnoreCase("genomADfinnish")) {
            setFeqTextField(value, genomADfinTextField);
        }else if(key.equalsIgnoreCase("genomADnonFinnishEuropean")) {
            setFeqTextField(value, genomADnfeTextField);
        }else if(key.equalsIgnoreCase("genomADothers")) {
            setFeqTextField(value, genomADotherTextField);
        }else if(key.equalsIgnoreCase("genomADsouthAsian")) {
            setFeqTextField(value, genomADsaTextField);
        }else if(key.equalsIgnoreCase("cosmicOccurence")) {
            haltCheckBox.setSelected(true);
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
            setTextField(values.get(0), endFractionTextField);
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

    private void setFeqTextField(String option, TextField textField) {
        textField.setText(option.substring(option.indexOf(":") + 1));
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
        if(!StringUtils.isEmpty(tgAllTextField.getText())) {
            setFrequency(list, tgAllTextField.getText(), "g1000All");
        }
        if(!StringUtils.isEmpty(tgafrTextField.getText())) {
            setFrequency(list, tgafrTextField.getText(), "g1000African");
        }
        if(!StringUtils.isEmpty(tgamrTextField.getText())) {
            setFrequency(list, tgamrTextField.getText(), "g1000American");
        }
        if(!StringUtils.isEmpty(tgeasTextField.getText())) {
            setFrequency(list, tgeasTextField.getText(), "g1000EastAsian");
        }
        if(!StringUtils.isEmpty(tgeurTextField.getText())) {
            setFrequency(list, tgeurTextField.getText(), "g1000European");
        }
        if(!StringUtils.isEmpty(tgsasTextField.getText())) {
            setFrequency(list, tgsasTextField.getText(), "g1000SouthAsian");
        }

        if(!StringUtils.isEmpty(espallTextField.getText())) {
            setFrequency(list, espallTextField.getText(), "esp6500All");
        }
        if(!StringUtils.isEmpty(espaaTextField.getText())) {
            setFrequency(list, espaaTextField.getText(), "esp6500aa");
        }
        if(!StringUtils.isEmpty(espeaTextField.getText())) {
            setFrequency(list, espeaTextField.getText(), "esp6500ea");
        }

        if(!StringUtils.isEmpty(keidTextField.getText())) {
            setFrequency(list, keidTextField.getText(), "koreanExomInformationDatabase");
        }
        if(!StringUtils.isEmpty(krgdTextField.getText())) {
            setFrequency(list, krgdTextField.getText(), "koreanReferenceGenomeDatabase");
        }
        if(!StringUtils.isEmpty(espeaTextField.getText())) {
            setFrequency(list, espeaTextField.getText(), "kohbraFreq");
        }
        if(!StringUtils.isEmpty(exacTextField.getText())) {
            setFrequency(list, exacTextField.getText(), "exac");
        }

        if(!StringUtils.isEmpty(genomADAllTextField.getText())) {
            setFrequency(list, genomADAllTextField.getText(), "genomADall");
        }
        if(!StringUtils.isEmpty(genomADmaTextField.getText())) {
            setFrequency(list, genomADmaTextField.getText(), "genomADadmixedAmerican");
        }
        if(!StringUtils.isEmpty(genomADaaaTextField.getText())) {
            setFrequency(list, genomADaaaTextField.getText(), "genomADafricanAfricanAmerican");
        }
        if(!StringUtils.isEmpty(genomADajgenomAD.getText())) {
            setFrequency(list, genomADajgenomAD.getText(), "genomADashkenaziJewish");
        }
        if(!StringUtils.isEmpty(genomADeaTextField.getText())) {
            setFrequency(list, genomADeaTextField.getText(), "genomADeastAsian");
        }
        if(!StringUtils.isEmpty(genomADfinTextField.getText())) {
            setFrequency(list, genomADfinTextField.getText(), "genomADfinnish");
        }
        if(!StringUtils.isEmpty(genomADnfeTextField.getText())) {
            setFrequency(list, genomADnfeTextField.getText(), "genomADnonFinnishEuropean");
        }
        if(!StringUtils.isEmpty(genomADotherTextField.getText())) {
            setFrequency(list, genomADotherTextField.getText(), "genomADothers");
        }
        if(!StringUtils.isEmpty(genomADsaTextField.getText())) {
            setFrequency(list, genomADsaTextField.getText(), "genomADsouthAsian");
        }
    }

    private void setFrequency(List<Object> list, String text, String key) {
        list.add(key + " gte:" + text);
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

        if("SOMATIC".equalsIgnoreCase(analysisType)) {
            if(caseACheckBox.isSelected()) {
                list.add("tier T1");
            }
            if(caseBCheckBox.isSelected()) {
                list.add("tier T2");
            }
            if(caseCCheckBox.isSelected()) {
                list.add("tier T3");
            }
            if(caseDCheckBox.isSelected()) {
                list.add("tier T4");
            }
        } else {
            if(caseACheckBox.isSelected()) {
                list.add("pathogenicity P");
            }
            if(caseBCheckBox.isSelected()) {
                list.add("pathogenicity LP");
            }
            if(caseCCheckBox.isSelected()) {
                list.add("pathogenicity US");
            }
            if(caseDCheckBox.isSelected()) {
                list.add("pathogenicity LB");
            }
            if(caseECheckBox.isSelected()) {
                list.add("pathogenicity B");
            }
        }
        if("SOMATIC".equalsIgnoreCase(analysisType)) {
            if (clinVarACheckBox.isSelected()) {
                list.add("clinVarClass T1");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass T2");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass T3");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass T4");
            }
        } else {
            if (clinVarACheckBox.isSelected()) {
                list.add("clinVarClass Pathogenic");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass Likely Pathogenic");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass Uncertain Significance");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass Likely Benign");
            }
            if (clinVarBCheckBox.isSelected()) {
                list.add("clinVarClass Benign");
            }
        }

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

        if(haltCheckBox.isSelected()) {
            list.add("cosmicOccurence haematopoietic_and_lymphoid_tissue");
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
