package ngeneanalysys.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.PipelineAnnotationDatabase;
import ngeneanalysys.model.PipelineTool;
import ngeneanalysys.model.PipelineVersionView;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * @author Jang
 * @since 2018-04-11
 */
public class PublicDatabaseController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private ComboBox<ComboBoxItem> versionComboBox;

    private Integer panelId;

    private APIService apiService;

    private Stage dialogStage;

    private List<PipelineVersionView> list;

    @FXML
    private GridPane toolsContentsGridPane;

    @FXML
    private GridPane databaseContentsGridPane;

    @FXML
    private Label releaseDateLabel;

    @FXML
    private Label releaseNoteLabel;
    
    @FXML
    private ScrollPane releaseNoteScrollPane;

    @FXML
    private TabPane itemTabPane;

    /**
     * @param panelId Integer
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");

        apiService = APIService.getInstance();

        // Create the dialog Stage
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Pipeline information");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setMinWidth(1030);
        dialogStage.setMinHeight(566);

        versionComboBox.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if(ov != nv) {
                Optional<PipelineVersionView> optionalPipelineVersionView =
                        list.stream().filter(item -> item.getId().equals(Integer.parseInt(nv.getValue()))).findFirst();
                if(optionalPipelineVersionView.isPresent()) {
                    if (optionalPipelineVersionView.get().getReleaseNote().isEmpty()) {
                        releaseNoteLabel.setText("There is no release notes.");
                        releaseNoteScrollPane.setPrefHeight(30);
                    }else {
                        releaseNoteLabel.setText(optionalPipelineVersionView.get().getReleaseNote());
                    }
                    releaseDateLabel.setText(optionalPipelineVersionView.get().getReleaseDate());
                }
                setList(nv.getValue());
            }
        });

        setVersionComboBox();

        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private void setList(String id) {
        if(toolsContentsGridPane.getChildren() != null && !toolsContentsGridPane.getChildren().isEmpty()) {
            toolsContentsGridPane.getChildren().removeAll(toolsContentsGridPane.getChildren());
            while(Boolean.FALSE.equals(toolsContentsGridPane.getRowConstraints().isEmpty())) {
                toolsContentsGridPane.getRowConstraints().remove(0);
            }
        }
        if(databaseContentsGridPane.getChildren() != null && !databaseContentsGridPane.getChildren().isEmpty()) {
            databaseContentsGridPane.getChildren().removeAll(databaseContentsGridPane.getChildren());
            while(Boolean.FALSE.equals(databaseContentsGridPane.getRowConstraints().isEmpty())) {
                databaseContentsGridPane.getRowConstraints().remove(0);
            }
        }
        Task task = new Task() {
            List<PipelineAnnotationDatabase> dbList;
            List<PipelineTool> toolList;
            @Override
            protected Object call() throws Exception {
                HttpClientResponse response;
                response = apiService
                        .get("/pipelineVersions/" + id + "/annotationDatabases", null, null, null);
                dbList = (List<PipelineAnnotationDatabase>)response
                        .getMultiObjectBeforeConvertResponseToJSON(PipelineAnnotationDatabase.class, false);
                if(dbList != null && !dbList.isEmpty()) {
                    dbList.sort(Comparator.comparing(PipelineAnnotationDatabase::getCategory));
                }
                response = apiService.get("/pipelineVersions/" + id + "/tools", null, null, null);
                toolList = (List<PipelineTool>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineTool.class, false);
                if(toolList != null && !toolList.isEmpty()) {
                    toolList.sort(Comparator.comparing(PipelineTool::getName));
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(dbList != null && !dbList.isEmpty()) {
                    createGridRow("Category", "Database", "Version",
                            "Release", "Description", "Source" ,"Sample Count", false, true);
                    for(PipelineAnnotationDatabase pipelineAnnotationDatabase : dbList) {
                        createGridRow(pipelineAnnotationDatabase.getCategory(), pipelineAnnotationDatabase.getName(),
                                pipelineAnnotationDatabase.getVersion(), pipelineAnnotationDatabase.getReleaseDate(),
                                pipelineAnnotationDatabase.getDescription(), pipelineAnnotationDatabase.getSource(),
                                pipelineAnnotationDatabase.getSampleCount(), false, false);
                    }
                }
                dbList = null;
                if(toolList != null && !toolList.isEmpty()) {
                    createGridRow("Software", "License", "Version", "Release",
                            "Description", "Source", null, true, true);
                    for(PipelineTool pipelineTool : toolList) {
                        createGridRow(pipelineTool.getName(), pipelineTool.getLicense(),
                                pipelineTool.getVersion(), pipelineTool.getReleaseDate(), pipelineTool.getDescription(),
                                pipelineTool.getSource(), null, true, false);
                    }
                }
                toolList = null;
            }

            @Override
            protected void failed() {
                super.failed();
                Exception e = new Exception(getException());
                if (e instanceof WebAPIException){
                    WebAPIException wae = (WebAPIException)e;
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
                } else {
                    e.printStackTrace();
                    DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), dialogStage, true);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void createGridRow(final String column1, final String column2, final String column3, final String column4,
                               final String column5, final String column6, final String column7,
                               final boolean isTool, final boolean isTitle) {
        Label label1 = createLabel(column1, isTitle);
        Label label2 = createLabel(column2, isTitle);
        Label label3 = createLabel(column3, isTitle);
        Label label4 = createLabel(column4, isTitle);
        Label label5 = createLabel(column5, isTitle);
        Label label6 = createLabel(column6, isTitle);
        label6.setPadding(new Insets(0, 0, 5, 10));
        Label label7 = createLabel(column7, isTitle);

        label1.setAlignment(Pos.CENTER);
        label2.setAlignment(Pos.CENTER);
        label3.setAlignment(Pos.CENTER);
        label4.setAlignment(Pos.CENTER);
        if(isTitle) {
            label5.setAlignment(Pos.CENTER);
            label6.setAlignment(Pos.CENTER);
        } else {
            label5.setAlignment(Pos.TOP_LEFT);
            label6.setAlignment(Pos.TOP_LEFT);
        }
        label7.setAlignment(Pos.CENTER);

        if(isTool) {
            toolsContentsGridPane.addRow(toolsContentsGridPane.getChildren().size() / 6,
                    label1, label2, label3, label4, label5, label6);
        } else {
            databaseContentsGridPane.addRow(databaseContentsGridPane.getChildren().size() / 7,
                    label1, label2, label3, label4, label5, label6, label7);
        }

        if(isTitle) {
            GridPane.setValignment(label1, VPos.CENTER);
            GridPane.setValignment(label2, VPos.CENTER);
            GridPane.setValignment(label3, VPos.CENTER);
            GridPane.setValignment(label4, VPos.CENTER);
            GridPane.setValignment(label5, VPos.CENTER);
            GridPane.setValignment(label6, VPos.CENTER);
            if(!isTool) GridPane.setValignment(label7, VPos.CENTER);
        } else {
            GridPane.setValignment(label1, VPos.TOP);
            GridPane.setValignment(label2, VPos.TOP);
            GridPane.setValignment(label3, VPos.TOP);
            GridPane.setValignment(label4, VPos.TOP);
            GridPane.setValignment(label5, VPos.TOP);
            GridPane.setValignment(label6, VPos.TOP);
            if(!isTool) GridPane.setValignment(label7, VPos.TOP);
        }
    }

    private Label createLabel(String text, boolean isTitle) {
        Label label = new Label();
        label.setWrapText(true);
        if(!StringUtils.isEmpty(text)) label.setText(text);
        label.setMaxWidth(Double.MAX_VALUE);
        if(isTitle) {
            label.setStyle(label.getStyle() + "-fx-font-size : 13; -fx-font-family: \"Noto Sans KR Light\"");
        } else {
            label.setStyle(label.getStyle()
                    + "-fx-border-width : 0.5 0 0 0; -fx-border-color : #000000; -fx-font-family: \"Noto Sans KR Light\"");
        }

        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(7,0,7,0));
        return label;
    }

    @SuppressWarnings("unchecked")
    private void setVersionComboBox() {
        versionComboBox.setConverter(new ComboBoxConverter());
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                HttpClientResponse response = apiService.get("/panels/"+ panelId +"/pipelineVersions"/* + this.panelId*/, null, null, null);
                list = (List<PipelineVersionView>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineVersionView.class, false);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(list != null && !list.isEmpty()) {
                    for(PipelineVersionView pipelineVersionView : list) {
                        versionComboBox.getItems().add(new ComboBoxItem(pipelineVersionView.getId().toString(), pipelineVersionView.getVersion()));
                    }
                    versionComboBox.getSelectionModel().selectFirst();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                Exception e = new Exception(getException());
                if (e instanceof WebAPIException) {
                    WebAPIException wae = (WebAPIException)e;
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
                } else {
                    DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), dialogStage, true);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}
