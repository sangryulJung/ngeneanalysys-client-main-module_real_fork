package ngeneanalysys.controller.fragment;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.AnalysisDetailSNVController;
import ngeneanalysys.controller.ExcludeReportDialogController;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedSameVariantInterpretation;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-03-19
 */
public class AnalysisDetailInterpretationController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Label swTierLabel;
    @FXML
    private Label userTierLabel;

    @FXML
    private Label arrow;

    @FXML
    private CheckBox addToReportCheckBox;

    @FXML
    private TableView<SameVariantInterpretation> pastCasesTableView;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesSampleColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesTypeColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesEvidenceColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesInterpretationColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesEvidenceCommentColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesDateColumn;

    @FXML
    private TableView<SameVariantInterpretation> interpretationTableView;

    @FXML
    private TableColumn<SameVariantInterpretation, String> interpretationTypeColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> interpretationEvidenceColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> interpretationInterpretationColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> interpretationEvidenceCommentColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> interpretationDateColumn;

    @FXML
    private GridPane interpretationGridPane;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    private APIService apiService;

    private AnalysisDetailSNVController analysisDetailSNVController;

    private Sample sample;

    /**
     * @param analysisDetailSNVController
     */
    public void setAnalysisDetailSNVController(AnalysisDetailSNVController analysisDetailSNVController) {
        this.analysisDetailSNVController = analysisDetailSNVController;
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        sample = (Sample)getParamMap().get("sample");
        selectedAnalysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        if(StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getExpertTier())) arrow.setVisible(false);
        addToReportCheckBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addToReportBtn(addToReportCheckBox ));
        checkBoxSetting(addToReportCheckBox, selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport());

        interpretationTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelInterpretation().getClinicalVariantType()));
        //interpretationInterpretationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        //interpretationEvidenceCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));
        //interpretationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(org.apache.commons.lang3.time.DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));

        pastCasesSampleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleName()));
        pastCasesTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleName()));
        pastCasesEvidenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleName()));
        pastCasesInterpretationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTier()));
        pastCasesEvidenceCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleName()));
        //pastCasesDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd hh:mm:ss")));

        //setInterpretationTable();
        //setPastCases();
        setTier(selectedAnalysisResultVariant.getSnpInDel());

    }

    public void setGridPaneWidth(double size) {
        interpretationGridPane.setPrefWidth(size);
    }

    public void setInterpretationTable() {
        if(interpretationTableView.getItems() != null) interpretationTableView.getItems().removeAll(interpretationTableView.getItems());
        try {

            // Memo 데이터 API 요청
            //Map<String, Object> commentParamMap = new HashMap<>();
            HttpClientResponse responseMemo = apiService.get("/analysisResults/evidenceLog/" + selectedAnalysisResultVariant.getSnpInDel().getId()
                    , null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            PagedSameVariantInterpretation memoList = responseMemo.getObjectBeforeConvertResponseToJSON(PagedSameVariantInterpretation.class);
            if(!memoList.getResult().isEmpty()) interpretationTableView.getItems().addAll(memoList.getResult());

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void setPastCases() {
        if(pastCasesTableView.getItems() != null) pastCasesTableView.getItems().removeAll(pastCasesTableView.getItems());
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("chromosome", selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome());
            params.put("gene", selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene());
            params.put("ntChange", selectedAnalysisResultVariant.getSnpInDel().getSnpInDelExpression().getNtChange());

            HttpClientResponse response = apiService.get("/analysisResults/sameVariantInterpretations/" + selectedAnalysisResultVariant.getSnpInDel().getSampleId()
                    , params, null, false);

            List<SameVariantInterpretation> sameList = (List<SameVariantInterpretation>)response.getMultiObjectBeforeConvertResponseToJSON(SameVariantInterpretation.class, false);
            logger.info(sameList.size() + "");
            if( sameList != null && !sameList.isEmpty()) {
                pastCasesTableView.getItems().addAll(FXCollections.observableArrayList(sameList));
            }

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void addToReportBtn(CheckBox checkBox) {
        if(selectedAnalysisResultVariant != null) {
            String oldSymbol = selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport();
            if (checkBox.isSelected()) {
                try {
                    FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                    Node node = loader.load();
                    ExcludeReportDialogController excludeReportDialogController = loader.getController();
                    excludeReportDialogController.setMainController(mainController);
                    excludeReportDialogController.settingItem("Y", selectedAnalysisResultVariant, checkBox);
                    excludeReportDialogController.show((Parent) node);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                if(!oldSymbol.equals(selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport()))
                    analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
            } else {
                try {
                    FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                    Node node = loader.load();
                    ExcludeReportDialogController excludeReportDialogController = loader.getController();
                    excludeReportDialogController.setMainController(mainController);
                    excludeReportDialogController.settingItem("N", selectedAnalysisResultVariant, checkBox);
                    excludeReportDialogController.show((Parent) node);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if(!oldSymbol.equals(selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport()))
                analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
        }
    }

    public void checkBoxSetting(CheckBox checkBox, String Symbol) {
        if("Y".equals(Symbol)) {
            checkBox.setSelected(true);
        } else {
            checkBox.setSelected(false);
        }
    }

    public void returnTierClass(String tier, Label label) {
        label.setAlignment(Pos.CENTER);

        if(label == userTierLabel) arrow.setVisible(true);

        label.getStyleClass().removeAll(label.getStyleClass());
        if(!StringUtils.isEmpty(tier)) {
            if (tier.equalsIgnoreCase("T1")) {
                label.setText("T1");
                label.getStyleClass().add("tier_one");
            } else if (tier.equalsIgnoreCase("T2")) {
                label.setText("T2");
                label.getStyleClass().add("tier_two");
            } else if (tier.equalsIgnoreCase("T3")) {
                label.setText("T3");
                label.getStyleClass().add("tier_three");
            } else if (tier.equalsIgnoreCase("T4")) {
                label.setText("T4");
                label.getStyleClass().add("tier_four");
            }
        }
    }



    @FXML
    public void saveInterpretation() {
       /*
        params.put("snpInDelInterpretation", snpInDelInterpretation);

        if(!StringUtils.isEmpty(comment)) {
            params.put("comment", comment);
            try {
                params.put("tier", tier);

                params.put("snpInDelId", variantAndInterpretationEvidence.getSnpInDel().getId());

                apiService.put("analysisResults/snpInDels/" + variantAndInterpretationEvidence.getSnpInDel().getId() + "/updateTier", params, null, true);
                returnTierClass(tier, userTierLabel);
                analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
            } catch (WebAPIException wae) {
                wae.printStackTrace();
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }

        } else {
                DialogUtil.warning("The comment field is empty", " ", mainController.getPrimaryStage(), true);
            commentTextField.requestFocus();
        }*/
    }

    public void setTier(SnpInDel snpInDel) {
        returnTierClass(snpInDel.getSwTier(), swTierLabel);
        returnTierClass(snpInDel.getExpertTier(), userTierLabel);
    }


}
