package kpy.bracelet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kpy.sqlite.UserInfoDBHelper;

/**
 * Created by KPY on 2017/5/15.
 */

public class ForgetActivity extends BaseActivity {

    private ImageView back_forget;
    private EditText password_number;
    private EditText again_password_number;
    private ImageButton see_password;
    private ImageButton see_password_again;
    private Button reset_password;

    private String NewPassword;
    private String ConfirmPassword;

    private boolean isHidden=true; //密码是否可见
    private boolean isHiddenAgain=true; //再次密码是否可见

    private ProgressDialog mDialog;

    private UserInfoDBHelper userInfoDBHelper;
    private SQLiteDatabase userinfo;

    private String strPattern="^(?=.*\\d)((?=.*[a-z])||(?=.*[A-Z])).{8,16}$";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        InitView();
        InitEvent();
    }

    private void InitView() {
        back_forget= (ImageView) findViewById(R.id.back_forget);
        password_number= (EditText) findViewById(R.id.editText1);
        again_password_number= (EditText) findViewById(R.id.editText2);
        see_password= (ImageButton) findViewById(R.id.see_password);
        see_password_again= (ImageButton) findViewById(R.id.see_password_again);
        reset_password= (Button) findViewById(R.id.reset_password);
    }


    private void InitEvent() {

        password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());
        again_password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());

        see_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHidden)
                {
                    password_number.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see_open);
                }
                else
                {
                    password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see);
                }
                isHidden=!isHidden;
                password_number.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = password_number.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });

        see_password_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHiddenAgain)
                {
                    again_password_number.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see_open);
                }
                else
                {
                    again_password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see);
                }
                isHiddenAgain=!isHiddenAgain;
                password_number.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = again_password_number.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewPassword = password_number.getText().toString();
                ConfirmPassword= again_password_number.getText().toString();

                if(((NewPassword==null)||(NewPassword.equals("")))||((ConfirmPassword==null)||(ConfirmPassword.equals(""))))
                {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    if(NewPassword.equals(ConfirmPassword))
                    {
                        boolean flag=InvalidatePassword(NewPassword);
                        if(flag==false)
                        {
                            Toast.makeText(getApplicationContext(), "您输入的密码格式不符合规则！请重新输入", Toast.LENGTH_SHORT).show();
                            password_number.setText("");
                            again_password_number.setText("");
                        }
                        if(flag==true)
                        {
                            mDialog = new ProgressDialog(ForgetActivity.this);
                            mDialog.setTitle("修改密码");
                            mDialog.setMessage("正在修改密码，请稍后...");
                            mDialog.show();
                            ( new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Thread ResetThread = new Thread(new ResetThread());
                                    ResetThread.start();
                                }
                            }, 1000);

                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "您两次输入的密码不一致！请重新输入", Toast.LENGTH_SHORT).show();
                        password_number.setText("");
                        again_password_number.setText("");
                    }
                }
            }
        });

        back_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //验证注册密码的正则表达式函数
    private boolean InvalidatePassword(String Password)
    {
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(Password);
        return m.matches();
    }

    //RegisterThread线程类
    class ResetThread implements Runnable
    {
        @Override
        public void run() {
            String new_password = password_number.getText().toString().trim();

            userinfo=UserInfoDBHelper.getInstance().getWritableDatabase();

            // 获取启动该ResultActivity的Intent
            Intent intent = getIntent();
            // 获取该Intent所携带的数据
            Bundle bundle = intent.getExtras();
            String phone=bundle.getString("user_phone");

            ContentValues values=new ContentValues();
            values.put("user_password",new_password);
            userinfo.update("UserInfo",values,"user_id=?",new String[]{phone});
            values.clear();

            Message msg = handler.obtainMessage();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    //Handler
    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    mDialog.cancel();
                    showDialog("修改密码成功！");
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog(String str)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改密码");
        builder.setMessage(str);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(ForgetActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }
}
