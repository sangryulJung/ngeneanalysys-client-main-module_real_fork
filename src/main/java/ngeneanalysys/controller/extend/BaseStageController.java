package ngeneanalysys.controller.extend;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import ngeneanalysys.MainApp;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.util.PropertiesUtil;
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

    /** Spring Application Context 객체 */
    //protected ApplicationContext applicationContext;

    /** Properties Config */
    protected Properties config;

    /** 초기 화면 출력 실행 */
    public abstract void show(Parent root) throws IOException;

    /**
     * 메인 어플 객체 세팅
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        setConfig(PropertiesService.getInstance().getConfig());
    }

    /**
     * 메인 어플 객체 반환
     * @return
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
}