package ngeneanalysys.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedSampleView;
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

	@FXML
	private Pagination paginationList;

	@FXML
	private Button searchBtn;

	@FXML
	private VBox resultVBox;

	/** API Service */
	private APIService apiService;
	
	/** Timer */
	public Timeline autoRefreshTimeline;
	private int itemCountPerPage;

	List<RunStatusGirdPane> runStatusGirdPanes;


	/**
	 * 화면 출력
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void show(Parent root) throws IOException {
		logger.info("ExperimenterPastResultsController show..");
		itemCountPerPage = 5;
		// api service init..
		apiService = APIService.getInstance();
		apiService.setStage(getMainController().getPrimaryStage());

		// masker pane init
		experimentPastResultsWrapper.getChildren().add(maskerPane);
		maskerPane.setPrefWidth(getMainController().getMainFrame().getWidth());
		maskerPane.setPrefHeight(getMainController().getMainFrame().getHeight());
		maskerPane.setVisible(false);

		logger.info("choosePanel init..");
		initRunListLayout();

		initSampleListLayout();
		// 페이지 이동 이벤트 바인딩
		paginationList.setPageFactory(pageIndex -> {
				setList(pageIndex + 1);
				return new VBox();
		});

		// 시스템 설정에서 자동 새로고침 설정이 true 인경우 자동 새로고팀 실행
		//startAutoRefresh();
		
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

		/** End 검색 항목 설정 */
		return param;
	}

	private void setVBoxPrefSize(Node node) {
		if(node instanceof GridPane) resultVBox.setPrefHeight(resultVBox.getPrefHeight() + ((GridPane)node).getPrefHeight());
	}

	private void initRunListLayout() {
		try {
			runStatusGirdPanes = new ArrayList<>();
			for (int i = 0; i < itemCountPerPage; i++) {
				RunStatusGirdPane gridPane = new RunStatusGirdPane();
				runStatusGirdPanes.add(gridPane);
				resultVBox.getChildren().add(gridPane);
				Run run = new Run();
				run.setName("hhh");
				run.setSequencingPlatform("zzz");
				gridPane.setLabel(run);
				setVBoxPrefSize(gridPane);
			}
			logger.info("hhh");
		} catch (Exception e) {
			logger.error("HOME -> initRunListLayout", e);
		}
	}

	public void initSampleListLayout() {

	}
	/**
	 * 샘플 목록 화면 출력
	 * 
	 * @param list
	 */
	public void renderSampleList(List<SampleView> list) {
		if (list != null && !list.isEmpty()) {
			SampleInfoVBox vbox = new SampleInfoVBox();
			vbox.setSampleList(list);
			resultVBox.getChildren().add(vbox);
		}
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

	}

	class SampleInfoVBox extends VBox {
		private List<Label> sampleNames;
		private List<Label> sampleStatus;
		private List<Label> samplePanels;
		private List<Label> sampleVariants;
		private List<Label> sampleQCs;

		public SampleInfoVBox() {
			this.setPrefWidth(810);
			this.setMaxWidth(Double.MAX_VALUE);
			HBox titleBox = new HBox();
			String styleClass = "sample_header";
			Label name = new Label("NAME");
			labelSize(name, 150., styleClass);
			Label status = new Label("STATUS");
			labelSize(status, 90., styleClass);
			Label panel = new Label("PANEL");
			labelSize(panel, 150., styleClass);
			Label variants = new Label("VARIANTS");
			labelSize(variants, 320., styleClass);
			Label qc = new Label("QC");
			labelSize(qc, 100., styleClass);

			titleBox.getChildren().add(name);
			titleBox.getChildren().add(status);
			titleBox.getChildren().add(panel);
			titleBox.getChildren().add(variants);
			titleBox.getChildren().add(qc);

			this.getChildren().add(titleBox);

		}

		public void labelSize(Label label, Double size, String style) {
			label.setPrefWidth(size);
			label.setPrefHeight(40);
			label.setAlignment(Pos.CENTER);
			label.getStyleClass().add("sample_header");
		}

		public void setSampleList(List<SampleView> sampleList) {
			for(SampleView sampleView : sampleList) {
				HBox itemHBox = new HBox();

				String styleClass = "sample_list_label";
				Label name = new Label(sampleView.getName());
				labelSize(name, 150., styleClass);
				Label status = new Label(sampleView.getSampleStatus().getStatus());
				labelSize(status, 90., styleClass);
				Label panel = new Label(sampleView.getPanelName());
				labelSize(panel, 150., styleClass);
				Label variants = new Label();
				labelSize(variants, 320., styleClass);
				Label qc = new Label(sampleView.getQcResult());
				labelSize(qc, 100., styleClass);

				itemHBox.getChildren().add(name);
				itemHBox.getChildren().add(status);
				itemHBox.getChildren().add(panel);
				itemHBox.getChildren().add(variants);
				itemHBox.getChildren().add(qc);

				this.getChildren().add(itemHBox);
			}
		}
	}

	class RunStatusGirdPane extends GridPane {
		private Label runLabel;
		private Label sequencerLabel;
		private Label submitDateLabel;
		private Label finishDateLabel;
		private Label userLabel;


		public RunStatusGirdPane() {
			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPrefWidth(170);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPrefWidth(210);
			ColumnConstraints col3 = new ColumnConstraints();
			col3.setPrefWidth(130);
			ColumnConstraints col4 = new ColumnConstraints();
			col4.setPrefWidth(130);
			ColumnConstraints col5 = new ColumnConstraints();
			col5.setPrefWidth(170);
			this.getColumnConstraints().addAll(col1, col2, col3, col4, col5);

			runLabel = new Label();
			sequencerLabel = new Label();
			submitDateLabel = new Label();
			finishDateLabel = new Label();
			userLabel = new Label();

			this.add(runLabel, 0, 0);

			this.add(sequencerLabel, 1, 0);
			setLabelStyle(sequencerLabel);
			this.add(submitDateLabel, 2, 0);
			setLabelStyle(submitDateLabel);
			this.add(finishDateLabel, 3, 0);
			setLabelStyle(finishDateLabel);
			this.add(userLabel, 4, 0);
			setLabelStyle(userLabel);

			this.setPrefHeight(35);
			setLabelStyle(runLabel);

		}

		public void setLabelStyle(Label label) {
			label.setMaxWidth(Double.MAX_VALUE);
			label.setMaxHeight(Double.MAX_VALUE);
			label.setPrefHeight(35);
			label.setAlignment(Pos.CENTER);
			label.setStyle("-fx-font-family: Noto Sans CJK KR Regular;");
		}

		public void setLabel(Run run) {
			runLabel.setText(run.getName());
			sequencerLabel.setText(run.getSequencingPlatform());
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			if (run.getCreatedAt() != null)
				submitDateLabel.setText(format.format(run.getCreatedAt().toDate()));
			if (run.getCompletedAt() != null)
			finishDateLabel.setText(format.format(run.getCompletedAt().toDate()));
			userLabel.setText(run.getMemberName());
		}

	}

}
