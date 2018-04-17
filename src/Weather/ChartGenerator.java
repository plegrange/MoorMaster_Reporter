package Weather;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.WindDataset;

public class ChartGenerator implements ChartMouseListenerFX {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;

    public JFreeChart createWindChart(String title, String xLabel, String yLabel, WindDataset windDataset) {
        return ChartFactory.createWindPlot(title, xLabel, yLabel, windDataset, true, false, false);
    }

    @Override
    public void chartMouseClicked(ChartMouseEventFX chartMouseEventFX) {

    }

    @Override
    public void chartMouseMoved(ChartMouseEventFX chartMouseEventFX) {

    }
}
