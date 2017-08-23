package ngeneanalysys.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.PanelKitCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Run;
import ngeneanalysys.model.PagedRun;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.TopMenu;
import ngeneanalysys.model.render.AnalysisJobResultOverview;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.ExportVariantDataTask;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
	private TextField inputJob;
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

	/**
	 * 화면 출력
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void show(Parent root) throws IOException {
		logger.info("ExperimenterPastResultsController show..");

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
		for (PanelKitCode code : PanelKitCode.values()) {
			if (code.isAvailable()) {
				choosePanel.getItems().add(new ComboBoxItem(code.name(), code.getDescription()));
			}
		}
		choosePanel.getSelectionModel().selectFirst();
		
		logger.info("chooseSampleSource init..");
		chooseSampleSource.setConverter(new ComboBoxConverter());
		chooseSampleSource.getItems().add(new ComboBoxItem());
		for (SampleSourceCode code : SampleSourceCode.values()) {
			if (code.equals(SampleSourceCode.BLOOD)) {
				chooseSampleSource.getItems().add(new ComboBoxItem(code.name(), code.getDescription()));
			}
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

		// 페이지 이동 이벤트 바인딩
		paginationList.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer pageIndex) {
				setList(pageIndex + 1);
				return new VBox();
			}
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
			logger.info(String.format("[%s] timeline status : %s", this.getClass().getName(), autoRefreshTimeline.getStatus()));
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
			logger.info(String.format("[%s] timeline status : %s", this.getClass().getName(), autoRefreshTimeline.getStatus()));
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
		// 화면 출력 row수
		int limit = 5;
		// 조회 시작 index
		int offset = (page - 1) * limit;

		Map<String, Object> param = getSearchParam();
		param.put("limit", limit);
		param.put("offset", offset);
		
		try {
			HttpClientResponse response = apiService.get("/analysis_progress_state/filter", param, null, false);

			if (response != null) {
//				AnalysisProgressStateForPaging analysisProgressState = response
//						.getObjectBeforeConvertResponseToJSON(AnalysisProgressStateForPaging.class);
//				List<AnalysisSample> list = null;
//				if (analysisProgressState != null) {
//					totalCount = analysisProgressState.getCount();
//					list = analysisProgressState.getList();
//				}
				int pageCount = 0;
				if (totalCount > 0) {
					paginationList.setCurrentPageIndex(page - 1);
					pageCount = totalCount / limit;
					if (totalCount % limit > 0) {
						pageCount++;
					}
				}
				logger.info(String.format("total count : %s, page count : %s", totalCount, pageCount));
				sampleCount.setText(String.valueOf(totalCount));
				//renderSampleList(list);
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
					getMainApp().getPrimaryStage(), true);
		} catch (Exception e) {
			DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
		}
		maskerPane.setVisible(false);
	}

	private Map<String, Object> getSearchParam() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("format", "json");		
		param.put("step_pipeline", AnalysisJobStatusCode.SAMPLE_JOB_STATUS_COMPLETE);
		
		/** 검색 항목 설정 Start */
		// Assay Target
		if(choosePanel.getSelectionModel().getSelectedIndex() > -1 && choosePanel.getValue() != null) {
			param.put("kit", choosePanel.getValue().getValue());
		}
		// Sample Source
		if(chooseSampleSource.getSelectionModel().getSelectedIndex() > -1 && chooseSampleSource.getValue() != null) {
			param.put("job_run_group_source", chooseSampleSource.getValue().getValue());
		}
		// Status chooseStatus
		if(chooseStatus.getSelectionModel().getSelectedIndex() > -1 && chooseStatus.getValue() != null) {
			param.put("step_report", chooseStatus.getValue().getValue());
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
			param.put("min_created_at", minCreateAtUTCDate);
		}
		// Submitted [end]
		if(!StringUtils.isEmpty(maxCreateAt)) {
			// 로컬 타임 표준시로 변환
			String maxCreateAtUTCDate = ConvertUtil.convertLocalTimeToUTC(maxCreateAt + " 23:59:59", "yyyy-MM-dd HH:mm:ss", null);
			param.put("max_created_at", maxCreateAtUTCDate);
		}
		// job
		if(!StringUtils.isEmpty(inputJob.getText())) {
			param.put("name", inputJob.getText());
		}
		// job run group
		if(this.hiddenJobRunGroupId > 0 && !StringUtils.isEmpty(inputJobRunGroup.getText())) {
			param.put("job_run_group_id", this.hiddenJobRunGroupId);
		}
		/** End 검색 항목 설정 */
		return param;
	}

	/**
	 * 샘플 목록 화면 출력
	 * 
	 * @param list
	 */
