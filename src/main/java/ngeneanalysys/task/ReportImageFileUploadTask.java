package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.service.ImageUploadService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

/**
 * @author Jang
 * @since 2017-11-20
 */
public class ReportImageFileUploadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    private List<File> list = null;

    private int templateId = -1;

    private MainController mainController;

    ImageUploadService imageUploadService;

    public ReportImageFileUploadTask(List<File> list, int templateId, MainController mainController) {
        this.list = list;
        this.templateId = templateId;
        this.mainController = mainController;
        imageUploadService = ImageUploadService.getInstance();
    }

    @Override
    protected Object call() throws Exception {
        logger.info("image upload...");

        for(File imageFile : list) {
            logger.info(imageFile.getName());

            imageUploadService.uploadImage(templateId, imageFile, mainController);

        }

        return null;
    }
}
