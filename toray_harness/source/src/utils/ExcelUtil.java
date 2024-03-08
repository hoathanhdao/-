package utils;

import com.testarchitect.java.AbtLibrary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static void getCellValue(String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet (index 0)

            // Iterate through all the rows in the sheet
            for (Row row : sheet) {
                // Iterate through all the cells in the row
                for (Cell cell : row) {
                    CellStyle cellStyle = cell.getCellStyle();
                    Color color = cellStyle.getFillForegroundColorColor();

                    // Check if the cell has a background color
                    if (color != null) {
                        System.out.println("Cell " + cell.getStringCellValue() + " has background color: " + color.toString());
                    } else {
                        cell.setCellValue(9999);
                    }
                }
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

    public static void writeValueToCell(String filePath, String value, int columnIndex, int rowIndex) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet (index 0)
            int rowtmp = 0;


            // Iterate through all the rows in the sheet
            for (Row row : sheet) {
                // Iterate through all the cells in the row
                if (rowtmp > rowIndex) {
                    int coltmp = 0;
                    for (Cell cell : row) {
                        if (coltmp > columnIndex) {
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
