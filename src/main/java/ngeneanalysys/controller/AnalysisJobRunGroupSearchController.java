package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Run;
import ngeneanalysys.model.RunGroupForPaging;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-09-13
 */
public class AnalysisJobRunGroupSearchController extends BaseStageController {
    private static Logger logger = LoggerUtil.getLogger();

    /** content Area Pane */
    @FXML
    private Pane contentsArea;

    /** 검색박스 > 그룹명 */
    @FXML
    private TextField inputRefName;

    /** 검색박스 > 요청일 */
    @FXML
    private DatePicker datePickerRequestDate;

    /** 검색박스 > 시퀀서장비 */
    @FXML
    private ComboBox<ComboBoxItem> choosePlatform;

    /** 검색박스 > 검색 버튼 */
    @FXML
    private Button searchButton;

/** 목록 */

    @FXML
    private TableView<Run> list;


/** 목록 > 그룹명 컬럼 */

    @FXML
    private TableColumn<Run, String> columnRefName;

/** 목록 > 시퀀서 정보 컬럼 */

    @FXML
    private TableColumn<Run, String> columnPlatform;

/** 목록 > 요청일 컬럼 */

    @FXML
    private TableColumn<Run, String> columnRequestDate;


/** 목록 > 선택컬럼 */

    @FXML
    private TableColumn<Run, Boolean> columnSelect;


    /** 페이징 목록 */
    @FXML
    private Pagination paginationList;

    /** API Service */
    private APIService apiService;

    /** 분석자 Past Results Controller Class (Opener Controller Class) */
    private PastResultsController pastResultsController;

    /**
     * @param pastResultsController
     */
    public void setPastResultsController(PastResultsController pastResultsController) {
        this.pastResultsController = pastResultsController;
    }

    /**
     * 화면 출력
     */
    @Override
    public void show(Parent root) throws IOException {
        logger.info("AnalysisJobRunGroupSearchController show..");

        // api service init..
        apiService = APIService.getInstance();
        apiService.setStage(pastResultsController.getMainController().getPrimaryStage());

        logger.info("datePickerRequestDate init..");
        String dateFormat = "yyyy-MM-dd";
        datePickerRequestDate.setConverter(DatepickerConverter.getConverter(dateFormat));
        datePickerRequestDate.setPromptText(dateFormat);

        logger.info("choosePlatform init..");
        choosePlatform.setConverter(new ComboBoxConverter());
        choosePlatform.getItems().add(new ComboBoxItem());
        for (SequencerCode code : SequencerCode.values()) {
            choosePlatform.getItems().add(new ComboBoxItem(code.name(), code.getDescription()));
        }
        choosePlatform.getSelectionModel().selectFirst();

        // 목록 컬럼 설정
        columnRefName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        //columnSamples.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getSamples() != null) ? String.valueOf(cellData.getValue().getSamples().length) : "0"));
        columnPlatform.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequencingPlatform()));
        columnRequestDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt().toString()));
        columnSelect.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null));
        columnSelect.setCellFactory(param -> {
                TableCell<Run,Boolean> cell = new TableCell<Run, Boolean>() {
                    @Override
                    public void updateItem(Boolean value, boolean empty) {
                        if(!empty) {
                            Button button = new Button("CHOOSE");
                            button.setPrefWidth(65);
                            button.getStyleClass().add("btn_choose");
                            button.setOnAction(e -> {
                                Run run = param.getTableView().getItems().get(this.getIndex());
                                returnJobRunGroup(run);
                            });
                            setGraphic(button);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
                return cell;
        });

        // 페이지 이동 이벤트 바인딩
        paginationList.setPageFactory(pageIndex -> {
                setList(pageIndex + 1);
                return new VBox();
        });

        // Create the dialog Stage
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Select Run");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        contentsArea.getChildren().add(maskerPane);
        maskerPane.setPrefWidth(700);
        maskerPane.setPrefHeight(510);
        maskerPane.setVisible(false);

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    /**
     * 목록 검색
     */
    @FXML
    public void search() {
        setList(1);
    }

    /**
     * 목록 조회 화면 출력
     * @param page
     */
    public void setList(int page) {
        //this.list.setItems(null);
        maskerPane.setVisible(true);

        int totalCount = 0;
        // 화면 출력 row수
        int limit = 10;
        // 조회 시작 index
        int offset = (page - 1) * limit;

        Map<String, Object> param = new HashMap<>();
        param.put("format", "json");
        param.put("limit", limit);
        param.put("offset", offset);
        /** 검색 항목 설정 Start */
        // Platform
        if(choosePlatform.getSelectionModel().getSelectedIndex() > -1 && choosePlatform.getValue() != null) {
            param.put("sequencer", choosePlatform.getValue().getValue());
        }

        // request date
        if(datePickerRequestDate.getValue() != null && !StringUtils.isEmpty(datePickerRequestDate.getValue().toString())) {
            param.put("request_date", datePickerRequestDate.getValue().toString());
        }
        // ref name
        if(!StringUtils.isEmpty(inputRefName.getText())) {
            param.put("ref_name", inputRefName.getText());
        }
        /** End 검색 항목 설정 */
        try {
            HttpClientResponse response = apiService.get("/runs", param, null, false);

            logger.info(response.toString());
            if (response != null) {
                RunGroupForPaging runGroup = response.getObjectBeforeConvertResponseToJSON(RunGroupForPaging.class);
                if (runGroup != null) {
                    totalCount = runGroup.getCount();
                    this.list.setItems(FXCollections.observableArrayList(runGroup.getList()));
                } else {
                    this.list.setItems(null);
                }
                paginationList.setCurrentPageIndex(page - 1);
                int pageCount = totalCount / limit;
                if (totalCount % limit > 0) {
                    pageCount++;
                }
                paginationList.setPageCount(pageCount);

            } else { //검색 결과가 없는 경우 에러 아님.
                this.list.setItems(null);
                paginationList.setPageCount(0);
            }
        } catch (WebAPIException wae) {
            // DialogUtil.error(null, "Running and Recent Samples Search
            // Error.", getMainApp().getPrimaryStage(), true);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
        maskerPane.setVisible(false);
    }

    /**
     * 선택 분석 요청 그룹 부모창 컨트롤러로 리턴
     * @param run
     */
    public void returnJobRunGroup(Run run) {
        this.pastResultsController.setSearchJobRunGroupInfo(run.getId(), run.getName());
        // dialog close
        ((Stage) searchButton.getScene().getWindow()).close();
    }
}