//	public void renderSampleList(List<AnalysisSample> list) {
//		if (list != null && list.size() > 0) {
//			ObservableList<AnalysisSample> listData = FXCollections.observableArrayList(list);
//
//			// 기존 화면에 출력된 데이터 제거
//			listGrid.getChildren().clear();
//
//			int idx = 0;
//			for (AnalysisSample sample : listData) {
//				String submittedDate = "-";
//				String startedDate = "-";
//				String fastQC = sample.getQc();
//				fastQC = (StringUtils.isEmpty(fastQC) && sample.getAnalysisResultSummary() != null) ? sample.getAnalysisResultSummary().getQualityControl() : fastQC;
//				fastQC = (!StringUtils.isEmpty(fastQC)) ? fastQC.toUpperCase() : "NONE";
//
//				if(sample.getJobStatus() != null) {
//					submittedDate = DateFormatUtils.format(sample.getJobStatus().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss");
//					if(sample.getJobStatus().getPipelineStartedAt() != null) {
//						startedDate = DateFormatUtils.format(sample.getJobStatus().getPipelineStartedAt().toDate(), "yyyy-MM-dd HH:mm:ss");
//					}
//				}
//
//				HBox hBox = new HBox();
//				hBox.setId("item");
//
//				// Job Column Box
//				VBox jobVBox = new VBox();
//				jobVBox.setId("jobArea");
//				jobVBox.getStyleClass().add("colunmn");
//				// sample job name
//				Label jobLabel = new Label(sample.getName());
//				jobLabel.setId("job");
//				// 요청일시 라벨
//				Label submittedLabel = new Label("Submitted : " + submittedDate);
//				submittedLabel.setId("submitted");
//				// 작업 시작일시 라벨
//				Label startedLabel = new Label("Started : " + startedDate);
//				startedLabel.setId("started");
//				jobVBox.getChildren().setAll(jobLabel, submittedLabel, startedLabel);
//
//				// 목록 Grid 영역에 추가
//				listGrid.add(jobVBox, 0, idx);
//
//				// 시퀀스 장비 column box
//				VBox groupVBox = new VBox();
//				groupVBox.setId("groupArea");
//				groupVBox.getStyleClass().add("colunmn");
//				Label refName = new Label(sample.getJobRunGroupRefName());
//				refName.setId("refName");
//				refName.getStyleClass().add("font_size_12");
//				HBox sampleSourceHBox = new HBox();
//				sampleSourceHBox.setId("sampleSource");
//				Label sampleSource = new Label(sample.getJobRunGroupSource());
//				sampleSource.getStyleClass().add(sample.getJobRunGroupSource());
//				sampleSourceHBox.getChildren().setAll(new Label("Sample : "), sampleSource);
//				groupVBox.getChildren().setAll(refName, sampleSourceHBox);
//
//				// 목록 Grid 영역에 추가
//				listGrid.add(groupVBox, 1, idx);
//
//				// Assay Target (사용패널키트) Column Box
//				VBox assayTargetPlatformVBox = new VBox();
//				assayTargetPlatformVBox.getStyleClass().add("colunmn");
//				assayTargetPlatformVBox.setId("assayTargetPlatformArea");
//				Label assayTarget = new Label(PanelKitCode.valueOf(sample.getKit()).getDescription());
//				assayTarget.setId("assayTarget");
//				assayTarget.getStyleClass().add("font_size_12");
//				Label platform = new Label(SequencerCode.valueOf(sample.getJobRunGroupSequencer()).getDescription());
//				platform.setId("platform");
//				platform.getStyleClass().add("font_size_12");
//				assayTargetPlatformVBox.getChildren().setAll(assayTarget, platform);
//
//				// 목록 Grid 영역에 추가
//				listGrid.add(assayTargetPlatformVBox, 2, idx);
//
//				// Result Overview Column Box
//				AnalysisJobResultOverview jobResultOverview = new AnalysisJobResultOverview();
//				VBox jobResultOverviewVBox = jobResultOverview.getResultOverview(sample);
//				jobResultOverviewVBox.getStyleClass().add("colunmn");
//
//				// 목록 Grid 영역에 추가
//				listGrid.add(jobResultOverviewVBox, 3, idx);
//
//				// fastqc column box
//				VBox qcVBox = new VBox();
//				qcVBox.setId("qcArea");
//				qcVBox.getStyleClass().add("colunmn");
//
//				Image img = resourceUtil.getImage("/layout/images/icon_qc_" + fastQC.toLowerCase() + ".png");
//				if (img != null) {
//					ImageView imgVw = new ImageView(img);
//					qcVBox.getChildren().setAll(imgVw);
//				}
//				if (!"NONE".equals(fastQC)) {
//					Label qcLabel = null;
//					if("WARN".equals(fastQC)) {
//						qcLabel = new Label("WARNING");
//					} else {
//						qcLabel = new Label(fastQC);
//					}
//					qcLabel.setId(fastQC.toLowerCase());
//					qcLabel.getStyleClass().add("font_size_12");
//					qcVBox.getChildren().add(qcLabel);
//				}
//
//				// 목록 Grid 영역에 추가
//				listGrid.add(qcVBox, 4, idx);
//
//				VBox detailVBox = new VBox();
//				detailVBox.setId("detailArea");
//				detailVBox.getStyleClass().add("colunmn");
//				Button btn = new Button("Detail");
//				btn.getStyleClass().add("btn_detail");
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
//				detailVBox.getChildren().add(btn);
//
//				// 목록 Grid 영역에 추가
//				listGrid.add(detailVBox, 5, idx);
//
//				idx++;
//			}
//		} else {
//			// 기존 화면에 출력된 데이터 제거
//			listGrid.getChildren().clear();
//		}
//	}
	
	/**
	 * 그룹 목록 다이얼로그창 출력
	 */
	@FXML
	public void openGroupChooseDialog() {
//		try {
//			this.hiddenJobRunGroupId = 0;
//			this.inputJobRunGroup.setText(null);
//
//			FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_JOB_RUN_GROUP_SEARCH_DIALOG);
//			Pane dialogPane = (Pane) loader.load();
//			AnalysisJobRunGroupSearchController controller = loader.getController();
//			controller.setMainApp(getMainApp());
//			controller.setExperimenterPastResultsController(this);
//			controller.show(dialogPane);
//		} catch (Exception e) {
//			logger.error("job run group search dialog open fail.." + e.getMessage());
//		}
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
		inputJob.setText(null);
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
		List<Map<String, Object>> searchedSamples = new ArrayList<Map<String, Object>>();
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
						Node root = (Node) loader.load();
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
}
