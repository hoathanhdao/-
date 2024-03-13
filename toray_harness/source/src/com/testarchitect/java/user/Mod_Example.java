package com.testarchitect.java.user;

import com.testarchitect.java.AbtLibrary;
import net.sourceforge.tess4j.TesseractException;
import utils.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Example Module
 */
public class Mod_Example extends Utils {

    /**
     * declare the actions for the module
     */
    public static void setActions() {
        String module = "example";

        AbtLibrary.setActionScript("get current timestamp", module, 1);
        AbtLibrary.setActionScript("get first data of csv file", module, 1);
        AbtLibrary.setActionScript("delete first data of csv file", module, 1);
        AbtLibrary.setActionScript("test write excel", module, 1);
        AbtLibrary.setActionScript("remove space", module, 1);
        AbtLibrary.setActionScript("convert full-size to half-size", module, 1);

    }

    /**
     * map an action to its function
     *
     * @param actionName
     * @return
     */
    public static boolean divert(String actionName) {
        boolean result = true;
        if (actionName.equals("get current timestamp")) {
            action_getTimestamp();
        } else if (actionName.equals("get first data of csv file")) {
            action_getFirstDataOfCSV();
        } else if (actionName.equals("delete first data of csv file")) {
            action_deleteFirstDataOfCSV();
        } else if (actionName.equals("test write excel")) {
            action_writeExcel();
        } else if (actionName.equals("remove space")) {
            action_removeSpaceFromStrings();
        } else if (actionName.equals("convert full-size to half-size")) {
            action_convertFullsizeToHalfsize();
        } else {
            result = false;
            AbtLibrary.reportError("Don't know action {" + actionName + "}");
        }
        return result;
    }

    public static void action_getTimestamp() {
        String tmp1 = AbtLibrary.getArgByIndex(1);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String result = formatter.format(date);
        AbtLibrary.assign(tmp1, result);
    }


    public static void action_getFirstDataOfCSV(){
        String filePath = AbtLibrary.getArgByIndex(1);
        String result = AbtLibrary.getArgByIndex(2);
        try {
            String cell = CSVUtils.readCSV(filePath, 0);
            AbtLibrary.assign(result, cell);
        } catch (Exception e){
            AbtLibrary.report(e.getMessage());
        }
    }

    public static void action_deleteFirstDataOfCSV(){
        String filePath = AbtLibrary.getArgByIndex(1);
        String deleteData = AbtLibrary.getArgByIndex(2);
        try {
            CSVUtils.deleteRow(filePath, deleteData);
        } catch (Exception e){
            AbtLibrary.report(e.getMessage());
        }
    }

    public static void action_writeExcel() {
        String filePath = AbtLibrary.getArgByIndex(1);
        String writeValue = AbtLibrary.getArgByIndex(2);
        String rowIndex = AbtLibrary.getArgByIndex(3);
        String columnIndex = AbtLibrary.getArgByIndex(4);
        try {
            ExcelUtil.writeValueToCell(filePath, writeValue, Integer.parseInt(rowIndex), Integer.parseInt(columnIndex));
        } catch (Exception e){
            AbtLibrary.report(e.getMessage());
        }
    }

    public static void action_removeSpaceFromStrings(){
        String target = AbtLibrary.getArgByIndex(1);
        String result = AbtLibrary.getArgByIndex(2);
        try {
            String strWithoutSpace = StringUtils.removeSpaceFromString(target);
            AbtLibrary.assign(result, strWithoutSpace);
        } catch (Exception e){
            AbtLibrary.report(e.getMessage());
        }

    }

    public static void action_convertFullsizeToHalfsize(){
        String target = AbtLibrary.getArgByIndex(1);
        String result = AbtLibrary.getArgByIndex(2);
        try {
            String halfStr = StringUtils.fullWidthToHalfWidth(target);
            AbtLibrary.assign(result, halfStr);
        } catch (Exception e){
            AbtLibrary.report(e.getMessage());
        }
    }

    public static void main(String[] args) throws TesseractException, IOException {
        //String cell = CSVUtils.readCSV("C:\\Work\\NTTR\\mail_list.csv", 0);
        //System.out.println(cell);

        System.out.println(StringUtils.fullWidthToHalfWidth("％"));

        //ExcelUtil.getCellIndexByCellValue("C:\\Work\\東レ\\Test.xlsx","注文番号");

        //CSVUtils.deleteRow("C:\\Work\\NTTR\\mail_list.csv", "nttrt-at-shop-sp001@submail.agest.co.jp");
    }


}
