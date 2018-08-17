package ngeneanalysys.util;

import java.io.File;
import java.util.*;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import ngeneanalysys.MainApp;
import ngeneanalysys.controller.WorkProgressController;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.task.ExportInterpretationDataTask;
import ngeneanalysys.task.ExportVariantDataTask;
import org.slf4j.Logger;

/**
 * 워크 시트 유틸 클래스
 * @author gjyoo
 * @since 2016.06.15
 *
 */
public class WorksheetUtil {
	private static Logger logger = LoggerUtil.getLogger();

	public void exportInterpretation(String fileType, Map<String, Object> params, MainApp mainApp){
		try {
			// Show save file dialog
			FileChooser fileChooser = new FileChooser();
			if ("EXCEL".equals(fileType)) {
				fileChooser.getExtensionFilters()
						.addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx)", "*.xlsx"));
				params.put("dataType", fileType);
			} else {
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TSV (*.tsv)", "*.tsv"));
				params.put("dataType", fileType);
			}
			fileChooser.setTitle("export variants to " + fileType + " format file");
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
			if (file != null) {
				Task<Void> task = new ExportInterpretationDataTask(mainApp, fileType, file, params);
				Thread exportDataThread = new Thread(task);
				WorkProgressController<Void> workProgressController = new WorkProgressController<>(mainApp, "Export variant List", task);
				FXMLLoader loader = mainApp.load("/layout/fxml/WorkProgress.fxml");
				loader.setController(workProgressController);
				Node root = loader.load();
				workProgressController.show((Parent) root);
				exportDataThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.error("Save Fail.",
					"An error occurred during the creation of the " + fileType + " document."
							+ e.getMessage(),
					mainApp.getPrimaryStage(), false);
		}
	}

	public void exportVariantData(String fileType, Map<String, Object> params, MainApp mainApp, SampleView sample){
		try {
			// Show save file dialog
			FileChooser fileChooser = new FileChooser();
			if ("EXCEL".equals(fileType)) {
				fileChooser.getExtensionFilters()
						.addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx)", "*.xlsx"));
				fileChooser.setInitialFileName(sample.getName());
				params.put("exportDataType", fileType);
			} else {
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
				fileChooser.setInitialFileName(sample.getName());
				params.put("exportDataType", fileType);
			}
			fileChooser.setTitle("export variants to " + fileType + " format file");
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
			if (file != null) {
				Task<Void> task = new ExportVariantDataTask(mainApp, fileType, file, params, sample.getId());
				Thread exportDataThread = new Thread(task);
				WorkProgressController<Void> workProgressController = new WorkProgressController<>(mainApp, "Export variant List", task);
				FXMLLoader loader = mainApp.load("/layout/fxml/WorkProgress.fxml");
				loader.setController(workProgressController);
				Node root = loader.load();
				workProgressController.show((Parent) root);
				exportDataThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.error("Save Fail.",
					"An error occurred during the creation of the " + fileType + " document."
							+ e.getMessage(),
					mainApp.getPrimaryStage(), false);
		}
	}

	public void exportVariantData(Map<String, List<Object>> searchParams, Map<String, Object> params, MainApp mainApp){
		try {
			// Show save file dialog
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters()
					.addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx)", "*.xlsx"));

			fileChooser.setTitle("export variants to EXCEL format file");
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
			if (file != null) {
				Task<Void> task = new ExportVariantDataTask(mainApp, file, searchParams, params);
				Thread exportDataThread = new Thread(task);
				WorkProgressController<Void> workProgressController = new WorkProgressController<>(mainApp, "Export variant List", task);
				FXMLLoader loader = mainApp.load("/layout/fxml/WorkProgress.fxml");
				loader.setController(workProgressController);
				Node root = loader.load();
				workProgressController.show((Parent) root);
				exportDataThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.error("Save Fail.",
					"An error occurred during the creation of the EXCEL document."
							+ e.getMessage(),
					mainApp.getPrimaryStage(), false);
		}
	}
}
