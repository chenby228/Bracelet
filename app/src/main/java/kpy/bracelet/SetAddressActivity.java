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

public class SetAddressActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back_address;
    private EditText address_text;
    private Button save_address;
    private SQLiteDatabase userinfo;
    private SharedPreferences sp;

    private void assignViews() {
        back_address= (ImageView) findViewById(R.id.back_address);
        address_text= (EditText) findViewById(R.id.address_text);
        save_address= (Button) findViewById(R.id.save_address);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setaddress);
        userinfo=UserInfoDBHelper.getInstance().getWritableDatabase();
        sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        assignViews();
        addListener();
    }

    private void addListener() {
        back_address.setOnClickListener(this);
        save_address.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_address:
                finish();
                break;
            case R.id.save_address:
                String address=address_text.getText().toString().trim();
                ContentValues values=new ContentValues();
                values.put("user_address",address);
                String phone=sp.getString("user","");
                userinfo.update("UserInfo",values,"user_id=?",new String[]{phone});
                values.clear();
                finish();
                break;
        }
    }
}
