package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.testarchitect.java.AbtLibrary;

public class Utils {

	public static void writeErrorFile(String pathToResult, String fileErrorName, String content) {
		String lastText = pathToResult.substring(pathToResult.lastIndexOf("\\"));
		String pathToErroFile = pathToResult.replace(lastText, "\\" + fileErrorName + ".txt");

		try {
			PrintWriter writer = new PrintWriter(pathToErroFile);
			writer.println(content);
			writer.close();
			AbtLibrary.report("Please check error at: " + pathToErroFile);
		} catch (FileNotFoundException e) {
			AbtLibrary.report("Cannot create error file");
			AbtLibrary.report(e.getMessage());
		}

	}

	public static Sheet getSheet(String pathToExcelFile, String sheetName) {
		Sheet sheet = null;
		try {
			File inputFile = new File(pathToExcelFile);
			FileInputStream fis = new FileInputStream(inputFile);
			Workbook workbook = null;

			String ext = FilenameUtils.getExtension(inputFile.toString());
			if (ext.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(fis);
			} else if (ext.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(fis);
			}
			sheet = workbook.getSheet(sheetName);
			workbook.close();
		} catch (Exception e) {
			AbtLibrary.report("Cannot get sheet of excel file");
			AbtLibrary.report(e.getMessage());
		}
		return sheet;
	}

	public static HashMap<String, String> getDataFromResultFile(String pathToResultfile) {
		try {
			File file = new File(pathToResultfile);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String line;
			HashMap<String, String> hmap = new HashMap<String, String>();
			String inputKey;
			while ((line = br.readLine()) != null) {
				if (line.contains("GSL_BEGIN")) {
					inputKey = line.substring(0, line.indexOf("GSL_BEGIN")).trim();
					line = br.readLine();
					String ouputValue = "";
					while (!line.contains(inputKey)) {
						if (!ouputValue.equals("")) {
							ouputValue = ouputValue + "\n" + line;
						} else {
							ouputValue = line;
						}
						line = br.readLine();
					}
					hmap.put(inputKey, ouputValue);
				}
			}
			br.close();
			return hmap;
		} catch (IOException e) {
			AbtLibrary.report("Cannot get data from result file");
			AbtLibrary.report(e.getMessage());
		}
		return null;
	}

	public static int getInputKeyIndexByTestCaseNumber(Sheet sheet, String inputKey, double testcaseNumber) {
		try {
			Row row;
			int colInputKey = findIndexOfHeader(sheet, "input対象", true);
			int colTestCaseNum = findIndexOfHeader(sheet, "TID", true);
			int rowHeaderIndex = findIndexOfHeader(sheet, "TID", false);

			System.out.println(colTestCaseNum);
			System.out.println(rowHeaderIndex);
			System.out.println(inputKey);
			System.out.println(testcaseNumber);

			for (rowHeaderIndex = rowHeaderIndex + 1; rowHeaderIndex <= sheet.getLastRowNum(); rowHeaderIndex++) {
				row = sheet.getRow(rowHeaderIndex);
				Cell cellInputKey = row.getCell(colInputKey);
				Cell cellTestCaseNumber = row.getCell(colTestCaseNum);
				Cell testNumber = checkAndGetValueOfCell(sheet, cellTestCaseNumber, colTestCaseNum);

				if (cellInputKey.getStringCellValue().equals(inputKey)
						&& testNumber.getNumericCellValue() == testcaseNumber) {
					return cellInputKey.getRowIndex();
				}
			}
		} catch (Exception e) {
			AbtLibrary.report("Cannot get index of input key");
			AbtLibrary.report(e.getMessage());
		}
		return -1;
	}

	public static Cell checkAndGetValueOfCell(Sheet sheet, Cell cell, int colIndex) {
		Row row;
		Cell data;
		if (getMergedRegionForCell(cell) != null) {
			row = sheet.getRow(getMergedRegionForCell(cell).getFirstRow());
			data = row.getCell(colIndex);
		} else
			data = cell;
		return data;
	}

	public static CellRangeAddress getMergedRegionForCell(Cell c) {
		Sheet s = c.getRow().getSheet();
		for (CellRangeAddress mergedRegion : s.getMergedRegions()) {
			if (mergedRegion.isInRange(c.getRowIndex(), c.getColumnIndex())) {
				return mergedRegion;
			}
		}
		return null;
	}

