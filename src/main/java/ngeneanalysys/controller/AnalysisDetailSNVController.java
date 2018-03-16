package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-03-15
 */
public class AnalysisDetailSNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane snvWrapper;

    @FXML
    private Button leftSizeButton;

    @FXML
    private Button rightSizeButton;

    @FXML
    private Accordion overviewAccordion;

    @FXML
    private TableView variantListTableView;

    @FXML
    private VBox filterArea;

    private AnalysisDetailVariantsController variantsController;

    private final double leftFoldedWidth = 50;
    private final double leftExpandedWidth = 250;

    private final double rightFoldedWidth = 50;
    private final double rightStandardWidth = 1030;
    private final double rightFullWidth = 1230;

    private final double centerFoldedWidth = 0;
    private final double centerStandardWidth = 980;
    private final double centerFullWidth = 1180;

    private final double minSize = 0;
    private final double standardAccordionSize = 980;
    private final double maxAccordionSize = 1180;

    private final double standardTableSize = 980;
    private final double maxTableSize = 1180;

    /**
     * @param variantsController
     */
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("init snv controller");

        leftSizeButton.setOnMouseClicked(event -> {
            if (leftSizeButton.getStyleClass().get(0) == null){
                return;
            } else if(leftSizeButton.getStyleClass().contains("btn_fold")){
                foldLeft();
            } else {
                expandLeft();
            }
        });

        rightSizeButton.setOnMouseClicked(event -> {
            if (rightSizeButton.getStyleClass().get(0) == null){
                return;
            } else if(rightSizeButton.getStyleClass().contains("right_btn_fold")){
                foldRight();
            } else {
                expandRight();
            }
        });

        variantsController.getDetailContents().setCenter(root);
    }

    private void expandLeft() {
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(this.leftExpandedWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightStandardWidth);
            overviewAccordion.setPrefWidth(this.standardAccordionSize);
            variantListTableView.setPrefWidth(this.minSize);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFullWidth);
            overviewAccordion.setPrefWidth(this.minSize);
            variantListTableView.setPrefWidth(this.standardTableSize);
        }
        filterArea.setPrefWidth(200);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_fold");
    }
    private void foldLeft(){
        snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.leftFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFullWidth);
            overviewAccordion.setPrefWidth(this.maxAccordionSize);
            variantListTableView.setPrefWidth(this.minSize);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerStandardWidth);
            overviewAccordion.setPrefWidth(this.minSize);
            variantListTableView.setPrefWidth(this.maxTableSize);
        }
        filterArea.setPrefWidth(0);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_expand");
    }




    private void expandRight() {
        snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(0).getPrefWidth() == 250) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightStandardWidth);

            variantListTableView.setPrefWidth(this.minSize);
        } else {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFullWidth);
        }
        overviewAccordion.setPrefWidth(this.minSize);
        rightSizeButton.getStyleClass().clear();
        rightSizeButton.getStyleClass().add("right_btn_fold");
    }
    private void foldRight(){
        snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(0).getPrefWidth() == 250) {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerStandardWidth);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFullWidth);
        }
        overviewAccordion.setPrefWidth(this.standardAccordionSize);
        rightSizeButton.getStyleClass().clear();
        rightSizeButton.getStyleClass().add("right_btn_expand");
    }
}
