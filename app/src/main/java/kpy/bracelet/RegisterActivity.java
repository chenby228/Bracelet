package kpy.bracelet;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.pc.ioc.verification.annotation.ConfirmPassword;
import com.android.pc.ioc.verification.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kpy.service.HttpUtils;
import kpy.sqlite.UserInfoDBHelper;


/**
 * Created by KPY on 2017/5/15.
 */

public class RegisterActivity extends BaseActivity {

    private EditText username;
    private EditText password_number;
    private EditText again_password_number;
    private ImageView back_register;
    private ImageButton see_password;
    private ImageButton see_password_again;
    private Button register_button;

    private String userId;
    private String NewPassword;
    private String ConfirmPassword;

    private boolean isHidden = true; //密码是否可见
    private boolean isHiddenAgain = true; //再次密码是否可见

    private String strPattern = "^(?=.*\\d)((?=.*[a-z])||(?=.*[A-Z])).{8,16}$";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitView();
        InitEvent();
    }

    private void InitView() {
        username = (EditText) findViewById(R.id.editText);
        password_number = (EditText) findViewById(R.id.editText1);
        again_password_number = (EditText) findViewById(R.id.editText2);
        see_password = (ImageButton) findViewById(R.id.see_password);
        see_password_again = (ImageButton) findViewById(R.id.see_password_again);
        register_button = (Button) findViewById(R.id.register_button);
        back_register = (ImageView) findViewById(R.id.back_register);
    }

    JSONObject respondJson = null;

    private void InitEvent() {

        password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());
        again_password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());

        see_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    password_number.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see_open);
                } else {
                    password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see);
                }
                isHidden = !isHidden;
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
                if (isHiddenAgain) {
                    again_password_number.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see_open);
                } else {
                    again_password_number.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    v.setBackgroundResource(R.mipmap.see);
                }
                isHiddenAgain = !isHiddenAgain;
                password_number.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = again_password_number.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = username.getText().toString().trim();
                NewPassword = password_number.getText().toString().trim();
                ConfirmPassword = again_password_number.getText().toString().trim();
                if (((userId == null) || (userId.equals(""))) || ((NewPassword == null) || (NewPassword.equals(""))) || ((ConfirmPassword == null) || (ConfirmPassword.equals("")))) {
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (NewPassword.equals(ConfirmPassword)) {
                        boolean flag = InvalidatePassword(NewPassword);

                        if (!flag) {
                            Toast.makeText(getApplicationContext(), "您输入的密码格式不符合规则！请重新输入", Toast.LENGTH_SHORT).show();
                            password_number.setText("");
                            again_password_number.setText("");
                        }
                        if (flag) {
                            Thread registerThread = new Thread(new RegisterThread());
                            registerThread.start();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "您两次输入的密码不一致！请重新输入", Toast.LENGTH_SHORT).show();
                        password_number.setText("");
                        again_password_number.setText("");
                    }
                }

            }


        });
        back_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    //验证注册密码的正则表达式函数
    private boolean InvalidatePassword(String Password) {
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(Password);
        return m.matches();
    }

    class RegisterThread implements Runnable {

        @Override
        public void run() {
            Message msg = handler.obtainMessage();

            try {
                JSONObject body = new JSONObject();
                body.put("msgType", "register")
                        .put("userId", userId)
                        .put("username", randomName())
                        .put("password", NewPassword);

                String respond = HttpUtils.post(body.toString());

                respondJson = new JSONObject(respond);
                System.out.println("返回数据: " + respondJson);
                if (respondJson.getString("status").equals("1")) {  //1  已注册  //0 未注册    //-1 连接异常
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "账号已注册", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    //Handler
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showDialog("注册成功！");
                    break;
//                case 1:
//                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2:
//                    Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
//                    break;
                default:
                    break;
            }

        }
    };

    private void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注册");
        builder.setMessage(str);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    //生成随机username
    public String randomName(){
        String sources = "0123456789";
        Random rand = new Random();
        StringBuilder flag = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            flag.append(sources.charAt(rand.nextInt(9)));
        }
        return "Bracelet" + flag + "";
    }
}
