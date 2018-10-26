package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.service.ClinicalVariantFileService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;

/**
 * @author Jang
 * @since 2018-01-09
 */
public class ClinicalFileUploadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private MainController controller;

    private ClinicalVariantFileService clinicalVariantFileService;

    private File bedFile;

    /** 진행상태 박스 id */
    private String progressBoxId;

    public ClinicalFileUploadTask(File bedFile) {
        clinicalVariantFileService = ClinicalVariantFileService.getInstance();
        this.bedFile = bedFile;
    }

    @Override
    protected Void call() {

        try {
            if(bedFile != null) {
                clinicalVariantFileService.uploadFile(bedFile);
            }
        } catch (WebAPIException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 실패시 처리
     */
    @Override
    protected void failed() {
        logger.error(String.format("bed bedFile upload task fail!!"));
        DialogUtil.error("bed bedFile upload fail", "bed bedFile upload fail.", controller.getMainApp().getPrimaryStage(), true);
        controller.removeProgressTaskItemById(progressBoxId);
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        logger.debug("bed bedFile upload task complete");
    }

}
