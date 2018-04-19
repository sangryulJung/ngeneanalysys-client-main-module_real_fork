package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    /**
     * @param panelId Integer
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

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
        dialogStage.resizableProperty().setValue(false);

        versionComboBox.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            logger.info("test");
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
        });

        setVersionComboBox();

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setList(String id) {
        if(toolsContentsGridPane.getChildren() != null && !toolsContentsGridPane.getChildren().isEmpty()) {
            toolsContentsGridPane.getChildren().removeAll(toolsContentsGridPane.getChildren());
            toolsContentsGridPane.getRowConstraints().removeAll(toolsContentsGridPane.getRowConstraints());
            toolsContentsGridPane.setPrefHeight(0);
        }
        if(databaseContentsGridPane.getChildren() != null && !databaseContentsGridPane.getChildren().isEmpty()) {
            databaseContentsGridPane.getChildren().removeAll(databaseContentsGridPane.getChildren());
            databaseContentsGridPane.getRowConstraints().removeAll(databaseContentsGridPane.getRowConstraints());
            databaseContentsGridPane.setPrefHeight(0);
        }
        Platform.runLater(() -> {
            try {
                HttpClientResponse response = apiService.get("/pipelineVersions/" + id + "/annotationDatabases", null, null, null);

                List<PipelineAnnotationDatabase> list = (List<PipelineAnnotationDatabase>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineAnnotationDatabase.class, false);

                if(list != null && !list.isEmpty()) {
                    list.sort(Comparator.comparing(PipelineAnnotationDatabase::getCategory));
                    for(PipelineAnnotationDatabase pipelineAnnotationDatabase : list) {
                        createGridRow(pipelineAnnotationDatabase.getCategory(), pipelineAnnotationDatabase.getName(),
                                pipelineAnnotationDatabase.getVersion(), pipelineAnnotationDatabase.getReleaseDate(), pipelineAnnotationDatabase.getDescription(), false);
                    }
                }
            } catch (WebAPIException wae) {
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
            }
        });
        Platform.runLater(() -> {
            try {
                HttpClientResponse response = apiService.get("/pipelineVersions/" + id + "/tools", null, null, null);

                List<PipelineTool> list = (List<PipelineTool>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineTool.class, false);

                if(list != null && !list.isEmpty()) {
                    list.sort(Comparator.comparing(PipelineTool::getName));
                    for(PipelineTool pipelineTool : list) {
                        createGridRow(pipelineTool.getName(), pipelineTool.getLicense(),
                                pipelineTool.getVersion(), pipelineTool.getReleaseDate(), pipelineTool.getDescription(), true);
                    }
                }
            } catch (WebAPIException wae) {
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
            }
        });

    }

    private void createGridRow(String column1, String column2, String column3, String column4, String column5, boolean isTool) {
        Label label1 = createLabel(column1);
        Label label2 = createLabel(column2);
        Label label3 = createLabel(column3);
        Label label4 = createLabel(column4);
        Label label5 = createLabel(column5);

        label1.setAlignment(Pos.CENTER);
        label2.setAlignment(Pos.CENTER);
        label3.setAlignment(Pos.CENTER);
        label4.setAlignment(Pos.CENTER);
        label5.setAlignment(Pos.TOP_LEFT);
        
        if(isTool) {
            toolsContentsGridPane.addRow(toolsContentsGridPane.getChildren().size() / 5, label1, label2, label3, label4, label5);
        } else {
            databaseContentsGridPane.addRow(databaseContentsGridPane.getChildren().size() / 5, label1, label2, label3, label4, label5);
        }
        GridPane.setValignment(label1, VPos.TOP);
        GridPane.setValignment(label2, VPos.TOP);
        GridPane.setValignment(label3, VPos.TOP);
        GridPane.setValignment(label4, VPos.TOP);
        GridPane.setValignment(label5, VPos.TOP);
    }

    private Label createLabel(String text) {
        Label label = new Label();
        label.setWrapText(true);
        label.setText(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle(label.getStyle() + "-fx-border-width : 0.5 0 0 0; -fx-border-color : #000000; -fx-font-family: \"Noto Sans KR Light\"");
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(7,0,7,0));
        return label;
    }

    private void setVersionComboBox() {
        versionComboBox.setConverter(new ComboBoxConverter());
        try {
            HttpClientResponse response = apiService.get("/panels/"+ panelId +"/pipelineVersions"/* + this.panelId*/, null, null, null);

            List<PipelineVersionView> list = (List<PipelineVersionView>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineVersionView.class, false);
            this.list = list;
            if(list != null && !list.isEmpty()) {
                for(PipelineVersionView pipelineVersionView : list) {
                    versionComboBox.getItems().add(new ComboBoxItem(pipelineVersionView.getId().toString(), pipelineVersionView.getVersion()));
                }
                versionComboBox.getSelectionModel().selectFirst();
            }
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
        }
    }

}
