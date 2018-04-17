package Weather;

import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.WindDataset;

import java.awt.*;
import java.util.List;


public class ChartGenerator implements ChartMouseListenerFX {
    private DefaultCategoryDataset dataset;

    public void resetData() {
        dataset = new DefaultCategoryDataset();
    }

    public void addData(List<Pair<String, Double>> A, String a) {
        for (int i = 0; i < A.size(); i++) {
            dataset.addValue(A.get(i).getValue(), a, A.get(i).getKey());
        }
    }

    public JFreeChart createWindChart(String title, String xLabel, String yLabel, WindDataset windDataset) {
        return ChartFactory.createWindPlot(title, xLabel, yLabel, windDataset, true, false, false);
    }

    public JFreeChart createBarChart(String title, String xLabel, String yLabel, String subtitle) {
        JFreeChart chart = ChartFactory.createBarChart(title, xLabel, yLabel, dataset);
        chart.addSubtitle(new TextTitle(subtitle));
        chart.setBorderPaint(Color.white);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        chart.getLegend().setFrame(BlockBorder.NONE);
        return chart;
    }

    @Override
    public void chartMouseClicked(ChartMouseEventFX chartMouseEventFX) {

    }

    @Override
    public void chartMouseMoved(ChartMouseEventFX chartMouseEventFX) {

    }
}
