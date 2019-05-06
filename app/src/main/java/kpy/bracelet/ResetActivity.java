package kpy.bracelet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kpy.App;
import kpy.sqlite.UserInfoDBHelper;


/**
 * Created by KPY on 2017/6/14.
 */

public class ResetActivity extends BaseActivity{

    private String phone;
    private String code;
    private int time = 60;
    private boolean flag = true;
    private ImageView back_reset;
    private EditText phone_edit;
    private EditText code_edit;
    private Button reset_button;
    private Button get_code;
    private UserInfoDBHelper userInfoDBHelper;
    private SQLiteDatabase userinfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        initView();
        initEvent();
    }

    private void initView() {
        back_reset= (ImageView) findViewById(R.id.back_reset);
        phone_edit= (EditText) findViewById(R.id.phone_edit);
        code_edit= (EditText) findViewById(R.id.code_edit);
        reset_button= (Button) findViewById(R.id.reset_button);
        get_code= (Button) findViewById(R.id.get_code);
    }

    private void initEvent() {
        back_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(App.getContext(), "后期修改", Toast.LENGTH_SHORT).show();
                if(!TextUtils.isEmpty(code_edit.getText().toString().trim())){
                    if(code_edit.getText().toString().trim().length()==4){
                        code = code_edit.getText().toString().trim();

                        flag = false;
                    }else{
                        Toast.makeText(ResetActivity.this, "请输入完整验证码", Toast.LENGTH_LONG).show();
                        code_edit.requestFocus();
                    }
                }else{
                    Toast.makeText(ResetActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
                    code_edit.requestFocus();
                }
            }
        });
        get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(phone_edit.getText().toString().trim())){
                    if(phone_edit.getText().toString().trim().length()==11){
                        phone = phone_edit.getText().toString().trim();

                        code_edit.requestFocus();
                        get_code.setClickable(false);
                    }else{
                        Toast.makeText(ResetActivity.this, "请输入完整电话号码", Toast.LENGTH_LONG).show();
                        phone_edit.requestFocus();
                    }
                }else{
                Toast.makeText(getApplicationContext(), "请输入您的电话号码", Toast.LENGTH_LONG).show();
                phone_edit.requestFocus();
                }
            }
        });
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
