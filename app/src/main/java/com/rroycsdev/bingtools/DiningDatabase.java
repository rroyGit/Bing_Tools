package com.rroycsdev.bingtools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Rated on 12/25/2017.
 */

public class DiningDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BingMenu.db";
    private static final int DATABASE_VERSION = 1;
    private String MENU_TABLE_NAME;
    private static final String MENU_COLUMN_ID = "_id";
    private static final String MENU_COLUMN_DAY = "day";
    static final String MENU_COLUMN_BREAKFAST = "breakfast";
    static final String MENU_COLUMN_LUNCH = "lunch";
    static final String MENU_COLUMN_AFTERNOON_SNACK = "afternoon_snack";
    static final String MENU_COLUMN_DINNER = "dinner";
    private Context context;


    public DiningDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //empty, must call createTable(String tableName)
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MENU_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void createTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = getDBHelper(this.getWritableDatabase());
        MENU_TABLE_NAME = tableName;

        String query = "CREATE TABLE IF NOT EXISTS " + MENU_TABLE_NAME + "(" +
                MENU_COLUMN_ID + " INTEGER PRIMARY KEY, " + MENU_COLUMN_DAY + " TEXT, " +
                MENU_COLUMN_BREAKFAST + " TEXT, "+ MENU_COLUMN_LUNCH + " TEXT, " +
                MENU_COLUMN_AFTERNOON_SNACK + " TEXT, " +
                MENU_COLUMN_DINNER + " TEXT)";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public boolean insertMenuItem(String day, String breakfast, String lunch, String afternoon, String dinner) {
        SQLiteDatabase sqLiteDatabase = getDBHelper(this.getWritableDatabase());
        ContentValues contentValues = new ContentValues();

        contentValues.put(MENU_COLUMN_DAY, day);
        contentValues.put(MENU_COLUMN_BREAKFAST, breakfast);
        contentValues.put(MENU_COLUMN_LUNCH, lunch);
        contentValues.put(MENU_COLUMN_AFTERNOON_SNACK, afternoon);
        contentValues.put(MENU_COLUMN_DINNER, dinner);

        sqLiteDatabase.insert(MENU_TABLE_NAME,null, contentValues);
        sqLiteDatabase.close();
        return true;
    }

    public boolean updateMenuItem(Integer id, String day, String breakfast, String lunch, String afternoon, String dinner) {
        SQLiteDatabase sqLiteDatabase = getDBHelper(this.getWritableDatabase());
        ContentValues contentValues = new ContentValues();

        contentValues.put(MENU_COLUMN_DAY, day);
        contentValues.put(MENU_COLUMN_BREAKFAST, breakfast);
        contentValues.put(MENU_COLUMN_LUNCH, lunch);
        contentValues.put(MENU_COLUMN_AFTERNOON_SNACK, afternoon);
        contentValues.put(MENU_COLUMN_DINNER, dinner);

        sqLiteDatabase.update(MENU_TABLE_NAME, contentValues, MENU_COLUMN_ID + "= ?", new String[] {Integer.toString(id)});
        sqLiteDatabase.close();
        return true;
    }

    public Cursor getMenuItem(Integer id) {
        SQLiteDatabase sqLiteDatabase = getDBHelper(this.getReadableDatabase());
        String query = "SELECT * FROM " + MENU_TABLE_NAME + " WHERE " + MENU_COLUMN_ID + "= ?";
        return sqLiteDatabase.rawQuery(query, new String[] {Integer.toString(id)});
    }

    public Cursor getAllItems() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + MENU_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public Integer deleteItem(Integer id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "WHERE " + MENU_COLUMN_ID + "= ?";
        Integer ret = sqLiteDatabase.delete(MENU_TABLE_NAME, query, new String[]{Integer.toString(id)});
        sqLiteDatabase.close();
        return ret;
    }

    public int deleteAllItems() {
        SQLiteDatabase sqLiteDatabase = getDBHelper(this.getWritableDatabase());
        int numItems = -1;
        try {
             numItems = sqLiteDatabase.delete(MENU_TABLE_NAME, null, null);
        } catch (IllegalStateException exception) {
            Toast.makeText(context, "Delete All - " + exception.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
            return numItems;
        }
    }

    public int getDatabaseCount() {
        SQLiteDatabase sqLiteDatabase = getDBHelper(this.getReadableDatabase());
        String query = "SELECT * FROM " + MENU_TABLE_NAME;

        int ret;
        //cursor operations are NOT protected by reference counting
        //https://stackoverflow.com/questions/23293572/android-
        //cannot-perform-this-operation-because-the-connection-pool-has-been-clos
        synchronized (DATABASE_NAME) {
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            ret = cursor.getCount();
            cursor.close();
        }

        sqLiteDatabase.close();
        return ret;
    }

    private SQLiteDatabase getDBHelper(SQLiteDatabase sqLiteDatabase) {
        if (!sqLiteDatabase.isOpen()) {
            Toast.makeText(context, "SQL database not open, waiting for database to open", Toast.LENGTH_SHORT).show();
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sqLiteDatabase;
    }

}