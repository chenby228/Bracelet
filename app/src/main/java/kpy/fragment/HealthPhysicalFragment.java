package kpy.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.GraphicalView;

import java.util.Random;

import kpy.bracelet.R;
import kpy.chart.MyChart;
import kpy.view.AlarmReceiver;

public class HealthPhysicalFragment extends BaseFragment {

    private View view;
	private LayoutInflater inflater;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MyChart temperatureChart = new MyChart();
    private MyChart heartRateChart = new MyChart();
    private GraphicalView temView;
    private GraphicalView heartView;
    private LinearLayout chartTemperatureView;
    private LinearLayout chartHeartRateView;
    private TextView realTemperature;
    private TextView realHeartRate;
    private int NowTemp;
    private int NowHeart;
    private int AveTemp;
    private int AveHeart;
    private int AveNowTemp;
    private int AveNowHeart;
    private AlarmManager alarmManager;

    //数据
    private int TempXData[]=new int[]{15, 16, 17, 18, 19, 20, 21, 22};
    private int TempYData[]=new int[]{36, 36, 36, 36, 36, 36, 36, 36};

    private int HeartXData[]=new int[]{15, 16, 17, 18, 19, 20, 21, 22};
    private int HeartYData[]=new int[]{78, 70, 75, 65, 80, 125, 121, 74};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater=inflater;
        view = inflater.inflate(R.layout.activity_health_physical, container, false);
        init();
        mTimeHandler.sendEmptyMessageDelayed(0, 10000);
        addListener();
        alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

        return view;
    }

    private void addListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        chartTemperatureView.removeAllViews();
                        UpdateTemperature();
                        chartHeartRateView.removeAllViews();
                        UpdateHeartRate();
                        AveTemp=AveNowTemp;
                        AveHeart=AveNowHeart;
                    }
                }, 1000);
            }
        });
    }

    private void UpdateTemperature() {
        AveNowTemp=0;
        Random random = new Random();
        for(int i=0;i<8;i++)
        {
            TempYData[i]= random.nextInt(39)%4 + 36;
            AveNowTemp=AveNowTemp+TempYData[i];
        }
        AveNowTemp=AveNowTemp/8;
        temperatureChart.setY_axis(TempYData);
        setChartTemperature();
        temView = temperatureChart.executeView(getActivity());
        chartTemperatureView.addView(temView);
        AveTemp=AveNowTemp;
    }

    private void UpdateHeartRate() {
        AveNowHeart=0;
        Random random = new Random();
        for(int i=0;i<8;i++)
        {
            HeartYData[i]= random.nextInt(90)%31 + 60;
            AveNowHeart=AveNowHeart+HeartYData[i];
        }
        AveNowHeart=AveNowHeart/8;
        heartRateChart.setY_axis(HeartYData);
        setChartHeartRate();
        heartView = heartRateChart.executeView(getActivity());
        chartHeartRateView.addView(heartView);
        AveHeart=AveNowHeart;
    }

    public void init() {

        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_fresh);
        chartTemperatureView = (LinearLayout) view.findViewById(R.id.chartTemperature);
        chartHeartRateView = (LinearLayout) view.findViewById(R.id.chartHeartRate);
        realTemperature= (TextView) view.findViewById(R.id.realTemperature);
        realHeartRate= (TextView) view.findViewById(R.id.realHeartRate);
        UpdateTemperature();
        UpdateHeartRate();
	}

    public void setChartTemperature() {
        temperatureChart.setTitles(new String[]{"体温"});
        temperatureChart.setX_axis(TempXData);
        temperatureChart.setY_axis(TempYData);
        temperatureChart.setChartTitle("近八小时体温统计");
        temperatureChart.setxTitle("小时");
        temperatureChart.setyTitle("体温");
        temperatureChart.setxMin(14.5);
        temperatureChart.setxMax(22.5);
        temperatureChart.setyMin(35);
        temperatureChart.setyMax(42);
        temperatureChart.setPanLimits(new double[]{14, 23, 35, 60});
        temperatureChart.setZoomLimits(new double[]{14, 23, 35, 60});
    }

    public void setChartHeartRate() {
        heartRateChart.setTitles(new String[]{"心率"});
        heartRateChart.setX_axis(HeartXData);
        heartRateChart.setY_axis(HeartYData);
        heartRateChart.setChartTitle("近八小时心率统计");
        heartRateChart.setxTitle("小时");
        heartRateChart.setyTitle("心率");
        heartRateChart.setxMin(14.5);
        heartRateChart.setxMax(22.5);
        heartRateChart.setyMin(0);
        heartRateChart.setyMax(200);
        heartRateChart.setPanLimits(new double[]{14, 23, 0, 200});
        heartRateChart.setZoomLimits(new double[]{14, 23, 0, 200});
    }


    Handler mTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                Random random = new Random();
                NowTemp= random.nextInt(38)%3 + 36;
                realTemperature.setText(""+NowTemp+" ℃");

                Random Heart_random = new Random();
                NowHeart= Heart_random.nextInt(90)%31 + 60;
                realHeartRate.setText(""+NowHeart+" 次/分");

//                Toast.makeText(getContext(),""+AveTemp+AveHeart,Toast.LENGTH_SHORT).show();
                if(NowTemp>AveTemp+1||NowHeart>AveHeart+15) {
                    Intent intent = new Intent(getContext(), AlarmReceiver.class);//创建Intent对象
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);//创建PendingIntent
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);//设置闹钟，当前时间就唤醒
                }
                sendEmptyMessageDelayed(0, 10000);
            }
        }
    };
}
