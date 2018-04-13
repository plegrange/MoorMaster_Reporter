package Energy;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputManager {

    private ChartGenerator chartGenerator;
    private JFreeChart chart;
    private PDFDocument pdfDoc;
    private XLSWriter xlsWriter;

    public void generateReport(List<Month> months, Stage stage, String startDate, String endDate) {
        List<Pair<String, Double>> setA = new ArrayList<>(), setB = new ArrayList<>(), setC = new ArrayList<>();

        chartGenerator = new ChartGenerator();
        for (Month m : months) {
            Pair<String, Double> a = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getCombokWh()));
            setA.add(a);
            Pair<String, Double> b = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getAMSCost()));
            setB.add(b);
            Pair<String, Double> c = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getPoNkWh()));
            setC.add(c);
        }
        chartGenerator.resetData();
        chartGenerator.addData(setA, "Combo Usage (kWh)");
        chartGenerator.addData(setB, "AMS Usage (kWh)");
        chartGenerator.addData(setC, "PoN Usage (kWh)");
        chart = chartGenerator.createBarChart("Monthly Energy Usage", "Time (Months)", "Energy Usage (kWh)", "Combo vs AMS vs PoN");
        //displayReport(stage);

        createPDFDoc("Report_" + startDate + "_" + endDate, "SimiusDev");
        amendPDFDoc(700, 400, chart);

        String fileName = "Report_" + months.get(0) + "_" + months.get(months.size());
        writePDFDoc(fileName);
        xlsWriter = new XLSWriter();
        xlsWriter.writeWorkbook(months, buildTables(months));
        try {
            Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Pair<String, String[][]>> buildTables(List<Month> months) {
        List<Pair<String, String[][]>> tables = new ArrayList<>();
        tables.add(buildAccumulativeTable(months));
        return tables;
    }

    private Pair<String, String[][]> buildAccumulativeTable(List<Month> months) {
        String[][] accumulativeTable = new String[months.size() + 1][38];

        accumulativeTable[0][1] = "Combo Usage (kWh)";
        accumulativeTable[0][2] = "Combo Cost (Rands)";
        accumulativeTable[0][3] = "TPT Usage (kWh)";
        accumulativeTable[0][4] = "TPT Cost (Rands)";
        accumulativeTable[0][5] = "AMS Usage (kWh)";
        accumulativeTable[0][6] = "AMS Cost (Rands)";
        accumulativeTable[0][7] = "Maximum Demand Charge (Rands)";
        accumulativeTable[0][8] = "Berth Active Time (Hours)";
        accumulativeTable[0][9] = "PoN Usage (kWh)";
        accumulativeTable[0][10] = "PoN Cost (Rands)";

        for (int m = 0; m < months.size(); m++) {
            Month month = months.get(m);
            for (int a = 0; a < month.getAttributes().size(); a++) {
                accumulativeTable[m + 1][a] = month.getAttributes().get(a);
            }
        }

        accumulativeTable[0][12] = "ACCUMULATIVE";
        accumulativeTable[0][13] = "Combo Usage (kWh)";
        accumulativeTable[0][14] = "Combo Cost (Rands)";
        accumulativeTable[0][15] = "TPT Usage (kWh)";
        accumulativeTable[0][16] = "TPT Cost (Rands)";
        accumulativeTable[0][17] = "AMS Usage (kWh)";
        accumulativeTable[0][18] = "AMS Cost (Rands)";
        accumulativeTable[0][19] = "Maximum Demand Charge (Rands)";
        accumulativeTable[0][20] = "Berth Active Time (Hours)";
        accumulativeTable[0][21] = "PoN Usage (kWh)";
        accumulativeTable[0][22] = "PoN Cost (Rands)";

        List<String> accumulator = months.get(0).getAttributes();
        for (int m = 1; m < months.size(); m++) {
            Month month = months.get(m);
            for (int a = 1; a < month.getAttributes().size(); a++) {
                accumulativeTable[m][a + 12] = accumulator.get(a);
                accumulator.set(a, accumulator.get(a) + month.getAttributes().get(a));
            }
        }

        accumulativeTable[0][24] = "Combo Total Usage (kWh)";
        accumulativeTable[0][25] = "PoN Total Usage (kWh)";
        accumulativeTable[0][26] = "AMS Total Usage kWh)";

        accumulativeTable[1][24] = "0.0";
        accumulativeTable[1][25] = "0.0";
        accumulativeTable[1][26] = "0.0";

        for (Month month : months) {
            accumulativeTable[1][24] = String.valueOf(Double.valueOf(month.getCombokWh()) +
                    Double.valueOf(accumulativeTable[1][24]));
            accumulativeTable[1][25] = String.valueOf(Double.valueOf(month.getPoNkWh()) +
                    Double.valueOf(accumulativeTable[1][25]));
            accumulativeTable[1][26] = String.valueOf(Double.valueOf(month.getAMSkWh()) +
                    Double.valueOf(accumulativeTable[1][26]));
        }


        accumulativeTable[0][27] = "Combo Total Cost (Rands)";
        accumulativeTable[0][28] = "PoN Total Cost (Rands)";
        accumulativeTable[0][29] = "AMS Total Cost (Rands)";

        accumulativeTable[1][27] = "0.0";
        accumulativeTable[1][28] = "0.0";
        accumulativeTable[1][29] = "0.0";

        for (Month month : months) {
            accumulativeTable[1][27] = String.valueOf(Double.valueOf(month.getComboCost()) +
                    Double.valueOf(accumulativeTable[1][27]));
            accumulativeTable[1][28] = String.valueOf(Double.valueOf(month.getPoNCost()) +
                    Double.valueOf(accumulativeTable[1][28]));
            accumulativeTable[1][29] = String.valueOf(Double.valueOf(month.getAMSCost()) +
                    Double.valueOf(accumulativeTable[1][29]));
        }

        accumulativeTable[0][31] = "PoN Energy Usage Percentage";
        accumulativeTable[1][31] =
                String.valueOf(Double.valueOf(accumulativeTable[1][25]) /
                        Double.valueOf(accumulativeTable[1][24]) * 100.0);

        accumulativeTable[0][32] = "AMS Energy Usage Percentage";
        accumulativeTable[1][32] =
                String.valueOf(Double.valueOf(accumulativeTable[1][26]) /
                        Double.valueOf(accumulativeTable[1][24]) * 100.0);

        accumulativeTable[0][34] = "PoN Energy Cost Percentage";
        accumulativeTable[1][34] =
                String.valueOf(Double.valueOf(accumulativeTable[1][28]) /
                        Double.valueOf(accumulativeTable[1][27]) * 100.0);

        accumulativeTable[0][35] = "AMS Energy Cost Percentage";
        accumulativeTable[1][35] =
                String.valueOf(Double.valueOf(accumulativeTable[1][29]) /
                        Double.valueOf(accumulativeTable[1][27]) * 100.0);

        return new Pair<>("Accumulative", accumulativeTable);
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

    private void displayReport(Stage stage) {
        ChartViewer viewer = new ChartViewer(chart);
        stage.setScene(new Scene(viewer));
        stage.setTitle("Energy Chart Viewer");
        stage.setWidth(700);
        stage.setHeight(390);
        stage.show();
    }

}
