package utils;

import com.testarchitect.java.AbtLibrary;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Array;
import java.util.ArrayList;

public class ExcelUtil {

    public static int[] getCellIndexByCellValue(String filePath, String searchCellValue) {
        int rowIndex = -1; // Initialize the row index to -1, which means not found
        int colIndex = -1; // Initialize the row index to -1, which means not found

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet != null) {

                // Iterate through each row in the sheet
                // Iterate through all the rows in the sheet
                for (Row row : sheet) {
                    // Iterate through all the cells in the row
                    for (Cell cell : row) {
                        if (cell.getCellType() == CellType.STRING) {
                            String cellValue = cell.getStringCellValue();
                            System.out.println(cellValue);
                            if (cellValue.equals(searchCellValue)) {
                                rowIndex = cell.getRowIndex();
                                colIndex = cell.getColumnIndex();
                                System.out.println("Cell with value \"" + searchCellValue + "\" found at: Row " + (rowIndex + 1) + ", Column " + (colIndex + 1));
                                break;
                            }
                        }
                    }
                }

                if (rowIndex != -1) {
                    System.out.println("Row index containing the cell value: " + rowIndex);
                    System.out.println("Column index containing the cell value: " + colIndex);
                } else {
                    System.out.println("Cell value not found in the specified column of the Excel file.");
                }
            }
            return new int[] {rowIndex, colIndex};
        } catch (IOException e) {
            e.printStackTrace();
            return null;

            //AbtLibrary.report(e.getMessage());
        }
    }

    public static int getRowIndexByCellValueAtSpefifiedColumn(XSSFSheet sheet, String searchCellValue, int searchColumnIndex) {
        int rowIndex = -1; // Initialize the row index to -1, which means not found
        if (sheet != null) {

            // Iterate through each row in the sheet
            for (Row row : sheet) {
                Cell cell = row.getCell(searchColumnIndex);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue();
                    // Check if the cell value matches the search value
                    if (cellValue.equals(searchCellValue)) {
                        rowIndex = row.getRowNum(); // Get the row index
                        break; // Stop searching once a match is found
                    }
                }
            }

            if (rowIndex != -1) {
                System.out.println("Row index containing the cell value: " + rowIndex);
            } else {
                System.out.println("Cell value not found in the specified column of the Excel file.");
            }
        }
        return rowIndex;
    }

    public static boolean CheckSheetName(XSSFWorkbook workbook, String sheetName) {
        int sheetCnt = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCnt; i++) {
            String name = workbook.getSheetAt(i).getSheetName();
            if (name.equals(sheetName)) {
                return true;
            }
        }

        return false;
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
            if (workbook != null) {
                sheet = workbook.getSheet(sheetName);
                workbook.close();
            }

        } catch (Exception e) {
            AbtLibrary.report("Cannot get sheet of excel file");
            AbtLibrary.report(e.getMessage());
        }
        return sheet;
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

    public static void writeValueToCellsWithoutBackgroundColor(String filePath, String value, int columnIndex, int rowIndex) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet (index 0)
            int rowtmp = 0;


            // Iterate through all the rows in the sheet
            for (Row row : sheet) {
                // Iterate through all the cells in the row
                if (rowtmp >= rowIndex) {
                    int coltmp = 0;
                    for (Cell cell : row) {
                        if (coltmp >= columnIndex) {
                            CellStyle cellStyle = cell.getCellStyle();
                            Color color = cellStyle.getFillForegroundColorColor();

                            // Check if the cell has a background color
                            if (color != null) {
                                System.out.println("Cell " + cell.getStringCellValue() + " has background color: " + color.toString());
                            } else {
                                cell.setCellValue(value);
                            }
                        }
                        coltmp++;
                    }
                }
                rowtmp++;
            }

            // write to excel file

            FileOutputStream out = new FileOutputStream(filePath);
            workbook.write(out);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            //AbtLibrary.report(e.getMessage());
        }
    }
}
