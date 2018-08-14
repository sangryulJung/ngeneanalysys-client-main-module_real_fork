package ngeneanalysys.model.render;

import java.util.Map;
import java.util.Set;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.model.Panel;
import ngeneanalysys.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
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
			/*box.setAlignment(Pos.BOTTOM_LEFT);
			Map<String,String> map = JsonUtil.fromJsonToMap(jsonStr.replace("'", "\""));
			Set<Map.Entry<String, String>> mapKey = map.entrySet();
			int currentIndex = 1;
			for(Map.Entry<String, String> entry : mapKey) {
				//if(key.toUpperCase().equals("WARNING")) continue;

				if (panel.getName().equals(CommonConstants.TST_170_DNA) ||
						entry.getKey().equalsIgnoreCase("low_variant_coverage_depth") ||
						entry.getKey().equalsIgnoreCase("low_variant_fraction") ||
						entry.getKey().equalsIgnoreCase("homopolymer_region") ||
						entry.getKey().equalsIgnoreCase("soft_clipped_amplicon") ||
						entry.getKey().equalsIgnoreCase("primer_deletion") ||
						entry.getKey().equalsIgnoreCase("low_read_depth") ||
						entry.getKey().equalsIgnoreCase("low_allele_fraction") ||
						entry.getKey().equalsIgnoreCase("low_confidence")) {

					String warningString = map.getOrDefault(entry.getKey(), "N/A");
					String titleString = "* " + WordUtils.capitalize(entry.getKey().replaceAll("_", " ")) + " : ";
					HBox hbox = getWarningReasonItemBox(titleString, warningString, panel);
					box.getChildren().add(hbox);
					if (mapKey.size() > currentIndex) box.setMargin(hbox, new Insets(5, 0, 0, 0));
					currentIndex++;
				}
			}*/
			box.setAlignment(Pos.BOTTOM_LEFT);
			String[] list = jsonStr.split(",");
			int currentIndex = 0;
			for(String item : list) {
				currentIndex++;
				if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode()) ||
						item.contains("low_variant_coverage_depth") ||
						item.contains("low_variant_fraction") ||
						item.contains("homopolymer_region") ||
						item.contains("soft_clipped_amplicon") ||
						item.contains("primer_deletion") ||
						item.contains("low_read_depth") ||
						item.contains("low_allele_fraction") ||
						item.contains("low_confidence")) {
					String[] splitItem = item.split(":");

					String titleString = "* " + WordUtils.capitalize(splitItem[0].replaceAll("_", " ")) + " : ";
					HBox hbox = getWarningReasonItemBox(titleString, splitItem[1], panel);
					box.getChildren().add(hbox);

					if (list.length > currentIndex) box.setMargin(hbox, new Insets(5, 0, 0, 0));

				}
			}

			/*if(map != null && !map.isEmpty() && map.size() > 0) {
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
			}*/
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
		if(StringUtils.isNotEmpty(flag) && "YES".equals(flag.toUpperCase())) {
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
