package ngeneanalysys.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedRunSampleView;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.util.StringUtils;
import org.controlsfx.control.textfield.AutoCompletionBinding;
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
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
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
	private FlowPane searchListFlowPane;

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

	public void setSearchOption() {
		searchOption.put("SAMPLE","sampleName");
		searchOption.put("RUN","runName");
		searchOption.put("PANEL","panelName");
	}

	/**
	 * 화면 출력
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void show(Parent root) throws IOException {
		setSearchOption();
		logger.info("PastResultsController show..");
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
			if(newV.getValue().equalsIgnoreCase("String")) {
				filterSearchArea.getChildren().removeAll(filterSearchArea.getChildren());
				//TextField textField = new TextField();
				CustomTextField textField = new CustomTextField();
				ImageView imageView = new ImageView(resourceUtil.getImage("/layout/images/renewal/search_icon.png"));
				imageView.setStyle("-fx-cursor:hand;");
				imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if(StringUtils.isEmpty(textField.getText())) {
						oneItem = false;
					} else {
						oneItem = true;
					}
					search();
				});
				textField.setRight(imageView);
				textField.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {

						if (searchComboBox.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase("RUN") ||
								searchComboBox.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase("SAMPLE")) {
							try {
								Map<String, Object> params = new HashMap<>();
								params.put("target", searchComboBox.getSelectionModel().getSelectedItem().getText().toLowerCase());
								params.put("keyword", textField.getText());
								params.put("resultCount", 15);
								HttpClientResponse response = apiService.get("/filter", params, null, false);
								logger.info(response.getContentString());
								JSONParser jsonParser = new JSONParser();
								JSONArray jsonArray = (JSONArray) jsonParser.parse(response.getContentString());
								AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(textField, getAllData(jsonArray));
								binding.setVisibleRowCount(10);

							} catch (WebAPIException wae) {
								wae.printStackTrace();
							} catch (ParseException pe) {
								pe.printStackTrace();
							}
						} else if (searchComboBox.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase("PANEL")) {
							List<Panel> panels = (List<Panel>) getMainController().getBasicInformationMap().get("panels");
							List<Panel> filterPanel = null;
							if (StringUtils.isEmpty(textField.getText())) {
								filterPanel = panels.stream().filter(panel -> panel.getName().contains(textField.getText())).collect(Collectors.toList());
							} else {
								filterPanel = panels;
							}
							TextFields.bindAutoCompletion(textField, getAllPanel(filterPanel)).setVisibleRowCount(10);
						}
					});
				textField.setPrefWidth(Double.MAX_VALUE);
				filterSearchArea.getChildren().add(textField);
			}
		});

		searchComboBox.getSelectionModel().select(0);
	}

	private Set<String> getAllData(JSONArray array) {
		Set<String> data = new HashSet<>();
		array.stream().forEach(item -> data.add(item.toString()));
		return data;
	}

	private Set<String> getAllPanel(List<Panel> panelList) {
		Set<String> panels = new HashSet<>();
		panelList.stream().forEach(panel -> panels.add(panel.getName()));
		return panels;
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

			if(autoRefreshTimeline == null) {
				autoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(refreshPeriodSecond),
						ae -> setList(paginationList.getCurrentPageIndex() + 1)));
				autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
			} else {
				logger.info(String.format("[%s] timeline restart", this.getClass().getName()));
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

	public void setComboBoxItem() {
		searchComboBox.setConverter(new ComboBoxConverter());
		searchComboBox.getItems().add(new ComboBoxItem("String", "RUN"));
		searchComboBox.getItems().add(new ComboBoxItem("String", "SAMPLE"));
		searchComboBox.getItems().add(new ComboBoxItem("String", "PANEL"));
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
        int refreshPeriodSecond = (Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000) - 1;
		// 기능 실행중인 상태인 경우 실행
		if(autoRefreshTimeline != null && isAutoRefreshOn) {
			logger.info(String.format("[%s] timeline status : %s", this.getClass().getName(),
					autoRefreshTimeline.getStatus()));
			// 시작
			if(autoRefreshTimeline.getStatus() == Animation.Status.PAUSED) {
			    autoRefreshTimeline.playFrom(Duration.millis(refreshPeriodSecond));
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
		Map<String, List<Object>> subParams = getSubSearchParam();
		param.put("limit", itemCountPerPage);
		param.put("offset", offset);
		param.put("ordering", "DESC");
		
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
			e.printStackTrace();
		}
		maskerPane.setVisible(false);
	}

	private Map<String, Object> getSearchParam() {
		Map<String, Object> param = new HashMap<>();
		param.put("format", "json");		
		param.put("step", AnalysisJobStatusCode.JOB_RUN_GROUP_PIPELINE);
		param.put("status", AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE);

		if(oneItem) {
			final CustomTextField textField = (CustomTextField)filterSearchArea.getChildren().get(0);
			param.put(searchOption.get(searchComboBox.getSelectionModel().getSelectedItem().getText()), textField.getText());
		}

		/** End 검색 항목 설정 */
		return param;
	}

	private Map<String, List<Object>> getSubSearchParam() {
		Map<String, List<Object>> param = new HashMap<>();

		if(!oneItem && !searchListFlowPane.getChildren().isEmpty()){
			for(Node node : searchListFlowPane.getChildren()) {
				VBox vbox = (VBox) node;
				Label label = (Label) vbox.getChildren().get(0);
				FlowPane flowPane = (FlowPane) vbox.getChildren().get(1);
				List<Object> value = new ArrayList<>();
				for(Node item : flowPane.getChildren()) {
					value.add(item.getId());
				}
				if(searchOption.containsKey(label.getText())) {
					param.put(searchOption.get(label.getText()), value);
				}
			}
		} else {
		}

		/** End 검색 항목 설정 */
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
	 * @param list
	 */
	public void renderSampleList(List<RunSampleView> list) {

		resultVBox.getChildren().removeAll(resultVBox.getChildren());
		resultVBox.setPrefHeight(0);
		if (list != null && !list.isEmpty()) {
			for(RunSampleView runSampleView : list) {
				RunStatusGirdPane gridPane = new RunStatusGirdPane();
				runStatusGirdPanes.add(gridPane);
				resultVBox.getChildren().add(gridPane);
				gridPane.setLabel(runSampleView.getRun());
				setVBoxPrefSize(gridPane);

				SampleInfoVBox vbox = new SampleInfoVBox();
				vbox.setSampleList(runSampleView.getSampleViews());
				resultVBox.getChildren().add(vbox);
				setVBoxPrefSize(vbox);
				if(list.indexOf(runSampleView) < list.size() - 1) {
                    Insets insets = new Insets(0, 0, 30, 0);
                    VBox.setMargin(vbox, insets);
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
		addSearchArea();
		setList(1);
	}

	public void addSearchItem(final CustomTextField textField) {
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER_LEFT);
		box.setPrefWidth(240);
		box.setId(searchComboBox.getSelectionModel().getSelectedItem().getText());
		Label title = new Label(searchComboBox.getSelectionModel().getSelectedItem().getText());
		title.getStyleClass().add("font_size_13");
		box.setSpacing(10);
		box.getChildren().add(title);
		FlowPane flowPane = new FlowPane();
		HBox hBox = new HBox();
		hBox.setId(textField.getText());
		Label label = new Label(textField.getText());
		Label xLabel = new Label("X");
		xLabel.setCursor(Cursor.HAND);
		xLabel.setOnMouseClicked(ev -> {
			if(flowPane.getChildren().size() == 1) {
				searchListFlowPane.getChildren().remove(box);
			} else {
				flowPane.getChildren().remove(hBox);
			}
			setList(1);
		});
		xLabel.getStyleClass().add("remove_btn");
		hBox.getChildren().addAll(label, xLabel);
		hBox.setSpacing(5);
		flowPane.getChildren().add(hBox);
		box.getChildren().add(flowPane);
		searchListFlowPane.getChildren().add(box);
	}

	public void addSearchArea() {
		if(searchComboBox.getSelectionModel().getSelectedItem() != null && !oneItem) {
			if(filterSearchArea.getChildren().get(0) instanceof CustomTextField) {
				final CustomTextField textField = (CustomTextField)filterSearchArea.getChildren().get(0);
				if(!StringUtils.isEmpty(textField.getText())) {
					if(searchListFlowPane.getChildren().isEmpty()) {
						addSearchItem(textField);
					} else {
						Node containNode = null;
						for(Node node : searchListFlowPane.getChildren()) {
							if(node.getId().equalsIgnoreCase(searchComboBox.getSelectionModel().getSelectedItem().getText())) {
								containNode = node;
								break;
							}
						}

						if(containNode != null) {
							VBox box = (VBox) containNode;
							FlowPane child = (FlowPane) box.getChildren().get(1);
							boolean isContain = false;
							String text = textField.getText();
							for(Node node : child.getChildren()) {
                                if(node.getId().equalsIgnoreCase(text)) {
                                    isContain = true;
                                    break;
                                }
                            }
                            if(!isContain) {
								HBox hBox = new HBox();
								hBox.setId(text);
							    Label label = new Label(text);
							    Label xLabel = new Label("X");
							    xLabel.getStyleClass().add("remove_btn");
								xLabel.setCursor(Cursor.HAND);
								xLabel.setOnMouseClicked(ev -> {
									if(child.getChildren().size() == 1) {
										searchListFlowPane.getChildren().remove(box);
									} else {
										child.getChildren().remove(hBox);
									}
								});
								hBox.getChildren().addAll(label, xLabel);
                                child.getChildren().add(hBox);
								hBox.setSpacing(5);
                                FlowPane.setMargin(label, new Insets(0, 0, 0, 10));
                            }
						} else {
							addSearchItem(textField);
						}
					}
                    textField.setText("");
				}
			}
		}
	}
	
	/**
	 * 검색 폼 리셋
	 */
	@FXML
	public void resetSearchForm() {
		searchListFlowPane.getChildren().removeAll(searchListFlowPane.getChildren());
		setList(1);
	}

	class SampleInfoVBox extends VBox {
		public SampleInfoVBox() {
			this.setPrefWidth(810);
			this.setMaxWidth(Double.MAX_VALUE);
			HBox titleBox = new HBox();
			String styleClass = "sample_list_label";
			Label name = new Label("SAMPLE");
			labelSize(name, 180., styleClass);
			Label status = new Label("STATUS");
			labelSize(status, 80., styleClass);
			Label panel = new Label("PANEL");
			labelSize(panel, 170., styleClass);
			Label variants = new Label("VARIANTS");
			labelSize(variants, 340., styleClass);
			Label qc = new Label("QC");
			labelSize(qc, 88., styleClass);
			titleBox.getChildren().addAll(name, panel, status, variants, qc);

			this.getChildren().add(titleBox);
			this.setPrefHeight(30);
		}

		public void labelSize(Label label, Double size, String style) {
			label.setPrefWidth(size);
			label.setPrefHeight(35);
			label.setAlignment(Pos.CENTER);
			if(style != null) label.getStyleClass().add(style);
		}

		public void setSampleList(List<SampleView> sampleList) {
			for(SampleView sampleView : sampleList) {
			    if(sampleView.getSampleStatus().getStatus().equalsIgnoreCase(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_QUEUED)) {
			        this.setDisable(true);
                } else {
                    this.setDisable(false);
                }
				HBox itemHBox = new HBox();
				itemHBox.setStyle(itemHBox.getStyle() + "-fx-cursor:hand;");
				final SampleView sample = sampleView;
				itemHBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if(event.getClickCount() == 2) {
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
							DialogUtil.alert("Job Status", sample.getSampleStatus().getStatusMsg(), mainController.getPrimaryStage(), true);
						}
					}
				});
				String styleClass = null;
				Label name = new Label(sampleView.getName());
				labelSize(name, 180., styleClass);
				HBox statusHBox = new HBox();
				statusHBox.setPrefWidth(80);
				statusHBox.getStyleClass().add("variant_hbox");
				Label status = new Label(sampleView.getSampleStatus().getStatus().substring(0,1));
				if(sampleView.getSampleStatus().getStatus().startsWith("C")) {
					status.getStyleClass().addAll("label","complete_icon");
				} else if(sampleView.getSampleStatus().getStatus().startsWith("F")) {
					status.getStyleClass().addAll("label","failed_icon");
				} else if(sampleView.getSampleStatus().getStatus().startsWith("R")) {
					status.getStyleClass().addAll("label","run_icon");
					if(sample.getSampleStatus().getProgressPercentage() != null) {
                        Tooltip tooltip = new Tooltip(sample.getSampleStatus().getProgressPercentage().toString());
                        status.setTooltip(tooltip);
                    }
				} else if(sampleView.getSampleStatus().getStatus().startsWith("Q")) {
					status.getStyleClass().addAll("label","queued_icon");
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
				labelSize(qc, 88., styleClass);

				itemHBox.getChildren().addAll(name, panel, statusHBox, variants, qc);

				this.getChildren().add(itemHBox);
				this.setPrefHeight(this.getPrefHeight() + 35);
			}
		}

		private void setVariantHBox(HBox variantHBox, SampleView sample) {
			AnalysisResultSummary summary = sample.getAnalysisResultSummary();
			if(sample.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
				Label lv1Icon = new Label("P");
				lv1Icon.getStyleClass().add("lv_i_icon");
				Label lv1Value = new Label(summary.getLevel1VariantCount().toString());
				lv1Value.getStyleClass().add("count_label");
				lv1Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv2Icon = new Label("LP");
				lv2Icon.getStyleClass().add("lv_ii_icon");
				Label lv2Value = new Label(summary.getLevel2VariantCount().toString());
				lv2Value.getStyleClass().add("count_label");
				lv2Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv3Icon = new Label("US");
				lv3Icon.getStyleClass().add("lv_i_icon");
				Label lv3Value = new Label(summary.getLevel3VariantCount().toString());
				lv3Value.getStyleClass().add("count_label");
				lv3Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv4Icon = new Label("LB");
				lv4Icon.getStyleClass().add("lv_iv_icon");
				Label lv4Value = new Label(summary.getLevel4VariantCount().toString());
				lv4Value.getStyleClass().add("count_label");
				lv4Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv5Icon = new Label("B");
				lv5Icon.getStyleClass().add("lv_v_icon");
				Label lv5Value = new Label(summary.getLevel5VariantCount().toString());
				lv5Value.getStyleClass().add("count_label");
				lv5Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				variantHBox.getChildren()
						.addAll(lv1Icon, lv1Value,
								lv2Icon, lv2Value,
								lv3Icon, lv3Value,
								lv4Icon, lv4Value,
								lv5Icon, lv5Value);

			} else if (sample.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
				Label lv1Icon = new Label("T1");
				lv1Icon.getStyleClass().add("lv_i_icon");
				Label lv1Value = new Label(summary.getLevel1VariantCount().toString());
				lv1Value.getStyleClass().add("count_label");
				lv1Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv2Icon = new Label("T2");
				lv2Icon.getStyleClass().add("lv_ii_icon");
				Label lv2Value = new Label(summary.getLevel2VariantCount().toString());
				lv2Value.getStyleClass().add("count_label");
				lv2Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv3Icon = new Label("T3");
				lv3Icon.getStyleClass().add("lv_i_icon");
				Label lv3Value = new Label(summary.getLevel3VariantCount().toString());
				lv3Value.getStyleClass().add("count_label");
				lv3Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				Label lv4Icon = new Label("T4");
				lv4Icon.getStyleClass().add("lv_iv_icon");
				Label lv4Value = new Label(summary.getLevel4VariantCount().toString());
				lv4Value.getStyleClass().add("count_label");
				lv4Value.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");

				variantHBox.getChildren()
						.addAll(lv1Icon, lv1Value,
								lv2Icon, lv2Value,
								lv3Icon, lv3Value,
								lv4Icon, lv4Value);

			}
		}

	}

	class RunStatusGirdPane extends GridPane {
		private Label runLabel;
		private Label sequencerLabel;
		private Label submitDateLabel;
		private Label finishDateLabel;

		public RunStatusGirdPane() {
			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPrefWidth(225);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPrefWidth(265);
			ColumnConstraints col3 = new ColumnConstraints();
			col3.setPrefWidth(185);
			ColumnConstraints col4 = new ColumnConstraints();
			col4.setPrefWidth(185);
			this.getColumnConstraints().addAll(col1, col2, col3, col4);

			runLabel = new Label();
			sequencerLabel = new Label();
			submitDateLabel = new Label();
			finishDateLabel = new Label();

			this.add(runLabel, 0, 0);

			this.add(sequencerLabel, 1, 0);
			setLabelStyle(sequencerLabel);
			this.add(submitDateLabel, 2, 0);
			setLabelStyle(submitDateLabel);
			this.add(finishDateLabel, 3, 0);
			setLabelStyle(finishDateLabel);

			this.setPrefHeight(35);
			setLabelStyle(runLabel);

		}

		public void setLabelStyle(Label label) {
			label.setMaxWidth(Double.MAX_VALUE);
			label.setMaxHeight(Double.MAX_VALUE);
			label.setPrefHeight(35);
			label.setAlignment(Pos.CENTER);
			//label.setStyle("-fx-font-family: Noto Sans CJK KR Regular;");
			label.getStyleClass().add("sample_header");
		}

		public void setLabel(Run run) {
			runLabel.setText(run.getName());
			sequencerLabel.setText(run.getSequencingPlatform());
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			if (run.getCreatedAt() != null)
				submitDateLabel.setText(format.format(run.getCreatedAt().toDate()));
			if (run.getCompletedAt() != null)
			finishDateLabel.setText(format.format(run.getCompletedAt().toDate()));
		}

	}

}
