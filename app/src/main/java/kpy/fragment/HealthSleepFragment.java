package kpy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.achartengine.GraphicalView;
import kpy.bracelet.R;
import kpy.chart.MyChart;

public class HealthSleepFragment extends BaseFragment {

    private MyChart sleepChart = new MyChart();
    private LinearLayout chartSleepView;
	private View view;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.inflater=inflater;
		view = inflater.inflate(R.layout.activity_health_sleep, container, false);
		init();
		return view;
	}

	public void init() {
		// TODO Auto-generated method stub
		chartSleepView = (LinearLayout) view.findViewById(R.id.chartSleepView);
        setSleepChart();
        GraphicalView sleepView = sleepChart.executeView(getActivity());
        chartSleepView.addView(sleepView);
	}

    public void setSleepChart() {
        sleepChart.setTitles(new String[]{"睡眠深度"});
        sleepChart.setX_axis(new int[]{15, 16, 17, 17, 18, 19, 19, 20, 20, 21, 22});
        sleepChart.setY_axis(new int[]{100, 100, 100, 50, 50, 50, 100, 100, 50, 50, 50});
        sleepChart.setChartTitle("近八小时睡眠深度统计");
        sleepChart.setxTitle("小时");
        sleepChart.setyTitle("睡眠深度");
        sleepChart.setxMin(14.5);
        sleepChart.setxMax(22.5);
        sleepChart.setyMin(30);
        sleepChart.setyMax(120);
        sleepChart.setPanLimits(new double[]{14, 23, 0, 200});
        sleepChart.setZoomLimits(new double[]{14, 23, 0, 200});
    }
}
