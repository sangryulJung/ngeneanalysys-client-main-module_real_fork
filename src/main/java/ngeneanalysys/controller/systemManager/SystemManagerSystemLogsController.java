package ngeneanalysys.controller.systemManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.paged.PagedSystemLogView;
import ngeneanalysys.model.SystemLogView;
import ngeneanalysys.service.APIService;
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

/**
 * @author Jang
 * @since 2017-09-04
 */
public class SystemManagerSystemLogsController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    APIService apiService = null;

    @FXML
    private TableView<SystemLogView> logListTable;
    @FXML
    private TableColumn<SystemLogView, String> createdAtTableColumn;
    @FXML
    private TableColumn<SystemLogView, String> logTypeTableColumn;
    @FXML
    private TableColumn<SystemLogView, String> logMsgTableColumn;
    @FXML
    private TableColumn<SystemLogView, String> userNameTableColumn;
    @FXML
    private TableColumn<SystemLogView, String> userGroupNameTableColumn;
    @FXML
    private TableColumn<SystemLogView, String> loginIdTableColumn;
    @FXML
    private TableColumn<SystemLogView, String> roleTableColumn;

    @FXML
    private TextField pageText;

    @FXML
    private Pagination paginationList;

    @Override
    public void show(Parent root) throws IOException {

        apiService = APIService.getInstance();

        createdAtTableColumn.setCellValueFactory(item -> new SimpleStringProperty(DateFormatUtils.format(
                item.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));
        logTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLogType()));
        logMsgTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLogMessage()));
        userGroupNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getMemberGroupName()));
        userNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getMemberName()));
        loginIdTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLoginId()));
        roleTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getMemberRole()));

        paginationList.setPageFactory(pageIndex -> {
            setLogList(pageIndex + 1);
            return new VBox();
        });
    }

    public Map<String, Object> getLogSearchParam() {


        return new HashMap<>();
    }

    public void setLogList(int page) {

        int totalCount = 0;
        int limit = 20;
        int offset = (page - 1)  * limit;

        HttpClientResponse response = null;

        try {
            Map<String, Object> param = getLogSearchParam();
            param.put("limit", limit);
            param.put("offset", offset);

            response = apiService.get("/admin/systemLogs", param, null, false);

            if(response != null) {
                PagedSystemLogView pagedSystemLogView =
                        response.getObjectBeforeConvertResponseToJSON(PagedSystemLogView.class);
                List<SystemLogView> list = null;

                if(pagedSystemLogView != null) {
                    totalCount = pagedSystemLogView.getCount();
                    list = pagedSystemLogView.getResult();
                }

                int pageCount = 0;

                if(totalCount > 0) {
                    pageCount = totalCount / limit;
                    paginationList.setCurrentPageIndex(page - 1);
                    if(totalCount % limit > 0) {
                        pageCount++;
                    }
                }

                logListTable.setItems((FXCollections.observableList(list)));

                logger.debug(String.format("total count : %s, page count : %s", totalCount, pageCount));

                if (pageCount > 0) {
                    paginationList.setVisible(true);
                    paginationList.setPageCount(pageCount);
                } else {
                    paginationList.setVisible(false);
                }
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    @FXML
    public void search() {
        setLogList(1);
    }

    @FXML
    public void pageMove() {
        if(!StringUtils.isEmpty(pageText.getText()) && pageText.getText().trim().length() != 0) {
            try {
                int page = Integer.parseInt(pageText.getText());
                setLogList(page);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @FXML
    public void reset() {

    }

    @FXML
    public void prevMove() {
        int pageIndex = paginationList.getCurrentPageIndex();

        int movePage = pageIndex / 10;

        if(movePage == 0)
            paginationList.setCurrentPageIndex(0);
        else
            paginationList.setCurrentPageIndex(((movePage - 1) * 10));
    }

    @FXML
    public void nextMove() {
        int totalPage = paginationList.getPageCount();
        int pageIndex = paginationList.getCurrentPageIndex();

        int movePage = pageIndex / 10;

        if(totalPage == pageIndex)
            paginationList.setCurrentPageIndex(totalPage);
        else {
            int tempTotalPage = totalPage / 10;
            if(movePage == tempTotalPage)
                paginationList.setCurrentPageIndex(totalPage);
            else
                paginationList.setCurrentPageIndex(((movePage + 1) * 10));
        }
    }
}
