package kpy.bracelet;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import kpy.sqlite.UserInfoDBHelper;


/**
 * Created by KPY on 2017/6/6.
 */

public class SetSignActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back_sign;
    private EditText sign_text;
    private Button save_sign;
    private SQLiteDatabase userinfo;
    private SharedPreferences sp;

    private void assignViews() {
        back_sign= (ImageView) findViewById(R.id.back_sign);
        sign_text= (EditText) findViewById(R.id.sign_text);
        save_sign= (Button) findViewById(R.id.save_sign);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setsign);
        userinfo=UserInfoDBHelper.getInstance().getWritableDatabase();
        sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        assignViews();
        addListener();
    }

    private void addListener() {
        back_sign.setOnClickListener(this);
        save_sign.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_sign:
                finish();
                break;
            case R.id.save_sign:
                String sign=sign_text.getText().toString().trim();
                ContentValues values=new ContentValues();
                values.put("user_sign",sign);
                String phone=sp.getString("user","");
                userinfo.update("UserInfo",values,"user_id=?",new String[]{phone});
                values.clear();
                finish();
                break;
        }
    }
}
