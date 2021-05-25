package ko.kr.kms.covid19_inoculation_checklist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ko.kr.kms.covid19_inoculation_checklist.Item;

import java.util.ArrayList;

public class Database {

    SQLiteDatabase db;
    DBHelper dbHelper;

    private Database() {
    }

    private static class LazyHolder {
        public static final Database instance = new Database();
    }

    public static Database getInstance() {
        return LazyHolder.instance;
    }

    public void createDatabase(Context context) {
        dbHelper = new DBHelper(context, "data.db", null, 1);

        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
    }

    public SQLiteDatabase getDB() {
        return db;
    }
}
