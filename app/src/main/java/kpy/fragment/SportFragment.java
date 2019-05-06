package kpy.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import kpy.App;
import kpy.bracelet.R;
import kpy.sqlite.SportDataDBHelper;

/**
 * Created by KPY on 2017/4/17.
 */

public class SportFragment extends Fragment implements View.OnClickListener{


    private Button SportButton;
    private SportDataDBHelper sportDataHelper;
    private TextView Sport_mile;
    private TextView Sport_time;
    private TextView Sport_num;

    private long totaltime_1=0;
    private int sportTimes_1=0;
    private int distance_1=0;

    private static String CURRENTDATE = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.first_fragment,container,false);
        SportButton= (Button) view.findViewById(R.id.tab_sport_start_bt);
        Sport_mile= (TextView) view.findViewById(R.id.tab_sport_mile);
        Sport_time= (TextView) view.findViewById(R.id.tab_sport_time_num);
        Sport_num= (TextView) view.findViewById(R.id.tab_sport_num);
        initData();
        CURRENTDATE=getTodayDate();
        SportButton.setOnClickListener(this);
        return view;
    }

    private void initData() {
        CURRENTDATE=getTodayDate();
        //创建数据库
        sportDataHelper=new SportDataDBHelper(getActivity(),"sport.db",null,1);
        SQLiteDatabase database=sportDataHelper.getWritableDatabase();
        Cursor cursor=database.rawQuery("select * from SportData where sport_date=?",new String[]{CURRENTDATE});
        if(cursor.getCount()==0)
        {
            distance_1=0;
            totaltime_1=0;
            sportTimes_1=0;

            //插入数据
            ContentValues values=new ContentValues();
            values.put("sport_date",CURRENTDATE);
            values.put("sport_distance",distance_1);
            values.put("sport_total_time",totaltime_1);
            values.put("sport_times",sportTimes_1);
            database.insert("SportData",null,values);
        }
        else
        {
            while (cursor.moveToNext())
            {
                distance_1=cursor.getInt(cursor.getColumnIndex("sport_distance"));
                totaltime_1=cursor.getInt(cursor.getColumnIndex("sport_total_time"));
                sportTimes_1=cursor.getInt(cursor.getColumnIndex("sport_times"));
            }
            Sport_mile.setText(""+distance_1);
            Sport_time.setText(""+totaltime_1/1000);
            Sport_num.setText(""+sportTimes_1);
        }
    }

    /**
     * 监听晚上0点变化初始化数据
     */
    private boolean isNewDay() {
        if (!CURRENTDATE.equals(getTodayDate())) {
            return true;
        }
        return false;
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            //如果是开始运动的按钮
            case R.id.tab_sport_start_bt:
//                Intent intent = new Intent(getActivity(),MapActivity.class);
//                startActivityForResult(intent,1);
//                startActivity(intent);
                Toast.makeText(App.getContext(), "这个页面做攻略的界面", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1)
        {
            if(resultCode==2)
            {
                Bundle bundle=data.getExtras();
                Sport_mile.setText(""+bundle.getInt("distance"));
                Sport_time.setText(""+bundle.getInt("totaltime"));
                Sport_num.setText(""+bundle.getInt("sportTimes"));
            }
        }
    }

}
