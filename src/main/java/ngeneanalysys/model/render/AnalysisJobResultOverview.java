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
	 * Past Results > list > Result Overview 컬럼 화면 출력
	 * @param sample
	 * @return
	 */
	@SuppressWarnings("static-access")
	public VBox getResultOverview(Sample sample) {
		VBox vBox = new VBox();
//		vBox.setId("result_overview");
//
//		if(sample != null && sample.getAnalysisResultSummary() != null) {
//			AnalysisResultSummary summary = sample.getAnalysisResultSummary();
//
//			GridPane gridPane = new GridPane();
//			ColumnConstraints col1 = new ColumnConstraints();
//			col1.setPercentWidth(30);
//			ColumnConstraints col2 = new ColumnConstraints();
//			col2.setPercentWidth(34);
//			ColumnConstraints col3 = new ColumnConstraints();
//			col3.setPercentWidth(36);
//			gridPane.getColumnConstraints().addAll(col1, col2, col3);
//
//			// gene count
//			String genes = (summary.getGenes() != null) ? String.valueOf(summary.getGenes()) : "-";
//			HBox genesHBox = getCountInfo("GENES : ", genes);
//			gridPane.add(genesHBox, 0, 0);
//
//			// min depth count
//			HBox depthMinHBox = getCountInfo("DEPTH MIN : ", summary.getDepthMin());
//			gridPane.add(depthMinHBox, 1, 0);
//
//			// max depth count
//			HBox depthMaxHBox = getCountInfo("DEPTH MAX : ", summary.getDepthMax());
//			gridPane.add(depthMaxHBox, 2, 0);
//
//			// total variant count
//			HBox variantsHBox = getCountInfo("VARIANTS : ", StringUtils.defaultIfEmpty(summary.getTotalVariants(), "0"));
//			gridPane.add(variantsHBox, 0, 1);
//
//			// warnings count
//			HBox warnHBox = getCountInfo("WARNING : ", StringUtils.defaultIfEmpty(summary.getWarning(), "0"));
//			gridPane.add(warnHBox, 1, 1);
//
//			// qc flag box
//			HBox qcFlagHbox = new HBox();
//			qcFlagHbox.getStyleClass().add("alignment_center_left");
//
//			Button absoluteCoverageButton = getQCIcon(summary.getRoiCoverageMessage(),
//					summary.getRoiCoveragePercentage(), "ROI Coverage",
//					"Percentage of ROI region\nwith coverage of least 20X (\u2265 100%)");
//			Button meanReadQualityButton = getQCIcon(summary.getMeanReadQualityMessage(),
//					summary.getMeanReadQualityPercentage(), "Mean Read Quality",
//					"Percentage of reads\nwith mean Phred base quality above 30 (\u2265 90%)");
//			Button retainedReadsButton = getQCIcon(summary.getRetainedReadsMessage(),
//					summary.getRetainedReadsPercentage(), "Retained Reads", "Percentage of QC passed reads (\u2265 80%)");
//			Button ampliconCoverageButton = getQCIcon(summary.getCoverageUniformityMessage(),
//					summary.getCoverageUniformityPercentage(), "Coverage Uniformity",
//					"Percentage of bases\ncovered at \u2265 20% of the mean coverage");
//
//			qcFlagHbox.getChildren().addAll(absoluteCoverageButton, meanReadQualityButton, retainedReadsButton, ampliconCoverageButton);
//			qcFlagHbox.setMargin(meanReadQualityButton, new Insets(0, 0, 0, 5));
//			qcFlagHbox.setMargin(retainedReadsButton, new Insets(0, 0, 0, 5));
//			qcFlagHbox.setMargin(ampliconCoverageButton, new Insets(0, 0, 0, 5));
//
//			if(sample.getJobStatus() != null && !StringUtils.isEmpty(sample.getJobStatus().getStepReport())) {
//				Label reportLabel = new Label();
//				// 보고서 작성 완료 상태인 경우
//				if(AnalysisJobStatusCode.SAMPLE_JOB_STATUS_COMPLETE.equals(sample.getJobStatus().getStepReport())) {
//					reportLabel.setText("REPORTED");
//					reportLabel.setId("jobStatus_sm_COMPLETE");
//				} else if(AnalysisJobStatusCode.SAMPLE_JOB_STATUS_RUNNING.equals(sample.getJobStatus().getStepReport())) {
//					reportLabel.setText("REVIEWING");
//					reportLabel.setId("jobStatus_sm_RUNNING");
//				}
//				qcFlagHbox.getChildren().add(reportLabel);
//				qcFlagHbox.setMargin(reportLabel, new Insets(0, 0, 0, 5));
//			}
//
//			vBox.getChildren().addAll(gridPane, qcFlagHbox);
//			vBox.setMargin(qcFlagHbox, new Insets(5, 0, 0, 0));
//		} else {
//			Label emptyLabel = new Label("empty result overview data.");
//			emptyLabel.getStyleClass().add("txt_gray");
//			vBox.getChildren().add(emptyLabel);
//		}
		return vBox;
	}
	
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
