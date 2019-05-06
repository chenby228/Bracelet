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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import kpy.sqlite.UserInfoDBHelper;


/**
 * Created by KPY on 2017/6/6.
 */

public class SetNameActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back_name;
    private EditText name_text;
    private Button save_name;
    private SQLiteDatabase userinfo;
    private SharedPreferences sp;


    private void assignViews() {
        back_name= (ImageView) findViewById(R.id.back_name);
        name_text= (EditText) findViewById(R.id.name_text);
        save_name= (Button) findViewById(R.id.save_name);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setname);
        userinfo=UserInfoDBHelper.getInstance().getWritableDatabase();
        sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        assignViews();
        addListener();
    }

    private void addListener() {
        back_name.setOnClickListener(this);
        save_name.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_name:
                finish();
                break;
            case R.id.save_name:
                String name=name_text.getText().toString().trim();
                ContentValues values=new ContentValues();
                values.put("user_name",name);
                String phone=sp.getString("user","");
                userinfo.update("UserInfo",values,"user_id=?",new String[]{phone});
                values.clear();
                finish();
                break;
        }
    }
}
