package ngeneanalysys.model.render;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver;


/**
 * @author Jang
 * @since 2018-04-09
 */
public class LowConfidenceList {

    public static Button getLowConfidencePopOver(String lowConfidence) {
        Button button = new Button();
        button.getStyleClass().add("btn_warn");

        VBox box = new VBox();
        box.setStyle("-fx-padding:10;");
        if(StringUtils.isEmpty(lowConfidence) || "NONE".equals(lowConfidence)) {
            box.setAlignment(Pos.CENTER);
            Label label = new Label("< Empty Warning Reason >");
            box.getChildren().add(label);
        } else {
            box.setAlignment(Pos.BOTTOM_LEFT);

            HBox hbox = getLowConfidenceItemBox(lowConfidence);
            box.getChildren().add(hbox);

        }

        button.setOnAction(event -> {
            PopOver popOver = new PopOver();
            popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
            popOver.setHeaderAlwaysVisible(true);
            popOver.setAutoHide(true);
            popOver.setAutoFix(true);
            popOver.setDetachable(true);
            popOver.setArrowSize(15);
            popOver.setArrowIndent(30);
            popOver.setContentNode(box);
            popOver.show(button);
        });

        return button;
    }

    /**
     * Warning 상세 이유 개별 항목 HBOX 객체 반환
     * @param title String
     * @return HBox
     */
    private static HBox getLowConfidenceItemBox(String title) {
        HBox hBox = new HBox();
        hBox.getChildren().add(new Label(title.toUpperCase().replaceAll("\\|", ", ")));
        return hBox;
    }
}
