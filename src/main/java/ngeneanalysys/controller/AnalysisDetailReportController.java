package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.Sample;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class AnalysisDetailReportController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Label patientDiseaseLabel;

    @FXML
    private Label panelLabel;

    @Override
    public void show(Parent root) throws IOException {
        Sample sample = (Sample)paramMap.get("sample");

        logger.info(sample.toString());


    }

    @FXML
    public void save() {

    }

    @FXML
    public void createPDFAsDraft() {

    }

    @FXML
    public void createPDFAsFinal() {

    }

    @FXML
    public void confirmPDFAsFinal() {

    }
}
