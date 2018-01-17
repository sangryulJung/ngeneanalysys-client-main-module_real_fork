package ngeneanalysys.controller;

import javafx.scene.Parent;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ExonSkip;
import ngeneanalysys.model.paged.PagedExonSkip;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author Jang
 * @since 2018-01-12
 */
public class AnalysisDetailExonSkippingController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    private Sample sample;

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    /**
     * @param analysisDetailFusionMainController
     */
    public void setAnalysisDetailFusionMainController(AnalysisDetailFusionMainController analysisDetailFusionMainController) {
        this.analysisDetailFusionMainController = analysisDetailFusionMainController;
    }

    public void setList() {
        try {
            HttpClientResponse response = apiService.get("analysisResults/sampleExonSkip/" + sample.getId(), null, null, false);

            PagedExonSkip pagedExonSkip = response.getObjectBeforeConvertResponseToJSON(PagedExonSkip.class);

            List<ExonSkip> exonSkipList = pagedExonSkip.getResult();

            if(!exonSkipList.isEmpty()) {

            }

        } catch (WebAPIException wae) {

        }
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show Fusion");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");

        setList();

        analysisDetailFusionMainController.subTabExonSkipping.setContent(root);
    }
}
