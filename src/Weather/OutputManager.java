package Weather;


import Weather.XLSWriter;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import javafx.collections.ObservableList;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultWindDataset;
import org.jfree.data.xy.WindDataset;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OutputManager {
    //private ChartGenerator chartGenerator;
    private JFreeChart chart;
    private PDFDocument pdfDoc;
    private XLSWriter xlsWriter;
    private ChartGenerator chartGenerator;

    public void generateReport(ObservableList<Ship> ships, String reportTitle) {
        xlsWriter = new XLSWriter();
        xlsWriter.writeWorkbook(ships, reportTitle);
        createWindPlots(ships);
    }

    private void createWindPlots(List<Ship> ships) {
        createPDFDoc("Report", "Simius");
        chartGenerator = new ChartGenerator();
        Object[][][] windDataSet;
        List<String> seriesKeys;
        for (Ship ship : ships) {
            seriesKeys = new ArrayList<>();
            seriesKeys.add(ship.getName());
            windDataSet = ship.getWindData();
            chart = chartGenerator.createWindChart(ship.getName(), "Time", "Wind Vector", new DefaultWindDataset(seriesKeys, windDataSet));
            amendPDFDoc(700, 400, chart);
        }
        writePDFDoc("ReportSample");
    }

    private void createPDFDoc(String title, String author) {
        pdfDoc = new PDFDocument();
        pdfDoc.setTitle(title);
        pdfDoc.setAuthor(author);
    }

    private void amendPDFDoc(int w, int h, JFreeChart c) {
        Page page = pdfDoc.createPage(new Rectangle(w, h));
        PDFGraphics2D g2 = page.getGraphics2D();
        c.draw(g2, new Rectangle(w, h));
    }

    private void writePDFDoc(String fileName) {
        pdfDoc.writeToFile(new File(fileName + ".pdf"));
    }
}
