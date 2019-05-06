package kpy.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by KPY on 2017/6/4.
 */

public class MapDataDBHelper extends SQLiteOpenHelper {

    private Context context;

    public MapDataDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS MapData(id integer primary key autoincrement," +
                "map_date TEXT not null," +
                "map_latitude DOUBLE not null,"+
                "map_longitude DOUBLE not null )");
        Toast.makeText(context,"Created MapData Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
