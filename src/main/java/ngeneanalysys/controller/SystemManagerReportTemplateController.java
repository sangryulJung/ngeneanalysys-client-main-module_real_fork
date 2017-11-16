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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.PagedReportTemplate;
import ngeneanalysys.model.ReportTemplate;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Jang
 * @since 2017-11-14
 */
public class SystemManagerReportTemplateController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private TextField reportNameTextField;

    @FXML
    private TextField variableNameTextField;

    @FXML
    private TextField displayNameTextField;

    @FXML
    private ComboBox<String> variableTypeComboBox;

    @FXML
    private ComboBox<String> variableListComboBox;

    @FXML
    private ComboBox<String> imageListComboBox;

    @FXML
    private Button modifyVariableButton;

    @FXML
    private Button addVariableButton;

    @FXML
    private Button removeVariableButton;

    @FXML
    private Button removeImageButton;

    @FXML
    private Button reportTemplateSelectionButton;

    @FXML
    private Button reportSaveButton;

    @FXML
    private Button selectImageButton;

    @FXML
    private TableView<ReportTemplate> reportTemplateListTable;

    @FXML
    private TableColumn<ReportTemplate, Integer> reportIdTableColumn;

    @FXML
    private TableColumn<ReportTemplate, String> reportNameTableColumn;

    @FXML
    private TableColumn<ReportTemplate, String> createdAtTableColumn;

    @FXML
    private TableColumn<ReportTemplate, String> updatedAtTableColumn;

    @FXML
    private TableColumn<ReportTemplate, String> deletedAtTableColumn;

    @FXML
    private TableColumn<ReportTemplate, String> deletedTableColumn;

    @FXML
    private TableColumn<ReportTemplate, Boolean> editReportTemplateTableColumn;

    @FXML
    private Pagination reportTemplatePagination;

    //vm 파일 내용
    private String contents = null;

    //사용할 이미지 모음
    private List<File> imageList = null;

    private Map<String, Object> variableNameList = new HashMap<>();

    private Integer id = 0;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("system manager report template init");

        apiService = APIService.getInstance();

        imageList = new ArrayList<>();

        reportIdTableColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());
        reportNameTableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        deletedTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDeleted() == 0) ? "N" : "Y"));
        editReportTemplateTableColumn.setSortable(false);
        editReportTemplateTableColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        editReportTemplateTableColumn.setCellFactory(param -> new ReportTemplateModifyButton());

        createdAtTableColumn.setCellValueFactory(item -> new SimpleStringProperty(DateFormatUtils.format(
                item.getValue().getCreatedAt().toDate(), "yyyy-MM-dd")));
        updatedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getUpdatedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                        item.getValue().getUpdatedAt().toDate(), "yyyy-MM-dd"));
            else
                return new SimpleStringProperty("");
        });
        deletedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getDeletedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                        item.getValue().getDeletedAt().toDate(), "yyyy-MM-dd"));
            else
                return new SimpleStringProperty("");
        });

        reportTemplatePagination.setPageFactory(pageIndex -> {
            setReportTableList(pageIndex + 1);
            return new VBox();
        });

        //variableListComboBox.addEventHandler();

        setDisabledItem(true);

        variableListComboBox.getSelectionModel().selectedIndexProperty().addListener((ov, oldIdx, newIdx) -> {
            if (oldIdx != newIdx) {
                Map<String, String> item = (Map<String, String>)variableNameList.get(variableListComboBox.getSelectionModel().getSelectedItem());

                if(item != null) {
                    displayNameTextField.setText(item.get("displayName"));
                    variableNameTextField.setText(variableListComboBox.getSelectionModel().getSelectedItem());
                    variableTypeComboBox.getSelectionModel().select(item.get("variableType"));
                }
            }
        });
    }

    public void setReportTableList(int page) {

        int totalCount = 0;
        int limit = 17;
        int offset = (page - 1)  * limit;

        try {
            Map<String, Object> param = new HashMap<>();

            param.put("limit", limit);
            param.put("offset", offset);

            HttpClientResponse response = apiService.get("admin/reportTemplate", param, null, false);

            PagedReportTemplate pagedReportTemplate = response.getObjectBeforeConvertResponseToJSON(PagedReportTemplate.class);

            if(pagedReportTemplate != null) {
                totalCount = pagedReportTemplate.getCount();
                reportTemplateListTable.getItems().clear();
                reportTemplateListTable.setItems(FXCollections.observableArrayList(pagedReportTemplate.getResult()));
            }

            int pageCount = 0;

            if(totalCount > 0) {
                pageCount = totalCount / limit;
                reportTemplatePagination.setCurrentPageIndex(page - 1);
                if(totalCount % limit > 0) {
                    pageCount++;
                }
            }

            logger.info(String.format("total count : %s, page count : %s", totalCount, pageCount));

            if (pageCount > 0) {
                reportTemplatePagination.setVisible(true);
                reportTemplatePagination.setPageCount(pageCount);
            } else {
                reportTemplatePagination.setVisible(false);
            }

        } catch(WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }
    }

    @FXML
    public void selectReportTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Report Template File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("vm", "*.vm"));
        File file = fileChooser.showOpenDialog(mainController.getPrimaryStage());

        if(file != null && file.getName().toLowerCase().endsWith(".vm")) {
            try (BufferedReader in = new BufferedReader(new FileReader(file))){
                StringBuilder sb = new StringBuilder();
                String s;
                while((s = in.readLine()) != null) {
                    sb = sb.append(s + "\n");
                }

                contents = sb.toString();

            } catch (Exception e) {
                DialogUtil.error(e.getMessage(), e.getMessage(), mainController.getPrimaryStage() ,true);
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void saveReportTemplate() {
        String reportName = reportNameTextField.getText();
        if(!StringUtils.isEmpty(reportName) && !StringUtils.isEmpty(contents)) {
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("name", reportName);
                param.put("contents", contents);

                if(id == 0) {
                    apiService.post("admin/reportTemplate", param, null, true);
                } else {
                    apiService.put("admin/reportTemplate/" + id, param, null, true);
                    id = 0;
                }
                setReportTableList(1);

                resetItem();
                setDisabledItem(true);

            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            } catch (IOException e) {
                DialogUtil.error(e.getMessage(), e.getMessage(), mainController.getPrimaryStage(), true);
            }
        }
    }

    @FXML
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Report Image Files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("image", "*.jpg", "*.gif", "*.png"));
        List<File> imageList = fileChooser.showOpenMultipleDialog(mainController.getPrimaryStage());

        if(imageList != null) {
            if(this.imageList.size() == 0) {
                this.imageList.addAll(imageList);
            } else {
                for (File image : imageList) {
                    Optional<File> duplicatedFile = this.imageList.stream().filter(item -> item.getName().equals(image.getName())).findFirst();
                    //중복 파일이 아닐 경우 리스트에 추가
                    if(!duplicatedFile.isPresent()) {
                        this.imageList.add(image);
                    }
                }
            }

            settingImageListComboBox();
        }
    }

    @FXML
    public void modifyVariable() {

    }

    @FXML
    public void addVariable() {
        String variableName = variableNameTextField.getText();
        String displayName = displayNameTextField.getText();
        String variableType = variableTypeComboBox.getSelectionModel().getSelectedItem();

        if(!StringUtils.isEmpty(variableName) && !StringUtils.isEmpty(displayName)
                && !StringUtils.isEmpty(variableType)) {

            Map<String , String> item = new HashMap<>();
            item.put("displayName", displayName);
            item.put("variableType", variableType);

            variableNameList.put(variableName, item);

            settingVariableListComboBox();

            variableNameTextField.setText("");
            displayNameTextField.setText("");
            variableTypeComboBox.getSelectionModel().clearSelection();
        }

    }

    @FXML
    public void removeVariable() {
        variableNameTextField.setText("");
        displayNameTextField.setText("");
        variableTypeComboBox.getSelectionModel().clearSelection();

        String item = variableListComboBox.getSelectionModel().getSelectedItem();

        variableNameList.remove(item);

        settingVariableListComboBox();
    }

    @FXML
    public void removeImage() {
        if(imageList != null) {

        }
    }

    @FXML
    public void reportAdd() {
        setDisabledItem(false);
        settingVariableTypeComboBox();
    }

    public void settingVariableTypeComboBox() {
        variableTypeComboBox.getItems().add("String");
        variableTypeComboBox.getItems().add("Date");
        variableTypeComboBox.getItems().add("Integer");
    }

    public void settingImageListComboBox() {
        imageListComboBox.getItems().removeAll(imageListComboBox.getItems());
        if(imageList != null) {
            for(File image : imageList) {
                imageListComboBox.getItems().add(image.getName());
            }
        }
    }

    public void settingVariableListComboBox() {
        variableListComboBox.getItems().removeAll(variableListComboBox.getItems());
        if(variableNameList.size() > 0) {
            Set<String> keySet = variableNameList.keySet();

            for(String name : keySet) {
                variableListComboBox.getItems().add(name);
            }
        }
    }

    public void resetItem() {
        reportNameTextField.setText("");
        contents = "";
        variableNameTextField.setText("");
        displayNameTextField.setText("");
        variableTypeComboBox.getItems().removeAll(variableTypeComboBox.getItems());
        variableListComboBox.getItems().removeAll(variableListComboBox.getItems());
        imageListComboBox.getItems().removeAll(imageListComboBox.getItems());
        imageList.clear();
        variableNameList.clear();
    }

    public void setDisabledItem(boolean condition) {
        resetItem();
        reportNameTextField.setDisable(condition);
        reportTemplateSelectionButton.setDisable(condition);
        reportSaveButton.setDisable(condition);
        selectImageButton.setDisable(condition);
        variableNameTextField.setDisable(condition);
        displayNameTextField.setDisable(condition);
        variableTypeComboBox.setDisable(condition);
        variableListComboBox.setDisable(condition);
        imageListComboBox.setDisable(condition);
        modifyVariableButton.setDisable(condition);
        addVariableButton.setDisable(condition);
        removeVariableButton.setDisable(condition);
        removeImageButton.setDisable(condition);
    }

    public void deleteReportTemplate(int reportTemplateId) {
        try {
            apiService.delete("admin/reportTemplate/" + reportTemplateId);
        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getMessage(), mainController.getPrimaryStage(), true);
        }

    }

    private class ReportTemplateModifyButton extends TableCell<ReportTemplate, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        public ReportTemplateModifyButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ReportTemplate reportTemplate = ReportTemplateModifyButton.this.getTableView().getItems().get(
                        ReportTemplateModifyButton.this.getIndex());

                setDisabledItem(false);

                id = reportTemplate.getId();

                reportNameTextField.setText(reportTemplate.getName());
                contents = reportTemplate.getContents();

                reportTemplateSelectionButton.setDisable(false);
                reportSaveButton.setDisable(false);

            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                String alertHeaderText = null;
                String alertContentText = "Are you sure to delete this panel?";

                alert.setTitle("Confirmation Dialog");
                ReportTemplate reportTemplate = ReportTemplateModifyButton.this.getTableView().getItems().get(
                        ReportTemplateModifyButton.this.getIndex());
                alert.setHeaderText(reportTemplate.getName());
                alert.setContentText(alertContentText);
                logger.info(reportTemplate.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deleteReportTemplate(reportTemplate.getId());
                } else {
                    logger.info(result.get() + " : button select");
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

            ReportTemplate reportTemplate = ReportTemplateModifyButton.this.getTableView().getItems().get(
                    ReportTemplateModifyButton.this.getIndex());

            if(reportTemplate.getDeleted() != 0) {
                return;
            }

            box = new HBox();

            box.setAlignment(Pos.CENTER);

            box.setSpacing(10);

            box.getChildren().add(img1);
            box.getChildren().add(img2);

            setGraphic(box);

        }
    }

}
