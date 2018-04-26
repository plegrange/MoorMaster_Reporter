package Weather;


import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultWindDataset;

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
    List<List<Pair<Integer, Double>>> dailyAverages;

    public void generateReport(ObservableList<Ship> ships, String reportTitle) {
        xlsWriter = new XLSWriter();
        xlsWriter.writeWorkbook(ships, reportTitle);
        createWeatherPlots(ships, reportTitle);
    }

    private class Month {
        private int month, year;

        public Month(int month, int year) {
            this.month = month;
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        public boolean isSameMonth(Month other) {
            if (this.year == other.year && this.month == other.month) {
                return true;
            }
            return false;
        }
    }

    private ObservableList<String> monthIndex =
            FXCollections.observableArrayList(
                    "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

    private void outputMonthlyAverages(List<Ship> ships) {
        List<List<Pair<String, Double>>> monthlyAverages = new ArrayList<>();
        List<Month> months = populateMonths(ships);
        for (Month m : months) {
            double hsLongTotal;
            double tpLongTotal;
            double hsSwellTotal;
            double tpSwellTotal;
            for (int i = 0; i < 4; i++) {
                monthlyAverages.add(new ArrayList<>());
            }
            int counter;
            int daysInMonth;
            switch (m.getMonth()) {
                case 0:
                case 2:
                case 4:
                case 6:
                case 7:
                case 9:
                case 11:
                    daysInMonth = 31;
                    break;
                case 1:
                    daysInMonth = 28;
                    break;
                default:
                    daysInMonth = 30;
                    break;
            }
            for (int day = 0; day < daysInMonth; day++) {
                hsLongTotal = 0.0;
                tpLongTotal = 0.0;
                hsSwellTotal = 0.0;
                tpSwellTotal = 0.0;
                counter = 0;
                boolean found = false;
                for (Ship ship : ships) {
                    List<Date> dates = ship.getTimeStamps();
                    for (int i = 0; i < dates.size(); i++) {
                        Date date = dates.get(i);
                        if (m.isSameMonth(new Month(date.getMonth(), date.getYear()))) {
                            if (date.getDate() == day) {
                                hsLongTotal = hsLongTotal + ship.getHsLong().get(i);
                                tpLongTotal = tpLongTotal + ship.getTpLong().get(i);
                                hsSwellTotal = hsSwellTotal + ship.getHsSwell().get(i);
                                tpSwellTotal = tpSwellTotal + ship.getTpSwell().get(i);
                                counter++;
                                found = true;
                            }
                        }
                    }
                }
                if (found) {
                    monthlyAverages = addToLists(monthlyAverages, new Pair<>(String.valueOf(day), hsLongTotal / (counter * 1.0)), 0, day+1);
                    monthlyAverages = addToLists(monthlyAverages, new Pair<>(String.valueOf(day), tpLongTotal / (counter * 1.0)), 1, day+1);
                    monthlyAverages = addToLists(monthlyAverages, new Pair<>(String.valueOf(day), hsSwellTotal / (counter * 1.0)), 2, day+1);
                    monthlyAverages = addToLists(monthlyAverages, new Pair<>(String.valueOf(day), tpSwellTotal / (counter * 1.0)), 3, day+1);
                } else {
                    monthlyAverages = addToLists(monthlyAverages, null, 0, day+1);
                    monthlyAverages = addToLists(monthlyAverages, null, 1, day+1);
                    monthlyAverages = addToLists(monthlyAverages, null, 2, day+1);
                    monthlyAverages = addToLists(monthlyAverages, null, 3, day+1);
                }
            }
            chartGenerator.resetData();
            chartGenerator.addData(monthlyAverages.get(0), "HS Long");
            chartGenerator.addData(monthlyAverages.get(2), "HS Swell");
            chart = chartGenerator.createBarChart("Daily Average HS", "Day of month", "Length", monthIndex.get(m.getMonth()) + " " + String.valueOf(m.getYear()));
            amendPDFDoc(1000, 800, chart);

            chartGenerator.resetData();
            chartGenerator.addData(monthlyAverages.get(1), "TP Long");
            chartGenerator.addData(monthlyAverages.get(3), "TP Swell");
            chart = chartGenerator.createBarChart("Daily Average TP", "Day of month", "Period", monthIndex.get(m.getMonth()) + " " + String.valueOf(m.getYear()));
            amendPDFDoc(1000, 800, chart);
        }
    }

    private List<List<Pair<String, Double>>> addToLists(List<List<Pair<String, Double>>> lists, Pair<String, Double> pair, int index, int day) {
        List<Pair<String, Double>> list = lists.get(index);
        if (pair == null) {
            list.add(new Pair<>(String.valueOf(day), 0.0));
        } else {
            list.add(pair);
        }
        lists.set(index, list);
        return lists;
    }

    private List<Month> populateMonths(List<Ship> ships) {
        List<Month> months = new ArrayList<>();
        for (Ship ship : ships) {
            List<Date> dates = ship.getTimeStamps();
            for (Date date : dates) {
                Month month = new Month(date.getMonth(), date.getYear());
                months = insertUnique(months, month);
            }
        }
        return months;
    }

    private List<Month> insertUnique(List<Month> months, Month month) {
        for (Month m : months) {
            if (m.isSameMonth(month))
                return months;
        }
        months.add(month);
        return months;
    }

    private void createWeatherPlots(List<Ship> ships, String reportTitle) {

        createPDFDoc(reportTitle, "MoorMaster Reporter");
        chartGenerator = new ChartGenerator();
        Object[][][] windDataSet;
        List<String> seriesKeys;

        for (Ship ship : ships) {
            seriesKeys = new ArrayList<>();
            seriesKeys.add(ship.getName());
            windDataSet = ship.getWindData();
            chart = chartGenerator.createWindChart(ship.getName(), "Hour", "Wind Force (Beaufort-Scale : 1-12)", new DefaultWindDataset(seriesKeys, windDataSet));
            chart.addSubtitle(new TextTitle(ship.getStartDate() + " - " + ship.getEndDate()));
            XYPlot xyPlot = chart.getXYPlot();
            xyPlot.getRenderer().setSeriesItemLabelsVisible(0, false);
            ValueAxis domainAxis = xyPlot.getDomainAxis();
            ValueAxis rangeAxis = xyPlot.getRangeAxis();
            Range domain = domainAxis.getRange();
            double maxRange = getMaxWindForce(ship.getWindSpeeds());
            rangeAxis.setUpperMargin(0.1);
            rangeAxis.setLowerMargin(0.1);
            rangeAxis.setRange(-maxRange, maxRange);
            domainAxis.setUpperMargin(0.1);
            domainAxis.setLowerMargin(0.1);
            amendPDFDoc(1000, 800, chart);

        }
        outputMonthlyAverages(ships);

        writePDFDoc(reportTitle.replaceAll(".xls", ""));
    }

    List<Pair<String, Double>> hsLongList = new ArrayList<>(), tpLongList = new ArrayList<>(), hsSwellList = new ArrayList<>(), tpSwellList = new ArrayList<>();

    private List<Pair<String, Double>> addDataToSet(List<Pair<String, Double>> dataSet, Double value, String date) {
        Pair<String, Double> dataPoint;
        dataPoint = new Pair(date, value);
        dataSet.add(dataPoint);
        return dataSet;
    }

    private double getMaxWindForce(List<Double> windForces) {
        double max = -99999;
        for (Double d : windForces) {
            if (d > max) {
                max = d;
            }
        }
        return max;
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
