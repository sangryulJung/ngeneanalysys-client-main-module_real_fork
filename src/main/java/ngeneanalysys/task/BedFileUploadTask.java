package ngeneanalysys.task;

import javafx.concurrent.Task;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.service.BedFileService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;

/**
 * @author Jang
 * @since 2017-10-13
 */
public class BedFileUploadTask extends Task {
    private static Logger logger = LoggerUtil.getLogger();

    /** 컨트롤러 클래스 */
    private MainController controller;

    /** 프로세스 정상 실행 여부 */
    private boolean isExecute = true;

    private BedFileService bedFileService;

    private int panelId;

    private File file;

    /** 진행상태 박스 id */
    private String progressBoxId;

    public BedFileUploadTask(int panelId, File file) {
        bedFileService = BedFileService.getInstance();
        this.panelId = panelId;
        this.file = file;
    }

    @Override
    protected Void call() {

        try {
            bedFileService.uploadFile(panelId, file);
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
        logger.error(String.format("bed file upload task fail!!"));
        DialogUtil.error("bed file upload fail", "bed file upload fail.", controller.getMainApp().getPrimaryStage(), true);
        controller.removeProgressTaskItemById(progressBoxId);
    }

    /**
     * 성공시 처리
     */
    @Override
    protected void succeeded() {
        logger.info("bed file upload task complete");

        //controller.removeProgressTaskItemById(progressBoxId);
    }


}
