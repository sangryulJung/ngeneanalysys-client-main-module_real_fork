package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.SystemLogView;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class SystemManagerSystemLogsController extends SubPaneController {

    private TableColumn<SystemLogView, String> createdAtTableColumn;
    private TableColumn<SystemLogView, String> logTypeTableColumn;
    private TableColumn<SystemLogView, String> logMsgTableColumn;
    private TableColumn<SystemLogView, String> userNameTableColumn;
    private TableColumn<SystemLogView, String> userGroupNameTableColumn;
    private TableColumn<SystemLogView, String> loginIdTableColumn;
    private TableColumn<SystemLogView, String> roleTableColumn;

    @Override
    public void show(Parent root) throws IOException {

    }
    
    private void setSystemLogList() {

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
