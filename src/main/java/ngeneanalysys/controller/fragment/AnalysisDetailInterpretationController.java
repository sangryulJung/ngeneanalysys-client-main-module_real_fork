package ngeneanalysys.controller.fragment;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.SnpInDelInterpretation;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-03-19
 */
public class AnalysisDetailInterpretationController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Label TherapeuticALabel;

    @FXML
    private Label TherapeuticBLabel;

    @FXML
    private Label TherapeuticCLabel;

    @FXML
    private Label TherapeuticDLabel;

    @FXML
    private Label PrognosisALabel;

    @FXML
    private Label PrognosisBLabel;

    @FXML
    private Label PrognosisCLabel;

    @FXML
    private Label PrognosisDLabel;

    @FXML
    private Label DiagnosisALabel;

    @FXML
    private Label DiagnosisBLabel;

    @FXML
    private Label DiagnosisCLabel;

    @FXML
    private Label DiagnosisDLabel;

    @Override
    public void show(Parent root) throws IOException {

    }

    public void setLabel(SnpInDelInterpretation interpretation) {

        setNull();

        if(interpretation == null) return;

        if(interpretation.getTherapeuticEvidence() != null) {
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelA()))
                TherapeuticALabel.setText(interpretation.getTherapeuticEvidence().getLevelA());
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelB()))
                TherapeuticBLabel.setText(interpretation.getTherapeuticEvidence().getLevelB());
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelC()))
                TherapeuticCLabel.setText(interpretation.getTherapeuticEvidence().getLevelC());
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelD()))
                TherapeuticDLabel.setText(interpretation.getTherapeuticEvidence().getLevelD());
        }

        if(interpretation.getPrognosisEvidence() != null) {
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelA()))
                PrognosisALabel.setText(interpretation.getPrognosisEvidence().getLevelA());
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelB()))
                PrognosisBLabel.setText(interpretation.getPrognosisEvidence().getLevelB());
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelC()))
                PrognosisCLabel.setText(interpretation.getPrognosisEvidence().getLevelC());
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelD()))
                PrognosisDLabel.setText(interpretation.getPrognosisEvidence().getLevelD());
        }

        if(interpretation.getDiagnosisEvidence() != null) {
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelA()))
                DiagnosisALabel.setText(interpretation.getDiagnosisEvidence().getLevelA());
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelB()))
                DiagnosisBLabel.setText(interpretation.getDiagnosisEvidence().getLevelB());
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelC()))
                DiagnosisCLabel.setText(interpretation.getDiagnosisEvidence().getLevelC());
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelD()))
                DiagnosisDLabel.setText(interpretation.getDiagnosisEvidence().getLevelD());
        }

    }

    private void setNull() {
        TherapeuticALabel.setText("");
        TherapeuticBLabel.setText("");
        TherapeuticCLabel.setText("");
        TherapeuticDLabel.setText("");
        PrognosisALabel.setText("");
        PrognosisBLabel.setText("");
        PrognosisCLabel.setText("");
        PrognosisDLabel.setText("");
        DiagnosisALabel.setText("");
        DiagnosisBLabel.setText("");
        DiagnosisCLabel.setText("");
        DiagnosisDLabel.setText("");
    }
}
