package kpy.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.achartengine.GraphicalView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kpy.bean.StepData;
import kpy.bracelet.R;
import kpy.chart.MyChart;
import kpy.sqlite.MonthDataDBHelper;
import kpy.sqlite.MotionDataDBHelper;
import kpy.sqlite.YearDataBDHelper;
import kpy.utils.DbUtils;

public class HealthMotionFragment extends BaseFragment {

	private MyChart weekChart = new MyChart();// 本周步数折线图
	private MyChart monthChart = new MyChart();// 本月步数折线图
	private MyChart yearChart = new MyChart();// 年度步数折线图

	//定义3个LinearLayout，用于装载3种不同的图表视图
	private LinearLayout weekChartView;
	private LinearLayout monthChartView;
	private LinearLayout yearChartView;

	//定义4个LinearLayout，用于3个标签切换
	private LinearLayout weekTab;
	private LinearLayout monthTab;
	private LinearLayout yearTab;
	private LinearLayout[] tabs;
	private LinearLayout content;// 用于加载图表以下视图布局
	private View viewSports;// 图表以下视图布局
	private View view;

	//今日计步
	private TextView todaySteps;
	private TextView realTimeSteps;
	private String CURRENTDATE;
	private int nowSteps;

	//用于接收3种不同图表的view
	private GraphicalView weekView;
	private GraphicalView monthView;
	private GraphicalView yearView;

	//数据
	int XData[]=new int[]{1,2,3,4,5,6,7};
	int YData[]=new int[]{8000,2000,3000,4000,5000,6000,7000};

	int MonthXData[]=new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
			16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
	int MonthYData[]=new int[] {8000,9000,12212,10245,8457,3264,15620,19248,4587,1265,8987,21546,12549,4654,22596,
			11522,12510,7554,7899,4653,5621,2345,2358,5345,8954,23546,6515,4521,12321,6541,3121 };

	int YearXData[]=new int[]{1,2,3,4,5,6,7,8,9,10,11,12};
	int YearYData[]=new int[]{8000,9000,10245,8457,3264,15620,19248,4587,1265,8987,21546,12549};


    private MotionDataDBHelper motionDataDBHelper;
	private MonthDataDBHelper monthDataDBHelper;
	private YearDataBDHelper yearDataDBHelper;

    private SQLiteDatabase database;
	private SQLiteDatabase monthbase;
	private SQLiteDatabase yearbase;

	private int steps;
	private int monthsteps;
	private int yearsteps;
	private int history_steps;
    private int save_steps;

	private Cursor cursor;
	private Cursor monthcursor;
	private Cursor yearcursor;

	private ContentValues values;
	private ContentValues monthvalues;
	private ContentValues yearvalues;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.inflater=inflater;
		//周数据库
		motionDataDBHelper=new MotionDataDBHelper(getActivity(),"Motion.db",null,1);
		database=motionDataDBHelper.getWritableDatabase();
		//月数据库
		monthDataDBHelper=new MonthDataDBHelper(getActivity(),"MonthData.db",null,1);
		monthbase=monthDataDBHelper.getWritableDatabase();
		//年数据库
		yearDataDBHelper=new YearDataBDHelper(getActivity(),"YearData.db",null,1);
		yearbase=yearDataDBHelper.getWritableDatabase();

		view = inflater.inflate(R.layout.activity_health_motion, container, false);
		mTimeHandler.sendEmptyMessageDelayed(0, 1000);

