package ngeneanalysys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import ngeneanalysys.MainApp;
import ngeneanalysys.controller.WorkProgressController;
import ngeneanalysys.exceptions.ExcelParseException;
import ngeneanalysys.task.ExportVariantDataTask;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import com.opencsv.CSVWriter;

/**
 * 워크 시트 유틸 클래스
 * @author gjyoo
 * @since 2016.06.15
 *
 */
public class WorksheetUtil {
	private static Logger logger = LoggerUtil.getLogger();

	/**
	 *
	 * @param targetFile
	 * @param sheetNm
	 * @param title
	 * @param contentsList
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static boolean createXlsxFile(File targetFile, String sheetNm, String[] title, List<String[]> contentsList) {
		boolean result = true;

		try {
			if (title != null && contentsList != null) {
				Workbook workbook = new XSSFWorkbook();
				Row row = null;
				CellStyle style = null;
				Cell titleCell = null;
				Cell contentsCell = null;
				int rowIdx = 0;

				style = workbook.createCellStyle();
				style.setFillForegroundColor(HSSFColor.YELLOW.index);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				style.setBorderTop(style.BORDER_THIN);
				style.setBorderBottom(style.BORDER_THIN);
				style.setBorderLeft(style.BORDER_THIN);
				style.setBorderRight(style.BORDER_THIN);
				style.setAlignment(XSSFCellStyle.ALIGN_CENTER);

				Sheet sheet = workbook.createSheet(sheetNm);

				// Title Row Create
				if (title != null && title.length > 0) {
					row = sheet.createRow(rowIdx);
					int titleIdx = 0;
					for (String str : title) {
						titleCell = row.createCell(titleIdx);
						titleCell.setCellValue(str);
						titleCell.setCellStyle(style);
						titleIdx++;
					}
					rowIdx++;
				}

				// Contents Row Create
				if (contentsList != null && !contentsList.isEmpty()) {
					// 내용 스타일 지정
					style = workbook.createCellStyle();
					style.setBorderTop(style.BORDER_THIN);
					style.setBorderBottom(style.BORDER_THIN);
					style.setBorderLeft(style.BORDER_THIN);
					style.setBorderRight(style.BORDER_THIN);

					for (String[] arr : contentsList) {
						if (arr != null && arr.length > 0) {
							row = sheet.createRow(rowIdx);
							int contentsIdx = 0;
							for (String string : arr) {
								contentsCell = row.createCell(contentsIdx);
								contentsCell.setCellValue(string);
								contentsCell.setCellStyle(style);
								contentsIdx++;
							}
							rowIdx++;
						}
					}
				}

				// 디렉토리 생성
				if (!targetFile.getParentFile().exists()) {
					logger.debug("file parent directory create!");
					targetFile.getParentFile().mkdirs();
				}

				FileOutputStream fileOutput = new FileOutputStream(targetFile);
				workbook.write(fileOutput);
				fileOutput.close();

				logger.debug("File >>> " + targetFile.getAbsolutePath());
				logger.debug("Excel File Create Success!!");
			} else {
				result = false;
				logger.error("Empty Contents.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	/**
	 * CSV 파일 생성
	 * @param targetFile
	 * @param title
	 * @param contentsList
	 * @return
	 */
	public static boolean createCSVFile(File targetFile, String[] title, List<String[]> contentsList) {
		boolean result = true;
		if (title != null && contentsList != null) {
			try (CSVWriter writer = new CSVWriter(new FileWriter(targetFile), ',')){
				// title row write
				writer.writeNext(title);

				// contents row write.
				for (String[] row : contentsList) {
					writer.writeNext(row);
				}

				logger.debug("File >>> " + targetFile.getAbsolutePath());
				logger.debug("CSV File Create Success!!");
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
		} else {
			result = false;
			logger.error("Empty Contents.");
		}
		return result;
	}

	public void exportVariantData(String fileType, Map<String, Object> params, MainApp mainApp, int sampleId){
		try {
			// Show save file dialog
			FileChooser fileChooser = new FileChooser();
			if ("EXCEL".equals(fileType)) {
				fileChooser.getExtensionFilters()
						.addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx)", "*.xlsx"));
				params.put("dataType", fileType);
			} else {
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
				params.put("dataType", fileType);
			}
			fileChooser.setTitle("export variants to " + fileType + " format file");
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
			if (file != null) {
				Task<Void> task = new ExportVariantDataTask(mainApp, fileType, file, params, sampleId);
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
}
