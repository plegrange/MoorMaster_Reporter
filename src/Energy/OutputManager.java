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
import java.util.ArrayList;
import java.util.List;

public class OutputManager {

    private ChartGenerator chartGenerator;
    private JFreeChart chart;
    private PDFDocument pdfDoc;
    private XLSWriter xlsWriter;

    public void generateReport(List<Month> months, Stage stage, String startDate, String endDate) {
        List<Pair<String, Double>> setA = new ArrayList<>(), setB = new ArrayList<>(), setC = new ArrayList<>();
        createPDFDoc("Report_" + months.get(0).getMonth() + "_" + months.get(0).getYear() + "-" + months.get(months.size() - 1).getMonth() + "_" + months.get(months.size() - 1).getYear(), "SimiusDev");

        chartGenerator = new ChartGenerator();
        //Chart 1
        for (Month m : months) {
            Pair<String, Double> a = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getCombokWh()));
            setA.add(a);
            Pair<String, Double> b = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getAMSkWh()));
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
        amendPDFDoc(700, 400, chart);
        chartGenerator.resetData();

        //Chart 2
        setA = new ArrayList<>();
        setB = new ArrayList<>();
        double sumA = 0.0, sumB = 0.0;
        for (Month m : months) {
            m.calculateTotalMonthHours();
            Pair<String, Double> a = new Pair<>(m.getMonth() + "-" + m.getYear(), sumA = Double.valueOf(m.getBerthMonthTotal()) + sumA);
            setA.add(a);
            Pair<String, Double> b = new Pair<>(m.getMonth() + "-" + m.getYear(), sumB = Double.valueOf(m.getAMSkWh()) + sumB);
            setB.add(b);
        }
        chartGenerator.addData(setA, "Berth Active Time (Hours)");
        chartGenerator.addData(setB, "AMS Usage (kWh)");
        chart = chartGenerator.createBarChart("Cumulative Activity vs AMS Usage", "Time (Months)", "Active Time (Hours) / Usage (kWh)", "");
        amendPDFDoc(700, 400, chart);
        chartGenerator.resetData();

        //Chart 3
        setA = new ArrayList<>();
        setB = new ArrayList<>();
        for (Month m : months) {
            m.calculateTotalMonthHours();
            Pair<String, Double> a = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getBerthMonthTotal()));
            setA.add(a);
            Pair<String, Double> b = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getAMSkWh()));
            setB.add(b);
        }
        chartGenerator.addData(setA, "Berth Active Time (Hours)");
        chartGenerator.addData(setB, "AMS Usage (kWh)");
        chart = chartGenerator.createBarChart("Monthly Activity vs AMS Usage", "Time (Months)", "Active Time (Hours) / Usage (kWh)", "");
        amendPDFDoc(700, 400, chart);
        chartGenerator.resetData();

        //Chart 4
        setA = new ArrayList<>();
        setB = new ArrayList<>();
        setC = new ArrayList<>();
        for (Month m : months) {
            Pair<String, Double> a = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getComboCost()));
            setA.add(a);
            Pair<String, Double> b = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getAMSCost()));
            setB.add(b);
            Pair<String, Double> c = new Pair<>(m.getMonth() + "-" + m.getYear(), Double.valueOf(m.getPoNCost()));
            setC.add(c);
        }
        chartGenerator.resetData();
        chartGenerator.addData(setA, "Combo Cost (Rands)");
        chartGenerator.addData(setB, "AMS Cost (Rands)");
        chartGenerator.addData(setC, "PoN Cost (Rands)");
        chart = chartGenerator.createBarChart("Monthly Energy Cost", "Time (Months)", "Cost (Rands)", "Combo vs AMS vs PoN");
        //displayReport(stage);
        amendPDFDoc(700, 400, chart);
        chartGenerator.resetData();

        //Chart 5

        writePDFDoc("Report_" + months.get(0).getMonth() + "_" +
                months.get(0).getYear() + "-" +
                months.get(months.size() - 1).getMonth() +
                "_" + months.get(months.size() - 1).getYear());

        xlsWriter = new XLSWriter();
        xlsWriter.writeWorkbook(months, buildTables(months));
    }

    private List<Pair<String, String[][]>> buildTables(List<Month> months) {
        List<Pair<String, String[][]>> tables = new ArrayList<>();
        tables.add(buildCumulativeTable(months));
        return tables;
    }

    private Pair<String, String[][]> buildCumulativeTable(List<Month> months) {
        String[][] cumulativeTable = new String[36][months.size() + 1];

        cumulativeTable[1][0] = "Combo Usage (kWh)";
        cumulativeTable[2][0] = "Combo Cost (Rands)";
        cumulativeTable[3][0] = "TPT Usage (kWh)";
        cumulativeTable[4][0] = "TPT Cost (Rands)";
        cumulativeTable[5][0] = "AMS Usage (kWh)";
        cumulativeTable[6][0] = "AMS Cost (Rands)";
        cumulativeTable[7][0] = "Maximum Demand Charge (Rands)";
        cumulativeTable[8][0] = "Berth Active Time (Hours)";
        cumulativeTable[9][0] = "PoN Usage (kWh)";
        cumulativeTable[10][0] = "PoN Cost (Rands)";

        for (int m = 0; m < months.size() - 1; m++) {
            Month month = months.get(m);
            month.calculateTotalMonthHours();
            for (int a = 0; a < month.getAttributes().size(); a++) {
                cumulativeTable[a][m + 1] = month.getAttributes().get(a);
            }
        }

        cumulativeTable[12][0] = "CUMULATIVE";
        cumulativeTable[13][0] = "Combo Usage (kWh)";
        cumulativeTable[14][0] = "Combo Cost (Rands)";
        cumulativeTable[15][0] = "TPT Usage (kWh)";
        cumulativeTable[16][0] = "TPT Cost (Rands)";
        cumulativeTable[17][0] = "AMS Usage (kWh)";
        cumulativeTable[18][0] = "AMS Cost (Rands)";
        cumulativeTable[19][0] = "Maximum Demand Charge (Rands)";
        cumulativeTable[20][0] = "Berth Active Time (Hours)";
        cumulativeTable[21][0] = "PoN Usage (kWh)";
        cumulativeTable[22][0] = "PoN Cost (Rands)";

        Month month = months.get(0);
        for (int a = 1; a < month.getAttributes().size(); a++) {
            cumulativeTable[a + 12][1] = month.getAttributes().get(a);
        }
        for (int m = 1; m < months.size() - 1; m++) {
            month = months.get(m);
            for (int a = 1; a < month.getAttributes().size(); a++) {
                cumulativeTable[a + 12][m + 1] = add(cumulativeTable[a + 12][m], month.getAttributes().get(a));
            }
        }

        cumulativeTable[24][0] = "Combo Total Usage (kWh)";
        cumulativeTable[25][0] = "PoN Total Usage (kWh)";
        cumulativeTable[26][0] = "AMS Total Usage kWh)";

        cumulativeTable[24][1] = "0.0";
        cumulativeTable[25][1] = "0.0";
        cumulativeTable[26][1] = "0.0";

        for (Month m : months) {
            cumulativeTable[24][1] = String.valueOf(Double.valueOf(m.getCombokWh()) +
                    Double.valueOf(cumulativeTable[24][1]));
            cumulativeTable[25][1] = String.valueOf(Double.valueOf(m.getPoNkWh()) +
                    Double.valueOf(cumulativeTable[25][1]));
            cumulativeTable[26][1] = String.valueOf(Double.valueOf(m.getAMSkWh()) +
                    Double.valueOf(cumulativeTable[26][1]));
        }


        cumulativeTable[27][0] = "Combo Total Cost (Rands)";
        cumulativeTable[28][0] = "PoN Total Cost (Rands)";
        cumulativeTable[29][0] = "AMS Total Cost (Rands)";

        cumulativeTable[27][1] = "0.0";
        cumulativeTable[28][1] = "0.0";
        cumulativeTable[29][1] = "0.0";

        for (Month m : months) {
            cumulativeTable[27][1] = String.valueOf(Double.valueOf(month.getComboCost()) +
                    Double.valueOf(cumulativeTable[27][1]));
            cumulativeTable[28][1] = String.valueOf(Double.valueOf(month.getPoNCost()) +
                    Double.valueOf(cumulativeTable[28][1]));
            cumulativeTable[29][1] = String.valueOf(Double.valueOf(month.getAMSCost()) +
                    Double.valueOf(cumulativeTable[29][1]));
        }

        cumulativeTable[31][0] = "PoN Energy Usage Percentage";
        cumulativeTable[31][1] =
                String.valueOf(Double.valueOf(cumulativeTable[25][1]) /
                        Double.valueOf(cumulativeTable[24][1]) * 100.0);

        cumulativeTable[32][0] = "AMS Energy Usage Percentage";
        cumulativeTable[32][1] =
                String.valueOf(Double.valueOf(cumulativeTable[26][1]) /
                        Double.valueOf(cumulativeTable[24][1]) * 100.0);

        cumulativeTable[34][0] = "PoN Energy Cost Percentage";
        cumulativeTable[34][1] =
                String.valueOf(Double.valueOf(cumulativeTable[28][1]) /
                        Double.valueOf(cumulativeTable[27][1]) * 100.0);

        cumulativeTable[35][0] = "AMS Energy Cost Percentage";
        cumulativeTable[35][1] =
                String.valueOf(Double.valueOf(cumulativeTable[29][1]) /
                        Double.valueOf(cumulativeTable[27][1]) * 100.0);

        return new Pair<>("Accumulative", cumulativeTable);
    }

    private String add(String a, String b) {
        return String.valueOf(Math.round((Double.valueOf(a) + Double.valueOf(b)) * 100.0) / 100.0);
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
