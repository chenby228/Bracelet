package kpy.bracelet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import kpy.service.HttpUtils;
import kpy.sqlite.UserInfoDBHelper;


/**
 * Created by KPY on 2017/5/15.
 */

public class LoginActivity extends BaseActivity {

    //登录
    private EditText login_name;//用户
    private EditText login_password;//密码
    private Button login_button;//登录
    private TextView register;//注册
    private TextView forget_password;//忘记密码
    private ImageButton close_user;//删除用户名
    private ImageButton see_password;//密码可见
    private ProgressDialog mDialog;
//    private String responseMsg = "";

    //注册
    private EditText register_phoneid;
    private EditText password_number;
    private EditText again_password_number;
    private Button register_button;

    private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
    private static final int LOGIN_OK = 1;
    private static int Login_success=1;

    private boolean isHidden=true; //密码是否可见


    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);



        sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        editor = sp.edit();

        //初始化View
        InitView();
        //处理按钮事件
        InitEvent();
        //检查网络
        CheckNetworkState();
    }

    private void InitView() {
        login_name= (EditText) findViewById(R.id.username_et);
        login_password= (EditText) findViewById(R.id.password_et);
        login_button= (Button) findViewById(R.id.login_bt);
        register= (TextView) findViewById(R.id.register);
        forget_password= (TextView) findViewById(R.id.forget_password);
        close_user= (ImageButton) findViewById(R.id.close_user);
        see_password= (ImageButton) findViewById(R.id.see_password);
        register_button= (Button) findViewById(R.id.register_button);
        //初始化SDK
    }

    private void InitEvent() {

        login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setTitle("登录");
                mDialog.setMessage("正在登录服务器，请稍后...");
                mDialog.show();
                Login_success=0;

                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Thread loginThread = new Thread(new LoginThread());
                        loginThread.start();
                    }
                }, 1000);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        close_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_name.setText("");
            }
        });

        close_user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    v.setBackgroundResource(R.mipmap.close_press);
                }
                else if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    v.setBackgroundResource(R.mipmap.close);
                }
                return false;
            }
        });

        see_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHidden)
                {
                    login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    view.setBackgroundResource(R.mipmap.see_open);
                }
                else
                {
                    login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    view.setBackgroundResource(R.mipmap.see);
                }
                isHidden=!isHidden;
                login_password.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = login_password.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                RegisterPage registerPage=new RegisterPage();
//                registerPage.setRegisterCallback(new EventHandler() {
//
//                    @Override
//                    public void afterEvent(int event, int result, Object data) {
//                        if (result == SMSSDK.RESULT_COMPLETE) {
//                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
//                            String country = (String) phoneMap.get("country");
//                            String phone = (String) phoneMap.get("phone");
//                        }
//                    }
//                });
//                registerPage.show(LoginActivity.this);
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResetActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void LoadUserData() {
//        boolean check=sharedPreferences.getBoolean("check_status",false);
//        if(check)
//        {
//            String realUsername = sharedPreferences.getString("username", "");
//            String realPassword = sharedPreferences.getString("password", "");
//            if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
//            {
//                login_name.setText("");
//                login_password.setText("");
//                login_name.setText(realUsername);
//                login_password.setText(realPassword);
//            }
//        }
//        else
//        {
//            login_name.setText("");
//            login_password.setText("");
//        }
//    }

    private void CheckNetworkState()
    {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null)
        {
            Network[] networks = manager.getAllNetworks();
            NetworkInfo networkInfo;
            if (networks != null)
            {
                for (Network network: networks)
                {
                    networkInfo=manager.getNetworkInfo(network);
                    if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                    {
                        return;
                    }
                }
            }
        }
        ShowTips();
    }

    //如果未连接网络则打开网络设置
    private void ShowTips()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
//        builder.setTitle("没有可用网络");
        builder.setMessage("当前网络不可用，是否设置网络？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 如果没有网络连接，则进入网络设置界面
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                LoginActivity.this.finish();
            }
        });
        builder.create();
        builder.show();
    }

    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "用户名不存在或密码错误", Toast.LENGTH_SHORT).show();
                    break;
//                case 2:
//                    mDialog.cancel();
//                    Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
//                    break;
                default:
                    break;
            }
        }
    };

    class LoginThread implements Runnable {
        @Override
        public void run() {
            String username = login_name.getText().toString().trim();
            String password = login_password.getText().toString().trim();

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msgType", "login")
                        .put("userId", username)
                        .put("password", password);

                String respond = HttpUtils.post(jsonObject.toString());
                JSONObject jsonObject1 = new JSONObject(respond);
                System.out.println("服务器返回： " + jsonObject1);
                Message msg = handler.obtainMessage();
                if(jsonObject1.getString("status").equals("1")) {
                    editor.putString("user", username);
                    editor.commit();
                    msg.what = 0;
                    handler.sendMessage(msg);
                } else if(jsonObject1.getString("status").equals("0")) {
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Login_success==0)
        {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
