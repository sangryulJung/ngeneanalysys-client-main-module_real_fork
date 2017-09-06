package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Run;
import ngeneanalysys.model.RunGroupForPaging;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class SystemManagerAnalysisStatusController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<Run> listTable;

    /** 목록 페이징 */
    @FXML
    private Pagination paginationList;

    private APIService apiService;

    @FXML
    private ComboBox<ComboBoxItem> searchStatus;

    @FXML
    private ComboBox<ComboBoxItem> searchPanelContain;

    @FXML
    private TextField runIdText;

    @FXML
    private TextField runNameText;

    @FXML
    private TextField userIdText;

    @FXML
    private TextField userNameText;

    @FXML
    private TextField pageText;

    @FXML
    private Button searchBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button pageButton;

    @FXML
    private Button prevBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private TableColumn<Run, Integer> id;

    @FXML
    private TableColumn<Run, String> runName;

    @FXML
    private TableColumn<Run, String> status;

    @FXML
    private TableColumn<Run, Integer> sc;

    @FXML
    private TableColumn<Run, Integer> userId;

    @FXML
    private TableColumn<Run, String> userName;

    @FXML
    private TableColumn<Run, String> createAt;

    @FXML
    private TableColumn<Run, Boolean> restart;

    @FXML
    private TableColumn<Run, Boolean> delete;


    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        paginationList.setPageFactory(pageIndex -> {
            setList(pageIndex + 1);
            return new VBox();
        });

        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        runName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        userId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMemberGroupId()).asObject());
        createAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt().toString()));
        sc.setCellValueFactory(cellData -> new SimpleIntegerProperty(
                cellData.getValue().getId()).asObject());
        userName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));


        this.mainController.getMainFrame().setCenter(root);
    }

    public void setList(int page) {

        int totalCount = 0;
        int limit = 17;
        int offset = (page - 1)  * limit;

        HttpClientResponse response = null;

        try {
            Map<String, Object> param = getSearchParam();
            param.put("limit", limit);
            param.put("offset", offset);
            param.put("ordering", "-id");

            response = apiService.get("/admin/runs", param, null, false);
            logger.info(response.getContentString());
            if(response != null) {
                RunGroupForPaging managerAnalysisStatus =
                        response.getObjectBeforeConvertResponseToJSON(RunGroupForPaging.class);
                List<Run> list = null;
                if(managerAnalysisStatus != null){
                    totalCount = managerAnalysisStatus.getCount();
                    list = managerAnalysisStatus.getList();
                }

                int pageCount = 0;

                if(totalCount > 0) {
                    paginationList.setCurrentPageIndex(page - 1);
                    pageCount = totalCount / limit;
                    if(totalCount % limit > 0) {
                        pageCount++;
                    }
                }

                listTable.setItems((FXCollections.observableList(list)));
                logger.info(String.format("total count : %s, page count : %s", totalCount, pageCount));

                if (pageCount > 0) {
                    paginationList.setVisible(true);
                    paginationList.setPageCount(pageCount);
                } else {
                    paginationList.setVisible(false);
                }

            } else {
                paginationList.setVisible(false);
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

    }

    public Map<String, Object> getSearchParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("format", "json");

        if(searchStatus.getSelectionModel().getSelectedIndex() != -1 && searchStatus.getValue() != null) {
            param.put("status", searchStatus.getValue().getValue());
        }

        if(!StringUtils.isEmpty(userNameText.getText()) && userNameText.getText().trim().length() != 0){
            param.put("user__name", userNameText.getText());
        }

        if(!StringUtils.isEmpty(runNameText.getText()) && runNameText.getText().trim().length() != 0){
            param.put("ref_name", runNameText.getText());
        }

        if(!StringUtils.isEmpty(userIdText.getText()) && userIdText.getText().trim().length() != 0){
            param.put("user", userIdText.getText());
        }

        if(!StringUtils.isEmpty(runIdText.getText()) && runIdText.getText().trim().length() != 0){
            param.put("id", runIdText.getText());
        }

        return param;
    }

    @FXML
    public void search() {

    }

    @FXML
    public void pageMove() {

    }

    @FXML
    public void reset() {

    }

    @FXML
    public void prevMove() {

    }

    @FXML
    public void nextMove() {

    }
}
