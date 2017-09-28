package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-09-25
 */
public class SystemManagerPanelController extends SubPaneController {

    private APIService apiService;

    @FXML
    private TextField panelNameTextField;

    @FXML
    private TextField panelCodeTextField;

    @FXML
    private TextField targetTextField;

    @FXML
    private ComboBox<ComboBoxItem> analysisTypeComboBox;

    @FXML
    private TextField sampleSourceTextField;

    @FXML
    private TableView<Panel> panelListTable;

    @FXML
    private TableColumn<Panel, String> panelId;

    @FXML
    private TableColumn<Panel, String> panelName;

    @FXML
    private TableColumn<Panel, String> panelCode;

    @FXML
    private TableColumn<Panel, String> analysisType;

    @FXML
    private TableColumn<Panel, String> sampleSource;

    @FXML
    private TableColumn<Panel, String> createdAt;

    @FXML
    private TableColumn<Panel, String> deleted;

    @FXML
    private TableColumn<Panel, Boolean> modify;

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();

    }

    @FXML
    public void panelAdd() {

    }
}
