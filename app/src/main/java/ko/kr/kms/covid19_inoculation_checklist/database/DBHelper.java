package ko.kr.kms.covid19_inoculation_checklist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ko.kr.kms.covid19_inoculation_checklist.Item;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "";

        sql = "CREATE TABLE if not exists unconfirmedList ("
                + "_id integer primary key autoincrement,"
                + "reservationDate text,"
                + "reservationTime text,"
                + "inoculated text,"
                + "subject text,"
                + "name text,"
                + "registrationNumber,"
                + "phoneNumber,"
                + "facilityName);";
        db.execSQL(sql);

        sql = "CREATE TABLE if not exists confirmedList ("
                + "_id integer primary key autoincrement,"
                + "reservationDate text,"
                + "reservationTime text,"
                + "inoculated text,"
                + "subject text,"
                + "name text,"
                + "registrationNumber,"
                + "phoneNumber,"
                + "facilityName);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "";

        sql = "DROP TABLE if exists unconfirmedList;";
        db.execSQL(sql);

        sql = "DROP TABLE if exists confirmedList";
        db.execSQL(sql);

        onCreate(db);
    }

    public void insertCheckListToDB(ArrayList<Item> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int totalDataCount = 8;

        for (int i = 0; i < items.size(); i++) {
            values.clear();

            for (int j = 0; j < totalDataCount; j++) {
                switch (j) {
                    case 0:
                        values.put("reservationDate", items.get(i).getReservationDate());
                        break;

                    case 1:
                        values.put("reservationTime", items.get(i).getReservationTime());
                        break;

                    case 2:
                        values.put("inoculated", items.get(i).getInoculated());
                        break;

                    case 3:
                        values.put("subject", items.get(i).getSubject());
                        break;

                    case 4:
                        values.put("name", items.get(i).getName());
                        break;

                    case 5:
                        values.put("registrationNumber", items.get(i).getRegistrationNumber());
                        break;

                    case 6:
                        values.put("phoneNumber", items.get(i).getPhoneNumber());
                        break;

                    case 7:
                        values.put("facilityName", items.get(i).getFacilityName());
                        break;
                }
            }

            db.insert("unconfirmedList", null, values);
        }
    }
}
