package kpy.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import kpy.App;

/**
 * Created by KPY on 2017/6/13.
 */

public class UserInfoDBHelper extends SQLiteOpenHelper {
    private static UserInfoDBHelper userInfoDBHelper = new UserInfoDBHelper();
    public UserInfoDBHelper() {
        super(App.getContext(), "UserData.db",null,1);
    }

    public static UserInfoDBHelper getInstance(){
        return userInfoDBHelper;
    }

    public static SQLiteDatabase getSQLiteDatabase(){
        return userInfoDBHelper.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS UserInfo(id integer primary key autoincrement," +
                "user_id TEXT," +
                "user_name TEXT," +
                "user_password TEXT,"+
                "user_email TEXT,"+
                "user_sex TEXT,"+
                "user_address TEXT,"+
                "user_sign TEXT,"+
                "user_photo TEXT )");
        Toast.makeText(App.getContext(),"Created UserInfo Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
