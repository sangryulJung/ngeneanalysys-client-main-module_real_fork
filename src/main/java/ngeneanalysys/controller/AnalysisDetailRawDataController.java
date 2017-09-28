package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.AnalysisFileList;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-29
 */
public class AnalysisDetailRawDataController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 현재 샘플 정보 객체 */
    private Sample sample;

    /** 전체 파일 목록 객체 */
    private List<AnalysisFile> totalList;

    /** 파일 목록 filter map 객체 */
    private Map<String,Integer> filterMap;

    @FXML
    private HBox filterTitleArea;

    @FXML
    private HBox filterList;

    /** 목록 Table View */
    @FXML
    private TableView<AnalysisFile> rawListTableView;

    /** 파일 타입 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> typeColumn;

    /** 파일명 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> filenameColumn;

    /** 파일 용량 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> sizeColumn;

    /** 파일 생성일시 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> createdColumn;

    /** 파일 다운로드 버튼 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,Object> downloadColumn;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");
        // 파일 목록 요청
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("sampleId", sample.getId());
        try {
            HttpClientResponse response = apiService.get("/analysisFiles", paramMap, null, false);
            totalList = null;
            AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);
            totalList = analysisFileList.getResult();
            int foundFileCount = (int)totalList.stream().filter(item -> "FASTQ.GZ".equals(item.getFileType())).count();

            /*for(AnalysisFilesSampleFileMeta sampleFileMeta : sample.getFileList()){
                AnalysisResultFile analysisResultFile = new AnalysisResultFile();
                analysisResultFile.setCreatedAt(sampleFileMeta.getCreatedAt());
                analysisResultFile.setId(sampleFileMeta.getId());
                analysisResultFile.setName(sampleFileMeta.getName());
                analysisResultFile.setSampleId(sampleFileMeta.getSample());
                analysisResultFile.setSize(sampleFileMeta.getSize());
                analysisResultFile.setTag("FASTQ");
                totalList.add(analysisResultFile);
            }*/
            //addFilterBox();
            filterTitleArea.setVisible(false);
            filterTitleArea.setPrefWidth(0);
            filterList.getChildren().add(new Label("Raw Data Download: Upload fastq files, Mapped file(BAM format), Variant file(VCF format), Data QC Report(PDF) "));
            setList("ALL");

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
        // 목록 컬럼 설정
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType()));
        filenameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.convertFileSizeFormat(cellData.getValue().getSize().longValue())));
        createdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));
        downloadColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        downloadColumn.setCellFactory(param -> {
            TableCell<AnalysisFile, Object> cell = new TableCell<AnalysisFile, Object>() {
                @Override
                public void updateItem(Object item, boolean empty) {
                    if (item != null) {
                        Button button = new Button("Download");
                        button.getStyleClass().add("btn_raw_data_download");
                        button.setOnAction(event -> {
                            //download((AnalysisResultFile) item);
                        });
                        setGraphic(button);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return cell;
        });



        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());

        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    /**
     * 필터 박스 추가
     */
    @SuppressWarnings("static-access")
    public void addFilterBox() {
        if(totalList != null && totalList.size() > 0) {
            this.filterMap = new HashMap<>();
            for(AnalysisFile item : totalList) {
                Integer count = (filterMap.containsKey(item.getFileType())) ? filterMap.get(item.getFileType()) : 0;
                count++;
                filterMap.put(item.getFileType(), count);
            }

            VBox allFilterBox = getFilterBox("ALL", totalList.size());
            this.filterList.getChildren().add(allFilterBox);
            this.filterList.setMargin(allFilterBox, new Insets(0, 5, 0, 0));

            if(!filterMap.isEmpty() && filterMap.size() > 0) {
                for(String key : filterMap.keySet()) {
                    VBox box = getFilterBox(key, filterMap.get(key).intValue());
                    this.filterList.getChildren().add(box);
                    this.filterList.setMargin(box, new Insets(0, 5, 0, 0));
                }
            }
        }
    }

    /**
     * 필터 박스 반환
     * @param tag
     * @param count
     * @return
     */
    public VBox getFilterBox(String tag, int count) {
        VBox box = new VBox();
        box.setId(tag);

        Region region = new Region();

        Label levelLabel = new Label(tag);
        levelLabel.getStyleClass().add("level");

        if("ALL".equals(tag)) {
            box.getStyleClass().add("selected");
            region.getStyleClass().add("level_ALL");
            levelLabel.getStyleClass().add("txt_black");
        }

        Label countLabel = new Label(String.valueOf(count));
        countLabel.getStyleClass().add("count");

        box.getChildren().setAll(region, levelLabel, countLabel);

        // 마우스 클릭 이벤트 바인딩
        box.setOnMouseClicked(event -> {
            setList(tag);
            setOnSelectedFilter(tag);
        });

        return box;
    }

    /**
     * 필터 선택 표시
     * @param tag
     */
    public void setOnSelectedFilter(String tag) {
        if(filterList.getChildren() != null && filterList.getChildren().size() > 0) {
            for (Node node : filterList.getChildren()) {
                VBox box = (VBox) node;
                // 선택 속성 클래스 삭제
                box.getStyleClass().remove("selected");
                if(box.getId().equals(tag)) {
                    // 선택 속성 클래스 추가
                    box.getStyleClass().add("selected");
                }
            }
        }
    }

    /**
     * 목록 화면 출력
     * @param tag
     */
    public void setList(String tag) {
        ObservableList<AnalysisFile> displayList = null;

        if(StringUtils.isEmpty(tag) || "ALL".equals(tag)) {
            if(totalList != null && totalList.size() > 0) {
                displayList = FXCollections.observableArrayList(totalList);
            }
        } else {
            if(totalList != null && totalList.size() > 0) {
                displayList = FXCollections.observableArrayList();
                for (AnalysisFile item : totalList) {
                    if(tag.equals(item.getFileType())) {
                        displayList.add(item);
                    }
                }
            }
        }

        // 목록 초기화
        if(rawListTableView.getItems() != null && rawListTableView.getItems().size() > 0) {
            rawListTableView.getItems().clear();
        }
        rawListTableView.setItems(displayList);
    }
}