		init();
		iniData();
		updateChart();
		initMonth();
		initYear();
		return view;
	}

	//初始化每周数据
	private void iniData()
	{
		values=new ContentValues();
		//星期一
		cursor= database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期一"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期一");
			values.put("motion_steps",5000);
			database.insert("MotionData",null,values);
			values.clear();
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[0]=steps;
		}

		//星期二
		cursor = database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期二"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期二");
			values.put("motion_steps",20000);
			database.insert("MotionData",null,values);
			values.clear();
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[1]=steps;
		}

		//星期三
		cursor = database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期三"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期三");
			values.put("motion_steps",3000);
			database.insert("MotionData",null,values);
			values.clear();
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[2]=steps;
		}

		//星期四
		cursor = database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期四"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期四");
			values.put("motion_steps",900);
			database.insert("MotionData",null,values);
			values.clear();
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[3]=steps;
		}

		//星期五
		cursor = database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期五"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期五");
			values.put("motion_steps",6000);
			database.insert("MotionData",null,values);
			values.clear();
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[4]=steps;
		}

		//星期六
		cursor = database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期六"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期六");
			values.put("motion_steps",800);
			database.insert("MotionData",null,values);
			values.clear();
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[5]=steps;
		}

		//星期日
		cursor = database.rawQuery("select * from MotionData where motion_date=?",new String[]{"星期日"});
		if(!cursor.moveToFirst())
		{
			values.put("motion_date","星期日");
			values.put("motion_steps",700);
			database.insert("MotionData",null,values);
		}
		else
		{
			while(cursor.moveToNext())
			{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
			}
			YData[6]=steps;
		}
		cursor.close();
	}

	//初始化每月数据
	private void initMonth() {
		monthvalues=new ContentValues();
		for(int i=1;i<=31;i++)
		{

			monthcursor= monthbase.rawQuery("select * from MonthData where date_month=?",new String[]{""+i});
			if(!monthcursor.moveToFirst())
			{
				monthvalues.put("date_month",""+i);
				monthvalues.put("data_steps",5000+i*100);
				monthbase.insert("MonthData",null,monthvalues);
				monthvalues.clear();
			}
			else
			{
				while(monthcursor.moveToNext())
				{
					monthsteps=monthcursor.getInt(monthcursor.getColumnIndex("motion_steps"));
				}
				MonthYData[i-1]=monthsteps;
			}
		}
		monthcursor.close();
	}

	//初始化每年数据
	private void initYear() {
		yearvalues=new ContentValues();
		for(int i=1;i<=12;i++)
		{
			yearcursor= yearbase.rawQuery("select * from YearData where date_year=?",new String[]{""+i});
			if(!yearcursor.moveToFirst())
			{
				yearvalues.put("date_year",""+i);
				yearvalues.put("year_steps",10000+1000*i);
				yearbase.insert("YearData",null,yearvalues);
				yearvalues.clear();
			}
			else
			{
				while(yearcursor.moveToNext())
				{
					yearsteps=yearcursor.getInt(yearcursor.getColumnIndex("year_steps"));
				}
				YearYData[i-1]=yearsteps;
			}
		}
		yearcursor.close();
	}

	//监听晚上0点变化初始化数据
	private boolean isNewDay() {
		if (!(CURRENTDATE).equals(getTodayDate())) {
			return true;
		}
		return false;
	}


	//更新每周数据
	private void updateChart() {
		int i=0;
		Cursor cursor=database.query("MotionData",null,null,null,null,null,null);
		if(cursor.moveToFirst())
		{
			do{
				steps=cursor.getInt(cursor.getColumnIndex("motion_steps"));
				YData[i]=steps;
				i++;
			}while(cursor.moveToNext());

		}
		cursor.close();
		weekChart.setY_axis(YData);
		setWeekChart();
		weekView = weekChart.executeView(getActivity());
		weekChartView.addView(weekView);
	}

	//更新每月数据
	private void updateMonthChart() {
		int i=0;
		Cursor monthcursor=monthbase.query("MonthData",null,null,null,null,null,null);
		if(monthcursor.moveToFirst())
		{
			do{
				monthsteps=monthcursor.getInt(monthcursor.getColumnIndex("data_steps"));
				MonthYData[i]=monthsteps;
				i++;
			}while(monthcursor.moveToNext());
		}
		monthcursor.close();
		monthChart.setY_axis(MonthYData);
		setMonthChart();
		monthView = monthChart.executeView(getActivity());
		monthChartView.addView(monthView);
	}

	//更新每月数据
	private void updateYearChart() {
		int i=0;
		Cursor yearcursor=yearbase.query("YearData",null,null,null,null,null,null);
		if(yearcursor.moveToFirst())
		{
			do{
				yearsteps=yearcursor.getInt(yearcursor.getColumnIndex("year_steps"));
				YearYData[i]=yearsteps;
				i++;
			}while(yearcursor.moveToNext());
		}
		yearcursor.close();

		yearChart.setY_axis(YearYData);
		setYearChart();
		yearView = yearChart.executeView(getActivity());
		yearChartView.addView(yearView);
	}

	public void init() {
		weekTab = (LinearLayout) view.findViewById(R.id.weekTab);
		monthTab = (LinearLayout) view.findViewById(R.id.monthTab);
		yearTab = (LinearLayout) view.findViewById(R.id.yearTab);
		tabs = new LinearLayout[] {weekTab, monthTab, yearTab };
		content = (LinearLayout) view.findViewById(R.id.content);

		weekChartView = (LinearLayout) view.findViewById(R.id.chartView);
		monthChartView = (LinearLayout) view.findViewById(R.id.chartView);
		yearChartView = (LinearLayout) view.findViewById(R.id.chartView);

		LayoutInflater factory = LayoutInflater.from(getActivity());
		viewSports = factory.inflate(R.layout.view_sports, null);
		todaySteps= (TextView) viewSports.findViewById(R.id.todaySteps);
		realTimeSteps= (TextView) viewSports.findViewById(R.id.realTimeSteps);
		content.addView(viewSports);

		weekTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabChecked();
				weekChartView.removeAllViews();

				//更新折线图数据
				updateChart();

				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
				weekChartView.startAnimation(animation);
				weekTab.setBackgroundResource(R.drawable.tabselected);
				TextView txt = (TextView) weekTab.getChildAt(0);
				txt.setTextColor(getActivity().getResources().getColor(R.color.white,null));
			}
		});
		monthTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabChecked();
				monthChartView.removeAllViews();

				//更新折线图数据
				updateMonthChart();

				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
				monthChartView.startAnimation(animation);
				monthTab.setBackgroundResource(R.drawable.tabselected);
				TextView txt = (TextView) monthTab.getChildAt(0);
				txt.setTextColor(getActivity().getResources().getColor(R.color.white,null));
			}
		});
		yearTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabChecked();
				yearChartView.removeAllViews();

				//更新折线图数据
				updateYearChart();

				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
				yearChartView.startAnimation(animation);
				yearTab.setBackgroundResource(R.drawable.tabselected);
				TextView txt = (TextView) yearTab.getChildAt(0);
				txt.setTextColor(getActivity().getResources().getColor(R.color.white,null));
			}
		});
	}


	public void setWeekChart() {
		weekChart.setTitles(new String[] { "步数" });
		weekChart.setX_axis(XData);
		weekChart.setY_axis(YData);
		weekChart.setChartTitle("近7天步数统计");
		weekChart.setxTitle("星期");
		weekChart.setyTitle("步数");
		weekChart.setxMin(0.5);
		weekChart.setxMax(7.5);
		weekChart.setyMin(-50);
		weekChart.setyMax(25000);
		weekChart.setPanLimits(new double[] { 0.5, 7.5, -50, 30000 });
		weekChart.setZoomLimits(new double[] { 0.5, 7.5, -50, 30000 });
	}

	public void setMonthChart() {
		monthChart.setTitles(new String[] { "步数" });
		monthChart.setX_axis(MonthXData);
		monthChart.setY_axis(MonthYData);
		monthChart.setChartTitle("近1个月步数统计");
		monthChart.setxTitle("日期");
		monthChart.setyTitle("步数");
		monthChart.setxMin(0.5);
		monthChart.setxMax(31.5);
		monthChart.setyMin(-50);
		monthChart.setyMax(25000);
		monthChart.setPanLimits(new double[] { 0.5, 31.5, -50, 30000 });
		monthChart.setZoomLimits(new double[] { 0.5, 31.5, -50, 30000 });

	}

	public void setYearChart() {
		yearChart.setTitles(new String[] { "步数" });
		yearChart.setX_axis(YearXData);
		yearChart.setY_axis(YearYData);
		yearChart.setChartTitle("近1年步数统计");
		yearChart.setxTitle("月份");
		yearChart.setyTitle("步数");
		yearChart.setxMin(0.5);
		yearChart.setxMax(12.5);
		yearChart.setyMin(-50);
		yearChart.setyMax(25000);
		yearChart.setPanLimits(new double[] { 0.5, 12.5, -50, 30000 });
		yearChart.setZoomLimits(new double[] { 0.5, 12.5, -50, 30000 });
	}

	Handler mTimeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				nowSteps=QueryStepNum();
				ContentValues values=new ContentValues();
				values.put("motion_steps",nowSteps);
				database.update("MotionData",values,"motion_date=?",new String[]{WeekDate()});

				ContentValues monthvalues=new ContentValues();
				monthvalues.put("data_steps",nowSteps);
				monthbase.update("MonthData",monthvalues,"date_month=?",new String[]{MonthDate()});

				CURRENTDATE=getTodayDate();
				String time = "00:00";
				if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())))
				{
					yearcursor= yearbase.rawQuery("select * from YearData where date_year=?",new String[]{YearDate()});
					while(yearcursor.moveToNext())
					{
						history_steps=yearcursor.getInt(yearcursor.getColumnIndex("year_steps"));
					}
					save_steps=nowSteps+history_steps;
					ContentValues yearvalues=new ContentValues();
					yearvalues.put("year_steps",save_steps);
					yearbase.update("YearData",yearvalues,"date_year=?",new String[]{YearDate()});
				}

				todaySteps.setText(""+QueryStepNum()+" 步");
				realTimeSteps.setText(""+QueryStepNum()/60+" 步/秒");
				sendEmptyMessageDelayed(0, 1000);
			}
		}
	};

	//设置标签切换时候的样式变化
	public void setTabChecked() {
		for (int i = 0; i < tabs.length; i++) {
			tabs[i].setBackground(null);
			TextView txt = (TextView) tabs[i].getChildAt(0);
			txt.setTextColor(getActivity().getResources().getColor(R.color.darkgreen,null));
		}
	}

	private int QueryStepNum()
	{
		CURRENTDATE=getTodayDate();
		if(DbUtils.getLiteOrm()==null){
			DbUtils.createDb(getActivity(), "step");
		}
		List<StepData> list= DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
		return Integer.parseInt(list.get(0).getStep());
	}

	private String getTodayDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	//判断星期
	private String WeekDate()
	{
		String str[]={"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};//字符串数组
		Calendar rightNow=Calendar.getInstance();
		int day=rightNow.get(rightNow.DAY_OF_WEEK);//获取时间
		return str[day-1];
	}

	//判断月的某一天
	private String MonthDate()
	{
		Calendar rightNow=Calendar.getInstance();
		int day=rightNow.get(rightNow.DAY_OF_MONTH);
		return ""+day;
	}

	//判断年的某一月
	private String YearDate()
	{
		Calendar rightNow=Calendar.getInstance();
		int day=rightNow.get(rightNow.MONTH)+1;
		return ""+day;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		database.delete("MotionData",null,null);
//		monthbase.delete("MonthData",null,null);
//		yearbase.delete("YearData",null,null);
	}
}
