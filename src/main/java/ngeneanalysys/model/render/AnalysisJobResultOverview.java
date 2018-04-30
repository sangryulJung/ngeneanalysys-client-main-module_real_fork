package ngeneanalysys.model.render;

import ngeneanalysys.model.Sample;
//import org.apache.commons.lang3.StringUtils;
//import org.controlsfx.control.PopOver;
//import org.controlsfx.control.PopOver.ArrowLocation;
//
//import com.ngenebio.app.code.AnalysisJobStatusCode;
//import com.ngenebio.app.model.AnalysisResultSummary;
//import com.ngenebio.app.model.AnalysisSample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Past Results > 샘플 목록 > Result Overview Area 화면 출력 클래스
 * @author gjyoo
 * @since 2016.06.17
 *
 */
public class AnalysisJobResultOverview {

	/**
	 * 집계 정보 박스 반환
	 * @param title
	 * @param count
	 * @return
	 */
	@SuppressWarnings("static-access")
	public HBox getCountInfo(String title, String count) {
		HBox hBox = new HBox();
		Label titleLabel = new Label(title);
		titleLabel.getStyleClass().add("font_size_11");
		titleLabel.getStyleClass().add("txt_gray");
		Label countLabel = new Label(count);
		countLabel.getStyleClass().add("txt_black");
		countLabel.getStyleClass().add("weight_bold");
		countLabel.getStyleClass().add("font_size_11");
		hBox.getChildren().addAll(titleLabel, countLabel);
		hBox.setMargin(countLabel, new Insets(0, 15, 0, 0));
		return hBox;
	}
	
	/**
	 * QC 결과 아이콘 버튼 객체 반환
	 * @param flag
	 * @param percentage
	 * @param title
	 * @param contents
	 * @return
	 */
	@SuppressWarnings("static-access")
	public Button getQCIcon(String flag, String percentage, String title, String contents) {
//		if(!StringUtils.isEmpty(flag) && !StringUtils.isEmpty(percentage)) {
//			Button button = new Button();
//			if("PASS".equals(flag.toUpperCase())) {
//				button.getStyleClass().add("bullet_green");
//			} else {
//				button.getStyleClass().add("bullet_red");
//			}
//
//			button.setOnAction(event -> {
//				PopOver popOver = new PopOver();
//				popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
//				popOver.setHeaderAlwaysVisible(true);
//				popOver.setAutoHide(true);
//				popOver.setAutoFix(true);
//				popOver.setDetachable(true);
//				popOver.setArrowSize(15);
//				popOver.setArrowIndent(30);
//
//				VBox box = new VBox();
//				box.setStyle("-fx-padding:10;");
//				box.setAlignment(Pos.BOTTOM_RIGHT);
//				Label titleLabel = new Label(title);
//				titleLabel.getStyleClass().add("font_size_12");
//				titleLabel.getStyleClass().add("weight_bold");
//				titleLabel.getStyleClass().add("txt_gray_656565");
//				Label percentageLabel = new Label(String.format("%.2f", Double.parseDouble(percentage)));
//				percentageLabel.getStyleClass().add("font_size_14");
//				percentageLabel.getStyleClass().add("weight_bold");
//				percentageLabel.getStyleClass().add("txt_black");
//				Label contentsLabel = new Label(contents);
//				contentsLabel.getStyleClass().add("font_size_11");
//				contentsLabel.getStyleClass().add("txt_gray_656565");
//				box.getChildren().addAll(titleLabel, percentageLabel, contentsLabel);
//				box.setMargin(percentageLabel, new Insets(5, 0, 0, 0));
//				box.setMargin(contentsLabel, new Insets(5, 0, 0, 0));
//
//				popOver.setContentNode(box);
//				popOver.show((Node) button);
//			});
//			return button;
//		}
		return null;
	}
}
