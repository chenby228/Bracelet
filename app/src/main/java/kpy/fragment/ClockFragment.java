package kpy.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import kpy.bracelet.R;
import kpy.view.AlarmReceiver;
import kpy.view.AlarmView;

/**
 * Created by KPY on 2017/4/17.
 */

public class ClockFragment extends BaseFragment {

    private ImageButton AddAlarm;
    private ListView lvAlarmList;
    private ArrayAdapter<AlarmData> adapter;
    private static final String KEY_ALARM = "alarm_list";
    private AlarmManager alarmManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Init();
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.activity_clock, container, false);
        return view;
    }

    public void Init() {
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AddAlarm = (ImageButton) view.findViewById(R.id.AddAlarm);
        lvAlarmList= (ListView) view.findViewById(R.id.lvAlarmList);

        adapter = new ArrayAdapter<AlarmData>(getActivity(),android.R.layout.simple_list_item_1);
        lvAlarmList.setAdapter(adapter);
        readSavedAlarmList();

        AddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
//                Toast.makeText(getActivity(),"test",Toast.LENGTH_SHORT).show();
            }
        });

        lvAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(
                        new CharSequence[]{"删除", "全部删除"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        deleteAlarm(position);
                                        break;
                                    case 1:
                                        deleteAllAlarm();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                ).setNegativeButton("取消", null).show();

                return true;
            }
        });
    }

    private void readSavedAlarmList(){
        SharedPreferences sp = getContext().getSharedPreferences(
                AlarmView.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM,null);

        if(content != null){
            String[] timeStrings = content.split(",");
            for(String str : timeStrings){
                adapter.add(new AlarmData(Long.parseLong(str)));
            }
        }
    }

    private void deleteAlarm(int position){
        AlarmData ad = adapter.getItem(position);
        adapter.remove(ad);

        saveAlarmList();

        alarmManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),
                new Intent(getContext(), AlarmReceiver.class),0));
    }

    private void deleteAllAlarm(){

        int adapterCount =adapter.getCount();   // 为adapter的个数进行计数
        AlarmData ad;
        for(int i = 0; i < adapterCount; i++){
            ad = adapter.getItem(0);       // 每次从第1个开始移除
            adapter.remove(ad);
            saveAlarmList();       // 移除后重新保存列表
            alarmManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),
                    new Intent(getContext(),AlarmReceiver.class),0));   // 取消闹钟的广播
        }
    }

    private  void addAlarm(){

        Calendar c = Calendar.getInstance();

        new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute){
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                Calendar currentTime = Calendar.getInstance();

                if(calendar.getTimeInMillis() <= currentTime.getTimeInMillis()){
                    calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
                }

                AlarmData ad = new AlarmData(calendar.getTimeInMillis());
                adapter.add(ad);
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), 0,
                        PendingIntent.getBroadcast(getContext(), ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5*60*1000,
//                        PendingIntent.getBroadcast(getContext(), ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
                Toast.makeText(getContext(), "闹钟设置成功", Toast.LENGTH_SHORT).show();//提示用户
                saveAlarmList();
            }
        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true).show();
    }

    private void saveAlarmList(){
        SharedPreferences.Editor editor = getContext().getSharedPreferences(
                AlarmView.class.getName(),
                Context.MODE_PRIVATE).edit();

        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < adapter.getCount(); i++){
            sb.append(adapter.getItem(i).getTime()).append(",");
        }

        if(sb.length() > 1){
            String content = sb.toString().substring(0,sb.length()-1);

            editor.putString(KEY_ALARM, content);

            System.out.println(content);
        }else{
            editor.putString(KEY_ALARM, null);
        }

        editor.commit();

    }

    private static class AlarmData{

        private String timeLabel = "";
        private long time = 0;
        private Calendar date;

        public AlarmData(long time){
            this.time = time;
            date = Calendar.getInstance();
            date.setTimeInMillis(time);
            timeLabel = String.format("%d月%d日 %d:%d",
                    date.get(Calendar.MONTH)+1,
                    date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.HOUR_OF_DAY),
                    date.get(Calendar.MINUTE));
        }

        public long getTime(){
            return time;
        }

        public String getTimeLabel(){
            return timeLabel;
        }

        public int getId(){
            return (int)(getTime()/1000/60);
        }

        @Override
        public String toString(){
            return getTimeLabel();
        }
    }

}
