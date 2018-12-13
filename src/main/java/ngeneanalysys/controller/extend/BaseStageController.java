package ngeneanalysys.controller.extend;

import javafx.application.Platform;
import javafx.scene.Parent;
import ngeneanalysys.MainApp;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.util.ResourceUtil;
import org.controlsfx.control.MaskerPane;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Jang
 * @since 2017-08-03
 */
public abstract class BaseStageController {
    /** Resource Util */
    protected ResourceUtil resourceUtil = new ResourceUtil();

    /** 진행상태 표시 마스커 */
    protected MaskerPane maskerPane = new MaskerPane();

    /** Main Application Object */
    protected MainApp mainApp;

    /** Properties Config */
    protected Properties config;

    /** 초기 화면 출력 실행 */
    public abstract void show(Parent root) throws IOException;

    /**
     * 메인 어플 객체 세팅
     *
     * @param mainApp MainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        setConfig(PropertiesService.getInstance().getConfig());
    }

    /**
     * 메인 어플 객체 반환
     * @return MainApp
     */
    public MainApp getMainApp() {
        return this.mainApp;
    }


    /**
     * @return the config
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Properties config) {
        this.config = config;
    }

    public void setMaskerPaneVisable(boolean visable) {
        Platform.runLater(() -> this.maskerPane.setVisible(visable));
    }

    protected void setMaskerPanePrefWidth(double width) {
        Platform.runLater(() -> this.maskerPane.setPrefWidth(width));
    }
    protected void setMaskerPanePrefHeight(double height) {
        Platform.runLater(() -> this.maskerPane.setPrefHeight(height));
    }
}