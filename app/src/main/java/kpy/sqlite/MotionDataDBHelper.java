package kpy.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by KPY on 2017/6/2.
 */

public class MotionDataDBHelper extends SQLiteOpenHelper {

    private Context context;

    public MotionDataDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS MotionData(id integer primary key autoincrement," +
                "motion_date TEXT not null," +
                "motion_steps INT not null )");

        Toast.makeText(context,"Created MotionData Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
