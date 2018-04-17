package Weather;

import Energy.Month;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.lang.Number;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XLSWriter {
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String outputDataFile;
    private List<Ship> ships;
    private WritableWorkbook workbook;

    public void writeWorkbook(ObservableList<Ship> ships, String fileName) {
        this.ships = ships;
        this.outputDataFile = fileName;
        File file = new File(outputDataFile);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
            // Define the cell format
            times = new WritableCellFormat(times10pt);
            // Lets automatically wrap the cells
            times.setWrap(false);
            times.setLocked(true);

            // create create a bold font with underlines
            WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
                    UnderlineStyle.SINGLE);
            timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
            // Lets automatically wrap the cells
            timesBoldUnderline.setWrap(false);
            timesBoldUnderline.setLocked(true);

            CellView cv = new CellView();
            cv.setFormat(times);
            cv.setFormat(timesBoldUnderline);
            cv.setAutosize(true);
            writeToSheet(ships, "Ships", 0);
            for (int i = 0; i < ships.size(); i++) {
                writeShipSheet(ships.get(i), i + 1);
            }
            workbook.write();
            workbook.setProtected(true);
            workbook.close();
            //Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeShipSheet(Ship ship, int sheet) {
        workbook.createSheet(ship.getName(), sheet);
        try {
            addLabel(workbook.getSheet(sheet), 0, 0, "Date");
            addLabel(workbook.getSheet(sheet), 1, 0, "Time");
            addLabel(workbook.getSheet(sheet), 2, 0, "Wind force");
            addLabel(workbook.getSheet(sheet), 3, 0, "Wind direction");
            addLabel(workbook.getSheet(sheet), 4, 0, "HSLong");
            addLabel(workbook.getSheet(sheet), 5, 0, "TPLong");
            addLabel(workbook.getSheet(sheet), 6, 0, "HSSwell");
            addLabel(workbook.getSheet(sheet), 7, 0, "TPSwell");
        } catch (WriteException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < ship.getWindSpeeds().size(); i++) {
            try {
                Date timeStamp = ship.getTimeStamps().get(i);
                String date = timeStamp.getDate() + "/" + timeStamp.getMonth() + "/" + timeStamp.getYear();
                String time = String.valueOf(timeStamp.getHours()) + "h " + String.valueOf(timeStamp.getMinutes()) + "m " + String.valueOf(timeStamp.getSeconds()) + "s";
                addString(workbook.getSheet(sheet), 0, i + 1, date);
                addString(workbook.getSheet(sheet), 1, i + 1, time);
                addNumber(workbook.getSheet(sheet), 2, i + 1, ship.getWindSpeeds().get(i));
                addNumber(workbook.getSheet(sheet), 3, i + 1, ship.getWindDirections().get(i));
                addNumber(workbook.getSheet(sheet), 4, i + 1, ship.getHsLong().get(i));
                addNumber(workbook.getSheet(sheet), 5, i + 1, ship.getTpLong().get(i));
                addNumber(workbook.getSheet(sheet), 6, i + 1, ship.getHsSwell().get(i));
                addNumber(workbook.getSheet(sheet), 7, i + 1, ship.getTpSwell().get(i));
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
        sheetAutoFitColumns(workbook.getSheet(sheet));
    }

    private void writeToSheet(List<Ship> ships, String sheetName, int sheet) throws WriteException {
        workbook.createSheet(sheetName, sheet);
        addLabel(workbook.getSheet(0), 0, 0, "Ship Name");
        addLabel(workbook.getSheet(0), 1, 0, "Arrival Date");
        addLabel(workbook.getSheet(0), 2, 0, "Arrival Time");
        addLabel(workbook.getSheet(0), 3, 0, "Arrival Wind Speed");
        addLabel(workbook.getSheet(0), 4, 0, "Arrival HS_Long");
        addLabel(workbook.getSheet(0), 5, 0, "Arrival TP_Long");
        addLabel(workbook.getSheet(0), 6, 0, "Arrival HS_Swell");
        addLabel(workbook.getSheet(0), 7, 0, "Arrival TP_Swell");

        addLabel(workbook.getSheet(0), 8, 0, "Departure Date");
        addLabel(workbook.getSheet(0), 9, 0, "Departure Time");
        addLabel(workbook.getSheet(0), 10, 0, "Departure Wind Speed");
        addLabel(workbook.getSheet(0), 11, 0, "Departure HS_Long");
        addLabel(workbook.getSheet(0), 12, 0, "Departure TP_Long");
        addLabel(workbook.getSheet(0), 13, 0, "Departure HS_Swell");
        addLabel(workbook.getSheet(0), 14, 0, "Departure TP_Swell");

        addLabel(workbook.getSheet(0), 15, 0, "Average Wind Speed");
        addLabel(workbook.getSheet(0), 16, 0, "Average HS_Long");
        addLabel(workbook.getSheet(0), 17, 0, "Average TP_Long");
        addLabel(workbook.getSheet(0), 18, 0, "Average HS_Swell");
        addLabel(workbook.getSheet(0), 19, 0, "Average TP_Swell");

        for (int i = 0; i < ships.size(); i++) {
            addString(workbook.getSheet(0), 0, i + 1, ships.get(i).getName());
            addString(workbook.getSheet(0), 1, i + 1, ships.get(i).getStartDate());
            addString(workbook.getSheet(0), 2, i + 1, ships.get(i).getStartTime());
            addNumber(workbook.getSheet(0), 3, i + 1, ships.get(i).getWindSpeedStart());
            addNumber(workbook.getSheet(0), 4, i + 1, ships.get(i).getHsLongStart());
            addNumber(workbook.getSheet(0), 5, i + 1, ships.get(i).getTpLongStart());
            addNumber(workbook.getSheet(0), 6, i + 1, ships.get(i).getHsSwellStart());
            addNumber(workbook.getSheet(0), 7, i + 1, ships.get(i).getTpSwellStart());

            addString(workbook.getSheet(0), 8, i + 1, ships.get(i).getEndDate());
            addString(workbook.getSheet(0), 9, i + 1, ships.get(i).getEndTime());
            addNumber(workbook.getSheet(0), 10, i + 1, ships.get(i).getWindSpeedEnd());
            addNumber(workbook.getSheet(0), 11, i + 1, ships.get(i).getHsLongEnd());
            addNumber(workbook.getSheet(0), 12, i + 1, ships.get(i).getTpLongEnd());
            addNumber(workbook.getSheet(0), 13, i + 1, ships.get(i).getHsSwellEnd());
            addNumber(workbook.getSheet(0), 14, i + 1, ships.get(i).getTpSwellEnd());

            addNumber(workbook.getSheet(0), 15, i + 1, ships.get(i).getWindSpeedAverage());
            addNumber(workbook.getSheet(0), 16, i + 1, ships.get(i).getHsLongAverage());
            addNumber(workbook.getSheet(0), 17, i + 1, ships.get(i).getTpLongAverage());
            addNumber(workbook.getSheet(0), 18, i + 1, ships.get(i).getHsSwellAverage());
            addNumber(workbook.getSheet(0), 19, i + 1, ships.get(i).getTpSwellAverage());
        }
        sheetAutoFitColumns(workbook.getSheet(sheet));
    }


    private void sheetAutoFitColumns(WritableSheet sheet) {
        for (int i = 0; i < sheet.getColumns(); i++) {
            Cell[] cells = sheet.getColumn(i);
            int longestStrLen = -1;

            if (cells.length == 0)
                continue;

            /* Find the widest cell in the column. */
            for (int j = 0; j < cells.length; j++) {
                if (cells[j].getContents().length() > longestStrLen) {
                    String str = cells[j].getContents();
                    if (str == null || str.isEmpty())
                        continue;
                    longestStrLen = str.trim().length();
                }
            }

            /* If not found, skip the column. */
            if (longestStrLen == -1)
                continue;

            /* If wider than the max width, crop width */
            if (longestStrLen > 255)
                longestStrLen = 255;

            CellView cv = sheet.getColumnView(i);
            cv.setSize(longestStrLen * 256 + 100); /* Every character is 256 units wide, so scale it. */
            sheet.setColumnView(i, cv);
        }
    }

    private void addString(WritableSheet sheet, int column, int row,
                           String s) throws WriteException, RowsExceededException {
        jxl.write.Label label;
        label = new jxl.write.Label(column, row, s, times);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
                           double d) throws WriteException, RowsExceededException {
        jxl.write.Number number;
        number = new jxl.write.Number(column, row, d, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        jxl.write.Label label;
        label = new jxl.write.Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }
}
