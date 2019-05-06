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

public class SetEmailActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back_email;
    private EditText email_text;
    private Button save_email;
    private SQLiteDatabase userinfo;
    private SharedPreferences sp;

    private void assignViews() {
        back_email= (ImageView) findViewById(R.id.back_email);
        email_text= (EditText) findViewById(R.id.email_text);
        save_email= (Button) findViewById(R.id.save_email);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setemail);
        userinfo=UserInfoDBHelper.getInstance().getWritableDatabase();
        sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        assignViews();
        addListener();
    }

    private void addListener() {
        back_email.setOnClickListener(this);
        save_email.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_email:
                finish();
                break;
            case R.id.save_email:
                String email=email_text.getText().toString().trim();
                ContentValues values=new ContentValues();
                values.put("user_email",email);
                String phone=sp.getString("user","");
                userinfo.update("UserInfo",values,"user_id=?",new String[]{phone});
                values.clear();
                finish();
                break;
        }
    }
}
