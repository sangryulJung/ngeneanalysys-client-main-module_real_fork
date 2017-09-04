package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.User;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class SystemManagerUserAccountController extends SubPaneController{

    @Override
    public void show(Parent root) throws IOException {

    }

    @FXML
    public void userSearch() {

    }

    @FXML
    public void groupSearch() {

    }

    @FXML
    public void reset() {

    }

    @FXML
    public void groupAdd() {

    }

    @FXML
    public void userAdd() {
        userControllerInit("add", null);
    }

    public void userControllerInit(String type, User user) {

    }
}
