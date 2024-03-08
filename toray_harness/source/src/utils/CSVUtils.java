package utils;

import java.io.FileReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    public static void deleteRow(String filePath, String dataToDelete) {
        // Read the CSV file
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // If the line doesn't contain the data to delete, add it to the list
                if (!line.contains(dataToDelete)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the updated content back to the CSV file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readCSV(String filePath, int rowIndex) {
        String line = "";

        // Read the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            for (int i = 0; i <= rowIndex; i++) {
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }
}
