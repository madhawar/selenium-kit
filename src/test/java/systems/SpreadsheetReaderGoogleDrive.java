package systems;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class SpreadsheetReaderGoogleDrive {
    int numOfNonEmptyRows;

    public int getNonEmptyRows(String fileName, String sheetName) throws IOException {
        String[][] data = null;

        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sh = wb.getSheet(sheetName);
        XSSFRow row = sh.getRow(0);
        int noOfRows = sh.getPhysicalNumberOfRows();
        int noOfCols = row.getLastCellNum();
        Log.warn("[DATA PROVIDER] COLUMNS: " + noOfCols + " | ROWS: " + noOfRows);
        Cell cell;
        int i=1;
        while (i<noOfRows) {
            int j=0;
            while (j<noOfCols) {
                row = sh.getRow(i);
                cell = row.getCell(j);
                if (cell == null) {
                    numOfNonEmptyRows = i;
                    noOfCols = j;
                    noOfRows = i;
                    break;
                }
                j++;
            }
            i++;
        }
        return numOfNonEmptyRows;
    }

    public String[][] getData(String fileName, String sheetName){
        String[][] data = null;
        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sh = wb.getSheet(sheetName);
            XSSFRow row = sh.getRow(0);
            int noOfRows = sh.getPhysicalNumberOfRows();
            int noOfCols = row.getLastCellNum();
            int rowsNonEmpty = getNonEmptyRows(fileName, sheetName);
            int noOfTests = rowsNonEmpty-1;
            Log.warn("[DATA PROVIDER] TEST DATA: " + noOfTests);
            Cell cell;
            data = new String[noOfTests][noOfCols];
            for(int i =1; i<rowsNonEmpty;i++){
                for(int j=0;j<noOfCols;j++){
                    row = sh.getRow(i);
                    cell = row.getCell(j);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                data[i - 1][j] = cell.getRichStringCellValue().getString();
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    data[i - 1][j] = String.valueOf(cell.getDateCellValue());
                                } else {
                                    data[i - 1][j] = String.valueOf(cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                data[i - 1][j] = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                data[i - 1][j] = cell.getCellFormula();
                                break;
                            case BLANK:
                                System.out.println("Please check the Sheet for invalid values.");
                                break;
                            default:
                                System.out.println("Data provider unknown error.");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("The exception is: " +e.getMessage());
        }
        return data;
    }

}
