package kpy.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import java.util.ArrayList;
import java.util.List;


public class MyChart extends AbstractDemoChart {
    private String[] titles;//折线或者表格等内容
    private int[] x_axis;//x轴刻度
    private int[] y_axis;//y轴刻度
    private String chartTitle;//chart标题
    private String xTitle;//x轴标题
    private String yTitle;//y轴标题
    private double xMin;//x轴默认起始值
    private double xMax;//x轴默认结束值
    private double yMin;//y轴默认起始值
    private double yMax;//y轴默认结束值
    private double[] panLimits;//滑动坐标限制的范围
    private double[] zoomLimits;//缩放键限制缩放的范围

    public MyChart(){
        titles = new String[]{"步数"};
        x_axis = new int[]{1, 2, 3, 4, 5, 6, 7};
        y_axis = new int[]{8000, 9000, 10245, 8457, 3264, 15620, 19248};
        chartTitle = "近7天步数统计";
        xTitle = "步数";
        yTitle = "近7天";
        xMin = 0.5;
        xMax = 7.5;
        yMin = 0;
        yMax = 25000;
        panLimits = zoomLimits = new double[]{0.5, 7.5, 0, 30000};
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public void setX_axis(int[] x_axis) {
        this.x_axis = x_axis;
    }

    public void setY_axis(int[] y_axis) {
        this.y_axis = y_axis;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public void setxTitle(String xTitle) {
        this.xTitle = xTitle;
    }

    public void setyTitle(String yTitle) {
        this.yTitle = yTitle;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public void setPanLimits(double[] panLimits) {
        this.panLimits = panLimits;
    }

    public void setZoomLimits(double[] zoomLimits) {
        this.zoomLimits = zoomLimits;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public Intent execute(Context context) {
        return null;
    }

    @Override
    public GraphicalView executeView(Context context) {
        List<int[]> x = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            x.add(x_axis);
        }
        List<int[]> values = new ArrayList<>();
        values.add(y_axis);
        int[] colors = new int[]{Color.BLUE};
        PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        setChartSettings(renderer, chartTitle, xTitle, yTitle, xMin, xMax, yMin, yMax,
                Color.BLACK, Color.BLACK);

        renderer.setLabelsColor(Color.BLACK);
        renderer.setMargins(new int[]{50, 50, 50, 50});//边缘距离
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.setAxisTitleTextSize(25);
        renderer.setLabelsTextSize(25);
        renderer.setChartValuesTextSize(25);
        renderer.setLegendTextSize(25);
        renderer.setChartTitleTextSize(25);
        renderer.setLabelsColor(Color.RED);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setPanEnabled(true, true);
        renderer.setInScroll(true);//为了能在Scroll里使用
        renderer.setXLabels(7);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Paint.Align.LEFT);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setZoomButtonsVisible(true);
        renderer.setPanLimits(panLimits);
        renderer.setZoomLimits(zoomLimits);

        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
        //最后一个参数是设置actionbar的标题
        GraphicalView lineView = ChartFactory.getLineChartView(context, dataset, renderer);
        return lineView;
    }
}
