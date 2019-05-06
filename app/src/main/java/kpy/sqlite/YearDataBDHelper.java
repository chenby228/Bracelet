package kpy.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by KPY on 2017/6/3.
 */

public class YearDataBDHelper extends SQLiteOpenHelper {


    private Context context;
    public YearDataBDHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS YearData(id integer primary key autoincrement," +
                "date_year TEXT not null," +
                "year_steps INT not null )");
        Toast.makeText(context,"Created YearData Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
