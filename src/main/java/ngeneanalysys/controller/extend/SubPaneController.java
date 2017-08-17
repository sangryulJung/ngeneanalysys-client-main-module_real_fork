package ngeneanalysys.controller.extend;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ngeneanalysys.controller.MainController;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-10
 */
public abstract class SubPaneController extends BaseStageController {

    /** 메인화면 컨트롤러 객체 */
    protected MainController mainController;

    /** 화면 컨트롤 관련 부모 화면에서 전달된 파라미터 정보 */
    protected Map<String, Object> paramMap;

    /**
     * 메인 화면 컨트롤러 객체 세팅
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
    }

    /**
     *
     * @return
     */
    public MainController getMainController() {
        return mainController;
    }

    /**
     *
     * @return
     */
    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    /**
     *
     * @param paramMap
     */
    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * Dialog close
     * @param event
     */
    @FXML
    public void closeDialog(ActionEvent event) {
        Node eventElementNode = (Node) event.getSource();
        // dialog close
        Stage stage = (Stage) eventElementNode.getScene().getWindow();
        stage.close();
    }
}
