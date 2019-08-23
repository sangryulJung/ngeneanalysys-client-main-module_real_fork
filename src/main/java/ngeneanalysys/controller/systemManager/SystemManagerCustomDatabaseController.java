package ngeneanalysys.controller.systemManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.CustomDatabase;
import ngeneanalysys.model.CustomDatabaseContents;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;

/**
 * @author Jang
 * @since 2019-06-20
 */
public class SystemManagerCustomDatabaseController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TableView<CustomDatabaseContents> customDbTable;
    @FXML
    private TableColumn<CustomDatabaseContents, String> chrTableColumn;
    @FXML
    private TableColumn<CustomDatabaseContents, String> startTableColumn;
    @FXML
    private TableColumn<CustomDatabaseContents, String> refTableColumn;
    @FXML
    private TableColumn<CustomDatabaseContents, String> altTableColumn;
    @FXML
    private TableColumn<CustomDatabaseContents, String> annotationTableColumn;

    private APIService apiService;

    private Stage dialogStage;

    private Integer panelId;

    private Integer customDatabaseId = 0;

    private String customDatabaseContentsText;

    /**
     * @param panelId Panel id
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    void setCustomDatabase(CustomDatabase customDatabase) {
        this.panelId = customDatabase.getPanelId();
        titleTextField.setText(customDatabase.getTitle());
        descriptionTextField.setText(customDatabase.getDescription());
        initTableContents(customDatabase.getContents());
        customDatabaseId = customDatabase.getId();
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();

        chrTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChr()));
        startTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartPosition()));
        refTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRef()));
        altTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlt()));
        annotationTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAnnotation()));
        annotationTableColumn.setCellFactory(cell -> new TableCell<CustomDatabaseContents, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    setTooltip(new Tooltip(item));
                }
            }
        });

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Custom Database");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setResizable(false);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private boolean stringCheck(String[] str) {
        return str.length == 5 && str[1].matches("[0-9]*") && str[2].matches("[A-Za-z]*")
                && str[3].matches("[A-Za-z]*") && str[4].matches("[^;:=]*");
    }

    private String addHeader(String contents) {
        if(!contents.split("\t")[0].equalsIgnoreCase("Chr")) {
            contents = "Chr\tStart\tRef\tAlt\tAnnotaion\n" + contents;
        }
        return contents;
    }

    @FXML
    private void upload() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Text(*.txt)", "*.txt"));
        fileChooser.setTitle("format file");
        fileChooser.setInitialFileName("SampleSheet");
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if(file != null) {
            StringBuilder contentsSB = new StringBuilder();
            try (FileReader fr = new FileReader(file);
                 BufferedReader br = new BufferedReader(fr)){
                String line;
                while((line = br.readLine()) != null) {
                    contentsSB.append(line).append("\n");
                }

                String contents = addHeader(contentsSB.toString());

                initTableContents(contents);

            } catch (Exception e) {
                DialogUtil.error("Invalid content", "Please check the contents.", mainApp.getPrimaryStage(), true);
            }

        }
    }

    private void initTableContents(String contents) {
        String[] lineSplit = contents.split("\n");
        String[] testLine = Arrays.copyOfRange(lineSplit, 1, lineSplit.length);

        if(Arrays.stream(testLine).map(t -> t.split("\t")).allMatch(this::stringCheck)) {
            logger.debug("contents test success");
            customDatabaseContentsText = contents;
            List<CustomDatabaseContents> contentsList = new ArrayList<>();
            Arrays.stream(testLine).forEach(line -> {
                String[] model = line.split("\t");
                contentsList.add(new CustomDatabaseContents(model[0],
                        model[1],model[2], model[3], model[4]));
            });
            customDbTable.getItems().removeAll(customDbTable.getItems());
            customDbTable.getItems().addAll(contentsList);
        } else {
            DialogUtil.error("Invalid content", "Please check the contents.", mainApp.getPrimaryStage(), true);
        }
    }

    @FXML
    public void save() {
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();

        if(StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(customDatabaseContentsText)) {
            Map<String, Object> params = new HashMap<>();
            params.put("panelId", panelId);
            params.put("title", title);
            params.put("description", description);
            params.put("contents", customDatabaseContentsText);
            if(customDatabaseId > 0) {
                try {
                    params.put("id", customDatabaseId);
                    apiService.put("/admin/panels/customDatabase/" + panelId + "/" + customDatabaseId, params, null, true);
                    dialogStage.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            } else {
                try {
                    apiService.post("/admin/panels/customDatabase/" + panelId, params, null, true);
                    dialogStage.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        } else {
            DialogUtil.error("Contents Error", "Please enter Title and Contents.", mainApp.getPrimaryStage(), true);
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
