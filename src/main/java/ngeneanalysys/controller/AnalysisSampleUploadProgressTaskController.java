package ngeneanalysys.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.scene.Node;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.service.AnalysisRequestService;
import ngeneanalysys.task.AnalysisSampleUploadTask;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

/**
 * 분석 요청 샘플 업로드 Progress Task 화면 [메인화면하단출력]
 * 
 * @author gjyoo
 * @since 2016. 5. 26. 오후 8:46:26
 */
public class AnalysisSampleUploadProgressTaskController extends SubPaneController {
	private static final Logger logger = LoggerUtil.getLogger();
	
	/** 현재 진행중인 그룹명 출력 라벨 */
	@FXML
	private Label refName;
	
	/** 진행상태바 */
	@FXML
	private ProgressBar progressBar;
	
	/** 업로드 완료 샘플건수 */
	@FXML
	private Text completeCount;
	
	/** 총샘플건수 */
	@FXML
	private Text totalCount;
	
	/** 재시작버튼 */
	@FXML
	private Button buttonStart;
	
	/** 일시정지버튼 */
	@FXML
	private Button buttonPause;
	
	/** 진행중 아이콘 객체 */
	@FXML
	public ProgressIndicator progressIndicator;

	/** 분석 요청 서비스 */
	private AnalysisRequestService analysisRequestService;

	/** progress task object */
	private Task<?> task;
	/** running progress task thread  */
	private Thread thread;

	/** 현재 업로드중인 분석 요청 그롭 ID */
	private Integer currentUploadGroupId;
	/** 현재 업로드중인 분석 요청 그룹명 */
	private String currentUploadGroupRefName;
	
	/** 작업 일시정지 여부 */
	public boolean isPause = false;
	/** 작업 취소 여부 */
	public boolean isCancel = false;
	/** 작업 중지 여부 */
	public boolean isStop = false;

	private Node node;
	
	/**
	 * @param currentUploadGroupId the currentUploadGroupId to set
	 */
	public void setCurrentUploadGroupId(Integer currentUploadGroupId) {
		this.currentUploadGroupId = currentUploadGroupId;
	}

	/**
	 * @param currentUploadGroupRefName the currentUploadGroupRefName to set
	 */
	public void setCurrentUploadGroupRefName(String currentUploadGroupRefName) {
		this.currentUploadGroupRefName = currentUploadGroupRefName;
		if(this.refName != null) {
			this.refName.setText(this.currentUploadGroupRefName);
		}
	}

	/**
	 * 현재 그룹의 총 샘플 수 화면 출력
	 * @param totalCount String
	 */
	public void updateTotalCount(String totalCount) {
		this.totalCount.setText(totalCount);
	}
	
	/**
	 * 화면 출력 및 Task 실행
	 */

	@SuppressWarnings({"static-access", "unchecked"})
	@Override
	public void show(Parent root) throws IOException {
		this.analysisRequestService = AnalysisRequestService.getInstance();

		node = root;

		boolean isWorkStart = false;
		this.progressIndicator.setProgress(new ProgressBar().getProgress());

		List<File> fileList = (List<File>) paramMap.get("fileList");

		this.task = new AnalysisSampleUploadTask(this, fileList.size());

		progressBar.progressProperty().bind(this.task.progressProperty());
		completeCount.textProperty().bind(this.task.messageProperty());

		// 쓰레드 실행.
		thread = new Thread(this.task);
		thread.setDaemon(true);
		thread.start();
		this.mainController.getProgressTaskContentArea().getChildren().add(root);

	}
	
	/**
	 * 업로드 시작 처리
	 */

	@FXML
	public void startUpload() {
		logger.debug("resume from task controller..");
		try {
			Thread.sleep(100);
			isPause = false;
			this.buttonStart.setDisable(true);
			this.buttonPause.setDisable(false);
			this.progressIndicator.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 업로드 작업 일시정지
	 */
	@FXML
	public void pauseUpload() {
		logger.debug("pause from task controller..");
		try {
			Thread.sleep(100);
			isPause = true;
			this.buttonStart.setDisable(false);
			this.buttonPause.setDisable(true);
			this.progressIndicator.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 쓰레드 강제 종료
	 */
	public void interruptForce() {
		try {
			// 일시정지
			pauseUpload();
			Thread.sleep(100);
			isStop = true;
			isCancel = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 업로드 취소 처리
	 */
	@FXML
	public void cancelUpload() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		DialogUtil.setIcon(alert);
		alert.initOwner(getMainController().getPrimaryStage());
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Analysis Cancel Request");
		alert.setContentText("Do you want to cancel analysis request?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK){
			// 작업 일시 정지 처리.
			isPause = true;
			// 작업 정지 처리
			isStop = true;
			// 작업 취소 처리
			isCancel = true;
		} else {
			alert.close();
		}
	}
	
	/**
	 * 취소 완료 Alert창 출력
	 */
	public void showCancelCompleteDialog() {
		DialogUtil.alert("Cancel incomplete analysis request", "It has been canceled.", getMainController().getPrimaryStage(), true);
	}

	/**
	 * 업로드 작업 관련 화면 출력 정리
	 */
	public void clearWhenUploadTaskSucceeded() {
		this.mainController.clearProgressTaskArea(node);
	}

	/**
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}
}
