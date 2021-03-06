package ngeneanalysys.controller.extend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.model.LoginSession;

import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-10
 */
public abstract class SubPaneController extends BaseStageController {

    /** 메인화면 컨트롤러 객체 */
    protected MainController mainController;
    
    /** 화면 컨트롤 관련 부모 화면에서 전달된 파라미터 정보 */
    protected Map<String,Object> paramMap;

    /** 로그인 세션 객체 */
    protected LoginSession loginSession;

    /**
     * 메인 화면 컨트롤러 객체 세팅
     *
     * @param mainController mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
    }

    /**
     * @return the mainController
     */
    public MainController getMainController() {
        return mainController;
    }

    /**
     * 파라미터 정보 삽입
     * @param param parameter
     */
    public void setParamMap(Map<String,Object> param) {
        this.paramMap = param;
    }

    /**
     * @return the paramMap
     */
    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    /**
     * Dialog close
     * @param event actionEvent
     */
    @FXML
    public void closeDialog(ActionEvent event) {
        Node eventElementNode = (Node) event.getSource();
        // dialog close
        Stage stage = (Stage) eventElementNode.getScene().getWindow();
        stage.close();
    }
}
