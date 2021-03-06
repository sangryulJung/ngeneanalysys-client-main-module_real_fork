package ngeneanalysys.controller.manager;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private TableColumn<Run, String> createAt;

    @FXML
    private TableColumn<Run, Boolean> restart;

    @FXML
    private TableColumn<Run, Boolean> delete;

    @FXML
    private TableColumn<Run, String> loginIdColumn;

    @FXML
    private TableColumn<Run, String> memberNameColumn;

    @FXML
    private TableColumn<Run, String> groupNameColumn;


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
        status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRunStatus().getStatus()));
        loginIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoginId()));
        memberNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberName()));
        groupNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberGroupName()));
        createAt.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));

        /* 삭제 버튼 */
        delete.setSortable(false);
        delete.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        delete.setCellFactory(param -> new DeleteButtonCreate());

        /* 재시작 버튼 */
        restart.setSortable(false);
        restart.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        restart.setCellFactory(param -> new UpdateButtonCreate());

        pageText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(CommonConstants.NUMBER_PATTERN)) pageText.setText(oldValue);
        });


        this.mainController.getMainFrame().setCenter(root);
    }

    public void setList(int page) {

        int totalCount = 0;
        int limit = 15;
        int offset = (page - 1)  * limit;

        HttpClientResponse response;

        try {
            Map<String, Object> param = getSearchParam();
            param.put("limit", limit);
            param.put("offset", offset);
            param.put("ordering", "-id");

            response = apiService.get("/admin/runs", param, null, false);

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
                if (list != null) {
                    listTable.setItems((FXCollections.observableList(list)));
                }
                logger.debug(String.format("total count : %s, page count : %s", totalCount, pageCount));

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
            logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
            DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

    }

    private Map<String, Object> getSearchParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("format", "json");

        if(searchStatus.getSelectionModel().getSelectedIndex() != -1 && searchStatus.getValue() != null) {
            param.put("status", searchStatus.getValue().getValue());
        }

        if(!StringUtils.isEmpty(userNameText.getText()) && userNameText.getText().trim().length() != 0){
            param.put("user_name", userNameText.getText());
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
        setList(1);
    }

    @FXML
    public void pageMove() {
        if(!StringUtils.isEmpty(pageText.getText()) && pageText.getText().trim().length() != 0) {
            try {
                int page = Integer.parseInt(pageText.getText());
                setList(page);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @FXML
    public void reset() {
        searchStatus.setValue(new ComboBoxItem());
        runIdText.setText(null);
        runNameText.setText(null);
        userIdText.setText(null);
        userNameText.setText(null);
    }

    @FXML
    public void prevMove() {
        int pageIndex = paginationList.getCurrentPageIndex();

        int movePage = pageIndex / 10;

        if(movePage == 0)
            paginationList.setCurrentPageIndex(0);
        else
            paginationList.setCurrentPageIndex(((movePage - 1) * 10));
    }

    @FXML
    public void nextMove() {
        int totalPage = paginationList.getPageCount();
        int pageIndex = paginationList.getCurrentPageIndex();

        int movePage = pageIndex / 10;

        if(totalPage == pageIndex)
            paginationList.setCurrentPageIndex(totalPage);
        else {
            int tempTotalPage = totalPage / 10;
            if(movePage == tempTotalPage)
                paginationList.setCurrentPageIndex(totalPage);
            else
                paginationList.setCurrentPageIndex(((movePage + 1) * 10));
        }
    }

    /** 삭제 버튼 생성 */
    private class DeleteButtonCreate extends TableCell<Run, Boolean> {
        HBox box = null;
        final ImageView img = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        DeleteButtonCreate() {
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogUtil.setIcon(alert);
                String alertContentText = "Are you sure to delete this run?";

                alert.setTitle("Confirmation Dialog");
                Run run = DeleteButtonCreate.this.getTableView().getItems().get(
                        DeleteButtonCreate.this.getIndex());
                alert.setHeaderText(run.getName());
                alert.setContentText(alertContentText);
                logger.debug(run.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK) {
                    deleteRun(run.getId());
                } else {
                    result.ifPresent(buttonType -> logger.debug(buttonType + " : button select"));
                    alert.close();
                }
            });
        }

        /** 작업 삭제 */
        private void deleteRun(Integer id) {
            try {
                HttpClientResponse response = apiService.delete("/admin/runs/"+id);
                logger.debug("status code : " + response.getStatus());
                listTable.getItems().clear();
                search();
            } catch (WebAPIException wae) {
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                        getMainApp().getPrimaryStage(), true);
                wae.printStackTrace();
            } catch (Exception e) {
                logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
                DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
            }
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);

            if(item == null) {
                setGraphic(null);
                return;
            }
            img.getStyleClass().add("cursor_hand");
            box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(img);

            setGraphic(box);
        }
    }

    /** 재실행 버튼 생성 */
    private class UpdateButtonCreate extends TableCell<Run, Boolean> {
        HBox box = null;
        final ImageView img = new ImageView(resourceUtil.getImage("/layout/images/refresh.png", 18, 18));

        private void restartRun(Integer id) {
            try {
                apiService.get("admin/restartRun/" + id, null ,null, false);
            } catch(WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }

        }

        UpdateButtonCreate() {
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Run run = UpdateButtonCreate.this.getTableView().getItems().get(UpdateButtonCreate.this.getIndex());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogUtil.setIcon(alert);
                String alertContentText = "Are you sure to restart this run?";
                alert.setTitle("Confirmation Dialog");

                alert.setHeaderText(run.getName());
                alert.setContentText(alertContentText);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK) {
                    restartRun(run.getId());
                } else {
                    alert.close();
                }
            });

        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);

            if(item == null) {
                setGraphic(null);
                return;
            }
            img.getStyleClass().add("cursor_hand");
            box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(img);

            setGraphic(box);
        }
    }

}
