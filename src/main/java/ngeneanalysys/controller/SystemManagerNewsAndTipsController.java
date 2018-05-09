package ngeneanalysys.controller;

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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.NoticeView;
import ngeneanalysys.model.paged.PagedNotice;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-03-27
 */
public class SystemManagerNewsAndTipsController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea contentsTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<NoticeView> newsAndTipsListTable;
    @FXML
    private TableColumn<NoticeView, Integer> noticeIdTableColumn;
    @FXML
    private TableColumn<NoticeView, String> noticeTitleTableColumn;
    @FXML
    private TableColumn<NoticeView, String> createdAtTableColumn;
    @FXML
    private TableColumn<NoticeView, String> updatedAtTableColumn;
    @FXML
    private TableColumn<NoticeView, String> deletedAtTableColumn;
    @FXML
    private TableColumn<NoticeView, String> deletedTableColumn;
    @FXML
    private TableColumn<NoticeView, Boolean> editTableColumn;
    @FXML
    private Pagination noticePagination;

    private Integer id = 0;

    @Override
    public void show(Parent root) throws IOException {

        newsAndTipsListTable.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            newsAndTipsListTable.refresh();
            // close text box
            newsAndTipsListTable.edit(-1, null);
        });

        apiService = APIService.getInstance();

        noticeIdTableColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());
        noticeTitleTableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTitle()));
        deletedTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDeleted() == 0) ? "N" : "Y"));
        editTableColumn.setSortable(false);
        editTableColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        editTableColumn.setCellFactory(param -> new NewsAndTipsModifyButton());

        createdAtTableColumn.setCellValueFactory(item -> new SimpleStringProperty(DateFormatUtils.format(
                item.getValue().getCreatedAt().toDate(), DATE_FORMAT)));
        updatedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getUpdatedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                        item.getValue().getUpdatedAt().toDate(), DATE_FORMAT));
            else
                return new SimpleStringProperty("");
        });
        deletedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getDeletedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                        item.getValue().getDeletedAt().toDate(), DATE_FORMAT));
            else
                return new SimpleStringProperty("");
        });

        noticePagination.setPageFactory(pageIndex -> {
            setNewsAndTipsListTableList(pageIndex + 1);
            return new VBox();
        });

        setDisabledItem(true);
    }

    public void setNewsAndTipsListTableList(int page) {

        int totalCount = 0;
        int limit = 17;
        int offset = (page - 1)  * limit;

        try {
            Map<String, Object> param = new HashMap<>();

            param.put("limit", limit);
            param.put("offset", offset);

            HttpClientResponse response = apiService.get("admin/notices", param, null, false);

            PagedNotice pagedNotice = response.getObjectBeforeConvertResponseToJSON(PagedNotice.class);

            if(pagedNotice != null) {
                totalCount = pagedNotice.getCount();
                newsAndTipsListTable.getItems().clear();
                newsAndTipsListTable.setItems(FXCollections.observableArrayList(pagedNotice.getResult()));
            }

            int pageCount = 0;

            if(totalCount > 0) {
                pageCount = totalCount / limit;
                noticePagination.setCurrentPageIndex(page - 1);
                if(totalCount % limit > 0) {
                    pageCount++;
                }
            }

            logger.debug("total count : " + totalCount + ", page count : " + pageCount);

            if (pageCount > 0) {
                noticePagination.setVisible(true);
                noticePagination.setPageCount(pageCount);
            } else {
                noticePagination.setVisible(false);
            }

        } catch(WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }
    }

    public void resetItem() {
        titleTextField.setText("");
        contentsTextArea.setText("");
    }

    public void setDisabledItem(boolean condition) {
        resetItem();
        titleTextField.setDisable(condition);
        contentsTextArea.setDisable(condition);
        saveButton.setDisable(condition);
    }

    @FXML
    public void save() {
        String title = titleTextField.getText();
        String contents = contentsTextArea.getText();
        if(!StringUtils.isEmpty(title) && !StringUtils.isEmpty(contents)) {
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("title", title);
                param.put("contents", contents);
                if(id == 0) {
                    apiService.post("admin/notices", param, null, true);
                } else {
                    apiService.put("admin/notices/" + id, param, null, true);
                    id = 0;
                }
                setDisabledItem(true);
                setNewsAndTipsListTableList(1);
            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
                wae.printStackTrace();
            } catch (IOException ioe) {
                DialogUtil.error(ioe.getMessage(), ioe.getMessage(), mainController.getPrimaryStage(), true);
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void noticeAdd() {
        setDisabledItem(false);
    }

    public void deleteNewsAndTips(int noticeId) {
        try {
            apiService.delete("admin/notices/" + noticeId);
        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getMessage(), mainController.getPrimaryStage(), true);
        }

    }

    private class NewsAndTipsModifyButton extends TableCell<NoticeView, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        public NewsAndTipsModifyButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                NoticeView notice = NewsAndTipsModifyButton.this.getTableView().getItems().get(
                        NewsAndTipsModifyButton.this.getIndex());

                id = notice.getId();

                setDisabledItem(false);
                titleTextField.setText(notice.getTitle());
                contentsTextArea.setText(notice.getContents());

            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                String alertHeaderText = null;
                String alertContentText = "Are you sure to delete this notice?";

                alert.setTitle("Confirmation Dialog");
                NoticeView notice = NewsAndTipsModifyButton.this.getTableView().getItems().get(
                        NewsAndTipsModifyButton.this.getIndex());
                alert.setHeaderText(notice.getTitle());
                alert.setContentText(alertContentText);
                logger.debug(notice.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deleteNewsAndTips(notice.getId());
                } else {
                    logger.debug(result.get() + " : button select");
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

            NoticeView noticeView = NewsAndTipsModifyButton.this.getTableView().getItems().get(
                    NewsAndTipsModifyButton.this.getIndex());

            if(noticeView.getDeleted() != 0) {
                return;
            }

            box = new HBox();

            box.setAlignment(Pos.CENTER);

            box.setSpacing(10);
            img1.setStyle("-fx-cursor:hand;");
            img2.setStyle("-fx-cursor:hand;");
            box.getChildren().add(img1);
            box.getChildren().add(img2);

            setGraphic(box);

        }
    }

}
