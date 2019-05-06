package kpy.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import kpy.bean.StepData;
import kpy.bracelet.R;
import kpy.config.Constant;
import kpy.fragment.SportFragment;
import kpy.sqlite.SportDataDBHelper;
import kpy.utils.DbUtils;

public class StepService extends Service implements SensorEventListener {

    //默认为1秒进行一次存储
    private static int duration = 1000;
    private static String CURRENTDATE = "";
    private SensorManager sensorManager;
    private Messenger messenger = new Messenger(new MessenerHandler());
    private BroadcastReceiver mBatInfoReceiver;
    private TimeCount time;
    //当前步数
    private int CURRENT_SETP;
    //计步传感器类型 0-counter 1-detector
    private static int stepSensor = -1;
    private boolean hasRecord = false;
    private int hasStepCount = 0;
    private int prviousStepCount = 0;

    private class MessenerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_CLIENT:
                    try {
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", CURRENT_SETP);
                        replyMsg.setData(bundle);
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
        new Thread(new Runnable() {
            public void run() {
                startStepDetector();
            }
        }).start();

        startTimeCount();
        initTodayData();

    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 初始化当天的步数
     */
    private void initTodayData() {
        CURRENTDATE = getTodayDate();

        //创建数据库
        DbUtils.createDb(this, "step");
        DbUtils.getLiteOrm().setDebugged(false);

        //获取当天的数据，用于展示
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty())
        {
            CURRENT_SETP = 0;
        }
        else if (list.size() == 1)
        {
            CURRENT_SETP = Integer.parseInt(list.get(0).getStep());
        }
    }

    /**
     * 注册广播
     */
    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        // 监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    duration = 1000;
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    save();
                    duration = 1000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    save();
                } else if (Intent.ACTION_DATE_CHANGED.equals(action)) {
                    //日期变化步数重置为0
//                    isCall();
                    save();
                    isNewDay();
                } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {
                    //时间变化步数重置为0
                    isCall();
                    save();
                    isNewDay();
                } else if (Intent.ACTION_TIME_TICK.equals(action)) {
                    //日期变化步数重置为0
                    isCall();
                    save();
                    isNewDay();
                } else if(Intent.ACTION_RUN.equals(action))
                {
                    save();
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }


    /**
     * 监听晚上0点变化初始化数据
     */
    private void isNewDay() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())) || !CURRENTDATE.equals(getTodayDate())) {
            initTodayData();
        }
    }


    /**
     * 监听时间变化提醒用户锻炼
     */
    private void isCall() {
        String time = this.getSharedPreferences("share_date", Context.MODE_PRIVATE).getString("achieveTime", "21:00");
        String plan = this.getSharedPreferences("share_date", Context.MODE_PRIVATE).getString("planWalk_QTY", "7000");
        String remind = this.getSharedPreferences("share_date", Context.MODE_PRIVATE).getString("remind", "1");
        if (("1".equals(remind)) && (CURRENT_SETP < Integer.parseInt(plan)) && (time.equals(new SimpleDateFormat("HH:mm").format(new Date()))))
        {
            initNotify();
        }

    }

    private void startTimeCount() {
        time = new TimeCount(duration, 1000);
        time.start();
    }

    /**
     * Notification构造器
     */
    android.support.v4.app.NotificationCompat.Builder mBuilder;
    /**
     * Notification的ID
     */
    int notifyId = 100;

    /**
     * 初始化通知栏
     */
    private void initNotify() {
        String plan = this.getSharedPreferences("share_date", Context.MODE_MULTI_PROCESS).getString("planWalk_QTY", "7000");
        mBuilder = new android.support.v4.app.NotificationCompat.Builder(this);
        mBuilder.setContentTitle("今日步数" + CURRENT_SETP + " 步")
                .setContentText("距离目标还差" + (Integer.valueOf(plan) - CURRENT_SETP) + "步，加油！")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setTicker("Bracelet计步提醒您开始锻炼了")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.user_icon);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
    public PendingIntent getDefalutIntent(int flag) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flag);
        return pendingIntent;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return messenger.getBinder();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 获取传感器实例
     */
    private void startStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);

        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addCountStepListener();
        }
    }

    /**
     * 添加传感器监听
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensor = 0;
            sensorManager.registerListener(StepService.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (detectorSensor != null) {
            stepSensor = 1;
            sensorManager.registerListener(StepService.this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (stepSensor == 0)
        {
            int tempStep = (int) event.values[0];
            if (!hasRecord)
            {
                hasRecord = true;
                hasStepCount = tempStep;
            }
            else
            {
                int thisStepCount = tempStep - hasStepCount;
                CURRENT_SETP += (thisStepCount - prviousStepCount);
                prviousStepCount = thisStepCount;
            }
        }
        else if (stepSensor == 1)
        {
            if (event.values[0] == 1.0)
            {
                CURRENT_SETP++;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            save();
            startTimeCount();
        }
        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    private void save() {
        int tempStep = CURRENT_SETP;
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setToday(CURRENTDATE);
            data.setStep(tempStep + "");
            DbUtils.insert(data);
        } else if (list.size() == 1) {
            StepData data = list.get(0);
            data.setStep(tempStep + "");
            DbUtils.update(data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消前台进程
        stopForeground(true);
        DbUtils.closeDb();
        unregisterReceiver(mBatInfoReceiver);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
