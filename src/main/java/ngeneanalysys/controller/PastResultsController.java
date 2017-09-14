package ngeneanalysys.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;

import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.enums.PanelKitCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.ExportVariantDataTask;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * 분석자(실험자) Past Results (최근 완료 분석건) 화면 컨트롤러 Class
 * 
 * @author gjyoo
 * @since 2016. 5. 9. 오후 8:16:02
 */
public class PastResultsController extends SubPaneController {
	private static Logger logger = LoggerUtil.getLogger();
	
	/** 현재 화면 Wrapper Pane */
	@FXML
	private GridPane experimentPastResultsWrapper;

	/** Assay Target choose box */
	@FXML
	private ComboBox<ComboBoxItem> choosePanel;
	/** sample source  choose box */
	@FXML
	private ComboBox<ComboBoxItem> chooseSampleSource;
	/** status choose box */
	@FXML
	private ComboBox<ComboBoxItem> chooseStatus;
	/** Experimenter search label */
	@FXML
	private Label experimenterSearchLabel;
	/** Experimenter choose box */
	@FXML
	private ComboBox<ComboBoxItem> chooseExperimenter;
	/** Submitted Start Date Picker */
	@FXML
	private DatePicker submittedStartDatePicker;
	/** Submitted End Date Picker */
	@FXML
	private DatePicker submittedEndDatePicker;
	/** group name input */
	@FXML
	private TextField sampleNameTextField;
	/** group name search label */
	@FXML
	private Label runSearchLabel;
	/** group name input */
	@FXML
	private TextField inputJobRunGroup;
	private Integer hiddenJobRunGroupId = 0;
	/** file choose box open button */
	@FXML
	private Button searchBtn;
	/** job run group choose dialog open button */
	/** Search Area Form Reset Button */
	@FXML
	private Button buttonResetForm;
	
	/** Run 검색 팝업 출력 버튼 */
	@FXML
	private Button buttonGroupChoose;
	
	/** 샘플 수 출력 */
	@FXML
	private Label sampleCount;
	
	/** 목록 새로고침 버튼 */
	@FXML
	private Button buttonRefresh;
	
	/** 목록 GridPane */
	@FXML
	private GridPane listGrid;
	
	@FXML
	private Pagination paginationList;

	/** API Service */
	private APIService apiService;
	
	/** Timer */
	public Timeline autoRefreshTimeline;
	private int itemCountPerPage;
	private List<SampleNameFieldVBox> sampleNameFields = new ArrayList<>();
	private List<RunFieldVBox> runFields = new ArrayList<>();
	private List<AssayTargetFieldVBox> assayTargetFieldVBoxes = new ArrayList<>();
	private List<AnalysisResultOverviewVBox> analysisResultOverviewVBoxes = new ArrayList<>();
	private List<QcResultVBox> qcResultVBoxes = new ArrayList<>();
	private List<DetailFieldVBox> detailFieldVBoxes = new ArrayList<>();

