package kpy.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kpy.bean.StepData;
import kpy.bracelet.HistoryActivity;
import kpy.bracelet.R;
import kpy.bracelet.SetPlanActivity;
import kpy.config.Constant;
import kpy.service.StepService;
import kpy.utils.DbUtils;
import kpy.utils.SharedPreferencesUtils;
import kpy.utils.StepCountModeDispatcher;
import kpy.view.StepArcView;

/**
 * Created by KPY on 2017/5/16.
 */

public class StepFragment extends BaseFragment implements Handler.Callback,View.OnClickListener{

    private TextView tv_data;
    private StepArcView cc;
    private TextView tv_set;
    private TextView tv_isSupport;
    private SharedPreferencesUtils sp;

    private boolean isBind = false;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Messenger messenger;

    private View view;
    private static String CURRENTDATE = "";
    private String planWalk_QTY;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.activity_step, container, false);
        InitView();
        InitData();
        addListener();
        return view;
    }

    private void InitView() {
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        tv_data = (TextView)view.findViewById(R.id.tv_data);
        cc = (StepArcView) view.findViewById(R.id.cc);
        tv_set = (TextView) view.findViewById(R.id.tv_set);
        tv_isSupport = (TextView) view.findViewById(R.id.tv_isSupport);
    }

    private void addListener() {

        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), QueryStepNum());
                    }
                }, 1000);
            }
        });
    }

    private void InitData() {
        sp = new SharedPreferencesUtils(getActivity());
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        //判断设置是否支持计步
        if (StepCountModeDispatcher.isSupportStepCountSensor(getActivity())) {
            tv_isSupport.setText("计步中...");
            new Handler();
            setupService();
        }
        else
        {
            tv_isSupport.setText("该设备不支持计步功能");
        }
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
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

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(getActivity(), StepService.class);
        isBind = getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);
    }

    /**
     * 从service服务中拿到步数
     */
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
                cc.setCurrentCount(Integer.parseInt(planWalk_QTY), msg.getData().getInt("step"));
                break;
        }
        return false;
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.tv_set:
                Toast.makeText(getActivity(),""+QueryStepNum(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), SetPlanActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_data:
                startActivity(new Intent(getActivity(), HistoryActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            getActivity().unbindService(conn);
        }
    }

}
