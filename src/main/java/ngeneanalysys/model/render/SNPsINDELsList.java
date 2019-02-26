package ngeneanalysys.model.render;

import javafx.scene.input.MouseEvent;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.model.Panel;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * SNPs-INDELs 목록 출력 관련 객체 반환 클래스
 * 
 * @author gjyoo
 * @since 2016. 6. 17. 오후 11:14:41
 */
public class SNPsINDELsList {

	private SNPsINDELsList() {}
	/**
	 * Warning 상세 이유 팝업 출력 버튼 객체 반환
	 * @param jsonStr String
	 * @return Button
	 */
	@SuppressWarnings("static-access")
	public static Button getWarningReasonPopOver(String jsonStr, Panel panel) {
		Button button = new Button();
		button.getStyleClass().add("btn_warn");
		
		VBox box = new VBox();
		box.setStyle("-fx-padding:10;");
		if(StringUtils.isEmpty(jsonStr) || "NONE".equals(jsonStr)) {
			box.setAlignment(Pos.CENTER);
			Label label = new Label("< Empty Warning Reason >");
			box.getChildren().add(label);
		} else {
			box.setAlignment(Pos.BOTTOM_LEFT);
			String[] list = jsonStr.split(",");
			int currentIndex = 0;
			for(String item : list) {
				currentIndex++;
				HBox hbox = getWarningReasonItemBox(item, "NONE" , panel);
				box.getChildren().add(hbox);

				if (list.length > currentIndex) box.setMargin(hbox, new Insets(5, 0, 0, 0));
			}
		}
		
		button.setOnAction(event -> {
			PopOver popOver = new PopOver();
			popOver.setArrowLocation(ArrowLocation.LEFT_TOP);
			popOver.setHeaderAlwaysVisible(true);
			popOver.setAutoHide(true);
			popOver.setAutoFix(true);
			popOver.setDetachable(true);
			popOver.setArrowSize(15);
			popOver.setArrowIndent(30);
			popOver.setContentNode(box);
			popOver.show(button);
			popOver.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> popOver.hide());
		});
		
		return button;
	}
	
	/**
	 * Warning 상세 이유 개별 항목 HBOX 객체 반환
	 * @param title String
	 * @param flag String
	 * @return HBox
	 */
	private static HBox getWarningReasonItemBox(String title, String flag, Panel panel) {
		HBox hBox = new HBox();
		Label flagLabel = new Label();
		if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
			flagLabel.setText(flag);
		} else {
			flagLabel.setText(flag.toUpperCase());
		}
		if(StringUtils.isNotEmpty(flag) && "YES".equalsIgnoreCase(flag)) {
			flagLabel.getStyleClass().add("txt_green");
		} else {
			flagLabel.getStyleClass().add("txt_red");
		}
		flagLabel.getStyleClass().add("weight_bold");
		hBox.getChildren().add(new Label(title.replace(":", " : ")));
		//hBox.getChildren().add(flagLabel);
		return hBox;
	}
}
