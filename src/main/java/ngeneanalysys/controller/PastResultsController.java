package ngeneanalysys.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedRunSampleView;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.util.*;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
	private VBox resultVBox;

	@FXML
	private ComboBox<ComboBoxItem> searchComboBox;

	@FXML
	private VBox searchListVBox;

	@FXML
	private VBox filterSearchArea;

	@FXML
	private ScrollPane mainContentsScrollPane;

	@FXML
	private Label totalCountLabel;
	@FXML
	private Label queueLabel;
	@FXML
	private Label runningLabel;
	@FXML
	private Label completeLabel;
	@FXML
	private Label failLabel;

	/** API Service */
	private APIService apiService;
	
	/** Timer */
	public Timeline autoRefreshTimeline;
	private int itemCountPerPage;

	private List<RunStatusGirdPane> runStatusGirdPanes;

	private boolean oneItem = false;

	private Map<String, String> searchOption = new HashMap<>();

	SuggestionProvider<String> provider = null;

	public void setSearchOption() {
		searchOption.put("SAMPLE","sampleName");
		searchOption.put("RUN","runName");
		searchOption.put("PANEL","panelName");
		searchOption.put("DATE","createdAt");
	}

	/**
	 * 화면 출력
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void show(Parent root) throws IOException {
		setSearchOption();
		logger.debug("PastResultsController show..");
		itemCountPerPage = 5;
		// api service init..
		apiService = APIService.getInstance();
		apiService.setStage(getMainController().getPrimaryStage());

		runStatusGirdPanes = new ArrayList<>();

		// masker pane init
		experimentPastResultsWrapper.add(maskerPane, 0, 0, 6, 6);
		maskerPane.setPrefWidth(getMainController().getMainFrame().getWidth());
		maskerPane.setPrefHeight(getMainController().getMainFrame().getHeight());
		maskerPane.setVisible(false);

		// 페이지 이동 이벤트 바인딩
		paginationList.setPageFactory(pageIndex -> {
			mainContentsScrollPane.setVvalue(0);
			setList(pageIndex + 1);
			return new VBox();
		});

		// 시스템 설정에서 자동 새로고침 설정이 true 인경우 자동 새로고팀 실행
		startAutoRefresh();
		setComboBoxItem();
		this.mainController.getMainFrame().setCenter(root);

		searchComboBox.valueProperty().addListener((ov, oldV, newV) -> {
			if(newV == null) return;
			filterSearchArea.getChildren().removeAll(filterSearchArea.getChildren());
			if(newV.getValue().equalsIgnoreCase("String")) {
				CustomTextField textField = new CustomTextField();
				/*ImageView imageView = new ImageView(resourceUtil.getImage("/layout/images/renewal/search_icon.png"));
				imageView.setStyle("-fx-cursor:hand;");
				imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if(StringUtils.isEmpty(textField.getText())) {
						oneItem = false;
					} else {
						oneItem = true;
					}
					search();
				});
				textField.setRight(imageView);*/
				if(provider != null) provider = null;

				if (searchComboBox.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase("PANEL")) {
					List<Panel> panels = (List<Panel>) getMainController().getBasicInformationMap().get("panels");
					List<Panel> filterPanel = null;
					if (StringUtils.isEmpty(textField.getText())) {
						filterPanel = panels.stream().filter(panel -> panel.getName().contains(textField.getText())).collect(Collectors.toList());
					} else {
						filterPanel = panels;
					}
					TextFields.bindAutoCompletion(textField, getAllPanel(filterPanel)).setVisibleRowCount(10);
				} else if (searchComboBox.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase("RUN") ||
						searchComboBox.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase("SAMPLE")) {
					provider = SuggestionProvider.create(new HashSet<>());
					TextFields.bindAutoCompletion(textField, provider).setVisibleRowCount(10);
					textField.textProperty().addListener((ob, oValue, nValue) -> updateAutoCompletion(nValue, textField));

				}

				textField.setPrefWidth(Double.MAX_VALUE);
				filterSearchArea.getChildren().add(textField);
			} else if(newV.getValue().equalsIgnoreCase("DATE")) {
				final DatePicker startDate = new DatePicker();
				startDate.setPrefWidth(240);
				final DatePicker endDate = new DatePicker();
				endDate.setPrefWidth(240);

				String dateFormat = "yyyy-MM-dd";

				startDate.setConverter(DatepickerConverter.getConverter(dateFormat));
				startDate.setPromptText(dateFormat);
				startDate.valueProperty().addListener(element -> compareDate(startDate, endDate, true
				, "선택한 날짜가 검색의 마지막 날짜 이후의 날짜입니다.", "Date is later than the last day of the selected date search."));

				endDate.setConverter(DatepickerConverter.getConverter(dateFormat));
				endDate.setPromptText(dateFormat);
				endDate.valueProperty().addListener(element -> compareDate(startDate, endDate, false
						, "선택한 날짜가 검색의 시작 날짜 이전의 날짜입니다.", "The selected date is the date before the search of the start date."));

				filterSearchArea.getChildren().addAll(startDate, endDate);
			}
		});

		searchComboBox.getSelectionModel().select(0);
	}

	private void compareDate(DatePicker leftDate, DatePicker rightDate, boolean firstDateReset , String titleText, String contentText) {
		if(leftDate.getValue() != null && rightDate.getValue() != null) {
			int leftDateInt = Integer.parseInt(leftDate.getValue().toString().replace("-", ""));
			int rightDateInt = Integer.parseInt(rightDate.getValue().toString().replace("-", ""));
			if(leftDateInt > rightDateInt) {
				DialogUtil.warning(titleText, contentText, getMainApp().getPrimaryStage(), true);
				if(firstDateReset) {
					leftDate.setValue(null);
				} else {
					rightDate.setValue(null);
				}
			}
		}
	}

	private void updateAutoCompletion(final String value, final CustomTextField textField) {
		if(!StringUtils.isEmpty(value)) {
			try {
				Map<String, Object> params = new HashMap<>();
				params.put("target", searchComboBox.getSelectionModel().getSelectedItem().getText().toLowerCase());
				params.put("keyword", textField.getText());
				params.put("resultCount", 15);
				HttpClientResponse response = apiService.get("/filter", params, null, false);
				logger.debug(response.getContentString());
				JSONParser jsonParser = new JSONParser();
				JSONArray jsonArray = (JSONArray) jsonParser.parse(response.getContentString());
				provider.clearSuggestions();
				provider.addPossibleSuggestions(getAllData(jsonArray));
			} catch (WebAPIException wae) {
				wae.printStackTrace();
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Set<String> getAllData(JSONArray array) {
		Set<String> data = new HashSet<>();
		array.forEach(item -> data.add(item.toString()));
		return data;
	}

	private Set<String> getAllPanel(List<Panel> panelList) {
		Set<String> panels = new HashSet<>();
		panelList.forEach(panel -> panels.add(panel.getName()));
		return panels;
	}
	
	/**
	 * 자동 새로고침 시작 처리
	 */
	public void startAutoRefresh() {
		boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
		logger.debug(String.format("auto refresh on : %s", isAutoRefreshOn));
		
		if(isAutoRefreshOn) {
			// 갱신 시간 간격
			int refreshPeriodSecond = Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000;
			logger.debug(String.format("auto refresh period second : %s millisecond", refreshPeriodSecond));

			if(autoRefreshTimeline == null) {
				autoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(refreshPeriodSecond),
						ae -> setList(paginationList.getCurrentPageIndex() + 1)));
				autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
			} else {
				logger.debug(String.format("[%s] timeline restart", this.getClass().getName()));
				autoRefreshTimeline.stop();
				autoRefreshTimeline.getKeyFrames().removeAll(autoRefreshTimeline.getKeyFrames());
				autoRefreshTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(refreshPeriodSecond),
						ae -> setList(paginationList.getCurrentPageIndex() + 1)));
			}

			autoRefreshTimeline.play();
		} else {
			if(autoRefreshTimeline != null) {
				autoRefreshTimeline.stop();
			}
		}
	}

	private void setComboBoxItem() {
		searchComboBox.setConverter(new ComboBoxConverter());
		searchComboBox.getItems().add(new ComboBoxItem("String", "RUN"));
		searchComboBox.getItems().add(new ComboBoxItem("String", "SAMPLE"));
		searchComboBox.getItems().add(new ComboBoxItem("String", "PANEL"));
		searchComboBox.getItems().add(new ComboBoxItem("Date", "DATE"));
	}
	
	/**
	 * 자동 새로고침 일시정지
	 */
	public void pauseAutoRefresh() {
		boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
		// 기능 실행중인 상태인 경우 실행
		if(autoRefreshTimeline != null && isAutoRefreshOn) {
			logger.debug(String.format("[%s] timeline status : %s", this.getClass().getName(),
					autoRefreshTimeline.getStatus()));
			// 일시정지
			if(autoRefreshTimeline.getStatus() == Animation.Status.RUNNING) {
				autoRefreshTimeline.pause();
				logger.debug(String.format("[%s] auto refresh pause", this.getClass().getName()));
			}
		}
	}
	
	/**
	 * 자동 새로고침 시작
	 */
	public void resumeAutoRefresh() {
		boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
        int refreshPeriodSecond = (Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000) - 1;
		// 기능 실행중인 상태인 경우 실행
		if(autoRefreshTimeline != null && isAutoRefreshOn) {
			logger.debug(String.format("[%s] timeline status : %s", this.getClass().getName(),
					autoRefreshTimeline.getStatus()));
			// 시작
			if(autoRefreshTimeline.getStatus() == Animation.Status.PAUSED) {
			    autoRefreshTimeline.playFrom(Duration.millis(refreshPeriodSecond));
				autoRefreshTimeline.play();
				logger.debug(String.format("[%s] auto refresh resume", this.getClass().getName()));
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
	 * @param page int
	 */
	public void setList(int page) {
		if(autoRefreshTimeline != null) {
			logger.debug(String.format("auto refresh timeline status : %s", autoRefreshTimeline.getStatus()));
		}
		maskerPane.setVisible(true);
		
		int totalCount = 0;
		// 조회 시작 index
		int offset = (page - 1) * itemCountPerPage;

		Map<String, Object> param = getSearchParam();
		Map<String, List<Object>> subParams = getSubSearchParam();
		param.put("limit", itemCountPerPage);
		param.put("offset", offset);
		
		try {
			HttpClientResponse response = apiService.get("/searchSamples", param, null, subParams);

			if (response != null) {
				PagedRunSampleView searchedSamples = response
						.getObjectBeforeConvertResponseToJSON(PagedRunSampleView.class);

				/*totalCountLabel.setText(searchedSamples.getSampleAnalysisJobCount().getRunCount().toString());
				queueLabel.setText(searchedSamples.getSampleAnalysisJobCount().getQueuedSampleCount().toString());
				runningLabel.setText(searchedSamples.getSampleAnalysisJobCount().getRunningSampleCount().toString());
				completeLabel.setText(searchedSamples.getSampleAnalysisJobCount().getCompletedSampleCount().toString());
				failLabel.setText(searchedSamples.getSampleAnalysisJobCount().getFailedSampleCount().toString());*/

				List<RunSampleView> list = null;
				if (searchedSamples != null) {
					totalCount = searchedSamples.getCount();
					list = searchedSamples.getResult().stream().sorted((a, b) -> Integer.compare(b.getRun().getId(), a.getRun().getId())).collect(Collectors.toList());
				}
				int pageCount = 0;
				if (totalCount > 0) {
					paginationList.setCurrentPageIndex(page - 1);
					pageCount = totalCount / itemCountPerPage;
					if (totalCount % itemCountPerPage > 0) {
						pageCount++;
					}
				}
				logger.debug(String.format("total count : %s, page count : %s", totalCount, pageCount));

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
			logger.error("Unknown Error", e);
			DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(),
					false);
			e.printStackTrace();
		}
		maskerPane.setVisible(false);
	}

	private Map<String, Object> getSearchParam() {
		Map<String, Object> param = new HashMap<>();
		param.put("format", "json");
		/*param.put("step", AnalysisJobStatusCode.JOB_RUN_GROUP_PIPELINE);
		param.put("status", AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE);*/

		if(oneItem) {
			final CustomTextField textField = (CustomTextField)filterSearchArea.getChildren().get(0);
			param.put("search", searchOption.get(searchComboBox.getSelectionModel().getSelectedItem().getText()) + " " + textField.getText());
		}

		/** End 검색 항목 설정 */
		return param;
	}

	private Map<String, List<Object>> getSubSearchParam() {
		Map<String, List<Object>> param = new HashMap<>();
		List<Object> sort = new ArrayList<>();
		sort.add("runId DESC");
		sort.add("sampleId ASC");
		param.put("sort", sort);
		if(!oneItem && !searchListVBox.getChildren().isEmpty()){
			List<Object> value = new ArrayList<>();
			for(Node node : searchListVBox.getChildren()) {
				VBox vbox = (VBox) node;
				Label label = (Label) vbox.getChildren().get(0);
				FlowPane flowPane = (FlowPane) vbox.getChildren().get(1);
				if (searchOption.containsKey(label.getText())) {
					for (Node item : flowPane.getChildren()) {
						value.add(searchOption.get(label.getText()) + " " + item.getId());
					}
				}
			}
			param.put("search", value);
		}

		/* End 검색 항목 설정 */
		return param;
	}

	private void setVBoxPrefSize(Node node) {
		if(node instanceof GridPane) {
			resultVBox.setPrefHeight(resultVBox.getPrefHeight() + ((GridPane)node).getPrefHeight());
		} else if(node instanceof VBox) {
			resultVBox.setPrefHeight(resultVBox.getPrefHeight() + ((VBox)node).getPrefHeight());
		}
	}

	/**
	 * 샘플 목록 화면 출력
	 * 
	 * @param list List<RunSampleView>
	 */
	public void renderSampleList(List<RunSampleView> list) {

		resultVBox.getChildren().removeAll(resultVBox.getChildren());
		resultVBox.setPrefHeight(0);
		if (list != null && !list.isEmpty()) {
			for(RunSampleView runSampleView : list) {
				RunStatusGirdPane gridPane = new RunStatusGirdPane();
				runStatusGirdPanes.add(gridPane);
				resultVBox.getChildren().add(gridPane);
				gridPane.setLabel(runSampleView);
				setVBoxPrefSize(gridPane);

				SampleInfoVBox vBox = new SampleInfoVBox();
				vBox.setSampleList(runSampleView.getSampleViews());
				resultVBox.getChildren().add(vBox);
				setVBoxPrefSize(vBox);
				if(list.indexOf(runSampleView) < list.size() - 1) {
                    Insets insets = new Insets(0, 0, 30, 0);
                    VBox.setMargin(vBox, insets);
                    resultVBox.setPrefHeight(resultVBox.getPrefHeight() + 30);
                }
			}
		}
	}

	/**
	 * 검색
	 */
	@FXML
	public void search() {
		oneItem = false;
		addSearchArea();
		setList(1);
	}

	private VBox createFilterVBox() {
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER_LEFT);
		box.setPrefWidth(240);
		box.setId(searchComboBox.getSelectionModel().getSelectedItem().getText());
		Label title = new Label(searchComboBox.getSelectionModel().getSelectedItem().getText());
		title.getStyleClass().add("font_size_13");
		box.setSpacing(10);
		box.getChildren().add(title);

		return box;
	}

	private HBox createItemHBox(String id) {
		HBox hBox = new HBox();
		hBox.setStyle(hBox.getStyle() + "-fx-background-color : #273e5e; -fx-background-radius : 20; -fx-padding : 2 5 2 5");
		hBox.setAlignment(Pos.CENTER);
		hBox.setId(id);

		return hBox;
	}

	private Label createRemoveBtn(final FlowPane flowPane, final HBox hBox, final VBox box) {
		Label xLabel = new Label("X");
		xLabel.setCursor(Cursor.HAND);
		xLabel.setOnMouseClicked(ev -> {
			if(flowPane.getChildren().size() == 1) {
				searchListVBox.getChildren().remove(box);
			} else {
				flowPane.getChildren().remove(hBox);
			}
			setList(1);
		});
		xLabel.getStyleClass().add("remove_btn");

		return xLabel;
	}

	private void addDateLabel(final String startDate, final String endDate, final HBox hBox) {
		Label startLabel = new Label(startDate);
		startLabel.setStyle(startLabel.getStyle() + "-fx-text-fill : #FFFFFF;");
		Label label = new Label(" ~ ");
		label.setStyle(label.getStyle() + "-fx-text-fill : #FFFFFF;");
		Label endLabel = new Label(endDate);
		endLabel.setStyle(endLabel.getStyle() + "-fx-text-fill : #FFFFFF;");
		if(startDate != null && endDate != null) {
			hBox.getChildren().addAll(startLabel, label, endLabel);
		} else if(startDate != null) {
			hBox.getChildren().addAll(startLabel, label);
		} else if(endDate != null) {
			hBox.getChildren().addAll(label, endLabel);
		}
	}

	private void addItemSearchArea(HBox hBox, VBox box, FlowPane flowPane) {
		hBox.setSpacing(5);
		flowPane.getChildren().add(hBox);
		if(!box.getChildren().contains(flowPane)) {
			box.getChildren().add(flowPane);
		}
		FlowPane.setMargin(hBox, new Insets(0, 10, 0, 0));
		if(!searchListVBox.getChildren().contains(box)) {
			searchListVBox.getChildren().add(box);
		}
	}

	private void addSearchItem(VBox box, FlowPane flowPane, final CustomTextField textField) {
		if(box == null) box = createFilterVBox();
		if(flowPane == null) flowPane = new FlowPane();
		flowPane.setVgap(10);
		HBox hBox = createItemHBox(textField.getText());
		Label label = new Label(textField.getText());
		label.setStyle(label.getStyle() + "-fx-text-fill : #FFFFFF;");
		Label xLabel = createRemoveBtn(flowPane, hBox, box);
		hBox.getChildren().addAll(label, xLabel);
		addItemSearchArea(hBox, box, flowPane);
	}

	private void addSearchItem(VBox box, FlowPane flowPane, final String startDate, final String endDate, final String id) {
		if(box == null) box = createFilterVBox();
		if(flowPane == null) flowPane = new FlowPane();
		flowPane.setVgap(10);
		HBox hBox = createItemHBox(id);
		Label xLabel = createRemoveBtn(flowPane, hBox, box);
		addDateLabel(startDate, endDate, hBox);
		hBox.getChildren().add(xLabel);
		addItemSearchArea(hBox, box, flowPane);
	}

	private Node searchNode() {
		Node containNode = null;
		for(Node node : searchListVBox.getChildren()) {
			if(node.getId().equalsIgnoreCase(searchComboBox.getSelectionModel().getSelectedItem().getText())) {
				containNode = node;
				break;
			}
		}
		return containNode;
	}

	private boolean containCheck(FlowPane child, String id) {
		boolean isContain = false;
		for(Node node : child.getChildren()) {
			if(node.getId().equalsIgnoreCase(id)) {
				isContain = true;
				break;
			}
		}
		return isContain;
	}

	private void addSearchArea() {
		if(searchComboBox.getSelectionModel().getSelectedItem() != null && !oneItem) {
			if(filterSearchArea.getChildren().get(0) instanceof CustomTextField) {
				final CustomTextField textField = (CustomTextField)filterSearchArea.getChildren().get(0);
				if(!StringUtils.isEmpty(textField.getText())) {
					if(searchListVBox.getChildren().isEmpty()) {
						addSearchItem(null, null, textField);
					} else {
						Node containNode = searchNode();
						if(containNode != null) {
							VBox box = (VBox) containNode;
							FlowPane child = (FlowPane) box.getChildren().get(1);
							String text = textField.getText();
							boolean isContain = containCheck(child, text);

                            if(!isContain) {
								addSearchItem(box, child, textField);
                            }
						} else {
							addSearchItem(null,null, textField);
						}
					}
                    textField.setText("");
				}
			} else {
				final DatePicker startDate = (DatePicker)filterSearchArea.getChildren().get(0);
				final DatePicker endDate = (DatePicker)filterSearchArea.getChildren().get(1);
				String minCreateAt = (startDate.getValue() != null) ? startDate.getValue().toString() : null;
				String maxCreateAt = (endDate.getValue() != null) ? endDate.getValue().toString() : null;
				String id = "";
				if(!StringUtils.isEmpty(minCreateAt)) {
					// 로컬 타임 표준시로 변환
					id = "gt:"+startDate.getValue().atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				}
				// Submitted [end]
				if(!StringUtils.isEmpty(maxCreateAt)) {
					// 로컬 타임 표준시로 변환
					long endMilli= endDate.getValue().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
					if(StringUtils.isEmpty(id)) {
						id = "lt:"+endMilli;
					} else {
						id = id + ",lt:"+endMilli+",and";
					}
				}
				if(!StringUtils.isEmpty(minCreateAt) || !StringUtils.isEmpty(maxCreateAt)) {
					if(searchListVBox.getChildren().isEmpty()) {
						addSearchItem(null, null, minCreateAt, maxCreateAt, id);
					} else {
						Node containNode = searchNode();
						if(containNode != null) {
							VBox box = (VBox) containNode;
							FlowPane child = (FlowPane) box.getChildren().get(1);
							boolean isContain = containCheck(child, id);
							if(!isContain) {
								addSearchItem(box, child, minCreateAt, maxCreateAt, id);
							}
						} else {
							addSearchItem(null, null, minCreateAt, maxCreateAt, id);
						}
					}
				}
				startDate.setValue(null);
				endDate.setValue(null);
			}
		}
	}

	@FXML
	public void downloadExcel() {
		Map<String, List<Object>> searchParams = getSubSearchParam();
		Map<String, Object> params = new HashMap<>();
		params.put("timeHourDiff", getTimeHourDiff());
		WorksheetUtil worksheetUtil = new WorksheetUtil();
		worksheetUtil.exportVariantData(searchParams, params, this.getMainApp());
	}

	private int getTimeHourDiff() {
		TimeZone timeZone = TimeZone.getDefault();
		return timeZone.getRawOffset()/1000/60/60;
	}
	
	/**
	 * 검색 폼 리셋
	 */
	@FXML
	public void resetSearchForm() {
		searchListVBox.getChildren().removeAll(searchListVBox.getChildren());
		setList(1);
	}

	class SampleInfoVBox extends VBox {
		public SampleInfoVBox() {
			this.setPrefWidth(810);
			this.setMaxWidth(Double.MAX_VALUE);
			HBox titleBox = new HBox();
			String styleClass = "sample_list_label";
			Label name = new Label("Sample");
			labelSize(name, 180., styleClass);
			Label status = new Label("Status");
			labelSize(status, 70., styleClass);
			Label panel = new Label("Panel");
			labelSize(panel, 170., styleClass);
			Label variants = new Label("Variant");
			labelSize(variants, 340., styleClass);
			Label qc = new Label("QC");
			labelSize(qc, 78., styleClass);
			Label restart = new Label();
			labelSize(restart, 20., styleClass);
			titleBox.getChildren().addAll(name, panel, status, variants, qc, restart);

			this.getChildren().add(titleBox);
			this.setPrefHeight(30);
		}

		public void labelSize(Label label, Double size, String style) {
			label.setPrefWidth(size);
			label.setPrefHeight(35);
			label.setAlignment(Pos.CENTER);
			if(style != null) label.getStyleClass().add(style);
		}

		public void openSampleTab(final SampleView sample) {
			if(sample.getSampleStatus().getStep().equalsIgnoreCase(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STEP_PIPELINE) &&
					sample.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE)) {
				Map<String, Object> detailViewParamMap = new HashMap<>();
				detailViewParamMap.put("id", sample.getId());

				TopMenu menu = new TopMenu();
				menu.setId("sample_" + sample.getId());
				menu.setMenuName(sample.getName());
				menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_LAYOUT);
				menu.setParamMap(detailViewParamMap);
				menu.setStaticMenu(false);
				mainController.addTopMenu(menu, 2, true);
			} else if(sample.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_FAIL)) {
				DialogUtil.alert("Error message", sample.getSampleStatus().getStatusMsg(), mainController.getPrimaryStage(), true);
			}
		}

		public void setSampleList(List<SampleView> sampleList) {
			for(SampleView sampleView : sampleList) {
				HBox itemHBox = new HBox();
				itemHBox.setStyle(itemHBox.getStyle() + "-fx-cursor:hand;");
				if((sampleView.getSampleStatus().getStep().equalsIgnoreCase(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STEP_PIPELINE) &&
                        (sampleView.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE)
                        || sampleView.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_FAIL)))) {
					itemHBox.setDisable(false);
				} else {
					itemHBox.setDisable(true);
				}
				
				final SampleView sample = sampleView;
				
				itemHBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
					//itemHBox.setStyle(itemHBox.getStyle() + "-fx-text-fill: #CD0034; -fx-font-size: 13px; -fx-font-family:  Noto Sans KR Bold; ");
					//itemHBox.getChildren().clear();

					HBox currentVariants = (HBox)itemHBox.getChildren().get(3);
					int variantsSize = currentVariants.getChildren().size();
					String textFillStyle = "-fx-text-fill: #CD0034;";

					itemHBox.getChildren().get(0).setStyle(itemHBox.getStyle() + textFillStyle);
					itemHBox.getChildren().get(1).setStyle(itemHBox.getStyle() + textFillStyle);
					itemHBox.getChildren().get(4).setStyle(itemHBox.getStyle() + textFillStyle);

					for(int i = 1; i < variantsSize ; i+=2) {
						currentVariants.getChildren().get(i).setStyle(itemHBox.getStyle() + textFillStyle);
					}
			
				});
				itemHBox.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {

					HBox currentVariants = (HBox)itemHBox.getChildren().get(3);
					int variantsSize = currentVariants.getChildren().size();
					String textFillStyle = "-fx-text-fill: #323232;";

					itemHBox.getChildren().get(0).setStyle(itemHBox.getStyle() + textFillStyle);
					itemHBox.getChildren().get(1).setStyle(itemHBox.getStyle() + textFillStyle);
					itemHBox.getChildren().get(4).setStyle(itemHBox.getStyle() + textFillStyle);

					for(int i = 1; i < variantsSize ; i+=2) {
						currentVariants.getChildren().get(i).setStyle(itemHBox.getStyle() + textFillStyle);
					}

				});
				/*itemHBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if(event.getClickCount() == 1) {

					}
				});*/
				String styleClass = null;
				Label name = new Label(sampleView.getName());
				labelSize(name, 180., styleClass);
				HBox statusHBox = new HBox();
				statusHBox.setPrefWidth(70);
				statusHBox.getStyleClass().add("variant_hbox");
				Label status = new Label(sampleView.getSampleStatus().getStatus().substring(0,1));
				if(sampleView.getSampleStatus().getStatus().startsWith("C")) {
					status.getStyleClass().addAll("label","run_complete_icon");
				} else if(sampleView.getSampleStatus().getStatus().startsWith("F")) {
					status.getStyleClass().addAll("label","run_failed_icon");
				} else if(sampleView.getSampleStatus().getStatus().startsWith("R")) {
					status.getStyleClass().addAll("label","run_run_icon");
					if(sample.getSampleStatus().getProgressPercentage() != null) {
                        Tooltip tooltip = new Tooltip(sample.getSampleStatus().getProgressPercentage().toString());
                        status.setTooltip(tooltip);
                    }
				} else if(sampleView.getSampleStatus().getStatus().startsWith("Q")) {
					status.getStyleClass().addAll("label","run_queued_icon");
				}
				statusHBox.getChildren().add(status);
				Label panel = new Label(sampleView.getPanelName());
				labelSize(panel, 170., styleClass);
				HBox variants = new HBox();
				variants.getStyleClass().add("variant_hbox");
				setVariantHBox(variants, sampleView);
				variants.setPrefWidth(340);
				variants.setSpacing(5);
				String qcValue = sampleView.getQcResult();
				if(qcValue.equalsIgnoreCase("NONE")) qcValue = "";
				Label qc = new Label(qcValue);
				labelSize(qc, 78., styleClass);

				Label restart = new Label();
				labelSize(restart, 20., "sample_restart_button");

				name.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openSampleTab(sample));
				panel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openSampleTab(sample));
				statusHBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openSampleTab(sample));
				variants.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openSampleTab(sample));
				qc.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openSampleTab(sample));

                if(sampleView.getPanelName().contains("TruSight Tumor")) {
                    restart.setVisible(false);
                } else {
                	if(sampleView.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_FAIL)) {
                		restart.setVisible(false);
					} else {
						restart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showAlert(sampleView));
					}
                }

				itemHBox.getChildren().addAll(name, panel, statusHBox, variants, qc, restart);

				this.getChildren().add(itemHBox);
				this.setPrefHeight(this.getPrefHeight() + 35);
			}
		}

		private void showAlert(final SampleView sampleView) {

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.getDialogPane().setPrefWidth(450);
			alert.setGraphic(new ImageView(resourceUtil.getImage("/layout/images/renewal/icon_warn_small@2x.png")));
			alert.setTitle("Restart VCF Interpretation");
			alert.setHeaderText(sampleView.getName());
			alert.setContentText("If the analysis is restarted, the results that have already been processed will be deleted. Are you sure to restart this analysis?");

			DialogUtil.setIcon(alert);

			Optional<ButtonType> optional = alert.showAndWait();

			optional.ifPresent(buttonType -> {
				if(buttonType == ButtonType.OK) {
					try {
						HttpClientResponse response = apiService
								.get("restartInterpretation/"  + sampleView.getId(), null, null, null);

						logger.debug(response.getStatus() + "");

						mainController.deleteSampleTab("sample_" + sampleView.getId());

						refreshList();
					} catch (WebAPIException wae) {
						logger.debug(wae.getMessage());
					}
				}
			});
		}

		private void setVariantHBox(HBox variantHBox, SampleView sample) {
			AnalysisResultSummary summary = sample.getAnalysisResultSummary();
			if (summary == null) {
				return;
			}
			final String countLabelStyleClass = "count_label";
			final String countLabelStyle = "-fx-text-fill : gray;";
			if(sample.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
				Label lv1Icon = createIconLabel("P", "lv_i_icon");
				Label lv1Value = createValueLabel(summary.getLevel1VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv2Icon = createIconLabel("LP", "lv_ii_icon");
				Label lv2Value = createValueLabel(summary.getLevel2VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv3Icon = createIconLabel("US", "lv_iii_icon");
				Label lv3Value = createValueLabel(summary.getLevel3VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv4Icon = createIconLabel("LB", "lv_iv_icon");
				Label lv4Value = createValueLabel(summary.getLevel4VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv5Icon = createIconLabel("B", "lv_v_icon");
				Label lv5Value = createValueLabel(summary.getLevel5VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				variantHBox.getChildren()
						.addAll(lv1Icon, lv1Value,
								lv2Icon, lv2Value,
								lv3Icon, lv3Value,
								lv4Icon, lv4Value,
								lv5Icon, lv5Value);

			} else if (sample.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
				Label lv1Icon = createIconLabel("T1", "lv_i_icon");
				Label lv1Value = createValueLabel(summary.getLevel1VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv2Icon = createIconLabel("T2", "lv_ii_icon");
				Label lv2Value = createValueLabel(summary.getLevel2VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv3Icon = createIconLabel("T3", "lv_iii_icon");
				Label lv3Value = createValueLabel(summary.getLevel3VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				Label lv4Icon = createIconLabel("T4", "lv_iv_icon");
				Label lv4Value = createValueLabel(summary.getLevel4VariantCount().toString(), countLabelStyleClass,
						countLabelStyle);

				variantHBox.getChildren()
						.addAll(lv1Icon, lv1Value,
								lv2Icon, lv2Value,
								lv3Icon, lv3Value,
								lv4Icon, lv4Value);

			}
		}

		private Label createIconLabel(String text, String styleClass) {
			Label label = new Label(text);
			label.getStyleClass().add(styleClass);
			return label;
		}

		private Label createValueLabel(String text, String styleClass, String style) {
			Label label = new Label(text);
			label.getStyleClass().add(styleClass);
			label.setStyle(style);
			return label;
		}

	}

	class RunStatusGirdPane extends GridPane {
		private Label runLabel;
		private Label sequencerLabel;
		private Label submitDateLabel;
		private Label finishDateLabel;
		private Label downloadLabel;

		public RunStatusGirdPane() {
			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPrefWidth(225);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPrefWidth(235);
			ColumnConstraints col3 = new ColumnConstraints();
			col3.setPrefWidth(185);
			ColumnConstraints col4 = new ColumnConstraints();
			col4.setPrefWidth(185);
			ColumnConstraints col5 = new ColumnConstraints();
			col5.setPrefWidth(30);
			this.getColumnConstraints().addAll(col1, col2, col3, col4, col5);

			runLabel = new Label();
			sequencerLabel = new Label();
			submitDateLabel = new Label();
			finishDateLabel = new Label();
			downloadLabel = new Label();

			this.add(runLabel, 0, 0);

			this.add(sequencerLabel, 1, 0);
			setLabelStyle(sequencerLabel);
			this.add(submitDateLabel, 2, 0);
			setLabelStyle(submitDateLabel);
			this.add(finishDateLabel, 3, 0);
			setLabelStyle(finishDateLabel);
			this.add(downloadLabel, 4, 0);
			setLabelStyle(downloadLabel);
			downloadLabel.getStyleClass().add("fileDownloadButton");

			this.setPrefHeight(35);
			setLabelStyle(runLabel);

		}

		private void setLabelStyle(Label label) {
			label.setMaxWidth(Double.MAX_VALUE);
			label.setMaxHeight(Double.MAX_VALUE);
			label.setPrefHeight(35);
			label.setAlignment(Pos.CENTER);
			label.getStyleClass().add("sample_header");
		}

		public void setLabel(RunSampleView runSampleView) {
			runLabel.setText(runSampleView.getRun().getName());
			sequencerLabel.setText(runSampleView.getRun().getSequencingPlatform());
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			if (runSampleView.getRun().getCreatedAt() != null)
				submitDateLabel.setText(format.format(runSampleView.getRun().getCreatedAt().toDate()));
			if (runSampleView.getRun().getCompletedAt() != null)
			finishDateLabel.setText(format.format(runSampleView.getRun().getCompletedAt().toDate()));
			downloadLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
				try {
					FXMLLoader fxmlLoader = FXMLLoadUtil.load(FXMLConstants.RUN_RAW_DATA_DOWNLOAD);
					Node node = fxmlLoader.load();
					RunRawDataDownloadController runRawDataDownloadController = fxmlLoader.getController();
					runRawDataDownloadController.setRunSampleView(runSampleView);
					runRawDataDownloadController.setMainController(mainController);
					runRawDataDownloadController.show((Parent) node);
				} catch (IOException ioe) {
					logger.debug(ioe.getMessage());
				}
			});
		}

	}

}
