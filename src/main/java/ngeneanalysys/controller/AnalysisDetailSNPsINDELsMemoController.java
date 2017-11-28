package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.VariantInterpretationLogs;
import ngeneanalysys.util.LoggerUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;


import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class AnalysisDetailSNPsINDELsMemoController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<VariantInterpretationLogs> memoListTableView;
    /** 목록 등록일 컬럼 */
    @FXML
    private TableColumn<VariantInterpretationLogs,String> dateColumn;
    /** 목록 작업구분 컬럼 */
    @FXML
    private TableColumn<VariantInterpretationLogs,String> typeColumn;
    /** 목록 등록자 컬럼 */
    @FXML
    private TableColumn<VariantInterpretationLogs,String> userColumn;
    /** 목록 변경이전값 컬럼 */
    @FXML
    private TableColumn<VariantInterpretationLogs,String> preValueColumn;
    /** 목록 변경값 컬럼 */
    @FXML
    private TableColumn<VariantInterpretationLogs,String> nextValueColumn;
    /** 목록 코멘트 컬럼 */
    @FXML
    private TableColumn<VariantInterpretationLogs,String> commentColumn;

    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;

    /**
     * @return the analysisDetailSNPsINDELsController
     */
    public AnalysisDetailSNPsINDELsController getAnalysisDetailSNPsINDELsController() {
        return analysisDetailSNPsINDELsController;
    }
    /**
     * @param analysisDetailSNPsINDELsController the analysisDetailSNPsINDELsController to set
     */
    public void setAnalysisDetailSNPsINDELsController(
            AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController) {
        this.analysisDetailSNPsINDELsController = analysisDetailSNPsINDELsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterpretationType()));
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberLoginId()));
        preValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOldValue()));
        nextValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNewValue()));
        commentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));

        analysisDetailSNPsINDELsController.subTabMemo.setContent(root);
    }

    /**
     * 목록 화면 출력
     * @param list
     */
    public void displayList(ObservableList<VariantInterpretationLogs> list) {
        memoListTableView.setItems(list);
    }
}
