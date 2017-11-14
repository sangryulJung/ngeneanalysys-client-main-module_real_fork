package ngeneanalysys.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.model.*;
import ngeneanalysys.util.WorksheetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;

import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
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
	private ComboBox<ComboBoxItem> chooseAnalysisType;
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
	/** bedFile choose box open button */
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
	private Label sampleCountLabel;
	
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
	private List<QcResultHBox> qcResultHBoxes = new ArrayList<>();
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
		
		chooseAnalysisType.setConverter(new ComboBoxConverter());
		chooseAnalysisType.getItems().add(new ComboBoxItem());
		chooseAnalysisType.getItems().add(new ComboBoxItem(ExperimentTypeCode.GERMLINE.getDescription(), ExperimentTypeCode.GERMLINE.getDescription()));
		chooseAnalysisType.getItems().add(new ComboBoxItem(ExperimentTypeCode.SOMATIC.getDescription(), ExperimentTypeCode.SOMATIC.getDescription()));
		chooseAnalysisType.getSelectionModel().selectFirst();
		
		logger.info("chooseExperimenter init..");
		
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
					list = searchedSamples.getResult().stream().sorted((a, b) -> Integer.compare(b.getId(), a.getId())).collect(Collectors.toList());
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
				sampleCountLabel.setText(String.valueOf(totalCount));
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
		Map<String, Object> param = new HashMap<>();
		param.put("format", "json");		
		param.put("step", AnalysisJobStatusCode.JOB_RUN_GROUP_PIPELINE);
		param.put("status", AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE);
		
		/** 검색 항목 설정 Start */
		// Assay Target
		if(choosePanel.getSelectionModel().getSelectedIndex() > 0 && choosePanel.getValue() != null) {
			param.put("panelIds", choosePanel.getValue().getValue());
		}
		// Sample Source
		if(chooseSampleSource.getSelectionModel().getSelectedIndex() > 0 && chooseSampleSource.getValue() != null) {
			param.put("sampleSource", chooseSampleSource.getValue().getValue());
		}
		// Status chooseAnalysisType
		if(chooseAnalysisType.getSelectionModel().getSelectedIndex() > 0 && chooseAnalysisType.getValue() != null) {
			param.put("analysisType", chooseAnalysisType.getValue().getValue());
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
			param.put("runId", this.hiddenJobRunGroupId);
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

			QcResultHBox qcResultHBox = new QcResultHBox();
			qcResultHBox.setVisible(false);
			qcResultHBoxes.add(qcResultHBox);
			listGrid.add(qcResultHBox, 4, i);

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
				qcResultHBoxes.get(idx).setSampleView(sample);
				detailFieldVBoxes.get(idx).setSampleView(sample);
			}
			for(int idx = list.size(); idx < itemCountPerPage; idx++) {
				sampleNameFields.get(idx).setVisible(false);
				runFields.get(idx).setVisible(false);
				assayTargetFieldVBoxes.get(idx).setVisible(false);
				analysisResultOverviewVBoxes.get(idx).setVisible(false);
				qcResultHBoxes.get(idx).setVisible(false);
				detailFieldVBoxes.get(idx).setVisible(false);
			}
		} else {
			for(int idx = 0; idx < itemCountPerPage; idx++) {
				sampleNameFields.get(idx).setVisible(false);
				runFields.get(idx).setVisible(false);
				assayTargetFieldVBoxes.get(idx).setVisible(false);
				analysisResultOverviewVBoxes.get(idx).setVisible(false);
				qcResultHBoxes.get(idx).setVisible(false);
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
	 * @param runId
	 * @param runName
	 */
	public void setSearchJobRunGroupInfo(int runId,String runName) {
		this.hiddenJobRunGroupId = runId;
		this.inputJobRunGroup.setText(runName);
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
		chooseAnalysisType.setValue(new ComboBoxItem());
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
		Map<String, Object> params = getSearchParam();
		WorksheetUtil worksheetUtil = new WorksheetUtil();
		worksheetUtil.exportVariantData("EXCEL", params, this.getMainApp());
	}
	
	/**
	 * CSV 파일로 저장
	 */
	@FXML
	public void saveCSV(ActionEvent event) {
		Map<String, Object> params = getSearchParam();
		WorksheetUtil worksheetUtil = new WorksheetUtil();
		worksheetUtil.exportVariantData("CSV", params, this.getMainApp());
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

		private Button totalBaseButton;
		private Label totalBaseTitleLabel = new Label();
		private Label totalBaseContentsLabel = new Label();
		private Label totalBaseValueLabel = new Label();

		private Button q30TrimmedBaseButton;
		private Label q30TrimmedBaseTitleLabel = new Label();
		private Label q30TrimmedBaseContentsLabel = new Label();
		private Label q30TrimmedBaseValueLabel = new Label();

		private Button mappedBaseButton;
		private Label mappedBaseTitleLabel = new Label();
		private Label mappedBaseContentsLabel = new Label();
		private Label mappedBaseValueLabel = new Label();

		private Button ontTargetButton;
		private Label onTargetTitleLabel = new Label();
		private Label onTargetContentsLabel = new Label();
		private Label onTargetValueLabel = new Label();

		private Button onTargetCoverageButton;
		private Label onTargetCoverageTitleLabel = new Label();
		private Label onTargetCoverageContentsLabel = new Label();
		private Label onTargetCoverageValueLabel = new Label();

		private Button duplicatedReadsButton;
		private Label duplicatedReadsTitleLabel = new Label();
		private Label duplicatedReadsContentsLabel = new Label();
		private Label duplicatedReadsValueLabel = new Label();

		private Button roiCoverageButton;
		private Label roiCoverageTitleLabel = new Label();
		private Label roiCoverageContentsLabel = new Label();
		private Label roiCoverageValueLabel = new Label();

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

			totalBaseButton = getQCIcon(totalBaseTitleLabel, totalBaseContentsLabel,
					totalBaseValueLabel);

			q30TrimmedBaseButton = getQCIcon(q30TrimmedBaseTitleLabel, q30TrimmedBaseContentsLabel,
					q30TrimmedBaseValueLabel);

			mappedBaseButton = getQCIcon(mappedBaseTitleLabel, mappedBaseContentsLabel,
					mappedBaseValueLabel);

			ontTargetButton = getQCIcon(onTargetTitleLabel, onTargetContentsLabel,
					onTargetValueLabel);

			onTargetCoverageButton = getQCIcon(onTargetCoverageTitleLabel, onTargetCoverageContentsLabel,
					onTargetCoverageValueLabel);

			duplicatedReadsButton = getQCIcon(duplicatedReadsTitleLabel, duplicatedReadsContentsLabel,
					duplicatedReadsValueLabel);

			roiCoverageButton = getQCIcon(roiCoverageTitleLabel, roiCoverageContentsLabel,
					roiCoverageValueLabel);

			qcFlagHbox.getChildren().addAll(totalBaseButton, q30TrimmedBaseButton, mappedBaseButton,
					ontTargetButton, onTargetCoverageButton, duplicatedReadsButton, roiCoverageButton);
			qcFlagHbox.setMargin(totalBaseButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(q30TrimmedBaseButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(mappedBaseButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(ontTargetButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(onTargetCoverageButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(duplicatedReadsButton, new Insets(0, 0, 0, 5));
			qcFlagHbox.setMargin(roiCoverageButton, new Insets(0, 0, 0, 5));

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
		private Button getQCIcon(Label title, Label contents, Label percentageLabel) {
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
				title.getStyleClass().add("font_size_12");
				title.getStyleClass().add("weight_bold");
				title.getStyleClass().add("txt_gray_656565");
				percentageLabel.getStyleClass().add("font_size_12");
				percentageLabel.getStyleClass().add("weight_bold");
				percentageLabel.getStyleClass().add("txt_black");;
				contents.getStyleClass().add("font_size_11");
				contents.getStyleClass().add("txt_gray_656565");
				box.getChildren().addAll(title, percentageLabel, contents);
				box.setMargin(percentageLabel, new Insets(5, 0, 0, 0));
				box.setMargin(contents, new Insets(5, 0, 0, 0));

				popOver.setContentNode(box);
				popOver.show(qcButton);
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

				List<SampleQC> qcList = null;

				try {
					HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/"+ sample.getId(), null,
							null, false);

					qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

					SampleQC totalBase = findQCResult(qcList, "total_base");
					if(totalBase != null) {
						totalBaseTitleLabel.setText(totalBase.getQcType());
						totalBaseContentsLabel.setText(totalBase.getQcDescription() + " " + totalBase.getQcThreshold());
						totalBaseValueLabel.setText(totalBase.getQcValue().toString() + totalBase.getQcUnit());
					}

					SampleQC q30TrimmedBase = findQCResult(qcList, "q30_trimmed_base");
					if(q30TrimmedBase != null) {
						q30TrimmedBaseTitleLabel.setText(q30TrimmedBase.getQcType());
						q30TrimmedBaseContentsLabel.setText(q30TrimmedBase.getQcDescription() + " " + q30TrimmedBase.getQcThreshold());
						q30TrimmedBaseValueLabel.setText(q30TrimmedBase.getQcValue().toString() + q30TrimmedBase.getQcUnit());
					}

					SampleQC mappedBase = findQCResult(qcList, "mapped_base");
					if(mappedBase != null) {
						mappedBaseTitleLabel.setText(mappedBase.getQcType());
						mappedBaseContentsLabel.setText(mappedBase.getQcDescription() + " " + mappedBase.getQcThreshold());
						mappedBaseValueLabel.setText(mappedBase.getQcValue().toString() + mappedBase.getQcUnit());
					}

					SampleQC onTarget = findQCResult(qcList, "on_target");
					if(onTarget != null) {
						onTargetTitleLabel.setText(onTarget.getQcType());
						onTargetContentsLabel.setText(onTarget.getQcDescription() + " " + onTarget.getQcThreshold());
						onTargetValueLabel.setText(onTarget.getQcValue().toString() + onTarget.getQcUnit());
					}

					SampleQC onTargetCoverage = findQCResult(qcList, "on_target_coverage");
					if(onTargetCoverage != null) {
						onTargetCoverageTitleLabel.setText(onTargetCoverage.getQcType());
						onTargetCoverageContentsLabel.setText(onTargetCoverage.getQcDescription() + " " + onTargetCoverage.getQcThreshold());
						onTargetCoverageValueLabel.setText(onTargetCoverage.getQcValue().toString() + onTargetCoverage.getQcUnit());
					}

					SampleQC duplicatedReads = findQCResult(qcList, "duplicated_reads");
					if(duplicatedReads != null) {
						duplicatedReadsTitleLabel.setText(duplicatedReads.getQcType());
						duplicatedReadsContentsLabel.setText(duplicatedReads.getQcDescription() + " " + duplicatedReads.getQcThreshold());
						duplicatedReadsValueLabel.setText(duplicatedReads.getQcValue().toString() + duplicatedReads.getQcUnit());
					}

					SampleQC roiCoverage = findQCResult(qcList, "roi_coverage");
					if(roiCoverage != null) {
						roiCoverageTitleLabel.setText(roiCoverage.getQcType());
						roiCoverageContentsLabel.setText(roiCoverage.getQcDescription() + " " + roiCoverage.getQcThreshold());
						roiCoverageValueLabel.setText(roiCoverage.getQcValue().toString() + roiCoverage.getQcUnit());
					}

				} catch(WebAPIException e) {
					e.printStackTrace();
				}

				/*roiCoverageValueLabel.setText(analysisResultSummary.getRoiCoveragePercentage().toString());
				totalBaseValueLabel.setText(analysisResultSummary.getMeanReadQualityPercentage().toString());
				mappedBaseValueLabel.setText(analysisResultSummary.getCoverageUniformityPercentage().toString());*/
			}
			setVisible(true);
		}

		private SampleQC findQCResult(List<SampleQC> qcList, String qc) {
			if(qcList != null && !qcList.isEmpty()) {
				Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
				if(findQC.isPresent()) {
					return findQC.get();
				}
			}
			return null;
		}
	}
	class QcResultHBox extends HBox {
		QcResultHBox() {
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
				imgVw.setFitHeight(15.0);
				imgVw.setFitWidth(15.0);
				HBox.setMargin(imgVw, new Insets(0, 10.0, 0, 0));
				this.getChildren().setAll(imgVw);
			}
			if (!"NONE".equals(fastQC)) {
				Label qcLabel = new Label(fastQC);
				qcLabel.setId(fastQC.toLowerCase());
				qcLabel.setPrefWidth(80.0);
				qcLabel.getStyleClass().add("font_size_9");
				this.getChildren().add(qcLabel);
			}
		}
	}
	class DetailFieldVBox extends VBox {
		SampleView sample = null;
		DetailFieldVBox() {
			this.setId("detailArea");
			this.getStyleClass().add("column");
			Button btn = new Button("Open");
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
