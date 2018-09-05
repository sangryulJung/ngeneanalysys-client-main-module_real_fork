package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import org.controlsfx.control.CheckComboBox;
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
    private GridPane variantTabGridPane;

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
    private CheckBox clinVarDrugResponseCheckBox;

    @FXML
    private Label caseLabel;

    @FXML
    private ComboBox<ComboBoxItem> cosmicOccurrenceComboBox;

    @FXML
    private TextField depthCountTextField;

    @FXML
    private TextField altCountTextField;

    @FXML
    private TextField depthEndCountTextField;

    @FXML
    private TextField altEndCountTextField;

    @FXML
    private HBox warningHBox;

    @FXML
    private Button allLowConfidenceButton;

    @FXML
    private Button defaultLowConfidence;

    @FXML
    private Button uncheckLowConfidence;

    private Map<String, List<Object>> filter;

    private Panel panel;

    /*private String[] defaultFilterName = {"Tier I", "Tier II", "Tier III", "Tier IV", "Pathogenic", "Likely Pathogenic",
    "Uncertain Significance", "Likely Benign", "Benign", "Tier 1", "Tier 2", "Tier 3", "Tier 4"};*/

    private AnalysisDetailSNVController snvController;

    private CheckComboBox<String> warningCheckComboBox;

    private String currentFilterName;

    /**
     * @param snvController AnalysisDetailSNVController
     */
    void setSnvController(AnalysisDetailSNVController snvController) {
        this.snvController = snvController;
    }

    /**
     * @param panel panel
     */
    void setPanel(Panel panel) {
        this.panel = panel;
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

        createLowConfidence();

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

        depthCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) depthCountTextField.setText(oldValue);
        });

        altCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) altCountTextField.setText(oldValue);
        });

        depthEndCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) depthEndCountTextField.setText(oldValue);
        });

        altEndCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) altEndCountTextField.setText(oldValue);
        });

        startFractionTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv && checkNotEmptyTextField(startFractionTextField, endFractionTextField)
                        && checkInequality(startFractionTextField, endFractionTextField)) {
                startFractionTextField.setText("");
            }
        });

        endFractionTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv && checkNotEmptyTextField(startFractionTextField, endFractionTextField)
                    && checkInequality(startFractionTextField, endFractionTextField)) {
                endFractionTextField.setText("");
            }
        });

        altCountTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv && checkNotEmptyTextField(altCountTextField, altEndCountTextField)
                    && checkInequality(altCountTextField, altEndCountTextField)) {
                altCountTextField.setText("");
            }
        });

        altEndCountTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv && checkNotEmptyTextField(altCountTextField, altEndCountTextField)
                    && checkInequality(altCountTextField, altEndCountTextField)) {
                altEndCountTextField.setText("");
            }
        });

        depthCountTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv && checkNotEmptyTextField(depthCountTextField, depthEndCountTextField)
                    && checkInequality(depthCountTextField, depthEndCountTextField)) {
                depthCountTextField.setText("");
            }
        });

        depthEndCountTextField.focusedProperty().addListener((ol, ov, nv) -> {
            if(!nv && checkNotEmptyTextField(depthCountTextField, depthEndCountTextField)
                    && checkInequality(depthCountTextField, depthEndCountTextField)) {
                depthEndCountTextField.setText("");
            }
        });

        setComboBox();

        filterNameComboBox.valueProperty().addListener((ob, oValue, nValue) -> {
            if(StringUtils.isNotEmpty(nValue)) {
                currentFilterName = nValue;
                setCurrentOption(filter.get(nValue), nValue);
            }
        });

        //newFilterNameLabel.setVisible(false);
        //filterNameTextField.setVisible(false);
        saveBtn.setDisable(true);

        filterNameTextField.textProperty().addListener((ev, oldV, newV) -> {
            if(filterNameTextField.isVisible() && StringUtils.isNotEmpty(newV)) {
                saveBtn.setDisable(false);
            } else {
                saveBtn.setDisable(true);
            }
        });

        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private boolean checkNotEmptyTextField(TextField textField1, TextField textField2) {
        return StringUtils.isNotEmpty(textField1.getText()) && StringUtils.isNotEmpty(textField2.getText());
    }

    private boolean checkInequality(TextField textField1, TextField textField2) {
        return Double.parseDouble(textField1.getText()) > Double.parseDouble(textField2.getText());
    }

    @FXML
    public void setAllLowConfidence() {
        if(warningCheckComboBox != null) {
            warningCheckComboBox.getCheckModel().checkAll();
        }
    }

    @FXML
    public void setDefaultLowConfidence() {
        if(warningCheckComboBox != null) {
            if(panel.getCode().equals(PipelineCode.HEME_ACCUTEST_DNA.getCode())
                    || panel.getCode().equals(PipelineCode.TST170_DNA.getCode())
                    || panel.getCode().equals(PipelineCode.SOLID_ACCUTEST_DNA.getCode())) {
                warningCheckComboBox.getCheckModel().checkAll();
                warningCheckComboBox.getCheckModel().clearCheck("t_lod");
            } else if (panel.getCode().equals(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
                warningCheckComboBox.getCheckModel().checkAll();
            }
        }
    }

    @FXML
    public void setUncheckLowConfidence() {
        if(warningCheckComboBox != null) {
            warningCheckComboBox.getCheckModel().clearChecks();
        }
    }

    private void createLowConfidence() {
        CheckComboBox<String> lowConfidenceCheckComboBox = new CheckComboBox<>();
        if(panel != null) {
            lowConfidenceCheckComboBox.getItems().addAll(PipelineCode.getLowConfidences(panel.getCode()));
            if (panel.getCode().equals(PipelineCode.HEME_ACCUTEST_DNA.getCode()) ||
                    panel.getCode().equals(PipelineCode.HEME_ACCUTEST_DNA.getCode())) {
                lowConfidenceCheckComboBox.getItems().addAll("homopolymer", "repeat_sequence", "lowcoverage_indel", "lowcoverage_snv");
            }
        }

        lowConfidenceCheckComboBox.setPrefWidth(150);

        this.warningCheckComboBox = lowConfidenceCheckComboBox;
        warningHBox.getChildren().add(0, lowConfidenceCheckComboBox);
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
        comboBox.getItems().add(new ComboBoxItem("lt:", "<"));
        comboBox.getItems().add(new ComboBoxItem("gt:", ">"));
        comboBox.getItems().add(new ComboBoxItem("lte:", "≤"));
        comboBox.getItems().add(new ComboBoxItem("gte:", "≥"));
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
            //if(Arrays.stream(defaultFilterName).noneMatch(item -> item.equals(key))) {
            filterNameComboBox.getItems().add(key);
            //}
        }
    }

    private void setPathogenicity() {
        if("SOMATIC".equalsIgnoreCase(panel.getAnalysisType())) {
            caseLabel.setText("Tier");
            caseECheckBox.setVisible(false);
            caseACheckBox.setText("Tier1");
            caseBCheckBox.setText("Tier2");
            caseCCheckBox.setText("Tier3");
            caseDCheckBox.setText("Tier4");
        } else {
            caseLabel.setText("Pathogenicity");
            caseACheckBox.setText("P(Pathogentic)");
            caseBCheckBox.setText("LP(Likely Pathogenic)");
            caseCCheckBox.setText("US(Uncertatin Significance)");
            caseDCheckBox.setText("LB(Likely Benign)");
            caseECheckBox.setText("B(Benign)");

            if(panel.getCode().equalsIgnoreCase(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
                kohbraComboBox.setDisable(true);
                kohbraTextField.setDisable(true);
            } else if(panel.getCode().equalsIgnoreCase(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                    panel.getCode().equalsIgnoreCase(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())){
                gnomADaaaComboBox.setDisable(true);
                gnomADaaaTextField.setDisable(true);
                gnomADAllComboBox.setDisable(true);
                gnomADAllTextField.setDisable(true);
                gnomADeaComboBox.setDisable(true);
                gnomADeaTextField.setDisable(true);
                gnomADfinComboBox.setDisable(true);
                gnomADfinTextField.setDisable(true);
                gnomADmaComboBox.setDisable(true);
                gnomADmaTextField.setDisable(true);
                gnomADnfeComboBox.setDisable(true);
                gnomADnfeTextField.setDisable(true);
                gnomADotherComboBox.setDisable(true);
                gnomADotherTextField.setDisable(true);
                gnomADsaComboBox.setDisable(true);
                gnomADsaTextField.setDisable(true);
            }
        }

        if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode()) ||
                panel.getCode().equals(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
            if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode())
                    || panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())) warningHBox.setDisable(true);
            cosmicidCheckBox.setDisable(true);
            cosmicOccurrenceComboBox.setDisable(true);
        }
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

    private void setCurrentOption(List<Object> currentFilter, String filterName) {
        //filterNameTextField.setVisible(false);
        newFilterNameLabel.setVisible(false);
        saveBtn.setDisable(false);
        resetFilterList();
        filterNameTextField.setText(filterName);
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

    private void setVariantLevel(String value) {
        if(value.equalsIgnoreCase("T1") || value.equalsIgnoreCase("P")) {
            caseACheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T2") || value.equalsIgnoreCase("LP")) {
            caseBCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T3") || value.equalsIgnoreCase("US")) {
            caseCCheckBox.setSelected(true);
        } else if(value.equalsIgnoreCase("T4") || value.equalsIgnoreCase("LB")) {
            caseDCheckBox.setSelected(true);
        } else if (value.equalsIgnoreCase("B")){
            caseECheckBox.setSelected(true);
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
        } else if(value.equalsIgnoreCase("drug_response")) {
            clinVarDrugResponseCheckBox.setSelected(true);
        }
    }

    private void setKeyValue(String key, String value) {
        if(key.equalsIgnoreCase("tier") || key.equalsIgnoreCase("pathogenicity")) {
            setVariantLevel(value);
        } else if(key.equalsIgnoreCase("clinVarClass")) {
            setClinVar(value);
        } else if(key.equalsIgnoreCase("codingConsequence")) {
            codingConsequenceCheck(value);
        } else if(key.equalsIgnoreCase("gene")) {
            setTextArray(value, geneTextField);
        } else if(key.equalsIgnoreCase("chromosome")) {
            setTextArray(value, chromosomeTextField);
        } else if(key.equalsIgnoreCase("alleleFraction")) {
            alleSet(value, startFractionTextField, endFractionTextField);
        } else if(key.equalsIgnoreCase("variantType")) {
            if(value.equalsIgnoreCase("snp") || value.equalsIgnoreCase("snv")) {
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
        }else if(key.equalsIgnoreCase("warningReason")) {
            warningCheckComboBox.getCheckModel().check(value);
        }else if(key.equalsIgnoreCase("readDepth")) {
            alleSet(value, depthCountTextField, depthEndCountTextField);
        }else if(key.equalsIgnoreCase("altReadNum")) {
            alleSet(value, altCountTextField, altEndCountTextField);
        }
    }

    private void alleSet(String value, TextField startTextField, TextField endTextField) {
        //Pattern p = Pattern.compile("\\d*|\\d+\\.\\d*");
        Pattern p = Pattern.compile("\\d+");
        Matcher m;
        List<String> values = new ArrayList<>();
        m = p.matcher(value);
        while (m.find()) {
            values.add(m.group());
        }
        if(value.contains("and")) {
            setTextField(values.get(0), startTextField);
            setTextField(values.get(1), endTextField);
        } else if(value.contains("gt")) {
            setTextField(values.get(0), startTextField);
        } else {
            setTextField(values.get(0), endTextField);
        }

    }

    private void setTextArray(String option, TextField textField) {
        if(StringUtils.isNotEmpty(textField.getText())) {
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
        if(StringUtils.isNotEmpty(filterNameComboBox.getSelectionModel().getSelectedItem())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            DialogUtil.setIcon(alert);
            String alertHeaderText = filterNameComboBox.getSelectionModel().getSelectedItem();
            String alertContentText = "Are you sure to delete this filter?";

            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(alertHeaderText);
            alert.setContentText(alertContentText);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                filter.remove(filterNameComboBox.getSelectionModel().getSelectedItem());
                String name = filterNameComboBox.getSelectionModel().getSelectedItem();
                filterNameComboBox.getSelectionModel().clearSelection();
                filterNameComboBox.getItems().remove(name);
                resetFilterList();
                changeFilter();

                String valueJsonString = JsonUtil.toJson(filter);
                Map<String, Object> map = new HashMap<>();
                map.put("value", valueJsonString);
                try {
                    if (panel.getCode().equals(PipelineCode.HEME_ACCUTEST_DNA.getCode())) {
                        apiService.put("/member/memberOption/hemeFilter", map, null, true);
                    } else if (panel.getCode().equals(PipelineCode.SOLID_ACCUTEST_DNA.getCode())) {
                        apiService.put("/member/memberOption/solidFilter", map, null, true);
                    } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
                        apiService.put("/member/memberOption/tstDNAFilter", map, null, true);
                    } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                            panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())) {
                        apiService.put("/member/memberOption/brcaFilter", map, null, true);
                    } else if(panel.getCode().equals(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
                        apiService.put("/member/memberOption/heredFilter", map, null, true);
                    }

                } catch (WebAPIException wae) {
                    DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainApp.getPrimaryStage(), true);
                }
                filterNameTextField.setText("");
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
        //filterNameTextField.setVisible(true);
        filterNameTextField.setText("");
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
        filterName = filterNameTextField.getText();
        //if(filterNameTextField.isVisible()) {
        if(StringUtils.isEmpty(filterName)) {
            DialogUtil.alert("No filter name found", "Please enter a filter name", mainApp.getPrimaryStage(), true);
            filterNameTextField.requestFocus();
            return;
        } else if("All".equalsIgnoreCase(filterName) || (StringUtils.isNotEmpty(this.currentFilterName) &&
                !filterName.equals(currentFilterName) && filterNameComboBox.getItems().contains(filterName))) {
            DialogUtil.alert("Unavailable name", "Please edit the filter name", mainApp.getPrimaryStage(), true);
            filterNameTextField.requestFocus();
            return;
        }
        /*} else {
            filterName = filterNameComboBox.getSelectionModel().getSelectedItem();
        }*/
        filter.remove(currentFilterName);
        filter.put(filterName, list);

        changeFilter();

        String gg = JsonUtil.toJson(filter);
        Map<String, Object> map = new HashMap<>();
        map.put("value", gg);
        try {
            if (panel.getCode().equals(PipelineCode.HEME_ACCUTEST_DNA.getCode())) {
                apiService.put("/member/memberOption/hemeFilter", map, null, true);
            } else if (panel.getCode().equals(PipelineCode.SOLID_ACCUTEST_DNA.getCode())) {
                apiService.put("/member/memberOption/solidFilter", map, null, true);
            } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
                apiService.put("/member/memberOption/tstDNAFilter", map, null, true);
            } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                    panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())) {
                apiService.put("/member/memberOption/brcaFilter", map, null, true);
            } else if(panel.getCode().equals(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
                apiService.put("/member/memberOption/heredFilter", map, null, true);
            }
            filterNameTextField.setText("");
            currentFilterName = null;
        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainApp.getPrimaryStage(), true);
        }

        snvController.comboBoxSetAll();
        closeFilter();
    }

    private void changeFilter() {
        if (panel.getCode().equals(PipelineCode.HEME_ACCUTEST_DNA.getCode())) {
            mainController.getBasicInformationMap().remove("hemeFilter");
            mainController.getBasicInformationMap().put("hemeFilter", filter);
        } else if (panel.getCode().equals(PipelineCode.SOLID_ACCUTEST_DNA.getCode())) {
            mainController.getBasicInformationMap().remove("solidFilter");
            mainController.getBasicInformationMap().put("solidFilter", filter);
        } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
            mainController.getBasicInformationMap().remove("tstDNAFilter");
            mainController.getBasicInformationMap().put("tstDNAFilter", filter);
        } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())) {
            mainController.getBasicInformationMap().remove("brcaFilter");
            mainController.getBasicInformationMap().put("brcaFilter", filter);
        } else if(panel.getCode().equals(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
            mainController.getBasicInformationMap().remove("heredFilter");
            mainController.getBasicInformationMap().put("heredFilter", filter);
        }
    }

    private void populationFrequencySave(List<Object> list) {
        if(StringUtils.isNotEmpty(tgAllTextField.getText())) {
            setFrequency(list, tgAllTextField.getText(), tgAllComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000All");
        }
        if(StringUtils.isNotEmpty(tgafrTextField.getText())) {
            setFrequency(list, tgafrTextField.getText(), tgafrComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000African");
        }
        if(StringUtils.isNotEmpty(tgamrTextField.getText())) {
            setFrequency(list, tgamrTextField.getText(), tgamrComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000American");
        }
        if(StringUtils.isNotEmpty(tgeasTextField.getText())) {
            setFrequency(list, tgeasTextField.getText(), tgeasComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000EastAsian");
        }
        if(StringUtils.isNotEmpty(tgeurTextField.getText())) {
            setFrequency(list, tgeurTextField.getText(), tgeurComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000European");
        }
        if(StringUtils.isNotEmpty(tgsasTextField.getText())) {
            setFrequency(list, tgsasTextField.getText(), tgsasComboBox.getSelectionModel().getSelectedItem().getValue(), "g1000SouthAsian");
        }

        if(StringUtils.isNotEmpty(espallTextField.getText())) {
            setFrequency(list, espallTextField.getText(), espallComboBox.getSelectionModel().getSelectedItem().getValue(), "esp6500All");
        }
        if(StringUtils.isNotEmpty(espaaTextField.getText())) {
            setFrequency(list, espaaTextField.getText(), espaaComboBox.getSelectionModel().getSelectedItem().getValue(), "esp6500aa");
        }
        if(StringUtils.isNotEmpty(espeaTextField.getText())) {
            setFrequency(list, espeaTextField.getText(), espeaComboBox.getSelectionModel().getSelectedItem().getValue(), "esp6500ea");
        }

        if(StringUtils.isNotEmpty(keidTextField.getText())) {
            setFrequency(list, keidTextField.getText(), keidComboBox.getSelectionModel().getSelectedItem().getValue(), "koreanExomInformationDatabase");
        }
        if(StringUtils.isNotEmpty(krgdTextField.getText())) {
            setFrequency(list, krgdTextField.getText(), krgdComboBox.getSelectionModel().getSelectedItem().getValue(), "koreanReferenceGenomeDatabase");
        }
        if(StringUtils.isNotEmpty(kohbraTextField.getText())) {
            setFrequency(list, kohbraTextField.getText(), kohbraComboBox.getSelectionModel().getSelectedItem().getValue(), "kohbraFreq");
        }
        if(StringUtils.isNotEmpty(exacTextField.getText())) {
            setFrequency(list, exacTextField.getText(), exacComboBox.getSelectionModel().getSelectedItem().getValue(), "exac");
        }

        if("somatic".equalsIgnoreCase(panel.getAnalysisType())) {
            if (StringUtils.isNotEmpty(gnomADAllTextField.getText())) {
                setFrequency(list, gnomADAllTextField.getText(), gnomADAllComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADall");
            }
            if (StringUtils.isNotEmpty(gnomADmaTextField.getText())) {
                setFrequency(list, gnomADmaTextField.getText(), gnomADmaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADadmixedAmerican");
            }
            if (StringUtils.isNotEmpty(gnomADaaaTextField.getText())) {
                setFrequency(list, gnomADaaaTextField.getText(), gnomADaaaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADafricanAfricanAmerican");
            }
            if (StringUtils.isNotEmpty(gnomADeaTextField.getText())) {
                setFrequency(list, gnomADeaTextField.getText(), gnomADeaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADeastAsian");
            }
            if (StringUtils.isNotEmpty(gnomADfinTextField.getText())) {
                setFrequency(list, gnomADfinTextField.getText(), gnomADfinComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADfinnish");
            }
            if (StringUtils.isNotEmpty(gnomADnfeTextField.getText())) {
                setFrequency(list, gnomADnfeTextField.getText(), gnomADnfeComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADnonFinnishEuropean");
            }
            if (StringUtils.isNotEmpty(gnomADotherTextField.getText())) {
                setFrequency(list, gnomADotherTextField.getText(), gnomADotherComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADothers");
            }
            if (StringUtils.isNotEmpty(gnomADsaTextField.getText())) {
                setFrequency(list, gnomADsaTextField.getText(), gnomADsaComboBox.getSelectionModel().getSelectedItem().getValue(), "gnomADsouthAsian");
            }
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

        if("SOMATIC".equalsIgnoreCase(panel.getAnalysisType())) {
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
        if (clinVarDrugResponseCheckBox.isSelected()) {
            list.add("clinVarClass drug_response");
        }

        if(StringUtils.isNotEmpty(geneTextField.getText())) {

            String[] geneList = geneTextField.getText().replaceAll(" ", "").split(",");

            for(String gene : geneList) {
                list.add("gene " + gene);
            }
        }

        if(StringUtils.isNotEmpty(chromosomeTextField.getText())) {

            String[] chrList = chromosomeTextField.getText().replaceAll(" ", "").split(",");

            for(String chr : chrList) {
                list.add("chromosome " + chr);
            }
        }

        if(snvCheckBox.isSelected()) {
            list.add("variantType snv");
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
                StringUtils.isNotEmpty(cosmicOccurrenceComboBox.getSelectionModel().getSelectedItem().getValue())) {
            list.add("cosmicOccurrence " + cosmicOccurrenceComboBox.getSelectionModel().getSelectedItem().getValue());
        }

        if(warningCheckComboBox.getCheckModel().getCheckedItems() != null && !warningCheckComboBox.getCheckModel().isEmpty()) {
            warningCheckComboBox.getCheckModel().getCheckedItems().forEach(item -> list.add("warningReason " + item));
        }

        if(StringUtils.isEmpty(endFractionTextField.getText()) && StringUtils.isNotEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction gt:" + startFractionTextField.getText());
        } else if(StringUtils.isNotEmpty(endFractionTextField.getText()) && StringUtils.isEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction lt:" + endFractionTextField.getText());
        } else if(StringUtils.isNotEmpty(endFractionTextField.getText()) && StringUtils.isNotEmpty(startFractionTextField.getText())) {
            list.add("alleleFraction gt:" + startFractionTextField.getText() + ",lt:" + endFractionTextField.getText() + ",and");
        }

        if(StringUtils.isEmpty(depthEndCountTextField.getText()) && StringUtils.isNotEmpty(depthCountTextField.getText())) {
            list.add("readDepth gt:" + depthCountTextField.getText());
        } else if(StringUtils.isNotEmpty(depthEndCountTextField.getText()) && StringUtils.isEmpty(depthCountTextField.getText())) {
            list.add("readDepth lt:" + depthEndCountTextField.getText());
        } else if(StringUtils.isNotEmpty(depthEndCountTextField.getText()) && StringUtils.isNotEmpty(depthCountTextField.getText())) {
            list.add("readDepth gt:" + depthCountTextField.getText() + ",lt:" + depthEndCountTextField.getText() + ",and");
        }
        
        if(StringUtils.isEmpty(altEndCountTextField.getText()) && StringUtils.isNotEmpty(altCountTextField.getText())) {
            list.add("altReadNum gt:" + altCountTextField.getText());
        } else if(StringUtils.isNotEmpty(altEndCountTextField.getText()) && StringUtils.isEmpty(altCountTextField.getText())) {
            list.add("altReadNum lt:" + altEndCountTextField.getText());
        } else if(StringUtils.isNotEmpty(altEndCountTextField.getText()) && StringUtils.isNotEmpty(altCountTextField.getText())) {
            list.add("altReadNum gt:" + altCountTextField.getText() + ",lt:" + altEndCountTextField.getText() + ",and");
        }
    }

    private void resetFilterList() {
        warningCheckComboBox.getCheckModel().clearChecks();
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
        clinVarACheckBox.setSelected(false);
        clinVarBCheckBox.setSelected(false);
        clinVarCCheckBox.setSelected(false);
        clinVarDCheckBox.setSelected(false);
        clinVarECheckBox.setSelected(false);
        cosmicOccurrenceComboBox.getSelectionModel().clearSelection();
        depthCountTextField.setText("");
        altCountTextField.setText("");
        depthEndCountTextField.setText("");
        altEndCountTextField.setText("");

    }
}
