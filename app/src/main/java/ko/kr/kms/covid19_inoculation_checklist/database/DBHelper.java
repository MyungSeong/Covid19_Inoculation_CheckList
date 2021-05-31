package ko.kr.kms.covid19_inoculation_checklist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import es.dmoral.toasty.Toasty;
import ko.kr.kms.covid19_inoculation_checklist.Item;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;

    public static final int ATTRIBUTE_COUNT = 8;

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
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CheckListContract.CheckListEntry.RESERVATION_DATE + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.RESERVATION_TIME + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.INOCULATED + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.SUBJECT + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.NAME + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.REGISTRATION_NUMBER + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.PHONE_NUMBER + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.FACILITY_NAME + " TEXT NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE if not exists confirmedList ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CheckListContract.CheckListEntry.RESERVATION_DATE + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.RESERVATION_TIME + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.INOCULATED + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.SUBJECT + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.NAME + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.REGISTRATION_NUMBER + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.PHONE_NUMBER + " TEXT NOT NULL,"
                + CheckListContract.CheckListEntry.FACILITY_NAME + " TEXT NOT NULL);";
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

    public void insertCheckList(ArrayList<Item> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        onUpgrade(db, 1, 1);

        for (int i = 0; i < items.size(); i++) {
            values.clear();
            values = createCheckListValues(items.get(i));

            long newRowId = db.insert(CheckListContract.CheckListEntry.UNCONFIRMED_TABLE, null, values);

            /*if (newRowId != -1)
                Toasty.success(context, "삽입 성공", Toasty.LENGTH_SHORT).show();
            else
                Toasty.success(context, "삽입 실패", Toasty.LENGTH_SHORT).show();*/
        }
    }

    public ArrayList<Item> getCheckList(String table) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                CheckListContract.CheckListEntry.RESERVATION_DATE,
                CheckListContract.CheckListEntry.RESERVATION_TIME,
                CheckListContract.CheckListEntry.INOCULATED,
                CheckListContract.CheckListEntry.SUBJECT,
                CheckListContract.CheckListEntry.NAME,
                CheckListContract.CheckListEntry.REGISTRATION_NUMBER,
                CheckListContract.CheckListEntry.PHONE_NUMBER,
                CheckListContract.CheckListEntry.FACILITY_NAME
        };

        /*String selection = CheckListContract.CheckListEntry.REGISTRATION_NUMBER + " = ?";
        String[] selectionArgs = { registrationNumber };*/

        String sortOrder = CheckListContract.CheckListEntry.NAME + " ASC";

        /*Cursor cursor = db.query(
                table,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );*/
        Cursor cursor = db.query(
                table,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);

        ArrayList<Item> items = new ArrayList<>();

        while (cursor.moveToNext()) {
            String reservationDate = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.RESERVATION_DATE));
            String reservationTime = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.RESERVATION_TIME));
            String inoculated = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.INOCULATED));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.SUBJECT));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.NAME));
            String registrationNumber = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.REGISTRATION_NUMBER));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.PHONE_NUMBER));
            String facilityName = cursor.getString(cursor.getColumnIndexOrThrow(CheckListContract.CheckListEntry.FACILITY_NAME));

            items.add(new Item(reservationDate, reservationTime, inoculated,
                    subject, name, registrationNumber, phoneNumber, facilityName));
        }

        cursor.close();

        return items;
    }

    public void insertItem(Item item, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        long newRowId = db.insert(table, null, createCheckListValues(item));

        /*if (newRowId != -1)
            Toasty.success(context, "삽입 성공", Toasty.LENGTH_SHORT).show();
        else
            Toasty.success(context, "삽입 실패", Toasty.LENGTH_SHORT).show();*/
    }

    public void deleteItem(Context context, String table, String whereArg) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = CheckListContract.CheckListEntry.REGISTRATION_NUMBER + " LIKE ?";
        String[] selectionArgs = { whereArg };

        int deletedRows = db.delete(table, selection, selectionArgs);

        String msg = table.equals(CheckListContract.CheckListEntry.UNCONFIRMED_TABLE) ? "확인" : "삭제";

        if (deletedRows != 0) {
            Toasty.success(context, msg + " 성공", Toasty.LENGTH_SHORT).show();
        } else {
            Toasty.error(context, msg + " 실패", Toasty.LENGTH_SHORT).show();
        }
    }

    public ContentValues createCheckListValues(Item item) {
        ContentValues values = new ContentValues();

        for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
            switch (i) {
                case 0:
                    values.put(CheckListContract.CheckListEntry.RESERVATION_DATE, item.getReservationDate());
                    break;

                case 1:
                    values.put(CheckListContract.CheckListEntry.RESERVATION_TIME, item.getReservationTime());
                    break;

                case 2:
                    values.put(CheckListContract.CheckListEntry.INOCULATED, item.getInoculated());
                    break;

                case 3:
                    values.put(CheckListContract.CheckListEntry.SUBJECT, item.getSubject());
                    break;

                case 4:
                    values.put(CheckListContract.CheckListEntry.NAME, item.getName());
                    break;

                case 5:
                    values.put(CheckListContract.CheckListEntry.REGISTRATION_NUMBER, item.getRegistrationNumber());
                    break;

                case 6:
                    values.put(CheckListContract.CheckListEntry.PHONE_NUMBER, item.getPhoneNumber());
                    break;

                case 7:
                    values.put(CheckListContract.CheckListEntry.FACILITY_NAME, item.getFacilityName());
                    break;
            }
        }

        return values;
    }
}
