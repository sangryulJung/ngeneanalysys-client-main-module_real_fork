package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
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
    private ComboBox<String> filterNameComboBox;

    @FXML
    private TextField filterNameTextField;

    @FXML
    private TextField geneTextField;

    @FXML
    private TextField chromosomeTextField;

    @FXML
    private CheckBox snvCheckBox;

    @FXML
    private CheckBox indelCheckBox;

    @FXML
    private CheckBox delCheckBox;

    @FXML
    private CheckBox mnpCheckBox;

    @FXML
    private CheckBox complexCheckBox;

    @FXML
    private CheckBox cosmicidCheckBox;

    @FXML
    private CheckBox dbSNPidCheckBox;

    @FXML
    private TextField endFractionTextField;

    @FXML
    private TextField startFractionTextField;

    ///////////////////////////////////////////////

    @FXML
    private CheckBox fivePrimeUTRVCheckBox;

    @FXML
    private CheckBox cidCheckBox;

    @FXML
    private CheckBox didCheckBox;

    @FXML
    private CheckBox frameshiftVariantCheckBox;

    @FXML
    private CheckBox intronCheckBox;

    @FXML
    private CheckBox missenseCheckBox;

    @FXML
    private CheckBox spliceRegionVariantCheckBox;

    @FXML
    private CheckBox synonymousCheckBox;

    @FXML
    private CheckBox spliceAcceptorVariantCheckBox;

    @FXML
    private CheckBox stopGainedCheckBox;

    @FXML
    private CheckBox spliceDonorVariantCheckBox;

    @FXML
    private CheckBox stoplostCheckBox;

    @FXML
    private CheckBox inframeDeletionCheckBox;

    @FXML
    private CheckBox inframeInsertionCheckBox;

    @FXML
    private CheckBox ciiCheckBox;

    @FXML
    private CheckBox diiCheckBox;

    @FXML
    private CheckBox startLostCheckBox;

    @FXML
    private CheckBox threePrimeUTRVCheckBox;

    @FXML
    private CheckBox threePrimeUTRTCheckBox;

    @FXML
    private CheckBox fivePrimeUTRTCheckBox;

    @FXML
    private CheckBox civCheckBox;

    @FXML
    private CheckBox dgvCheckBox;

    @FXML
    private CheckBox irCheckBox;

    @FXML
    private CheckBox srvCheckbox;

    @FXML
    private CheckBox ugvCheckBox;

    ///////////////////////////////////////////////////////////////////

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
    private CheckBox predictionACheckBox;

    @FXML
    private CheckBox predictionBCheckBox;

    @FXML
    private CheckBox predictionCCheckBox;

    @FXML
    private CheckBox predictionDCheckBox;

    @FXML
    private CheckBox predictionECheckBox;

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
    private Label caseLabel;

    @FXML
    private ComboBox<ComboBoxItem> cosmicOccurrenceComboBox;

    private Map<String, List<Object>> filter;

    private String analysisType;

    private String[] defaultFilterName = {"Tier I", "Tier II", "Tier III", "Tier IV", "Pathogenic", "Likely Pathogenic",
    "Uncertain Significance", "Likely Benign", "Benign", "Tier 1", "Tier 2", "Tier 3", "Tier 4"};

    private AnalysisDetailSNVController snvController;

    /**
     * @param snvController AnalysisDetailSNVController
     */
    public void setSnvController(AnalysisDetailSNVController snvController) {
        this.snvController = snvController;
    }

    /**
     * @param analysisType String
     */
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    /**
     * @param filter Map<String, List<Object>>
     */
    public void setFilter(Map<String, List<Object>> filter) {
        this.filter = filter;
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

        //setFormat(startFractionTextField);
        //setFormat(endFractionTextField);

        setCosmicOccurrenceComboBoxItem();

        setFrequencyFormatter();

        setPathogenicity();

        startFractionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) startFractionTextField.setText(oldValue);
        });

        endFractionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) endFractionTextField.setText(oldValue);
        });

        startFractionTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv) {
                if(!StringUtils.isEmpty(startFractionTextField.getText())
                        && !StringUtils.isEmpty(endFractionTextField.getText())) {
                    if(Double.parseDouble(startFractionTextField.getText()) > Double.parseDouble(endFractionTextField.getText())) {
                        startFractionTextField.setText("");
                    }
                }
            }
        });

        endFractionTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv) {
                if(!StringUtils.isEmpty(endFractionTextField.getText())
                        && !StringUtils.isEmpty(startFractionTextField.getText())) {
                    if(Double.parseDouble(startFractionTextField.getText()) > Double.parseDouble(endFractionTextField.getText())) {
                        endFractionTextField.setText("");
                    }
                }
            }
        });

        setComboBox();

        filterNameComboBox.valueProperty().addListener((ob, oValue, nValue) -> {
            if(!StringUtils.isEmpty(nValue)) {
                setCurrentOption(filter.get(nValue));
            }
        });

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setCosmicOccurrenceComboBoxItem() {
        cosmicOccurrenceComboBox.setConverter(new ComboBoxConverter());
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("adrenal_gland", "Adrenal gland"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("autonomic_ganglia", "Autonomic ganglia"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("biliary_tract", "Biliary tract"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("bone", "Bone"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("breast", "Breast"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("central_nervous_system", "Central nervous system"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("cervix", "Cervix"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("endometrium", "Endometrium"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("eye", "Eye"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("fallopian_tube", "Fallopian tube"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("site_indeterminate", "site indeterminate"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("haematopoietic_and_lymphoid_tissue", "Haematopoietic and lymphoid tissue"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("kidney", "Kidney"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("large_intestine", "Large intestine"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("liver", "Liver"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("lung", "Lung"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("Meninges", "meninges"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("ns", "NS"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("oesophagus", "Oesophagus"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("ovary", "Ovary"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("pancreas", "Pancreas"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("paratesticular_tissues", "Paratesticular tissues"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("parathyroid", "Parathyroid"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("penis", "Penis"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("pericardium", "Pericardium"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("peritoneum", "Peritoneum"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("pituitary", "Pituitary"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("placenta", "Placenta"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("pleura", "Pleura"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("prostate", "Prostate"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("retroperitoneum", "Retroperitoneum"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("salivary_gland", "Salivary gland"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("skin", "Skin"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("small_intestine", "Small intestine"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("soft_tissue", "Soft tissue"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("stomach", "Stomach"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("testis", "Testis"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("thymus", "Thymus"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("thyroid", "Thyroid"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("upper_aerodigestive_tract", "Upper aerodigestive tract"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("urinary_tract", "Urinary tract"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("vagina", "Vagina"));
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("vulva", "Vulva"));
    }

    private void setComboBox() {
        Set<String> keySet = filter.keySet();

        for(String key : keySet) {
            if(!Arrays.stream(defaultFilterName).anyMatch(item -> item.equals(key))) {
                filterNameComboBox.getItems().add(key);
            }
        }
    }

    private void setPathogenicity() {
        if("SOMATIC".equalsIgnoreCase(analysisType)) {
            caseLabel.setText("Tier");
            caseECheckBox.setVisible(false);
            predictionECheckBox.setVisible(false);
            caseACheckBox.setText("Tier1");
            caseBCheckBox.setText("Tier2");
            caseCCheckBox.setText("Tier3");
            caseDCheckBox.setText("Tier4");

            predictionACheckBox.setText("Tier1");
            predictionBCheckBox.setText("Tier2");
            predictionCCheckBox.setText("Tier3");
            predictionDCheckBox.setText("Tier4");

        } else {
            caseLabel.setText("Pathogenicity");
            caseACheckBox.setText("P(Pathogentic)");
            caseBCheckBox.setText("LP(Likely Pathogenic)");
            caseCCheckBox.setText("US(Uncertatin Significance)");
            caseDCheckBox.setText("LB(Likely Benign)");
            caseECheckBox.setText("B(Benign)");

            predictionACheckBox.setText("P(Pathogentic)");
            predictionBCheckBox.setText("LP(Likely Pathogenic)");
            predictionCCheckBox.setText("US(Uncertatin Significance)");
            predictionDCheckBox.setText("LB(Likely Benign)");
            predictionECheckBox.setText("B(Benign)");
        }

        clinVarACheckBox.setText("P(Pathogentic)");
        clinVarBCheckBox.setText("LP(Likely Pathogenic)");
        clinVarCCheckBox.setText("US(Uncertatin Significance)");
        clinVarDCheckBox.setText("LB(Likely Benign)");
        clinVarECheckBox.setText("B(Benign)");
    }

    private void setFormat(TextField textField) {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
            pattern.matcher(change.getControlNewText()).matches() ? change : null);
        textField.setTextFormatter(formatter);
    }

    private void setFrequencyFormatter() {
        setFormat(tgAllTextField);
        setFormat(tgafrTextField);
        setFormat(tgamrTextField);
        setFormat(tgeasTextField);
        setFormat(tgeurTextField);
        setFormat(tgsasTextField);

        setFormat(espallTextField);
        setFormat(espaaTextField);
        setFormat(espeaTextField);

        setFormat(genomADAllTextField);
        setFormat(genomADmaTextField);
        setFormat(genomADaaaTextField);
        setFormat(genomADajgenomAD);
        setFormat(genomADeaTextField);
        setFormat(genomADfinTextField);
        setFormat(genomADnfeTextField);
        setFormat(genomADotherTextField);
        setFormat(genomADsaTextField);

        setFormat(exacTextField);
        setFormat(keidTextField);
        setFormat(krgdTextField);
        setFormat(kohbraTextField);
    }

    private void setCurrentOption(List<Object> currentFilter) {
        filterNameTextField.setVisible(false);
        resetFilterList();
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

    private void setPrediction(String value) {
        if(value.equalsIgnoreCase("T1") || value.equalsIgnoreCase("P")) {
            predictionACheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T2") || value.equalsIgnoreCase("LP")) {
            predictionBCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T3") || value.equalsIgnoreCase("US")) {
            predictionCCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T4") || value.equalsIgnoreCase("LB")) {
            predictionDCheckBox.setSelected(true);
        } else {
            predictionECheckBox.setSelected(true);
        }
    }

    private void setClinVar(String value) {
        if(value.equalsIgnoreCase("Pathogenic")) {
            clinVarACheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("Likely_pathogenic")) {
            clinVarBCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("Uncertain_significance")) {
            clinVarCCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("Likely_benign")) {
            clinVarDCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("Benign")) {
            clinVarECheckBox.setSelected(true);
        }
    }

    private void setKeyValue(String key, String value) {
        if(key.equalsIgnoreCase("expertTier") || key.equalsIgnoreCase("expertPathogenicity")) {
            setCase(value);
        } else if(key.equalsIgnoreCase("swTier") || key.equalsIgnoreCase("swPathogenicity")) {
            setPrediction(value);
        } else if(key.equalsIgnoreCase("clinVarClass")) {
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
            } else if(value.equalsIgnoreCase("del")) {
                delCheckBox.setSelected(true);
            } else if(value.equalsIgnoreCase("mnp")) {
                mnpCheckBox.setSelected(true);
            } else if(value.equalsIgnoreCase("complex")) {
                complexCheckBox.setSelected(true);
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
        }else if(key.equalsIgnoreCase("esp6500ea")) {
            setFeqTextField(value, espeaTextField);
        }else if(key.equalsIgnoreCase("koreanExomInformationDatabase")) {
            setFeqTextField(value, keidTextField);
        }else if(key.equalsIgnoreCase("koreanReferenceGenomeDatabase")) {
            setFeqTextField(value, krgdTextField);
        }else if(key.equalsIgnoreCase("kohbraFreq")) {
            setFeqTextField(value, kohbraTextField);
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
        }else if(key.equalsIgnoreCase("cosmicOccurrence")) {
            Optional<ComboBoxItem> comboBoxItem
                    = cosmicOccurrenceComboBox.getItems().stream().filter(item -> item.getValue().equals(value)).findFirst();
            comboBoxItem.ifPresent(comboBoxItem1 -> cosmicOccurrenceComboBox.getSelectionModel().select(comboBoxItem1));
        }
    }

    private void alleSet(String value) {
        //Pattern p = Pattern.compile("\\d*|\\d+\\.\\d*");
        Pattern p = Pattern.compile("\\d*");
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
        if(option.equalsIgnoreCase("stop_gained")) {
            stopGainedCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("stop_lost")) {
            stoplostCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("splice_donor_variant")) {
            spliceDonorVariantCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("splice_acceptor_variant")) {
            spliceAcceptorVariantCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("splice_region_variant")) {
            spliceRegionVariantCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("frameshift_variant")) {
            frameshiftVariantCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("inframe_deletion")) {
            inframeDeletionCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("inframe_insertion")) {
            inframeInsertionCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("ciiCheckBox")) {
            cidCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("conservative_inframe_insertion")) {
            ciiCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("disruptive_inframe_deletion")) {
            didCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("disruptive_inframe_insertion")) {
            diiCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("missense_variant")) {
            missenseCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("start_lost")) {
            startLostCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("3_prime_UTR_variant")) {
            threePrimeUTRVCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("3_prime_UTR_truncation")) {
            threePrimeUTRTCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("5_prime_UTR_variant")) {
            fivePrimeUTRVCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("5_prime_UTR_truncation")) {
            fivePrimeUTRTCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("conserved_intergenic_variant")) {
            civCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("downstream_gene_variant")) {
            dgvCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("intergenic_region")) {
            irCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("intron_variant")) {
            intronCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("stop_retained_variant")) {
            srvCheckbox.setSelected(true);
        } else if(option.equalsIgnoreCase("synonymous_variant")) {
            synonymousCheckBox.setSelected(true);
        } else if(option.equalsIgnoreCase("upstream_gene_variant")) {
            ugvCheckBox.setSelected(true);
        }
    }

    @FXML
    private void removeFilter() {
        if(!filterNameTextField.isVisible()
                && !StringUtils.isEmpty(filterNameComboBox.getSelectionModel().getSelectedItem())) {
            filter.remove(filterNameComboBox.getSelectionModel().getSelectedItem());
            String name = filterNameComboBox.getSelectionModel().getSelectedItem();
            filterNameComboBox.getSelectionModel().clearSelection();
            filterNameComboBox.getItems().remove(name);
            resetFilterList();
            changeFilter();

            filterNameTextField.setVisible(true);
        }
    }

    @FXML
    public void closeFilter() {
        dialogStage.close();
    }

    @FXML
    public void addFilter() {
        filterNameComboBox.getSelectionModel().clearSelection();
        filterNameTextField.setVisible(true);
        resetFilterList();
    }

    @FXML
    public void filterSave() {
        List<Object> list = new ArrayList<>();

        variantTabSave(list);

        consequenceSave(list);

        populationFrequencySave(list);

        String filterName = "";

        if(list.isEmpty()) return;

        if(filterNameTextField.isVisible()) {
            if(StringUtils.isEmpty(filterNameTextField.getText())) {
                DialogUtil.alert("No filter name found", "Please enter a filter name", mainApp.getPrimaryStage(), true);
                filterNameTextField.requestFocus();
                return;
            } else if(Arrays.stream(defaultFilterName).anyMatch(item -> item.equals(filterNameTextField.getText()))) {
                DialogUtil.alert("Unavailable name", "Please edit the filter name", mainApp.getPrimaryStage(), true);
                filterNameTextField.requestFocus();
                return;
            }
            filterName = filterNameTextField.getText();
        } else {
            filterName = filterNameComboBox.getSelectionModel().getSelectedItem();
        }

        filter.put(filterName, list);

        changeFilter();

        String gg = JsonUtil.toJson(filter);
        Map<String, Object> map = JsonUtil.fromJsonToMap(gg);
        
        snvController.comboBoxSetAll();
        closeFilter();
    }

    private void changeFilter() {
        if("somatic".equalsIgnoreCase(analysisType)) {
            mainController.getBasicInformationMap().remove("somaticFilter");
            mainController.getBasicInformationMap().put("somaticFilter", filter);
        } else if("germline".equalsIgnoreCase(analysisType)) {
            mainController.getBasicInformationMap().remove("germlineFilter");
            mainController.getBasicInformationMap().put("germlineFilter", filter);
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
        if(!StringUtils.isEmpty(kohbraTextField.getText())) {
            setFrequency(list, kohbraTextField.getText(), "kohbraFreq");
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

        if(stopGainedCheckBox.isSelected()) {
            list.add("codingConsequence stop_gained");
        }
        if(stoplostCheckBox.isSelected()) {
            list.add("codingConsequence stop_lost");
        }

        if(spliceRegionVariantCheckBox.isSelected()) {
            list.add("codingConsequence splice_region_variant");
        }
        if(spliceAcceptorVariantCheckBox.isSelected()) {
            list.add("codingConsequence splice_acceptor_variant");
        }
        if(spliceDonorVariantCheckBox.isSelected()) {
            list.add("codingConsequence splice_donor_variant");
        }

        if(frameshiftVariantCheckBox.isSelected()) {
            list.add("codingConsequence frameshift_variant");
        }
        if(inframeDeletionCheckBox.isSelected()) {
            list.add("codingConsequence inframe_deletion");
        }
        if(inframeInsertionCheckBox.isSelected()) {
            list.add("codingConsequence inframe_insertion");
        }
        if(cidCheckBox.isSelected()) {
            list.add("codingConsequence conservative_inframe_deletion");
        }
        if(ciiCheckBox.isSelected()) {
            list.add("codingConsequence conservative_inframe_insertion");
        }
        if(didCheckBox.isSelected()) {
            list.add("codingConsequence disruptive_inframe_deletion");
        }
        if(diiCheckBox.isSelected()) {
            list.add("codingConsequence disruptive_inframe_insertion");
        }

        if(missenseCheckBox.isSelected()) {
            list.add("codingConsequence missense_variant");
        }
        if(startLostCheckBox.isSelected()) {
            list.add("codingConsequence start_lost");
        }


        if(threePrimeUTRVCheckBox.isSelected()) {
            list.add("codingConsequence 3_prime_UTR_variant");
        }
        if(threePrimeUTRTCheckBox.isSelected()) {
            list.add("codingConsequence 3_prime_UTR_truncation");
        }
        if(fivePrimeUTRVCheckBox.isSelected()) {
            list.add("codingConsequence 5_prime_UTR_variant");
        }
        if(fivePrimeUTRTCheckBox.isSelected()) {
            list.add("codingConsequence 5_prime_UTR_truncation");
        }
        if(civCheckBox.isSelected()) {
            list.add("codingConsequence conserved_intergenic_variant");
        }
        if(dgvCheckBox.isSelected()) {
            list.add("codingConsequence downstream_gene_variant");
        }
        if(irCheckBox.isSelected()) {
            list.add("codingConsequence intergenic_region");
        }
        if(intronCheckBox.isSelected()) {
            list.add("codingConsequence intron_variant");
        }
        if(srvCheckbox.isSelected()) {
            list.add("codingConsequence stop_retained_variant");
        }
        if(synonymousCheckBox.isSelected()) {
            list.add("codingConsequence synonymous_variant");
        }
        if(ugvCheckBox.isSelected()) {
            list.add("codingConsequence upstream_gene_variant");
        }


    }

    private void variantTabSave(List<Object> list) {

        if("SOMATIC".equalsIgnoreCase(analysisType)) {
            if(caseACheckBox.isSelected()) {
                list.add("expertTier T1");
            }
            if(caseBCheckBox.isSelected()) {
                list.add("expertTier T2");
            }
            if(caseCCheckBox.isSelected()) {
                list.add("expertTier T3");
            }
            if(caseDCheckBox.isSelected()) {
                list.add("expertTier T4");
            }

            if(predictionACheckBox.isSelected()) {
                list.add("swTier T1");
            }
            if(predictionBCheckBox.isSelected()) {
                list.add("swTier T2");
            }
            if(predictionCCheckBox.isSelected()) {
                list.add("swTier T3");
            }
            if(predictionDCheckBox.isSelected()) {
                list.add("swTier T4");
            }
        } else {
            if(caseACheckBox.isSelected()) {
                list.add("expertPathogenicity P");
            }
            if(caseBCheckBox.isSelected()) {
                list.add("expertPathogenicity LP");
            }
            if(caseCCheckBox.isSelected()) {
                list.add("expertPathogenicity US");
            }
            if(caseDCheckBox.isSelected()) {
                list.add("expertPathogenicity LB");
            }
            if(caseECheckBox.isSelected()) {
                list.add("expertPathogenicity B");
            }

            if(predictionACheckBox.isSelected()) {
                list.add("swPathogenicity P");
            }
            if(predictionBCheckBox.isSelected()) {
                list.add("swPathogenicity LP");
            }
            if(predictionCCheckBox.isSelected()) {
                list.add("swPathogenicity US");
            }
            if(predictionDCheckBox.isSelected()) {
                list.add("swPathogenicity LB");
            }
            if(predictionECheckBox.isSelected()) {
                list.add("swPathogenicity B");
            }
        }

        if (clinVarACheckBox.isSelected()) {
            list.add("clinVarClass Pathogenic");
        }
        if (clinVarBCheckBox.isSelected()) {
            list.add("clinVarClass Likely_pathogenic");
        }
        if (clinVarCCheckBox.isSelected()) {
            list.add("clinVarClass Uncertain_significance");
        }
        if (clinVarDCheckBox.isSelected()) {
            list.add("clinVarClass Likely_benign");
        }
        if (clinVarECheckBox.isSelected()) {
            list.add("clinVarClass Benign");
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

        if(indelCheckBox.isSelected()) {
            list.add("variantType ins");
        }
        if(delCheckBox.isSelected()) {
            list.add("variantType del");
        }

        if(mnpCheckBox.isSelected()) {
            list.add("variantType mnp");
        }
        if(complexCheckBox.isSelected()) {
            list.add("variantType complex");
        }

        if(dbSNPidCheckBox.isSelected()) {
            list.add("dbsnpRsId");
        }

        if(cosmicidCheckBox.isSelected()) {
            list.add("cosmicIds");
        }

        if(cosmicOccurrenceComboBox.getSelectionModel().getSelectedItem() != null) {
            list.add("cosmicOccurrence " + cosmicOccurrenceComboBox.getSelectionModel().getSelectedItem().getValue());
        }

        if(StringUtils.isEmpty(endFractionTextField.getText()) && !StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction gt:" + startFractionTextField.getText());
        } else if(!StringUtils.isEmpty(endFractionTextField.getText()) && StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction lt:" + endFractionTextField.getText());
        } else if(!StringUtils.isEmpty(endFractionTextField.getText()) && !StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction gt:" + startFractionTextField.getText() + ",lt:" + endFractionTextField.getText() + ",and");
        }
    }

    private void resetFilterList() {
        filterNameTextField.setText("");
        geneTextField.setText("");
        chromosomeTextField.setText("");
        //snvCheckBox.setSelected(false);
        snvCheckBox.selectedProperty().setValue(false);
        indelCheckBox.setSelected(false);
        delCheckBox.setSelected(false);
        fivePrimeUTRVCheckBox.setSelected(false);
        cidCheckBox.setSelected(false);
        didCheckBox.setSelected(false);
        frameshiftVariantCheckBox.setSelected(false);
        intronCheckBox.setSelected(false);
        missenseCheckBox.setSelected(false);
        spliceRegionVariantCheckBox.setSelected(false);
        synonymousCheckBox.setSelected(false);
        spliceAcceptorVariantCheckBox.setSelected(false);
        stopGainedCheckBox.setSelected(false);
        ugvCheckBox.setSelected(false);
        srvCheckbox.setSelected(false);
        irCheckBox.setSelected(false);
        dgvCheckBox.setSelected(false);
        civCheckBox.setSelected(false);
        fivePrimeUTRTCheckBox.setSelected(false);
        threePrimeUTRTCheckBox.setSelected(false);
        threePrimeUTRVCheckBox.setSelected(false);
        startLostCheckBox.setSelected(false);
        diiCheckBox.setSelected(false);
        ciiCheckBox.setSelected(false);
        inframeInsertionCheckBox.setSelected(false);
        inframeDeletionCheckBox.setSelected(false);
        stoplostCheckBox.setSelected(false);
        spliceDonorVariantCheckBox.setSelected(false);
        endFractionTextField.setText("");
        startFractionTextField.setText("");
        cosmicidCheckBox.setSelected(false);
        dbSNPidCheckBox.setSelected(false);
        tgAllTextField.setText("");
        tgafrTextField.setText("");
        tgamrTextField.setText("");
        tgeasTextField.setText("");
        tgeurTextField.setText("");
        tgsasTextField.setText("");
        espallTextField.setText("");
        espaaTextField.setText("");
        espeaTextField.setText("");
        keidTextField.setText("");
        krgdTextField.setText("");
        kohbraTextField.setText("");
        genomADAllTextField.setText("");
        genomADmaTextField.setText("");
        genomADaaaTextField.setText("");
        genomADajgenomAD.setText("");
        genomADeaTextField.setText("");
        genomADfinTextField.setText("");
        genomADnfeTextField.setText("");
        genomADotherTextField.setText("");
        genomADsaTextField.setText("");
        exacTextField.setText("");
        caseACheckBox.setSelected(false);
        caseBCheckBox.setSelected(false);
        caseCCheckBox.setSelected(false);
        caseDCheckBox.setSelected(false);
        caseECheckBox.setSelected(false);
        predictionACheckBox.setSelected(false);
        predictionBCheckBox.setSelected(false);
        predictionCCheckBox.setSelected(false);
        predictionDCheckBox.setSelected(false);
        predictionECheckBox.setSelected(false);
        clinVarACheckBox.setSelected(false);
        clinVarBCheckBox.setSelected(false);
        clinVarCCheckBox.setSelected(false);
        clinVarDCheckBox.setSelected(false);
        clinVarECheckBox.setSelected(false);
        cosmicOccurrenceComboBox.getSelectionModel().clearSelection();

    }
}
