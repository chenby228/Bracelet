package kpy.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by KPY on 2017/6/4.
 */

public class FenceDataDBHelper extends SQLiteOpenHelper {

    private Context context;
    public FenceDataDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS FenceData(id integer primary key autoincrement," +
                "fence_name TEXT not null," +
                "fence_shape TEXT not null,"+
                "fence_number INT not null,"+
                "fence_latitude DOUBLE not null,"+
                "fence_longitude DOUBLE not null )");
        Toast.makeText(context,"Created FenceData Succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
