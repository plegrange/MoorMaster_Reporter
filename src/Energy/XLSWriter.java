package Energy;

import javafx.util.Pair;
import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;

public class XLSWriter {
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String outputDataFile;
    private List<Month> months;
    private WritableWorkbook workbook;

    public void writeWorkbook(List<Month> months, List<Pair<String, String[][]>> sheets) {
        this.months = months;
        this.outputDataFile = months.get(0).getMonth() + months.get(0).getYear() + "-" + months.get(months.size() - 1).getMonth() + months.get(months.size() - 1).getYear() + ".xls";
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
            for (int s = 0; s < sheets.size(); s++) {
                writeToSheet(sheets.get(s).getValue(), sheets.get(s).getKey(), s);
            }
            workbook.write();
            workbook.setProtected(true);
            workbook.close();
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToSheet(String[][] data, String sheetName, int sheet) throws WriteException {
        workbook.createSheet(sheetName, sheet);
        for (int col = 0; col < data.length; col++) {
            addLabel(workbook.getSheet(sheet), col, 0, data[col][0]);
            for (int row = 1; row < data[0].length; row++) {
                addString(workbook.getSheet(sheet), col, row, data[col][row]);
            }
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
        Number number;
        number = new Number(column, row, d, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        jxl.write.Label label;
        label = new jxl.write.Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }
}
