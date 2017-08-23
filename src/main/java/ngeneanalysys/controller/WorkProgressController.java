package ngeneanalysys.controller;

import java.io.IOException;

import org.slf4j.Logger;
import ngeneanalysys.MainApp;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.util.LoggerUtil;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 서버 URL 설정 컨트롤러 클래스
 * 
 * @author gjyoo
 * @param <T>
 * @since 2016. 10. 24. 오후 5:22:03
 *
 */
public class WorkProgressController<T> extends BaseStageController {
	private static Logger logger = LoggerUtil.getLogger();
	
	private Stage workProgressStage;
	private String title;
	private Task<T> task;
	@FXML
	private Label workTitleLabel;
	@FXML
	private ProgressBar workProgressBar;
	@FXML
	private Label workProgressStatusLabel;
	
	public WorkProgressController(MainApp mainApp, String title, Task<T> task){
		super();
		this.mainApp = mainApp;
		this.title = title;
		this.task = task;
	}
	/**
	 * 화면 출력 실행.
	 */
	@Override
	public void show(Parent root) throws IOException {		
		workProgressStage = new Stage();
		workProgressStage.initStyle(StageStyle.DECORATED);
		workProgressStage.initModality(Modality.APPLICATION_MODAL);
		workProgressStage.setScene(new Scene(root));
		workProgressStage.setTitle(this.title);
		workTitleLabel.setText(this.title);
		// OS가 Window인 경우 아이콘 출력.
		if(System.getProperty("os.name").toLowerCase().contains("window")) {
			workProgressStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
		}
		workProgressStage.setOnCloseRequest(e -> {
			this.task.cancel();
			this.close();
		});
		workProgressStage.centerOnScreen();
		workProgressStage.initOwner(getMainApp().getPrimaryStage());
		
		workProgressBar.progressProperty().unbind();
		workProgressBar.progressProperty().bind(this.task.progressProperty());
		workProgressStatusLabel.textProperty().unbind();
		workProgressStatusLabel.textProperty().bind(this.task.messageProperty());
		this.task.setOnSucceeded( e -> {
			this.close();
			
		});
		workProgressStage.show();
		
		logger.info(String.format("start %s", workProgressStage.getTitle()));
	}

	/**
	 * 입력 URL 서버 연결 여부 확인
	 */
	@FXML
	public void cancelWork() {
		this.task.cancel();
		this.workProgressStage.close();
	}
	
	public void close(){
		this.workProgressStage.close();
	}
}