	/**
	 * 화면 출력
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void show(Parent root) throws IOException {
		logger.info("ExperimenterPastResultsController show..");
		itemCountPerPage = listGrid.getRowConstraints().size();
		// api service init..
		apiService = APIService.getInstance();
		apiService.setStage(getMainController().getPrimaryStage());
		
		// masker pane init
		experimentPastResultsWrapper.getChildren().add(maskerPane);
		maskerPane.setPrefWidth(getMainController().getMainFrame().getWidth());
		maskerPane.setPrefHeight(getMainController().getMainFrame().getHeight());
		maskerPane.setVisible(false);
		
		logger.info("choosePanel init..");
		choosePanel.setConverter(new ComboBoxConverter());
		choosePanel.getItems().add(new ComboBoxItem());
		List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
		for (Panel panel : panels) {
				choosePanel.getItems().add(new ComboBoxItem(panel.getId().toString(), panel.getName()));
		}
		choosePanel.getSelectionModel().selectFirst();
		
		logger.info("chooseSampleSource init..");
		chooseSampleSource.setConverter(new ComboBoxConverter());
		chooseSampleSource.getItems().add(new ComboBoxItem());
		for (SampleSourceCode code : SampleSourceCode.values()) {
				chooseSampleSource.getItems().add(new ComboBoxItem(code.name(), code.getDescription()));
		}
		chooseSampleSource.getSelectionModel().selectFirst();
		
		chooseStatus.setConverter(new ComboBoxConverter());
		chooseStatus.getItems().add(new ComboBoxItem());
		chooseStatus.getItems().add(new ComboBoxItem(AnalysisJobStatusCode.SAMPLE_JOB_STATUS_COMPLETE, "Reported"));
		chooseStatus.getItems().add(new ComboBoxItem(AnalysisJobStatusCode.SAMPLE_JOB_STATUS_RUNNING, "Reviewing"));
		chooseStatus.getItems().add(new ComboBoxItem(AnalysisJobStatusCode.SAMPLE_JOB_STATUS_NONE, "Not Reported"));
		chooseStatus.getSelectionModel().selectFirst();
		
		logger.info("chooseExperimenter init..");
		chooseExperimenter.setConverter(new ComboBoxConverter());
		chooseExperimenter.getItems().add(new ComboBoxItem());
		
		experimenterSearchLabel.setVisible(false);
		chooseExperimenter.setVisible(false);
		
		logger.info("submittedDatePicker init..");
		String dateFormat = "yyyy-MM-dd";
		submittedStartDatePicker.setConverter(DatepickerConverter.getConverter(dateFormat));
		submittedStartDatePicker.setPromptText(dateFormat);
		submittedStartDatePicker.valueProperty().addListener(element -> {
			if(submittedStartDatePicker.getValue() != null && submittedEndDatePicker.getValue() != null) {
				int minDate = Integer.parseInt(submittedStartDatePicker.getValue().toString().replace("-", ""));
				int maxDate = Integer.parseInt(submittedEndDatePicker.getValue().toString().replace("-", ""));
				if(minDate > maxDate) {
					DialogUtil.warning("선택한 날짜가 검색의 마지막 날짜 이후의 날짜입니다.", "Date is later than the last day of the selected date search.", getMainApp().getPrimaryStage(), true);
					submittedStartDatePicker.setValue(null);
				}
			}
		});
		submittedEndDatePicker.setConverter(DatepickerConverter.getConverter(dateFormat));
		submittedEndDatePicker.setPromptText(dateFormat);
		submittedEndDatePicker.valueProperty().addListener(element -> {
			if(submittedEndDatePicker.getValue() != null && submittedStartDatePicker.getValue() != null) {
				int minDate = Integer.parseInt(submittedStartDatePicker.getValue().toString().replace("-", ""));
				int maxDate = Integer.parseInt(submittedEndDatePicker.getValue().toString().replace("-", ""));
				if(minDate > maxDate) {
					DialogUtil.warning("선택한 날짜가 검색의 시작 날짜 이전의 날짜입니다.", "The selected date is the date before the search of the start date.", getMainApp().getPrimaryStage(), true);
					submittedEndDatePicker.setValue(null);
				}
			}
		});
		initSampleListLayout();
		// 페이지 이동 이벤트 바인딩
		paginationList.setPageFactory(pageIndex -> {
				setList(pageIndex + 1);
				return new VBox();
		});

		// 시스템 설정에서 자동 새로고침 설정이 true 인경우 자동 새로고팀 실행
		startAutoRefresh();
		
		this.mainController.getMainFrame().setCenter(root);
	}
	
	/**
	 * 자동 새로고침 시작 처리
	 */
	public void startAutoRefresh() {
		boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
		logger.info(String.format("auto refresh on : %s", isAutoRefreshOn));
		
		if(isAutoRefreshOn) {
			// 갱신 시간 간격
			int refreshPeriodSecond = Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000;
			logger.info(String.format("auto refresh period second : %s millisecond", refreshPeriodSecond));
			
			// 타임라인 객체가 없는 경우
			if(autoRefreshTimeline == null) {
				autoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(refreshPeriodSecond),
						ae -> setList(paginationList.getCurrentPageIndex() + 1)));
				autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
				autoRefreshTimeline.play();
			} else {
				logger.info(String.format("[%s] timeline restart", this.getClass().getName()));
				autoRefreshTimeline.stop();
				// 현재 선택된 시간간격으로 재설정
				autoRefreshTimeline.setDelay(Duration.millis(refreshPeriodSecond));
				autoRefreshTimeline.play();
			}
		} else {
			if(autoRefreshTimeline != null) {
				autoRefreshTimeline.stop();
			}
		}
	}
	
	/**
	 * 자동 새로고침 일시정지
	 */
	public void pauseAutoRefresh() {
		boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
		// 기능 실행중인 상태인 경우 실행
		if(autoRefreshTimeline != null && isAutoRefreshOn) {
			logger.info(String.format("[%s] timeline status : %s", this.getClass().getName(),
					autoRefreshTimeline.getStatus()));
			// 일시정지
			if(autoRefreshTimeline.getStatus() == Animation.Status.RUNNING) {
				autoRefreshTimeline.pause();
				logger.info(String.format("[%s] auto refresh pause", this.getClass().getName()));
			}
		}
	}
	
	/**
	 * 자동 새로고침 시작
	 */
	public void resumeAutoRefresh() {
		boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
		// 기능 실행중인 상태인 경우 실행
		if(autoRefreshTimeline != null && isAutoRefreshOn) {
			logger.info(String.format("[%s] timeline status : %s", this.getClass().getName(),
					autoRefreshTimeline.getStatus()));
			// 시작
			if(autoRefreshTimeline.getStatus() == Animation.Status.PAUSED) {
				autoRefreshTimeline.play();
				logger.info(String.format("[%s] auto refresh resume", this.getClass().getName()));
			}
		}
	}
	
	/**
	 * 목록 새로고침 실행.
	 */
	@FXML
	public void refreshList() {
		setList(paginationList.getCurrentPageIndex() + 1);
	}

	/**
	 * 분석 진행중인 목록 조회
	 * 
	 * @param page
	 * @return
	 */
	public void setList(int page) {
		if(autoRefreshTimeline != null) {
			logger.info(String.format("auto refresh timeline status : %s", autoRefreshTimeline.getStatus()));
		}
		maskerPane.setVisible(true);
		
		int totalCount = 0;
		// 조회 시작 index
		int offset = (page - 1) * itemCountPerPage;

		Map<String, Object> param = getSearchParam();
		param.put("limit", itemCountPerPage);
		param.put("offset", offset);
		param.put("ordering", "DESC");
		
		try {
			HttpClientResponse response = apiService.get("/searchSamples", param, null,
					false);

			if (response != null) {
				PagedSampleView searchedSamples = response
						.getObjectBeforeConvertResponseToJSON(PagedSampleView.class);
				List<SampleView> list = null;
				if (searchedSamples != null) {
					totalCount = searchedSamples.getCount();
					list = searchedSamples.getResult();
				}
				int pageCount = 0;
				if (totalCount > 0) {
					paginationList.setCurrentPageIndex(page - 1);
					pageCount = totalCount / itemCountPerPage;
					if (totalCount % itemCountPerPage > 0) {
						pageCount++;
					}
				}
				logger.info(String.format("total count : %s, page count : %s", totalCount, pageCount));
				sampleCount.setText(String.valueOf(totalCount));
				renderSampleList(list);
				if (pageCount > 0) {
					paginationList.setVisible(true);
					paginationList.setPageCount(pageCount);
				} else {
					paginationList.setVisible(false);
				}

			} else {
				paginationList.setVisible(false);
			}
		} catch (WebAPIException wae) {
			// DialogUtil.error(null, "Running and Recent Samples Search
			// Error.", getMainApp().getPrimaryStage(),true);
			DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
					getMainApp().getPrimaryStage(), false);
		} catch (Exception e) {
			DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(),
					false);
		}
		maskerPane.setVisible(false);
	}

	private Map<String, Object> getSearchParam() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("format", "json");		
		param.put("step", "PIPELINE");
		
		/** 검색 항목 설정 Start */
		// Assay Target
		if(choosePanel.getSelectionModel().getSelectedIndex() > 0 && choosePanel.getValue() != null) {
			param.put("panelIds", choosePanel.getValue().getValue());
		}
		// Sample Source
		if(chooseSampleSource.getSelectionModel().getSelectedIndex() > 0 && chooseSampleSource.getValue() != null) {
			param.put("sampleSource", chooseSampleSource.getValue().getValue());
		}
		// Status chooseStatus
		if(chooseStatus.getSelectionModel().getSelectedIndex() > 0 && chooseStatus.getValue() != null) {
			param.put("status", chooseStatus.getValue().getValue());
		}
		// Experiment
		if(chooseExperimenter.getSelectionModel().getSelectedIndex() > -1 && chooseExperimenter.getValue() != null) {
			param.put("job_run_group_user_id", chooseExperimenter.getValue().getValue());
		}
		
		String minCreateAt = (submittedStartDatePicker.getValue() != null) ? submittedStartDatePicker.getValue().toString() : null;
		String maxCreateAt = (submittedEndDatePicker.getValue() != null) ? submittedEndDatePicker.getValue().toString() : null;
		
		// Submitted [start]
		if(!StringUtils.isEmpty(minCreateAt)) {
			// 로컬 타임 표준시로 변환
			String minCreateAtUTCDate = ConvertUtil.convertLocalTimeToUTC(minCreateAt + " 00:00:00", "yyyy-MM-dd HH:mm:ss", null);
			param.put("createdAtFrom", minCreateAtUTCDate);
		}
		// Submitted [end]
		if(!StringUtils.isEmpty(maxCreateAt)) {
			// 로컬 타임 표준시로 변환
			String maxCreateAtUTCDate = ConvertUtil.convertLocalTimeToUTC(maxCreateAt + " 23:59:59", "yyyy-MM-dd HH:mm:ss", null);
			param.put("createdAtTo", maxCreateAtUTCDate);
		}
		// sampleName
		if(!StringUtils.isEmpty(sampleNameTextField.getText())) {
			param.put("sampleName", sampleNameTextField.getText());
		}
		// job run group
		if(this.hiddenJobRunGroupId > 0 && !StringUtils.isEmpty(inputJobRunGroup.getText())) {
			param.put("job_run_group_id", this.hiddenJobRunGroupId);
		}
		/** End 검색 항목 설정 */
		return param;
	}

	public void initSampleListLayout() {
		for(int i = 0; i < listGrid.getRowConstraints().size(); i++){
			SampleNameFieldVBox jobVBox = new SampleNameFieldVBox();
			jobVBox.setVisible(false);
			sampleNameFields.add(jobVBox);
			listGrid.add(jobVBox, 0, i);

			RunFieldVBox runFieldVBox = new RunFieldVBox();
			runFieldVBox.setVisible(false);
			runFields.add(runFieldVBox);
			listGrid.add(runFieldVBox, 1, i);

			AssayTargetFieldVBox assayTargetFieldVBox = new AssayTargetFieldVBox();
			assayTargetFieldVBox.setVisible(false);
			assayTargetFieldVBoxes.add(assayTargetFieldVBox);
			listGrid.add(assayTargetFieldVBox, 2, i);

			AnalysisResultOverviewVBox analysisResultOverviewVBox = new AnalysisResultOverviewVBox();
			analysisResultOverviewVBox.setVisible(false);
			analysisResultOverviewVBoxes.add(analysisResultOverviewVBox);
			listGrid.add(analysisResultOverviewVBox, 3, i);

			QcResultVBox qcResultVBox = new QcResultVBox();
			qcResultVBox.setVisible(false);
			qcResultVBoxes.add(qcResultVBox);
			listGrid.add(qcResultVBox, 4, i);

			DetailFieldVBox detailFieldVBox = new DetailFieldVBox();
			detailFieldVBox.setVisible(false);
			detailFieldVBoxes.add(detailFieldVBox);
			listGrid.add(detailFieldVBox, 5, i);
		}
	}
	/**
	 * 샘플 목록 화면 출력
	 * 
	 * @param list
	 */
	public void renderSampleList(List<SampleView> list) {
		if (list != null && list.size() > 0) {
			for(int idx = 0; idx < list.size(); idx++) {
				SampleView sample = list.get(idx);
				sampleNameFields.get(idx).setSampleView(sample);
				runFields.get(idx).setSampleView(sample);
				assayTargetFieldVBoxes.get(idx).setSampleView(sample);
				analysisResultOverviewVBoxes.get(idx).setSampleView(sample);
				qcResultVBoxes.get(idx).setSampleView(sample);
				detailFieldVBoxes.get(idx).setSampleView(sample);
			}
			for(int idx = list.size(); idx < itemCountPerPage; idx++) {
				sampleNameFields.get(idx).setVisible(false);
				runFields.get(idx).setVisible(false);
				assayTargetFieldVBoxes.get(idx).setVisible(false);
				analysisResultOverviewVBoxes.get(idx).setVisible(false);
				qcResultVBoxes.get(idx).setVisible(false);
				detailFieldVBoxes.get(idx).setVisible(false);
			}
		} else {
			for(int idx = 0; idx < itemCountPerPage; idx++) {
				sampleNameFields.get(idx).setVisible(false);
				runFields.get(idx).setVisible(false);
				assayTargetFieldVBoxes.get(idx).setVisible(false);
				analysisResultOverviewVBoxes.get(idx).setVisible(false);
				qcResultVBoxes.get(idx).setVisible(false);
				detailFieldVBoxes.get(idx).setVisible(false);
			}
		}
	}
	
	/**
	 * 그룹 목록 다이얼로그창 출력
	 */
	@FXML
	public void openGroupChooseDialog() {
		try {
			this.hiddenJobRunGroupId = 0;
			this.inputJobRunGroup.setText(null);

			FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_JOB_RUN_GROUP_SEARCH_DIALOG);
			Pane dialogPane = (Pane) loader.load();
			AnalysisJobRunGroupSearchController controller = loader.getController();
			controller.setMainApp(getMainApp());
			controller.setPastResultsController(this);
			controller.show(dialogPane);
		} catch (Exception e) {
			logger.error("job run group search dialog open fail.." + e.getMessage());
		}
	}
	
	/**
	 * 검색 대상 분석 요청 그룹 정보 설정
	 * @param jsonString
	 */
	public void setSearchJobRunGroupInfo(String jsonString) {
//		AnalysisJob analysisJob = JsonUtil.fromJson(jsonString, AnalysisJob.class);
//		this.hiddenJobRunGroupId = analysisJob.getId();
//		this.inputJobRunGroup.setText(analysisJob.getRefName());
	}
	
	/**
	 * 검색
	 */
	@FXML
	public void search() {
		setList(1);
	}
	
	/**
	 * 검색 폼 리셋
	 */
	@FXML
	public void resetSearchForm() {
		choosePanel.setValue(new ComboBoxItem());
		chooseSampleSource.setValue(new ComboBoxItem());
		chooseStatus.setValue(new ComboBoxItem());
		chooseExperimenter.setValue(new ComboBoxItem());
		submittedStartDatePicker.setValue(null);
		submittedEndDatePicker.setValue(null);
		sampleNameTextField.setText(null);
		inputJobRunGroup.setText(null);
		hiddenJobRunGroupId = 0;
	}
	
	/**
	 * 엑셀 파일로 저장
	 */
	@FXML
	public void saveExcel(ActionEvent event) {
		exportVariantData("Excel");
	}
	
	/**
	 * CSV 파일로 저장
	 */
	@FXML
	public void saveCSV(ActionEvent event) {
		exportVariantData("CSV");
	}
	
	@SuppressWarnings("unchecked")
	private void exportVariantData(String fileType){
		List<Map<String, Object>> searchedSamples = new ArrayList<>();
		Map<String, Object> param = getSearchParam();
		param.put("fields", "id,name,job_run_group_ref_name");
		try {
			HttpClientResponse response = apiService.get("/analysis_progress_state/filter", param, null, false);

			if (response != null) {
				searchedSamples = response.getObjectBeforeConvertResponseToJSON(searchedSamples.getClass());
				if (searchedSamples != null && searchedSamples.size() > 0) {
					// Show save file dialog
					FileChooser fileChooser = new FileChooser();
					if ("Excel".equals(fileType)) {
						fileChooser.getExtensionFilters()
								.addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx)", "*.xlsx"));						
					} else {
						fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));						
					}
					fileChooser.setTitle("export variants to " + fileType + " format file");
					File file = fileChooser.showSaveDialog(this.mainApp.getPrimaryStage());
					if (file != null) {						
						Task<Void> task = new ExportVariantDataTask(this.getMainApp(), fileType, file, searchedSamples);
						Thread exportDataThread = new Thread(task);
						WorkProgressController<Void> workProgressController = new WorkProgressController<Void>(this.getMainApp(), "Export variant List", task);
						FXMLLoader loader = this.mainApp.load("/layout/fxml/WorkProgress.fxml");
						loader.setController(workProgressController);
						Node root = loader.load();
						workProgressController.show((Parent) root);
						exportDataThread.start();
					}
				} else {
					return;
				}
			}
		} catch (WebAPIException wae) {
			DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
					this.mainApp.getPrimaryStage(), true);
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.error("Save Fail.", "An error occurred during the creation of the " + fileType + " document." + e.getMessage(),
					this.mainApp.getPrimaryStage(), false);			
		}
	}
	class SampleNameFieldVBox extends VBox {
		// 샘플명
		private TextField jobLabel;
		// 요청일시 라벨
		private TextField submittedLabel;
		// 작업 시작일시 라벨
		private TextField startedLabel;

		protected  SampleNameFieldVBox() {
			super();
			this.setId("jobArea");
			this.getStyleClass().add("colunmn");
			jobLabel = new TextField();
			jobLabel.setEditable(false);
			jobLabel.setId("job");
			submittedLabel = new TextField();
			submittedLabel.setId("submitted");
			startedLabel = new TextField();
			startedLabel.setId("started");
			//this.getChildren().setAll(jobLabel, submittedLabel, startedLabel);
			this.getChildren().setAll(jobLabel, submittedLabel);
		}
		protected void setSampleView(final SampleView sample) {
			this.setVisible(true);
			jobLabel.setText(sample.getName());
			submittedLabel.setText("Submitted : "
					+ DateFormatUtils.format(sample.getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss"));
			String startedDate = "-";
			if (sample.getSampleStatus().getPipelineStartedAt() != null) {
				startedDate = DateFormatUtils.format(sample.getSampleStatus().getPipelineStartedAt().toDate(), "yyyy-MM-dd HH:mm:ss");
			}
			startedLabel.setText("Started : " +  startedDate);
		}
	}
	class RunFieldVBox extends VBox {
		private TextField runName;
		private TextField sampleSource;
		RunFieldVBox() {
			super();
			// 시퀀스 장비 column box
			this.setId("groupArea");
			this.getStyleClass().add("colunmn");
			runName = new TextField();
			runName.setId("refName");
			//runName.getStyleClass().add("font_size_12");
			HBox sampleSourceHBox = new HBox();
			sampleSourceHBox.setId("sampleSource");
			sampleSource = new TextField();
			sampleSourceHBox.getChildren().setAll(new Label("Sample : "), sampleSource);
			this.getChildren().setAll(runName, sampleSourceHBox);
		}
		protected void setSampleView(SampleView sample) {
			this.setVisible(true);
			runName.setText(sample.getRunName());
			sampleSource.setText(sample.getSampleSource());
			sampleSource.getStyleClass().clear();
			sampleSource.getStyleClass().add(sample.getSampleSource());
		}
	}
	class AssayTargetFieldVBox extends VBox {
		private Label assayTarget;
		private Label platform;

		AssayTargetFieldVBox() {
			super();
			// Assay Target (사용패널키트) Column Box
			this.getStyleClass().add("colunmn");
			this.setId("assayTargetPlatformArea");
			assayTarget = new Label();
			assayTarget.setId("assayTarget");
			assayTarget.getStyleClass().add("font_size_9");
			platform = new Label("Illumina MiSeq DX");
			platform.setId("platform");
			platform.getStyleClass().add("font_size_12");
			this.getChildren().setAll(assayTarget, platform);
		}
		protected void setSampleView(SampleView sample) {
			this.setVisible(true);
			assayTarget.setText(sample.getPanelName());
		}
	}

	class AnalysisResultOverviewVBox extends VBox {
		private Label geneCountValueLabel = new Label();
		private Label depthMinValueLabel = new Label();
		private Label depthMaxValueLabel = new Label();
		private Label totalVariantCountValueLabel = new Label();
		private Label warningVariantCountValueLabel = new Label();
		private Button roiCoverageButton;
		private Label roiCoverageValueLabel = new Label();
		private Button meanReadQualityButton;
		private Label meanReadQualityValueLabel = new Label();
		private Button retainedReadsButton;
		private Label retainedReadsValueButton = new Label();
		private Button coverageUniformityButton;
		private Label coverageUniformityValueLabel = new Label();
		private Label reportLabel = new Label();

		AnalysisResultOverviewVBox() {
			super();
			// Result Overview Column Box
			this.getStyleClass().add("colunmn");
			this.setId("result_overview");
			GridPane gridPane = new GridPane();
			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPercentWidth(20);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPercentWidth(20);
			ColumnConstraints col3 = new ColumnConstraints();
			col3.setPercentWidth(20);
			ColumnConstraints col4 = new ColumnConstraints();
			col4.setPercentWidth(20);
			ColumnConstraints col5 = new ColumnConstraints();
			col5.setPercentWidth(20);
			gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5);

			// min depth count
			HBox depthMinHBox = getCountInfo("DEPTH MIN : ", depthMinValueLabel);
				gridPane.add(depthMinHBox, 0, 0);

			// max depth count
			HBox depthMaxHBox = getCountInfo("DEPTH MAX : ", depthMaxValueLabel);
			gridPane.add(depthMaxHBox, 1, 0);

			// gene count
			HBox geneCountHBox = getCountInfo("GENES : ", geneCountValueLabel);
			gridPane.add(geneCountHBox, 2, 0);


			// total variant count
			HBox variantsHBox = getCountInfo("VARIANTS : ", totalVariantCountValueLabel);
			gridPane.add(variantsHBox, 3, 0);

			// warnings count
			HBox warnHBox = getCountInfo("WARNING : ", warningVariantCountValueLabel);
			gridPane.add(warnHBox, 4, 0);

			// qc flag box
			HBox qcFlagHbox = new HBox();
			qcFlagHbox.getStyleClass().add("alignment_center_left");

			roiCoverageButton = getQCIcon("ROI Coverage",
					"Percentage of ROI region\nwith coverage of least 20X (\u2265 100%)",
					roiCoverageValueLabel);
			meanReadQualityButton = getQCIcon("Mean Read Quality",
					"Percentage of reads\nwith mean Phred base quality above 30 (\u2265 90%)",
					meanReadQualityValueLabel);
			retainedReadsButton = getQCIcon("Retained Reads",
					"Percentage of QC passed reads (\u2265 80%)",
					retainedReadsValueButton);
			coverageUniformityButton = getQCIcon("Coverage Uniformity",
					"Percentage of bases\ncovered at \u2265 20% of the mean coverage",
					coverageUniformityValueLabel);

			qcFlagHbox.getChildren().addAll(roiCoverageButton, meanReadQualityButton, retainedReadsButton,
					coverageUniformityButton);
			qcFlagHbox.setMargin(meanReadQualityButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(retainedReadsButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(coverageUniformityButton, new Insets(0, 0, 0, 5));

			qcFlagHbox.getChildren().add(reportLabel);
			qcFlagHbox.setMargin(reportLabel, new Insets(0, 0, 0, 5));

			this.getChildren().addAll(gridPane, qcFlagHbox);
			this.setMargin(qcFlagHbox, new Insets(0, 0, 0, 0));
		}

		private HBox getCountInfo(String title, Label valueLabel) {
			HBox hBox = new HBox();
			Label titleLabel = new Label(title);
			titleLabel.getStyleClass().add("font_size_10");
			titleLabel.getStyleClass().add("txt_gray");
			valueLabel.setText("0");
			valueLabel.getStyleClass().add("txt_black");
			valueLabel.getStyleClass().add("weight_bold");
			valueLabel.getStyleClass().add("font_size_10");
			hBox.getChildren().addAll(titleLabel, valueLabel);
			hBox.setMargin(valueLabel, new Insets(0, 10, 0, 0));
			return hBox;
		}
		private Button getQCIcon(String title, String contents, Label percentageLabel) {
			Button qcButton = new Button();
			qcButton.getStyleClass().add("bullet_green");
			qcButton.setOnAction(event -> {
				PopOver popOver = new PopOver();
				popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
				popOver.setHeaderAlwaysVisible(true);
				popOver.setAutoHide(true);
				popOver.setAutoFix(true);
				popOver.setDetachable(true);
				popOver.setArrowSize(15);
				popOver.setArrowIndent(30);

				VBox box = new VBox();
				box.setStyle("-fx-padding:5;");
				box.setAlignment(Pos.BOTTOM_RIGHT);
				Label titleLabel = new Label(title);
				titleLabel.getStyleClass().add("font_size_10");
				titleLabel.getStyleClass().add("weight_bold");
				titleLabel.getStyleClass().add("txt_gray_656565");
				percentageLabel.getStyleClass().add("font_size_10");
				percentageLabel.getStyleClass().add("weight_bold");
				percentageLabel.getStyleClass().add("txt_black");
				Label contentsLabel = new Label(contents);
				contentsLabel.getStyleClass().add("font_size_9");
				contentsLabel.getStyleClass().add("txt_gray_656565");
				box.getChildren().addAll(titleLabel, percentageLabel, contentsLabel);
				box.setMargin(percentageLabel, new Insets(5, 0, 0, 0));
				box.setMargin(contentsLabel, new Insets(5, 0, 0, 0));

				popOver.setContentNode(box);
				popOver.show((Node) qcButton);
				});
			return qcButton;
		}
		protected  void setSampleView(SampleView sample) {
			if(sample.getAnalysisResultSummary() != null) {
				AnalysisResultSummary analysisResultSummary = sample.getAnalysisResultSummary();
				depthMinValueLabel.setText(analysisResultSummary.getDepthMin().toString());
				depthMaxValueLabel.setText(analysisResultSummary.getDepthMax().toString());
				geneCountValueLabel.setText(analysisResultSummary.getGeneCount().toString());
				totalVariantCountValueLabel.setText(analysisResultSummary.getAllVariantCount().toString());
				warningVariantCountValueLabel.setText(analysisResultSummary.getWarningVariantCount().toString());
				roiCoverageValueLabel.setText(analysisResultSummary.getRoiCoveragePercentage().toString());
				meanReadQualityValueLabel.setText(analysisResultSummary.getMeanReadQualityPercentage().toString());
				coverageUniformityValueLabel.setText(analysisResultSummary.getCoverageUniformityPercentage().toString());
			}
			setVisible(true);
		}
	}
	class QcResultVBox extends VBox {
		QcResultVBox() {
			// fastqc column box
			this.setId("qcArea");
			this.getStyleClass().add("colunmn");
		}

		protected  void setSampleView(SampleView sample){
			setVisible(true);
			this.getChildren().clear();
			String fastQC = sample.getQcResult();
			fastQC = (StringUtils.isEmpty(fastQC) && sample.getAnalysisResultSummary() != null) ? sample.getAnalysisResultSummary().getQualityControlStatus() : fastQC;
			fastQC = (!StringUtils.isEmpty(fastQC)) ? fastQC.toUpperCase() : "NONE";
			Image img = resourceUtil.getImage("/layout/images/icon_qc_" + fastQC.toLowerCase() + ".png");
			if (img != null) {
				ImageView imgVw = new ImageView(img);
				this.getChildren().setAll(imgVw);
			}
			if (!"NONE".equals(fastQC)) {
				Label qcLabel = null;
				if("WARN".equals(fastQC)) {
					qcLabel = new Label("WARNING");
				} else {
					qcLabel = new Label(fastQC);
				}
				qcLabel.setId(fastQC.toLowerCase());
				qcLabel.getStyleClass().add("font_size_12");
				this.getChildren().add(qcLabel);
			}
		}
	}
	class DetailFieldVBox extends VBox {
		SampleView sample = null;
		DetailFieldVBox() {
			this.setId("detailArea");
			this.getStyleClass().add("column");
			Button btn = new Button("Detail");
			btn.getStyleClass().add("btn_detail");
			btn.setOnAction(e -> {
				if(sample != null) {
					Map<String, Object> detailViewParamMap = new HashMap<>();
					detailViewParamMap.put("id", sample.getId());

					TopMenu menu = new TopMenu();
					menu.setId("sample_" + sample.getId());
					menu.setMenuName(sample.getName());
					menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_LAYOUT);
					menu.setParamMap(detailViewParamMap);
					menu.setStaticMenu(false);
					mainController.addTopMenu(menu, 2, true);
				}
			});
//				if(sample.getAnalysisResultSummary() != null) {
//					btn.setOnAction(e -> {
//						Map<String,Object> detailViewParamMap = new HashMap<String,Object>();
//						detailViewParamMap.put("id", sample.getId());
//
//						TopMenu menu = new TopMenu();
//						menu.setId("sample_" + sample.getId());
//						menu.setMenuName(sample.getName());
//						menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_LAYOUT);
//						menu.setParamMap(detailViewParamMap);
//						menu.setStaticMenu(false);
//						mainController.addTopMenu(menu, 2, true);
//					});
//				} else {
//					btn.setOnAction(e -> {
//						DialogUtil.alert("Incomplete analysis of cases", "You can not browse the analytical work which has not been completed.", getMainApp().getPrimaryStage(), true);
//					});
//				}
			this.getChildren().add(btn);
		}
		protected void setSampleView(SampleView sample) {
			this.sample = sample;
			this.setVisible(true);
		}
	}

}
