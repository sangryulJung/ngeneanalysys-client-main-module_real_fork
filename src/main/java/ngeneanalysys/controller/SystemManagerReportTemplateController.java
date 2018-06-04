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
import ngeneanalysys.model.paged.PagedReportTemplate;
import ngeneanalysys.model.ReportContents;
import ngeneanalysys.model.ReportImage;
import ngeneanalysys.model.ReportTemplate;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.JarUploadTask;
import ngeneanalysys.task.ReportImageFileUploadTask;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.*;
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
    private TextArea customFieldsTextArea;

    @FXML
    private ComboBox<String> outputTypeComboBox;

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

    @FXML
    private ComboBox<String> comboBoxItemComboBox;

    @FXML
    private Button comboBoxItemAddBtn;

    @FXML
    private Button comboBoxItemDeleteBtn;

    //vm 파일 내용
    private String contents = null;

    //사용할 이미지 모음
    private List<File> imageList = null;

    private Map<String, Object> variableList = new HashMap<>();

    private Integer id = 0;

    private String selectedVariableName = null;

    //삭제할 이미지 모음
    private List<ReportImage> deleteImageList = new ArrayList<>();

    //기존 이미지 모음
    private List<ReportImage> currentImageList = new ArrayList<>();

    private File wordCreatorJar;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("system manager report template init");

        apiService = APIService.getInstance();

        imageList = new ArrayList<>();

        outputTypeComboBox.valueProperty().addListener((ov, t, t1) -> {
            if(t1 == null || t1.equalsIgnoreCase("pdf")) {
                reportTemplateSelectionButton.setText("Choose Template(*.vm) File");
            } else {
                reportTemplateSelectionButton.setText("Choose Template(*.jar) File");
            }
        });

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
                Map<String, String> item = (Map<String, String>) variableList.get(variableListComboBox.getSelectionModel().getSelectedItem());

                if(item != null) {
                    selectedVariableName = variableListComboBox.getSelectionModel().getSelectedItem();
                    displayNameTextField.setText(item.get("displayName"));
                    variableNameTextField.setText(variableListComboBox.getSelectionModel().getSelectedItem());
                    variableTypeComboBox.getSelectionModel().select(item.get("variableType"));
                    if(item.get("variableType").equalsIgnoreCase("ComboBox")) {
                        String list = item.get("comboBoxItemList");
                        String[] comboBoxItem = list.split("&\\^\\|");
                        comboBoxItemComboBox.setDisable(false);
                        comboBoxItemAddBtn.setDisable(false);
                        comboBoxItemDeleteBtn.setDisable(false);
                        comboBoxItemComboBox.getItems().addAll(comboBoxItem);
                        comboBoxItemComboBox.getSelectionModel().selectFirst();
                    }
                }
            }
        });

        variableTypeComboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldIdx, newIdx) -> {
          if(!StringUtils.isEmpty(newIdx) && newIdx.equalsIgnoreCase("ComboBox")) {
              comboBoxItemComboBox.setDisable(false);
              comboBoxItemAddBtn.setDisable(false);
              comboBoxItemDeleteBtn.setDisable(false);
          } else {
              if(!StringUtils.isEmpty(oldIdx) && oldIdx.equalsIgnoreCase("ComboBox")) {
                  comboBoxItemComboBox.getItems().removeAll(comboBoxItemComboBox.getItems());
              }
              comboBoxItemComboBox.setDisable(true);
              comboBoxItemAddBtn.setDisable(true);
              comboBoxItemDeleteBtn.setDisable(true);
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

            logger.debug("total count : " + totalCount + ", page count : " + pageCount);

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
        if(outputTypeComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("pdf")) {
            fileChooser.getExtensionFilters()
                    .addAll(new FileChooser.ExtensionFilter("vm", "*.vm"));
        } else {
            fileChooser.getExtensionFilters()
                    .addAll(new FileChooser.ExtensionFilter("jar", "*.jar"));
        }
        File file = fileChooser.showOpenDialog(mainController.getPrimaryStage());

        if(file != null && (file.getName().toLowerCase().endsWith(".vm"))) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))){
                final StringBuilder sb = new StringBuilder();
                in.lines().forEach(s -> sb.append(s).append("\n"));
                if(sb.length() > 0) contents = sb.toString();

            } catch (Exception e) {
                DialogUtil.error(e.getMessage(), e.getMessage(), mainController.getPrimaryStage() ,true);
                e.printStackTrace();
            }
        } else if(file != null && (file.getName().toLowerCase().endsWith(".jar"))) {
            contents = file.getName();
            wordCreatorJar = file;
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
                param.put("outputType", outputTypeComboBox.getSelectionModel().getSelectedItem());
                if(!StringUtils.isEmpty(customFieldsTextArea.getText())) {
                    param.put("customFields", customFieldsTextArea.getText());
                } else {
                    String variableJson = JsonUtil.toJson(variableList);
                    param.put("customFields", variableJson);
                    logger.debug(variableJson);
                }
                HttpClientResponse response = null;
                if(id == 0) {
                    response = apiService.post("admin/reportTemplate", param, null, true);
                } else {
                    response = apiService.put("admin/reportTemplate/" + id, param, null, true);
                    id = 0;

                    if(!deleteImageList.isEmpty()) {
                        for(ReportImage deleteImage : deleteImageList) {
                            apiService.delete("admin/reportImage/" + deleteImage.getId());
                        }
                    }
                }

                ReportTemplate reportTemplate = response.getObjectBeforeConvertResponseToJSON(ReportTemplate.class);

                if(outputTypeComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("MS_WORD")) {
                    JarUploadTask task = new JarUploadTask(wordCreatorJar, reportTemplate.getId(), getMainController());
                    final Thread downloadThread = new Thread(task);

                    // Thread 실행
                    downloadThread.setDaemon(true);
                    downloadThread.start();

                    task.setOnSucceeded(ev -> {
                        setReportTableList(1);
                        resetItem();
                        setDisabledItem(true);
                    });
                } else {
                    if (!this.imageList.isEmpty()) {
                        ReportImageFileUploadTask task = new ReportImageFileUploadTask(imageList, reportTemplate.getId(), getMainController());
                        final Thread downloadThread = new Thread(task);

                        // Thread 실행
                        downloadThread.setDaemon(true);
                        downloadThread.start();

                        task.setOnSucceeded(ev -> {
                            setReportTableList(1);
                            resetItem();
                            setDisabledItem(true);
                        });
                    } else {
                        setReportTableList(1);
                        resetItem();
                        setDisabledItem(true);
                    }
                }

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
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Report Image Files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("image", "*.jpg", "*.gif", "*.png"));
        List<File> imageList = fileChooser.showOpenMultipleDialog(mainController.getPrimaryStage());

        if(imageList != null && !imageList.isEmpty()) {
            if(this.imageList.isEmpty()) {
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
        editVariable();
    }

    @FXML
    public void addVariable() {
        editVariable();
    }

    private void editVariable() {
        String variableName = variableNameTextField.getText();
        String displayName = displayNameTextField.getText();
        String variableType = variableTypeComboBox.getSelectionModel().getSelectedItem();

        if(!StringUtils.isEmpty(variableName) && !StringUtils.isEmpty(displayName)
                && !StringUtils.isEmpty(variableType)) {

            if(!StringUtils.isEmpty(selectedVariableName)) {
                variableList.remove(selectedVariableName);
            }

            Map<String , String> item = new HashMap<>();
            item.put("displayName", displayName);
            item.put("variableType", variableType);
            if(variableTypeComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("ComboBox")) {
                String itemList = "";
                for(String comboBoxItem : comboBoxItemComboBox.getItems()) {
                    itemList += comboBoxItem + "&^|";
                }

                if(StringUtils.isEmpty(itemList)) {
                    DialogUtil.alert("empty comboBox", "empty comboBox", getMainApp().getPrimaryStage(), true);
                    return;
                }
                itemList = itemList.substring(0, itemList.length() - 3);
                logger.debug(itemList);
                item.put("comboBoxItemList", itemList);
            }

            variableList.put(variableName, item);

            settingVariableListComboBox();

            variableNameTextField.setText("");
            displayNameTextField.setText("");

            variableTypeComboBox.getSelectionModel().clearSelection();
            selectedVariableName = null;
        }
    }

    @FXML
    public void removeVariable() {
        variableNameTextField.setText("");
        displayNameTextField.setText("");

        if(variableTypeComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("ComboBox")) {
            comboBoxItemComboBox.getItems().removeAll(comboBoxItemComboBox.getItems());
            comboBoxItemComboBox.setDisable(true);
            comboBoxItemDeleteBtn.setDisable(true);
            comboBoxItemAddBtn.setDisable(true);
        }

        variableTypeComboBox.getSelectionModel().clearSelection();

        String item = variableListComboBox.getSelectionModel().getSelectedItem();

        variableList.remove(item);

        settingVariableListComboBox();
    }

    @FXML
    public void removeImage() {
        if((imageList != null && !imageList.isEmpty()) || (currentImageList != null && !currentImageList.isEmpty())) {
            String imageName = imageListComboBox.getSelectionModel().getSelectedItem();

            if(!StringUtils.isEmpty(imageName)) {
                Optional<ReportImage> currentImageOptional = currentImageList.stream().filter(currentImage -> currentImage.getName().equals(imageName)).findFirst();
                if(currentImageOptional.isPresent()) {
                    deleteImageList.add(currentImageOptional.get());
                    currentImageList.remove(currentImageOptional.get());
                } else {
                    Optional<File> deleteFile = imageList.stream().filter(item -> imageName.equals(item.getName())).findFirst();
                    deleteFile.ifPresent(file -> imageList.remove(file));
                }

                imageListComboBox.getItems().remove(imageName);
            }
        }
    }

    @FXML
    public void reportAdd() {
        setDisabledItem(false);
        settingVariableTypeComboBox();
        settingReportType();
    }

    private void settingVariableTypeComboBox() {
        variableTypeComboBox.getItems().add("String");
        variableTypeComboBox.getItems().add("Date");
        variableTypeComboBox.getItems().add("Integer");
        variableTypeComboBox.getItems().add("Image");
        variableTypeComboBox.getItems().add("ComboBox");
    }

    private void settingReportType() {
        outputTypeComboBox.getItems().add("PDF");
        outputTypeComboBox.getItems().add("MS_WORD");
    }

    private void settingImageListComboBox() {
        imageListComboBox.getItems().removeAll(imageListComboBox.getItems());
        //새로 추가하는 이미지 리스트
        if(imageList != null && !imageList.isEmpty()) {
            for(File image : imageList) {
                imageListComboBox.getItems().add(image.getName());
            }
        }
        //기존에 존재하던 이미지 리스트
        if(currentImageList != null && !currentImageList.isEmpty()) {
            for(ReportImage image : currentImageList) {
                imageListComboBox.getItems().add(image.getName());
            }
        }
    }

    private void settingVariableListComboBox() {
        variableListComboBox.getItems().removeAll(variableListComboBox.getItems());
        if(variableList.size() > 0) {
            Set<String> keySet = variableList.keySet();

            for(String name : keySet) {
                variableListComboBox.getItems().add(name);
            }
        }
    }

    @FXML
    public void deleteComboBoxItem() {
        if(comboBoxItemComboBox.getItems() != null && !comboBoxItemComboBox.getItems().isEmpty()) {
            comboBoxItemComboBox.getItems().remove(comboBoxItemComboBox.getSelectionModel().getSelectedItem());
            if(!comboBoxItemComboBox.getItems().isEmpty()) comboBoxItemComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void addComboBoxItem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text Input Dialog");
        dialog.setContentText("Please enter comboBoxItem");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            comboBoxItemComboBox.getItems().add(name);
            comboBoxItemComboBox.getSelectionModel().selectLast();
        });


    }

    private void resetItem() {
        customFieldsTextArea.setText("");
        reportNameTextField.setText("");
        contents = "";
        variableNameTextField.setText("");
        displayNameTextField.setText("");
        variableTypeComboBox.getItems().removeAll(variableTypeComboBox.getItems());
        variableListComboBox.getItems().removeAll(variableListComboBox.getItems());
        variableListComboBox.getItems().removeAll(variableListComboBox.getItems());
        outputTypeComboBox.getSelectionModel().selectFirst();
        deleteImageList.clear();
        currentImageList.clear();
        comboBoxItemComboBox.getItems().removeAll(comboBoxItemComboBox.getItems());
        imageListComboBox.getItems().removeAll(imageListComboBox.getItems());
        imageList.clear();
        variableList.clear();
    }

    public void setDisabledItem(boolean condition) {
        resetItem();
        customFieldsTextArea.setDisable(condition);
        reportNameTextField.setDisable(condition);
        reportTemplateSelectionButton.setDisable(condition);
        reportSaveButton.setDisable(condition);
        outputTypeComboBox.setDisable(condition);
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
        comboBoxItemComboBox.setDisable(true);
        comboBoxItemAddBtn.setDisable(true);
        comboBoxItemDeleteBtn.setDisable(true);
    }

    private void deleteReportTemplate(int reportTemplateId) {
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

        private ReportTemplateModifyButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ReportTemplate reportTemplate = ReportTemplateModifyButton.this.getTableView().getItems().get(
                        ReportTemplateModifyButton.this.getIndex());

                setDisabledItem(false);

                id = reportTemplate.getId();

                HttpClientResponse response;

                try {
                    response = apiService.get("reportTemplate/" + reportTemplate.getId(), null, null, false);

                    ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                    List<ReportImage> imageList = reportContents.getReportImages();
                    //이미지 리스트 설정
                    currentImageList.addAll(imageList);
                    settingImageListComboBox();

                } catch (WebAPIException wae) {
                    DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainApp.getPrimaryStage(), true);
                }

                variableList = JsonUtil.fromJsonToMap(reportTemplate.getCustomFields());
                settingVariableListComboBox();

                settingVariableTypeComboBox();
                settingReportType();
                if(reportTemplate.getOutputType() != null) outputTypeComboBox.getSelectionModel().select(reportTemplate.getOutputType());
                reportNameTextField.setText(reportTemplate.getName());
                contents = reportTemplate.getContents();

                reportTemplateSelectionButton.setDisable(false);
                reportSaveButton.setDisable(false);

            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogUtil.setIcon(alert);
                String alertHeaderText = "";
                String alertContentText = "Are you sure to delete this panel?";

                alert.setTitle("Confirmation Dialog");
                ReportTemplate reportTemplate = ReportTemplateModifyButton.this.getTableView().getItems().get(
                        ReportTemplateModifyButton.this.getIndex());
                alert.setHeaderText(reportTemplate.getName());
                alert.setContentText(alertContentText);
                logger.debug(reportTemplate.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deleteReportTemplate(reportTemplate.getId());
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

            ReportTemplate reportTemplate = ReportTemplateModifyButton.this.getTableView().getItems().get(
                    ReportTemplateModifyButton.this.getIndex());

            if(reportTemplate.getDeleted() != 0) {
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
