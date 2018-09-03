package ngeneanalysys.service;

import javafx.stage.Stage;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jang
 * @since 2018-09-03
 */
public class ExcelConvertReportInformationService {

    private ExcelConvertReportInformationService() {}

    public static void convertExcelData(String sampleName, File excelFile, Map<String, Object> contents,
                                        Map<String, Object> variableList, Stage primaryStage) {

        if(excelFile != null) {
            try(FileInputStream fileInputStream = new FileInputStream(excelFile)) {
                if(excelFile.getName().endsWith(".xls")) {
                    xlsFileConvert(sampleName, fileInputStream, contents, variableList);
                } else if(excelFile.getName().endsWith(".xlsx")) {
                    xlsxFileConvert(sampleName, fileInputStream, contents, variableList);
                }

            } catch (FileNotFoundException fnfe) {
                DialogUtil.error("File not found", fnfe.getMessage(), primaryStage, true);
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                DialogUtil.error("", ioe.getMessage(), primaryStage, true);
                ioe.printStackTrace();
            }
        }
    }

    private static String searchDisplayName(Map<String, Object> variableList, String name) {
        Set<String> keySet = variableList.keySet();

        for(String key : keySet) {
            Map<String, String> item = (Map<String, String>) variableList.get(key);
            if(name.equalsIgnoreCase(item.get("displayName"))) {
                return key;
            }
        }

        return "";
    }

    private static void xlsFileConvert(String sampleName, FileInputStream fileInputStream, Map<String, Object> contents,
                                       Map<String, Object> variableList) throws IOException {

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
        List<String> displayNames = new ArrayList<>();

        HSSFSheet curSheet;
        HSSFRow curRow;
        HSSFCell keyCell;
        HSSFCell valueCell;

        curSheet = hssfWorkbook.getSheetAt(0);

        for(int rowIndex = 0; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
            if(rowIndex == 0) {
                curRow = curSheet.getRow(rowIndex);
                for(int colIndex = curRow.getFirstCellNum() + 1 ; colIndex < curRow.getLastCellNum(); colIndex++) {
                    keyCell = curRow.getCell(colIndex);
                    displayNames.add(gethssfString(keyCell));
                }
            } else {

                boolean sampleSearchSuccess = false;

                curRow = curSheet.getRow(rowIndex);
                keyCell = curRow.getCell(0);
                String sample = gethssfString(keyCell);
                if(StringUtils.isNotEmpty(sample) && sample.equalsIgnoreCase(sampleName)) {
                    for(int colIndex = curRow.getFirstCellNum() + 1 ; colIndex < curRow.getLastCellNum(); colIndex++) {
                        String keyValue = searchDisplayName(variableList, displayNames.get(colIndex - 1));
                        if(StringUtils.isNotEmpty(keyValue)) {
                            valueCell = curRow.getCell(colIndex);
                            contents.put(keyValue, gethssfString(valueCell));
                        }
                    }
                    sampleSearchSuccess = true;
                    break;
                }

                if(!sampleSearchSuccess) {
                    for(String displayName : displayNames) {
                        String keyValue = searchDisplayName(variableList, displayName);
                        if(StringUtils.isNotEmpty(keyValue)) {
                            contents.put(keyValue, "");
                        }
                    }
                }

            }
        }

    }

    private static void xlsxFileConvert(String sampleName, FileInputStream fileInputStream, Map<String, Object> contents,
                                        Map<String, Object> variableList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        List<String> displayNames = new ArrayList<>();

        XSSFSheet curSheet;
        XSSFRow curRow;
        XSSFCell keyCell;
        XSSFCell valueCell;

        curSheet = workbook.getSheetAt(0);

        for(int rowIndex = 0; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
            if(rowIndex == 0) {
                curRow = curSheet.getRow(rowIndex);
                for(int colIndex = curRow.getFirstCellNum() + 1 ; colIndex < curRow.getLastCellNum(); colIndex++) {
                    keyCell = curRow.getCell(colIndex);
                    displayNames.add(getXssfString(keyCell));
                }
            } else {
                curRow = curSheet.getRow(rowIndex);
                keyCell = curRow.getCell(0);
                String sample = getXssfString(keyCell);

                boolean sampleSearchSuccess = false;

                if(StringUtils.isNotEmpty(sample) && sample.equalsIgnoreCase(sampleName)) {
                    for(int colIndex = curRow.getFirstCellNum() + 1 ; colIndex < curRow.getLastCellNum(); colIndex++) {
                        String keyValue = searchDisplayName(variableList, displayNames.get(colIndex - 1));
                        if(StringUtils.isNotEmpty(keyValue)) {
                            valueCell = curRow.getCell(colIndex);
                            contents.put(keyValue, getXssfString(valueCell));
                        }
                    }
                    sampleSearchSuccess = true;
                    break;
                }

                if(!sampleSearchSuccess) {
                    for(String displayName : displayNames) {
                        String keyValue = searchDisplayName(variableList, displayName);
                        if(StringUtils.isNotEmpty(keyValue)) {
                            contents.put(keyValue, "");
                        }
                    }
                }
            }
        }
    }

    private static String gethssfString(HSSFCell valueCell) {
        switch (valueCell.getCellTypeEnum()) {
            case BOOLEAN :
                return valueCell.getBooleanCellValue() ? "true" : "false";
            case STRING :
                return valueCell.getStringCellValue();
            case NUMERIC :
                if(DateUtil.isCellDateFormatted(valueCell)) {
                    Date date = valueCell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(date);
                } else {
                    return String.valueOf((int)valueCell.getNumericCellValue());
                }
            case FORMULA :
                return valueCell.getCellFormula();
            case BLANK :
                return "";
            default:
                return "";

        }
    }

    private static String getXssfString(XSSFCell valueCell) {
        switch (valueCell.getCellTypeEnum()) {
            case BOOLEAN :
                return valueCell.getBooleanCellValue() ? "true" : "false";
            case STRING :
                return valueCell.getStringCellValue();
            case NUMERIC :
                if(DateUtil.isCellDateFormatted(valueCell)) {
                    Date date = valueCell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(date);
                } else {
                    return String.valueOf((int)valueCell.getNumericCellValue());
                }
            case FORMULA :
                return valueCell.getCellFormula();
            case BLANK :
                return "";
            default:
                return "";

        }
    }
}
