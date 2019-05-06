package kpy.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by KPY on 2017/5/31.
 */

public class SportDataDBHelper extends SQLiteOpenHelper {

    private Context sportconext;

    public SportDataDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        sportconext=context;
    }

    //创建一个数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS SportData(id integer primary key autoincrement," +
                "sport_date TEXT not null," +
                "sport_distance INT not null ," +
                "sport_total_time INT not null ," +
                "sport_times INT not null )");

        Toast.makeText(sportconext,"Created Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
