package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.service.JarUploadService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;

/**
 * @author Jang
 * @since 2018-02-19
 */
public class JarUploadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    private File jar = null;

    private int templateId = -1;

    private MainController mainController;

    JarUploadService jarUploadService;

    public JarUploadTask(File jar, int templateId, MainController mainController) {
        this.jar = jar;
        this.templateId = templateId;
        this.mainController = mainController;
        jarUploadService = JarUploadService.getInstance();
    }

    @Override
    protected Object call() throws Exception {
        logger.debug("jar upload...");
        mainController.setContentsMaskerPaneVisible(true);
        jarUploadService.uploadJar(templateId, jar, mainController);


        return null;
    }

    @Override
    protected void succeeded() {
        mainController.setContentsMaskerPaneVisible(false);
        super.succeeded();
    }

    @Override
    protected void failed() {
        mainController.setContentsMaskerPaneVisible(false);
        super.failed();
    }

    @Override
    protected void cancelled() {
        mainController.setContentsMaskerPaneVisible(false);
        super.cancelled();
    }
}