	public static int findIndexOfHeader(Sheet sheet, String value, boolean isColumn) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() != CellType.STRING) {
					continue;
				} else {
					if (cell.getStringCellValue().equals(value)) {
						if (isColumn) {
							return cell.getColumnIndex();
						}
						return cell.getRowIndex();
					}
				}
			}
		}
		return -1;
	}

	public static String getConfigByName(String pathToExcelFile, String name) {
		try {
			String text = pathToExcelFile.substring(pathToExcelFile.lastIndexOf("\\"));
			String text1 = text.substring(0, text.indexOf(".xlsx"));

			Sheet sheet = getSheet(pathToExcelFile, "共通設定");
			Workbook workbook = sheet.getWorkbook();
			int rowIndex = findIndexOfHeader(sheet, name, false);

			Row row = sheet.getRow(rowIndex);
			Cell cell_output = row.getCell(2);

			workbook.close();

			return cell_output.toString() + text1;

		} catch (Exception ioe) {
			AbtLibrary.report("Cannot get config");
			writeErrorFile(pathToExcelFile, "GetConfigError", ioe.getMessage());
		}
		return null;
	}

	public static String removeWhiteSpace(String value) {
		while (Character.isWhitespace(value.charAt(0))) {
			value = value.substring(1);
		}
		return value;
	}

	public static Double convertTCName(String testCaseName) {
		return Double.parseDouble(testCaseName.replaceAll("[^\\d.]", ""));
	}

	public static String convertTimeZone(String timeZone) {
		String newOutput = timeZone.substring(0, timeZone.indexOf("+"));
		String time = timeZone.substring(timeZone.indexOf("+") + 1);
		String newTime = time.replaceAll("0", "").replaceAll("[^\\d.]", "");
		return newOutput + newTime + ")";
	}

	public static CellStyle setColor(Workbook workbook, Sheet sheet, IndexedColors textColor, Boolean withBorder) {
		// Create header CellStyle
		Font headerFont = workbook.createFont();
		headerFont.setColor(textColor.index);

		CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setWrapText(true);
		if (withBorder) {
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
		} else {
			headerCellStyle.setBorderBottom(BorderStyle.NONE);
			headerCellStyle.setBorderTop(BorderStyle.NONE);
			headerCellStyle.setBorderLeft(BorderStyle.NONE);
			headerCellStyle.setBorderRight(BorderStyle.NONE);
		}

		return headerCellStyle;
	}

	public static String getTextFromExcelSheet(Sheet sheet) {
		String text = "";
		try {
			Row row;
			int colInputKey = 0;
			int rowHeaderIndex = 0;
			for (rowHeaderIndex = rowHeaderIndex + 1; rowHeaderIndex <= sheet.getLastRowNum(); rowHeaderIndex++) {
				row = sheet.getRow(rowHeaderIndex);
				Cell cellInputKey = row.getCell(colInputKey);
				text = text + "," + cellInputKey;
			}
			return text;
		} catch (Exception e) {
			AbtLibrary.report("Cannot get data");
			AbtLibrary.report(e.getMessage());
		}
		return null;
	}

	public static boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		} else {
			for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
				Cell cell = row.getCell(c);
				boolean TypeFlag = true;

				switch (cell.getCellType()) {
				case BLANK:
					TypeFlag = false;
					break;
				default:
					break;
				}

				if (cell != null && TypeFlag)
					return false;
			}
			return true;
		}

	}

	public static int getColumnIndexByColumnName(XSSFSheet sheet, String columnName) {
		Map<String, Integer> map = new HashMap<String, Integer>(); // Create map
		XSSFRow firstRow = sheet.getRow(0); // Get first row

		// following is boilerplate from the java doc
		int minColIx = firstRow.getFirstCellNum(); // get the first column index for a row
		int maxColIx = firstRow.getLastCellNum(); // get the last column index for a row
		for (int colIx = minColIx; colIx < maxColIx; colIx++) { // loop from first to last index
			XSSFCell cell = firstRow.getCell(colIx); // get the cell
			map.put(cell.getStringCellValue(), cell.getColumnIndex()); // add the cell contents (name of column) and
																		// cell index to the map
		}
		return map.get(columnName);
	}
	
	public static int findIndex(Sheet sheet, String value, boolean isColumn) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() != CellType.STRING) {
					continue;

				} else {
					if (cell.getStringCellValue().equals(value)) {
						if (isColumn) {
							return cell.getColumnIndex();
						}
						return cell.getRowIndex();
					}
				}

			}
		}
		return 0;
	}
	
	public static int getRowIndexByTestCaseNumber(Sheet sheet, String testcaseNumber) {
		try {
			Row row;
			int colTestCaseNum = findIndexOfHeader(sheet, "TID", true);
			int rowHeaderIndex = findIndexOfHeader(sheet, "TID", false);

			System.out.println(colTestCaseNum);
			System.out.println(rowHeaderIndex);
			System.out.println(testcaseNumber);

			for (rowHeaderIndex = rowHeaderIndex + 1; rowHeaderIndex <= sheet.getLastRowNum(); rowHeaderIndex++) {
				row = sheet.getRow(rowHeaderIndex);
				Cell cellTestCaseNumber = row.getCell(colTestCaseNum);
				Cell testNumber = checkAndGetValueOfCell(sheet, cellTestCaseNumber, colTestCaseNum);
//				System.out.println(testNumber.toString());
				if (testNumber.toString().replace(".0", "").equals(testcaseNumber)) {
					return cellTestCaseNumber.getRowIndex();
				}
			}
		} catch (Exception e) {
			AbtLibrary.report("Cannot get index of input key");
			AbtLibrary.report(e.getMessage());
		}
		return -1;
	}

}
