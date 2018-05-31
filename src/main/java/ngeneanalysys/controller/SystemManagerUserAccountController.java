package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.SystemManagerUserGroupPaging;
import ngeneanalysys.model.SystemManagerUserInfoPaging;
import ngeneanalysys.model.User;
import ngeneanalysys.model.UserGroup;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
public class SystemManagerUserAccountController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<User> userListTable;

    @FXML
    private TableView<UserGroup> groupListTable;

    /** 목록 페이징 */
    @FXML
    private Pagination paginationList;

    @FXML
    private Pagination groupPaginationList;

    private APIService apiService;

    @FXML
    private TableColumn<User, Integer> id;

    @FXML
    private TableColumn<User, String> loginId;

    @FXML
    private TableColumn<User, String> name;

    @FXML
    private TableColumn<User, String> role;

    @FXML
    private TableColumn<User, Integer> groupId;

    @FXML
    private TableColumn<User, String> groupName;

    @FXML
    private TableColumn<User, String> createdAt;

    @FXML
    private TableColumn<User, String> updatedAt;

    @FXML
    private TableColumn<User, String> deletedAt;

    @FXML
    private TableColumn<User, String> deleted;

    @FXML
    private TableColumn<User, Boolean> userModify;

    @FXML
    private TableColumn<User, Boolean> userCheckBox;

    @FXML
    private TableColumn<User, Boolean> userDelete;

    @FXML
    private Button resetBtn;

    @FXML
    private TextField userIdText;

    @FXML
    private TextField userNameText;

    @FXML
    private ComboBox<ComboBoxItem> searchUserType;

    @FXML
    private ComboBox<ComboBoxItem> searchGroupName;

    @FXML
    private TableColumn<UserGroup, Integer> userGroupId;

    @FXML
    private TableColumn<UserGroup, String> userGroupName;

    @FXML
    private TableColumn<UserGroup, String> userGroupDeleted;

    @FXML
    private TableColumn<UserGroup, Boolean> userGroupModify;

    @FXML
    private TextField groupNameText;

    @FXML
    private Button groupSearchBtn;

    @FXML
    private Button groupAddBtn;

    @FXML
    private Button userAddBtn;

    @Override
    public void show(Parent root) throws IOException {

        apiService = APIService.getInstance();

        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        loginId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoginId()));
        name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        role.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        groupId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMemberGroupId()).asObject());
        groupName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberGroupName()));
        createdAt.setCellValueFactory(cellData -> new SimpleStringProperty(timeConvertString(cellData.getValue().getCreatedAt())));
        updatedAt.setCellValueFactory(cellData -> new SimpleStringProperty(timeConvertString(cellData.getValue().getUpdatedAt())));
        deletedAt.setCellValueFactory(cellData -> new SimpleStringProperty(timeConvertString(cellData.getValue().getDeletedAt())));
        deleted.setCellValueFactory(cellData -> new SimpleStringProperty(
                deletedShowString(cellData.getValue().getDeleted())));
        userModify.setSortable(false);
        userModify.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        userModify.setCellFactory(param -> new UserEditAndDeleteButton());

        /**
         * UserGroup TableView
         */
        userGroupId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        userGroupName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        userGroupDeleted.setCellValueFactory(cellData -> new SimpleStringProperty(
                deletedShowString(cellData.getValue().getDeleted())));

        userGroupModify.setSortable(false);
        userGroupModify.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        userGroupModify.setCellFactory(param -> new UserGroupButton());

        paginationList.setPageFactory(pageIndex -> {
            setList(pageIndex + 1);
            return new VBox();
        });

        groupPaginationList.setPageFactory(pageIndex -> {
            setGroupList(pageIndex + 1);
            return new VBox();
        });

    }

    private String timeConvertString(DateTime date) {
        if(date == null)
            return "-";

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return fmt.print(date);
    }

    private String deletedShowString(int deleted) {
        return (deleted == 0) ? "N" : "Y";
    }

    @FXML
    public void userSearch() {
        setList(1);
    }

    @FXML
    public void groupSearch() {
        setGroupList(1);
    }

    @FXML
    public void reset() {
        userIdText.setText(null);
        userNameText.setText(null);
        searchUserType.setValue(new ComboBoxItem());
        searchGroupName.setValue(new ComboBoxItem());
    }

    @FXML
    public void groupAdd() {
        groupControllerInit("Add", null);
    }

    @FXML
    public void userAdd() {
        userControllerInit("add", null);
    }

    private void userControllerInit(String type, User user) {
        try {
            FXMLLoader loader = mainApp.load(FXMLConstants.USER_ACCOUNT);
            Node root = loader.load();
            UserAccountController userAccountController = loader.getController();
            userAccountController.setMainController(this.getMainController());
            userAccountController.init(type, user);
            userAccountController.show((Parent) root);
            userSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setList(int page) {

        int totalCount = 0;
        int limit = 17;
        int offset = (page - 1)  * limit;

        HttpClientResponse response = null;

        try {
            Map<String, Object> param = getUserSearchParam();
            param.put("limit", limit);
            param.put("offset", offset);

            response = apiService.get("/admin/members", param, null, false);

            if(response != null) {
                SystemManagerUserInfoPaging systemManagerUserInfoPaging =
                        response.getObjectBeforeConvertResponseToJSON(SystemManagerUserInfoPaging.class);
                List<User> list = null;

                if(systemManagerUserInfoPaging != null) {
                    totalCount = systemManagerUserInfoPaging.getCount();
                    list = systemManagerUserInfoPaging.getList();
                }

                int pageCount = 0;

                if(totalCount > 0) {
                    pageCount = totalCount / limit;
                    paginationList.setCurrentPageIndex(page - 1);
                    if(totalCount % limit > 0) {
                        pageCount++;
                    }
                }

                userListTable.setItems(FXCollections.observableList(list));

                logger.debug(String.format("total count : %s, page count : %s", totalCount, pageCount));

                if (pageCount > 0) {
                    paginationList.setVisible(true);
                    paginationList.setPageCount(pageCount);
                } else {
                    paginationList.setVisible(false);
                }
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void setGroupList(int page) {

        int totalCount = 0;
        int limit = 5;
        int offset = (page - 1)  * limit;

        HttpClientResponse response = null;

        try {
            Map<String, Object> param = getGroupSearchParam();
            param.put("limit", limit);
            param.put("offset", offset);

            response = apiService.get("/admin/memberGroups", param, null, false);

            if(response != null) {
                SystemManagerUserGroupPaging systemManagerUserGroupPaging =
                        response.getObjectBeforeConvertResponseToJSON(SystemManagerUserGroupPaging.class);
                List<UserGroup> list = null;

                if(systemManagerUserGroupPaging != null) {
                    totalCount = systemManagerUserGroupPaging.getCount();
                    list = systemManagerUserGroupPaging.getList();
                }

                int pageCount = 0;

                if(totalCount > 0) {
                    pageCount = totalCount / limit;
                    groupPaginationList.setCurrentPageIndex(page - 1);
                    if(totalCount % limit > 0) {
                        pageCount++;
                    }
                }

                groupListTable.setItems((FXCollections.observableList(list)));

                logger.debug(String.format("total count : %s, page count : %s", totalCount, pageCount));

                if (pageCount > 0) {
                    groupPaginationList.setVisible(true);
                    groupPaginationList.setPageCount(pageCount);
                } else {
                    groupPaginationList.setVisible(false);
                }
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

    }

    private Map<String, Object> getUserSearchParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("format", "json");
        param.put("ordering", "-id");

        if(searchUserType.getSelectionModel().getSelectedIndex() != -1 && searchUserType.getValue() != null) {

            param.put("user_type", searchUserType.getValue().getValue());
        }
        if(searchGroupName.getSelectionModel().getSelectedIndex() != -1 && searchGroupName.getValue() != null) {
            param.put("group_name", searchGroupName.getValue().getValue());
        }
        if(!StringUtils.isEmpty(userIdText.getText()) && userIdText.getText().trim().length() != 0){
            param.put("login_id", userIdText.getText());
        }
        if(!StringUtils.isEmpty(userNameText.getText()) && userNameText.getText().trim().length() != 0){
            param.put("name", userNameText.getText());
        }

        return param;
    }

    private Map<String, Object> getGroupSearchParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("format", "json");
        param.put("ordering", "-id");

        if(!StringUtils.isEmpty(groupNameText.getText()) && groupNameText.getText().trim().length() != 0){
            param.put("name", groupNameText.getText());
        }

        return param;
    }

    /** 그룹 삭제 */
    public void deleteGroup(Integer id) {
        try {
            apiService.delete("/admin/memberGroups/"+id);
            groupListTable.getItems().clear();
            groupSearch();
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void groupControllerInit(String type, UserGroup group) {
        try {
            FXMLLoader loader = null;
            loader = mainApp.load(FXMLConstants.GROUP_ADD);
            Node root = loader.load();
            GroupAddController groupAddController = loader.getController();
            groupAddController.init(type, group);
            groupAddController.setMainController(this.getMainController());
            groupAddController.show((Parent) root);
            groupSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void groupNameComboBoxCreate() {
        searchGroupName.setConverter(new ComboBoxConverter());
        searchGroupName.getItems().add(new ComboBoxItem());

        HttpClientResponse response = null;

        try {
            response = apiService.get("/admin/memberGroups", null, null, false);
            logger.debug(response.getContentString());
            if(response != null) {
                SystemManagerUserGroupPaging systemManagerUserGroupPaging =
                        response.getObjectBeforeConvertResponseToJSON(SystemManagerUserGroupPaging.class);

                for(UserGroup group : systemManagerUserGroupPaging.getList()) {
                    searchGroupName.getItems().add(new ComboBoxItem(
                            group.getName(), group.getName()));
                }

                searchGroupName.getSelectionModel().selectFirst();
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }


    private class UserGroupButton extends TableCell<UserGroup, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        public UserGroupButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                UserGroup group = UserGroupButton.this.getTableView().getItems().get(
                        UserGroupButton.this.getIndex());

                groupControllerInit("modify", group);

                groupListTable.getItems().clear();
                groupSearch();
                searchGroupName.getSelectionModel().clearSelection();
                searchGroupName.getItems().clear();
                groupNameComboBoxCreate();
            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                String alertHeaderText = "Confirmation Dialog";
                String alertContentText = "Are you sure to delete this group?";

                alert.setTitle(alertHeaderText);
                UserGroup group = UserGroupButton.this.getTableView().getItems().get(
                        UserGroupButton.this.getIndex());
                alert.setHeaderText(group.getName());
                alert.setContentText(alertContentText);
                logger.debug(group.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deleteGroup(group.getId());
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

    private class UserEditAndDeleteButton extends TableCell<User, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        public UserEditAndDeleteButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                User user = UserEditAndDeleteButton.this.getTableView().getItems().get(
                        UserEditAndDeleteButton.this.getIndex());

                userControllerInit("modify", user);

                groupListTable.getItems().clear();
                groupSearch();
                searchGroupName.getSelectionModel().clearSelection();
                searchGroupName.getItems().clear();
                groupNameComboBoxCreate();
            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogUtil.setIcon(alert);
                String alertHeaderText = "";
                String alertContentText = "Are you sure to delete this user?";

                alert.setTitle("Confirmation Dialog");
                User user = UserEditAndDeleteButton.this.getTableView().getItems().get(
                        UserEditAndDeleteButton.this.getIndex());
                alert.setHeaderText(user.getName());
                alert.setContentText(alertContentText);
                logger.debug(user.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deleteUser(user.getId());
                } else {
                    logger.debug(result.get() + " : button select");
                    alert.close();
                }
            });
        }
        /** 사용자 삭제 */
        private void deleteUser(Integer userId) {
            try {
                apiService.delete("/admin/members/" + userId);
                userListTable.getItems().clear();
                userSearch();
            } catch (WebAPIException wae) {
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                        getMainApp().getPrimaryStage(), true);
            } catch (Exception e) {
                logger.error("Unknown Error", e);
                DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
            }
        }
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null) {
                setGraphic(null);
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