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
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
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

    private APIService apiService;

    private Stage dialogStage;

    @FXML
    private Button saveBtn;

    @FXML
    private Label newFilterNameLabel;

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
    private TextField gnomADAllTextField;

    @FXML
    private TextField gnomADmaTextField;

    @FXML
    private TextField gnomADaaaTextField;

    @FXML
    private TextField gnomADeaTextField;

    @FXML
    private TextField gnomADfinTextField;

    @FXML
    private TextField gnomADnfeTextField;

    @FXML
    private TextField gnomADotherTextField;

    @FXML
    private TextField gnomADsaTextField;

    @FXML
    private TextField exacTextField;

    @FXML
    private ComboBox<ComboBoxItem> tgAllComboBox;

    @FXML
    private ComboBox<ComboBoxItem> tgafrComboBox;

    @FXML
    private ComboBox<ComboBoxItem> tgamrComboBox;

    @FXML
    private ComboBox<ComboBoxItem> tgeasComboBox;

    @FXML
    private ComboBox<ComboBoxItem> tgeurComboBox;

    @FXML
    private ComboBox<ComboBoxItem> tgsasComboBox;

    @FXML
    private ComboBox<ComboBoxItem> espallComboBox;

    @FXML
    private ComboBox<ComboBoxItem> espaaComboBox;

    @FXML
    private ComboBox<ComboBoxItem> espeaComboBox;

    @FXML
    private ComboBox<ComboBoxItem> keidComboBox;

    @FXML
    private ComboBox<ComboBoxItem> krgdComboBox;

    @FXML
    private ComboBox<ComboBoxItem> kohbraComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADAllComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADmaComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADaaaComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADeaComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADfinComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADnfeComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADotherComboBox;

    @FXML
    private ComboBox<ComboBoxItem> gnomADsaComboBox;

    @FXML
    private ComboBox<ComboBoxItem> exacComboBox;

    /////

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

        apiService = APIService.getInstance();

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
        //setFormat(endFractionTextField);\

        createFrequency();

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

        newFilterNameLabel.setVisible(false);
        filterNameTextField.setVisible(false);
        saveBtn.setDisable(true);

        filterNameTextField.textProperty().addListener((ev, oldV, newV) -> {
            if(filterNameTextField.isVisible() && !StringUtils.isEmpty(newV)) {
                saveBtn.setDisable(false);
            } else {
                saveBtn.setDisable(true);
            }
        });

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void createFrequency() {
        createOperatorComboBox(tgAllComboBox);
        createOperatorComboBox(tgafrComboBox);
        createOperatorComboBox(tgamrComboBox);
        createOperatorComboBox(tgeasComboBox);
        createOperatorComboBox(tgeurComboBox);
        createOperatorComboBox(tgsasComboBox);

        createOperatorComboBox(espallComboBox);
        createOperatorComboBox(espaaComboBox);
        createOperatorComboBox(espeaComboBox);

        createOperatorComboBox(exacComboBox);
        createOperatorComboBox(keidComboBox);
        createOperatorComboBox(krgdComboBox);
        createOperatorComboBox(kohbraComboBox);

        createOperatorComboBox(gnomADAllComboBox);
        createOperatorComboBox(gnomADmaComboBox);
        createOperatorComboBox(gnomADaaaComboBox);
        createOperatorComboBox(gnomADeaComboBox);
        createOperatorComboBox(gnomADfinComboBox);
        createOperatorComboBox(gnomADnfeComboBox);
        createOperatorComboBox(gnomADotherComboBox);
        createOperatorComboBox(gnomADsaComboBox);
    }

    private void createOperatorComboBox(ComboBox<ComboBoxItem> comboBox) {
        comboBox.setConverter(new ComboBoxConverter());
        comboBox.getItems().add(new ComboBoxItem("gt:", ">"));
        comboBox.getItems().add(new ComboBoxItem("lt:", "<"));
        comboBox.getItems().add(new ComboBoxItem("gte:", "≥"));
        comboBox.getItems().add(new ComboBoxItem("lte:", "≤"));
        comboBox.getSelectionModel().selectFirst();
    }

    private void setOperator(String value, ComboBox<ComboBoxItem> comboBox) {
        if(value.contains("gte:")) {
            comboBox.getSelectionModel().select(2);
        } else if(value.contains("lte:")) {
            comboBox.getSelectionModel().select(3);
        } else if(value.contains("gt:")) {
            comboBox.getSelectionModel().select(0);
        } else if(value.contains("lt:")) {
            comboBox.getSelectionModel().select(1);
        }
    }

    private void setCosmicOccurrenceComboBoxItem() {
        cosmicOccurrenceComboBox.setConverter(new ComboBoxConverter());
        cosmicOccurrenceComboBox.getItems().add(new ComboBoxItem("", "No selection"));
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

        setFormat(gnomADAllTextField);
        setFormat(gnomADmaTextField);
        setFormat(gnomADaaaTextField);
        setFormat(gnomADeaTextField);
        setFormat(gnomADfinTextField);
        setFormat(gnomADnfeTextField);
        setFormat(gnomADotherTextField);
        setFormat(gnomADsaTextField);

        setFormat(exacTextField);
        setFormat(keidTextField);
        setFormat(krgdTextField);
        setFormat(kohbraTextField);
    }

    private void setCurrentOption(List<Object> currentFilter) {
        filterNameTextField.setVisible(false);
        newFilterNameLabel.setVisible(false);
        saveBtn.setDisable(false);
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
            setOperator(value, tgAllComboBox);
        }else if(key.equalsIgnoreCase("g1000African")) {
            setFeqTextField(value, tgafrTextField);
            setOperator(value, tgafrComboBox);
        }else if(key.equalsIgnoreCase("g1000American")) {
            setFeqTextField(value, tgamrTextField);
            setOperator(value, tgamrComboBox);
        }else if(key.equalsIgnoreCase("g1000EastAsian")) {
            setFeqTextField(value, tgeasTextField);
            setOperator(value, tgeasComboBox);
        }else if(key.equalsIgnoreCase("g1000European")) {
            setFeqTextField(value, tgeurTextField);
            setOperator(value, tgeurComboBox);
        }else if(key.equalsIgnoreCase("g1000SouthAsian")) {
            setFeqTextField(value, tgsasTextField);
            setOperator(value, tgsasComboBox);
        }else if(key.equalsIgnoreCase("esp6500All")) {
            setFeqTextField(value, espallTextField);
            setOperator(value, espallComboBox);
        }else if(key.equalsIgnoreCase("esp6500aa")) {
            setFeqTextField(value, espaaTextField);
            setOperator(value, espaaComboBox);
        }else if(key.equalsIgnoreCase("esp6500ea")) {
            setFeqTextField(value, espeaTextField);
            setOperator(value, espeaComboBox);
        }else if(key.equalsIgnoreCase("koreanExomInformationDatabase")) {
            setFeqTextField(value, keidTextField);
            setOperator(value, keidComboBox);
        }else if(key.equalsIgnoreCase("koreanReferenceGenomeDatabase")) {
            setFeqTextField(value, krgdTextField);
            setOperator(value, krgdComboBox);
        }else if(key.equalsIgnoreCase("kohbraFreq")) {
            setFeqTextField(value, kohbraTextField);
            setOperator(value, kohbraComboBox);
        }else if(key.equalsIgnoreCase("exac")) {
            setFeqTextField(value, exacTextField);
            setOperator(value, exacComboBox);
        }else if(key.equalsIgnoreCase("gnomADall")) {
            setFeqTextField(value, gnomADAllTextField);
            setOperator(value, gnomADAllComboBox);
        }else if(key.equalsIgnoreCase("gnomADadmixedAmerican")) {
            setFeqTextField(value, gnomADmaTextField);
            setOperator(value, gnomADmaComboBox);
        }else if(key.equalsIgnoreCase("gnomADafricanAfricanAmerican")) {
            setFeqTextField(value, gnomADaaaTextField);
            setOperator(value, gnomADaaaComboBox);
        }else if(key.equalsIgnoreCase("gnomADeastAsian")) {
            setFeqTextField(value, gnomADeaTextField);
            setOperator(value, gnomADeaComboBox);
        }else if(key.equalsIgnoreCase("gnomADfinnish")) {
            setFeqTextField(value, gnomADfinTextField);
            setOperator(value, gnomADfinComboBox);
        }else if(key.equalsIgnoreCase("gnomADnonFinnishEuropean")) {
            setFeqTextField(value, gnomADnfeTextField);
            setOperator(value, gnomADnfeComboBox);
        }else if(key.equalsIgnoreCase("gnomADothers")) {
            setFeqTextField(value, gnomADotherTextField);
            setOperator(value, gnomADotherComboBox);
        }else if(key.equalsIgnoreCase("gnomADsouthAsian")) {
            setFeqTextField(value, gnomADsaTextField);
            setOperator(value, gnomADsaComboBox);
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
        } else if(option.equalsIgnoreCase("conservative_inframe_deletion")) {
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            String alertHeaderText = "Confirmation Dialog";
            String alertContentText = "Are you sure to delete this filter?";

            alert.setTitle(alertHeaderText);
            alert.setHeaderText("Confirmation Dialog");
            alert.setContentText(alertContentText);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                filter.remove(filterNameComboBox.getSelectionModel().getSelectedItem());
                String name = filterNameComboBox.getSelectionModel().getSelectedItem();
                filterNameComboBox.getSelectionModel().clearSelection();
                filterNameComboBox.getItems().remove(name);
                resetFilterList();
                changeFilter();

                String gg = JsonUtil.toJson(filter);
                Map<String, Object> map = new HashMap<>();
                map.put("value", gg);
                try {
                    if ("somatic".equalsIgnoreCase(analysisType)) {
                        apiService.put("/member/memberOption/somaticFilter", map, null, true);
                    } else if ("germline".equalsIgnoreCase(analysisType)) {
                        apiService.put("/member/memberOption/germlineFilter", map, null, true);
                    }
                } catch (WebAPIException wae) {
                    DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainApp.getPrimaryStage(), true);
                }
                saveBtn.setDisable(true);
            } else {
                alert.close();
            }
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
        newFilterNameLabel.setVisible(true);
        saveBtn.setDisable(true);
        resetFilterList();
    }

    @FXML
    public void filterSave() {
        List<Object> list = new ArrayList<>();

        variantTabSave(list);

        consequenceSave(list);

        populationFrequencySave(list);

        String filterName = "";

        if(list.isEmpty()) {
            DialogUtil.alert("Warning", "No filtering elements found.", this.dialogStage, true);
            return;
        }

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
        Map<String, Object> map = new HashMap<>();
        map.put("value", gg);
        try {
            if ("somatic".equalsIgnoreCase(analysisType)) {
                apiService.put("/member/memberOption/somaticFilter", map, null, true);
            } else if ("germline".equalsIgnoreCase(analysisType)) {
                apiService.put("/member/memberOption/germlineFilter", map, null, true);
            }
        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainApp.getPrimaryStage(), true);
        }

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
            setFrequency(list, tgAllTextField.getText(), tgAllComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000All");
        }
        if(!StringUtils.isEmpty(tgafrTextField.getText())) {
            setFrequency(list, tgafrTextField.getText(), tgafrComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000African");
        }
        if(!StringUtils.isEmpty(tgamrTextField.getText())) {
            setFrequency(list, tgamrTextField.getText(), tgamrComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000American");
        }
        if(!StringUtils.isEmpty(tgeasTextField.getText())) {
            setFrequency(list, tgeasTextField.getText(), tgeasComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000EastAsian");
        }
        if(!StringUtils.isEmpty(tgeurTextField.getText())) {
            setFrequency(list, tgeurTextField.getText(), tgeurComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000European");
        }
        if(!StringUtils.isEmpty(tgsasTextField.getText())) {
            setFrequency(list, tgsasTextField.getText(), tgsasComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000SouthAsian");
        }

        if(!StringUtils.isEmpty(espallTextField.getText())) {
            setFrequency(list, espallTextField.getText(), espallComboBox.getSelectionModel().getSelectedItem().getValue(), "esp6500All");
        }
        if(!StringUtils.isEmpty(espaaTextField.getText())) {
            setFrequency(list, espaaTextField.getText(), espaaComboBox.getSelectionModel().getSelectedItem().getValue(), "esp6500aa");
        }
        if(!StringUtils.isEmpty(espeaTextField.getText())) {
            setFrequency(list, espeaTextField.getText(), espeaComboBox.getSelectionModel().getSelectedItem().getValue(), "esp6500ea");
        }

        if(!StringUtils.isEmpty(keidTextField.getText())) {
            setFrequency(list, keidTextField.getText(), keidComboBox.getSelectionModel().getSelectedItem().getValue(), "koreanExomInformationDatabase");
        }
        if(!StringUtils.isEmpty(krgdTextField.getText())) {
            setFrequency(list, krgdTextField.getText(), krgdComboBox.getSelectionModel().getSelectedItem().getValue(), "koreanReferenceGenomeDatabase");
        }
        if(!StringUtils.isEmpty(kohbraTextField.getText())) {
            setFrequency(list, kohbraTextField.getText(), kohbraComboBox.getSelectionModel().getSelectedItem().getValue(), "kohbraFreq");
        }
        if(!StringUtils.isEmpty(exacTextField.getText())) {
            setFrequency(list, exacTextField.getText(), exacComboBox.getSelectionModel().getSelectedItem().getValue(), "exac");
        }

        if(!StringUtils.isEmpty(gnomADAllTextField.getText())) {
            setFrequency(list, gnomADAllTextField.getText(), gnomADAllComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADall");
        }
        if(!StringUtils.isEmpty(gnomADmaTextField.getText())) {
            setFrequency(list, gnomADmaTextField.getText(), gnomADmaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADadmixedAmerican");
        }
        if(!StringUtils.isEmpty(gnomADaaaTextField.getText())) {
            setFrequency(list, gnomADaaaTextField.getText(), gnomADaaaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADafricanAfricanAmerican");
        }
        if(!StringUtils.isEmpty(gnomADeaTextField.getText())) {
            setFrequency(list, gnomADeaTextField.getText(), gnomADeaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADeastAsian");
        }
        if(!StringUtils.isEmpty(gnomADfinTextField.getText())) {
            setFrequency(list, gnomADfinTextField.getText(), gnomADfinComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADfinnish");
        }
        if(!StringUtils.isEmpty(gnomADnfeTextField.getText())) {
            setFrequency(list, gnomADnfeTextField.getText(), gnomADnfeComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADnonFinnishEuropean");
        }
        if(!StringUtils.isEmpty(gnomADotherTextField.getText())) {
            setFrequency(list, gnomADotherTextField.getText(), gnomADotherComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADothers");
        }
        if(!StringUtils.isEmpty(gnomADsaTextField.getText())) {
            setFrequency(list, gnomADsaTextField.getText(), gnomADsaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADsouthAsian");
        }
    }

    private void setFrequency(List<Object> list, String text,String operator , String key) {
        list.add(key + " " + operator + text);
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

        if(cosmicOccurrenceComboBox.getSelectionModel().getSelectedItem() != null &&
                !StringUtils.isEmpty(cosmicOccurrenceComboBox.getSelectionModel().getSelectedItem().getValue())) {
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
        mnpCheckBox.setSelected(false);
        complexCheckBox.setSelected(false);
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
        gnomADAllTextField.setText("");
        gnomADmaTextField.setText("");
        gnomADaaaTextField.setText("");
        gnomADeaTextField.setText("");
        gnomADfinTextField.setText("");
        gnomADnfeTextField.setText("");
        gnomADotherTextField.setText("");
        gnomADsaTextField.setText("");
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
