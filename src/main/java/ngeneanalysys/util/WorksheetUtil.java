package ngeneanalysys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import ngeneanalysys.exceptions.ExcelParseException;
import ngeneanalysys.model.QcData;
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
				if (contentsList != null && contentsList.size() > 0) {
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
					logger.info("file parent directory create!");
					targetFile.getParentFile().mkdirs();
				}

				FileOutputStream fileOutput = new FileOutputStream(targetFile);
				workbook.write(fileOutput);
				fileOutput.close();

				logger.info("File >>> " + targetFile.getAbsolutePath());
				logger.info("Excel File Create Success!!");
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

				logger.info("File >>> " + targetFile.getAbsolutePath());
				logger.info("CSV File Create Success!!");
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

	public static Map<String, QcData> readQCDataExcelSheet(File file) throws IOException, ExcelParseException {
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
		XSSFSheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		String errorMessagePrefix = "\nPlease refer to the software manual.";
		String errorMessage = null;
		Map<String, QcData> list = new TreeMap<>();
		//First Header Row Check
		boolean isMatchCode = false;
		if(lastRowNum <= 0) {
			errorMessage = "Empty Data";
		} else {
			XSSFRow firstRow = sheet.getRow(0);
			if(firstRow.getCell(0) == null || !"Date".equals(firstRow.getCell(0).toString())) {
				errorMessage = "A Column first row must be 'Date'";
			}
			if(firstRow.getCell(1) == null || !"Patient ID".equals(firstRow.getCell(1).toString())) {
				errorMessage = "B Column first row must be 'Patient ID'";
			}
			if(firstRow.getCell(2) == null || !"Name".equals(firstRow.getCell(2).toString())) {
				errorMessage = "C Column first row must be 'Name'";
			}
			if(firstRow.getCell(3) == null || !"Gender".equals(firstRow.getCell(3).toString())) {
				errorMessage = "D Column first row must be 'Gender'";
			}
			if(firstRow.getCell(4) == null || !"Panel".equals(firstRow.getCell(4).toString())) {
				errorMessage = "E Column first row must be 'Panel'";
			}
			if(firstRow.getCell(5) == null || !"Disease".equals(firstRow.getCell(5).toString())) {
				errorMessage = "F Column first row must be 'Disease'";
			}
			if(firstRow.getCell(6) == null || !"DNA QC".equals(firstRow.getCell(6).toString())) {
				errorMessage = "G Column first row must be 'DNA QC'";
			}
			if(firstRow.getCell(7) == null || !"Library QC".equals(firstRow.getCell(7).toString())) {
				errorMessage = "H Column first row must be 'Library QC'";
			}
			if(firstRow.getCell(8) == null || !"Seq. Cluster Density".equals(firstRow.getCell(8).toString())) {
				errorMessage = "I Column first row must be 'Seq. Cluster Density'";
			}
			if(firstRow.getCell(9) == null || !"Seq. >=Q30".equals(firstRow.getCell(9).toString())) {
				errorMessage = "J Column first row must be 'Seq. >=Q30'";
			}
			if(firstRow.getCell(10) == null || !"Seq. Cluster PF".equals(firstRow.getCell(10).toString())) {
				errorMessage = "K Column first row must be 'Seq. Cluster PF'";
			}
			if(firstRow.getCell(11) == null || !"Seq. Indexing PF CV".equals(firstRow.getCell(11).toString())) {
				errorMessage = "L Column first row must be 'Seq. Indexing PF CV'";
			}
		}
		if(errorMessage != null) {
			logger.error(errorMessage);
			throw new ExcelParseException(errorMessage + errorMessagePrefix);
		}

		for(int i = 1 ; i <= lastRowNum ; i++) {
			XSSFRow row = sheet.getRow(i);
			QcData qcData = new QcData();

			if(row.getCell(0).toString().equals("")) {
				break;
			}

			if(!("Pass".equals(row.getCell(6).toString()) || "Fail".equals(row.getCell(6).toString()))) {
				errorMessage = "Invalid : " + row.getCell(6).toString();
			}
			qcData.setDnaQC(row.getCell(6).toString());

			if(!("Pass".equals(row.getCell(7).toString()) || "Fail".equals(row.getCell(7).toString()))) {
				errorMessage = "Invalid : " + row.getCell(7).toString();
			}
			qcData.setLibraryQC(row.getCell(7).toString());

			if(!("Pass".equals(row.getCell(8).toString()) || "Fail".equals(row.getCell(8).toString()))) {
				errorMessage = "Invalid : " + row.getCell(8).toString();
			}
			qcData.setSeqClusterDensity(row.getCell(8).toString());

			if(!("Pass".equals(row.getCell(9).toString()) || "Fail".equals(row.getCell(9).toString()))) {
				errorMessage = "Invalid : " + row.getCell(9).toString();
			}
			qcData.setSeqQ30(row.getCell(9).toString());

			if(!("Pass".equals(row.getCell(10).toString()) || "Fail".equals(row.getCell(10).toString()))) {
				errorMessage = "Invalid : " + row.getCell(10).toString();
			}
			qcData.setSeqClusterPF(row.getCell(10).toString());

			if(!("Pass".equals(row.getCell(11).toString()) || "Fail".equals(row.getCell(11).toString()))) {
				errorMessage = "Invalid : " + row.getCell(11).toString();
			}
			qcData.setSeqIndexingPFCV(row.getCell(11).toString());

			if(list.get(row.getCell(1).toString()) != null) {
				errorMessage = "???";
			}

			list.put(row.getCell(2).toString(), qcData);
		}

		if(errorMessage != null) {
			logger.error(errorMessage);
			throw new ExcelParseException(errorMessage + errorMessagePrefix);
		}

		return list;
	}


	/*
	public List<Patient> readPatientExcelSheet(String fileName) throws FileNotFoundException, IOException, ExcelParseException {
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
		XSSFSheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		String errorMessagePrefix = "\nPlease refer to the software manual.";
		String errorMessage = null;
		List<Patient> patients = new ArrayList<Patient>();
		//Firt Header Row Check
		boolean isMatchCode = false;
		if (lastRowNum <= 0){
			errorMessage = "Empty Data";
		} else {
			XSSFRow firtRow = sheet.getRow(0);
			if(firtRow.getCell(0) == null || !"FirstName".equals(firtRow.getCell(0).toString())) {
				errorMessage = "A Column first row must be 'FirstName'";
			}
			if(firtRow.getCell(1) == null || !"LastName".equals(firtRow.getCell(1).toString())) {
				errorMessage = "B Column first row must be 'LastName'";
			}
			if(firtRow.getCell(2) == null || !"Birthday".equals(firtRow.getCell(2).toString())) {
				errorMessage = "C Column first row must be 'Birthday'";
			}
			if(firtRow.getCell(3) == null || !"Gender".equals(firtRow.getCell(3).toString())) {
				errorMessage = "D Column first row must be 'Gender'";
			}
			if(firtRow.getCell(4) == null || !"PatientID".equals(firtRow.getCell(4).toString())) {
				errorMessage = "E Column first row must be 'PatientID'";
			}
			if(firtRow.getCell(5) == null || !"Email".equals(firtRow.getCell(5).toString())) {
				errorMessage = "F Column first row must be 'Email'";
			}
			if(firtRow.getCell(6) == null || !"Phone".equals(firtRow.getCell(6).toString())) {
				errorMessage = "G Column first row must be 'Phone'";
			}
			if(firtRow.getCell(7) == null || !"Mobile".equals(firtRow.getCell(7).toString())) {
				errorMessage = "H Column first row must be 'Mobile'";
			}
			if(firtRow.getCell(8) == null || !"Street".equals(firtRow.getCell(8).toString())) {
				errorMessage = "I Column first row must be 'Street'";
			}
			if(firtRow.getCell(9) == null || !"City".equals(firtRow.getCell(9).toString())) {
				errorMessage = "J Column first row must be 'City'";
			}
			if(firtRow.getCell(10) == null || !"PostCode".equals(firtRow.getCell(10).toString())) {
				errorMessage = "K Column first row must be 'PostCode'";
			}
			if(firtRow.getCell(11) == null || !"Country".equals(firtRow.getCell(11).toString())) {
				errorMessage = "L Column first row must be 'Country'";
			}
			if(firtRow.getCell(12) == null || !"EthnicOrigin".equals(firtRow.getCell(12).toString())) {
				errorMessage = "M Column first row must be 'EthnicOrigin'";
			}
			if(firtRow.getCell(13) == null || !"Height".equals(firtRow.getCell(13).toString())) {
				errorMessage = "N Column first row must be 'Height'";
			}
			if(firtRow.getCell(14) == null || !"Weight".equals(firtRow.getCell(14).toString())) {
				errorMessage = "O Column first row must be 'Weight'";
			}
			if(firtRow.getCell(15) == null || !"ReasonForReferral".equals(firtRow.getCell(15).toString())) {
				errorMessage = "O Column first row must be 'ReasonForReferral'";
			}
			if(firtRow.getCell(16) == null || !"MedicalHistory".equals(firtRow.getCell(16).toString())) {
				errorMessage = "Q Column first row must be 'MedicalHistory'";
			}
			if(firtRow.getCell(17) == null || !"Medication".equals(firtRow.getCell(17).toString())) {
				errorMessage = "R Column first row must be 'Medication'";
			}
			if(firtRow.getCell(18) == null || !"MotherEthnicOrigin".equals(firtRow.getCell(18).toString())) {
				errorMessage = "S Column first row must be 'MotherEthnicOrigin'";
			}
			if(firtRow.getCell(19) == null || !"MotherMedialHistory".equals(firtRow.getCell(19).toString())) {
				errorMessage = "T Column first row must be 'MotherMedialHistory'";
			}
			if(firtRow.getCell(20) == null || !"FatherEthnicOrigin".equals(firtRow.getCell(20).toString())) {
				errorMessage = "U Column first row must be 'FatherEthnicOrigin'";
			}
			if(firtRow.getCell(21) == null || !"FatherMedicalHistory".equals(firtRow.getCell(21).toString())) {
				errorMessage = "V Column first row must be 'FatherMedicalHistory'";
			}	
		}
		if (errorMessage != null){
			logger.error(errorMessage);
			throw new ExcelParseException(errorMessage + errorMessagePrefix);
		}
		for(int i = 1; i < lastRowNum + 1; i++){
			XSSFRow row = sheet.getRow(i);
			Patient patient = new Patient();			
			// First Name
			if(row.getCell(0).toString().length() > 64) {
				errorMessage = "Invalid First Name : " + row.getCell(0).toString();
				break;
			}
			patient.setFirstName(row.getCell(0).toString());
			// Last Name
			if(row.getCell(1).toString().length() > 64) {
				errorMessage = "Invalid Last Name : " + row.getCell(1).toString();
				break;
			}
			patient.setLastName(row.getCell(1).toString());
			// Birthday
			try {
				SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");
				patient.setBirthday(birthdayFormat.format(row.getCell(2).getDateCellValue()));
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = "Invalid Birthday : " + row.getCell(2).toString();
				break;
			}
			// Gender
			if(!"M".equals(row.getCell(3).toString()) && !"F".equals(row.getCell(3).toString())){
				errorMessage = "Invalid Gender : " + row.getCell(3).toString();
				break;
			}
			patient.setGender(row.getCell(3).toString());
			// PatientID
			if(row.getCell(4).toString().length() > 32) {
				errorMessage = "Invalid PatientID : " + row.getCell(4).toString();
				break;
			}
			patient.setPatientId(row.getCell(4).toString());
			
			if(row.getCell(5).toString().length() > 150) {
				errorMessage = "Invalid Email : " + row.getCell(5).toString();
				break;
			}
			patient.setEmail(row.getCell(5).toString());
			// Email
			if(row.getCell(6).toString().length() > 50) {
				errorMessage = "Invalid Phone : " + row.getCell(6).toString();
				break;
			}
			patient.setPhone(row.getCell(6).toString());
			// Mobile
			if(row.getCell(7).toString().length() > 50) {
				errorMessage = "Invalid Mobile : " + row.getCell(7).toString();
				break;
			}
			patient.setMobile(row.getCell(7).toString());
			// Street
			if(row.getCell(8).toString().length() > 150) {
				errorMessage = "Invalid Street : " + row.getCell(8).toString();
				break;
			}
			patient.setStreetAddress(row.getCell(8).toString());
			// City
			if(row.getCell(9).toString().length() > 150) {
				errorMessage = "Invalid City : " + row.getCell(9).toString();
				break;
			}
			patient.setCity(row.getCell(9).toString());
			// Post Code
			if(row.getCell(10).toString().length() > 32) {
				errorMessage = "Invalid Post Code : " + row.getCell(10).toString();
				break;
			}
			patient.setPostalCode(row.getCell(10).toString());
			// Contry
			isMatchCode = false;
			if (row.getCell(11).toString().length() > 0) {
				for(CountryCode code : CountryCode.values()){
					if(code.name().equals(row.getCell(11).toString())) {
						isMatchCode = true;
						break;
					}
				}
			}
			if(isMatchCode == false) {
				errorMessage = "Invalid Contry : " + row.getCell(11).toString();
				break;
			}
			patient.setCountry(row.getCell(11).toString());
			// EthnicOrigin
			isMatchCode = false;
			if (row.getCell(12).toString().length() > 0) {
				for (EthnicOriginCode code : EthnicOriginCode.values()) {
					if (code.name().equals(row.getCell(12).toString())) {
						isMatchCode = true;
						break;
					}
				}
			}
			if(isMatchCode == false) {
				errorMessage = "Invalid EthnicOrigin : " + row.getCell(12).toString();
				break;
			}
			patient.setEthnicOrigin(row.getCell(12).toString());
			// Height
			if (row.getCell(13).toString().length() > 32) {
				errorMessage = "Invalid Height : " + row.getCell(13).toString();
				break;
			}
			patient.setHeight(row.getCell(13).toString());
			// Weight
			if (row.getCell(14).toString().length() > 32) {
				errorMessage = "Invalid Weight : " + row.getCell(14).toString();
				break;
			}
			patient.setWeight(row.getCell(14).toString());
			// ReasonForReferral
			patient.setReasonForReferral(row.getCell(15).toString());
			// MedicalHistory
			patient.setMedicalHistory(row.getCell(16).toString());
			// Medication
			patient.setMedication(row.getCell(17).toString());
			// Mother EthnicOrigin
			isMatchCode = false;
			if (row.getCell(18).toString().length() > 0) {
				for(EthnicOriginCode code : EthnicOriginCode.values()){
					if(code.name().equals(row.getCell(18).toString())) {
						isMatchCode = true;
						break;
					}
				}
			}
			if(isMatchCode == false) {
				errorMessage = "Invalid Mother EthnicOrigin : " + row.getCell(18).toString();
				break;
			}
			patient.setMotherEthnicOrigin(row.getCell(18).toString());
			//Mother Medical History
			patient.setMotherMedicalHistory(row.getCell(19).toString());
			// Father EthnicOrigin
			isMatchCode = false;
			if (row.getCell(20).toString().length() > 0){
				for (EthnicOriginCode code : EthnicOriginCode.values()) {
					if (code.name().equals(row.getCell(20).toString())) {
						isMatchCode = true;
						break;
					}
				}
			}
			if(isMatchCode == false) {
				errorMessage = "Invalid Father EthnicOrigin : " + row.getCell(20).toString();
				break;
			}			
			patient.setFatherEthnicOrigin(row.getCell(20).toString());
			// Father Medical History
			patient.setFatherMedicalHistory(row.getCell(21).toString());
			
			patients.add(patient);
		}
		
		if (errorMessage != null){
			logger.error(errorMessage);
			throw new ExcelParseException(errorMessage + errorMessagePrefix);
		}
		return patients;
	}*/

}
