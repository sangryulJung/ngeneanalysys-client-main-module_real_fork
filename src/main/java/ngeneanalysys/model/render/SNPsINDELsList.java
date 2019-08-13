package ngeneanalysys.model.render;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;

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

	public static void showItem(final String text, final TableCell<VariantAndInterpretationEvidence, String> tableCell) {
		PopOver popOver = new PopOver();
		popOver.setArrowLocation(ArrowLocation.LEFT_TOP);
		popOver.setHeaderAlwaysVisible(true);
		popOver.setAutoHide(true);
		popOver.setAutoFix(true);
		popOver.setDetachable(true);
		popOver.setArrowSize(15);
		popOver.setArrowIndent(30);
		popOver.setContentNode(getContentsNode(text));
		popOver.show(tableCell);
		popOver.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> popOver.hide());
	}

	private static ScrollPane getContentsNode(final String text) {
		ScrollPane mainContents = new ScrollPane();
		mainContents.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mainContents.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		mainContents.setMinWidth(400);
		mainContents.setPrefWidth(400);
		mainContents.setMaxWidth(400);
		mainContents.setMinHeight(150);
		mainContents.setPrefHeight(150);
		mainContents.setMaxHeight(150);
		VBox vBox = new VBox();
		vBox.setMinWidth(390);
		vBox.setPrefWidth(390);
		vBox.setMaxWidth(390);
		vBox.setMinHeight(Region.USE_COMPUTED_SIZE);
		vBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
		vBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
		mainContents.setContent(vBox);
		vBox.setStyle("-fx-padding:10;");
		String[] cDbs = text.split(";");

		Arrays.stream(cDbs).forEach(cDb -> {
			String[] contents = cDb.split(": ");
			if(contents.length == 2) {
				Label title = new Label(contents[0]);
				title.getStyleClass().addAll("bold", "txt_black");
				Label content = new Label(contents[1]);
				content.getStyleClass().addAll("customDatabaseLabel", "label");
				content.setAlignment(Pos.TOP_LEFT);
				content.setWrapText(true);
				content.setMinWidth(370);
				content.setPrefWidth(370);
				content.setMaxWidth(370);
				content.setMinHeight(20 * Math.ceil(content.getText().length() / 60.));
				//content.setPrefHeight(20 * Math.ceil(content.getText().length() / 60.));
				content.setPrefHeight(Region.USE_COMPUTED_SIZE);
				content.setMaxHeight(Region.USE_COMPUTED_SIZE);

				vBox.getChildren().addAll(title, content);
				VBox.setMargin(content, new Insets(0, 0, 5, 5));
			} else {
				Label cDbLabel = new Label(cDb);
				cDbLabel.getStyleClass().addAll("customDatabaseLabel", "label");
				vBox.getChildren().add(cDbLabel);
				VBox.setMargin(cDbLabel, new Insets(0, 0, 5, 5));
			}
		});

		return mainContents;
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
