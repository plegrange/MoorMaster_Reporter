package Energy;

import org.jfree.chart.axis.NumberAxis;
import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class ChartGenerator implements ChartMouseListenerFX {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;

    public void resetData() {
        dataset = new DefaultCategoryDataset();
    }

    public void addData(List<Pair<String, Double>> A, String a) {
        for (int i = 0; i < A.size(); i++) {
            dataset.addValue(A.get(i).getValue(), a, A.get(i).getKey());
        }
    }

    public JFreeChart createBarChart(String title, String xLabel, String yLabel, String subtitle) {
        chart = ChartFactory.createBarChart(title, xLabel, yLabel, dataset);
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

    public JFreeChart createPieChart(String title, String subtitle, PieDataset pieDataset) {
        chart = ChartFactory.createPieChart(title, pieDataset);
        chart.setBackgroundPaint(new GradientPaint(new Point(0, 0),
                new Color(20, 20, 20), new Point(400, 200), Color.DARK_GRAY));

        // customise the title position and font
        TextTitle t = chart.getTitle();
        t.setHorizontalAlignment(HorizontalAlignment.LEFT);
        t.setPaint(new Color(240, 240, 240));
        t.setFont(new Font("Arial", Font.BOLD, 26));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setInteriorGap(0.04);
        plot.setOutlineVisible(false);
        plot.setDefaultSectionOutlinePaint(Color.WHITE);
        plot.setSectionOutlinesVisible(true);
        plot.setDefaultSectionOutlineStroke(new BasicStroke(2.0f));

        chart.addSubtitle(new TextTitle(subtitle));
        return chart;
    }

    private RadialGradientPaint createGradientPaint(Color c1, Color c2) {
        Point2D center = new Point2D.Float(0, 0);
        float radius = 200;
        float[] dist = {0.0f, 1.0f};
        return new RadialGradientPaint(center, radius, dist,
                new Color[]{c1, c2});
    }

    @Override
    public void chartMouseClicked(ChartMouseEventFX chartMouseEventFX) {

    }

    @Override
    public void chartMouseMoved(ChartMouseEventFX chartMouseEventFX) {

    }
}
