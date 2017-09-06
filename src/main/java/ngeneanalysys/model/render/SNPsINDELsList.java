package ngeneanalysys.model.render;

import java.util.Map;

import ngeneanalysys.util.JsonUtil;
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

	/**
	 * Warning 상세 이유 팝업 출력 버튼 객체 반환
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Button getWarningReasonPopOver(String jsonStr) {
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
			Map<String,String> map = JsonUtil.fromJsonToMap(jsonStr.replace("'", "\""));
			if(map != null && !map.isEmpty() && map.size() > 0) {
				String lowVariantCoverageDepth = map.get("low_variant_coverage_depth");
				String lowVariantFraction = map.get("low_variant_fraction");				
				String homopolymerRegion = map.get("homopolymer_region");
				String softClippedRegion = map.get("soft_clipped_amplicon");
				String primerDeletionRegion = map.get("primer_deletion");
				
				HBox lowVariantCoverageDepthBox = getWarningReasonItemBox("* Low Variant Coverage Depth : ", lowVariantCoverageDepth == null ? "N/A" : lowVariantCoverageDepth );
				HBox lowVariantFractionBox = getWarningReasonItemBox("* Low Variant Fraction : ", lowVariantFraction == null ? "N/A" : lowVariantFraction);
				HBox homopolymerRegionBox = getWarningReasonItemBox("* Homopolymer Region : ", homopolymerRegion == null ? "N/A" : homopolymerRegion);
				HBox softClippedRegionBox = getWarningReasonItemBox("* Soft Clipped Region : ", softClippedRegion == null ? "N/A" : softClippedRegion);				
				HBox primerDeletionRegionBox = getWarningReasonItemBox("* Primer Deletion Region : ", primerDeletionRegion == null ? "N/A" : primerDeletionRegion);
				
				box.getChildren().add(lowVariantCoverageDepthBox);
				box.getChildren().add(lowVariantFractionBox);
				box.getChildren().add(homopolymerRegionBox);
				box.getChildren().add(softClippedRegionBox);
				box.getChildren().add(primerDeletionRegionBox);
				box.setMargin(lowVariantFractionBox, new Insets(5, 0, 0, 0));
				box.setMargin(softClippedRegionBox, new Insets(5, 0, 0, 0));
				box.setMargin(homopolymerRegionBox, new Insets(5, 0, 0, 0));
				box.setMargin(primerDeletionRegionBox, new Insets(5, 0, 0, 0));
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
		});
		
		return button;
	}
	
	/**
	 * Warning 상세 이유 개별 항목 HBOX 객체 반환
	 * @param title
	 * @param flag
	 * @return
	 */
	public static HBox getWarningReasonItemBox(String title, String flag) {
		HBox hBox = new HBox();
		Label flagLabel = new Label(flag.toUpperCase());
		if(!StringUtils.isEmpty(flag) && "YES".equals(flag.toUpperCase())) {
			flagLabel.getStyleClass().add("txt_green");
		} else {
			flagLabel.getStyleClass().add("txt_red");
		}
		flagLabel.getStyleClass().add("weight_bold");
		hBox.getChildren().add(new Label(title));
		hBox.getChildren().add(flagLabel);
		return hBox;
	}
}
