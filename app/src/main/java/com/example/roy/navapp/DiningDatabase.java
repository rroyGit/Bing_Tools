package com.example.roy.navapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Rated on 12/25/2017.
 */

public class DiningDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BingMenu.db";
    private static final int DATABASE_VERSION = 1;
    private final String MENU_TABLE_NAME;
    public static final String MENU_COLUMN_ID = "_id";
    public static final String MENU_COLUMN_DAY = "day";
    public static final String MENU_COLUMN_BREAKFAST = "breakfast";
    public static final String MENU_COLUMN_LUNCH = "lunch";
    public static final String MENU_COLUMN_DINNER = "dinner";


    public DiningDatabase(Context context, String MENU_TABLE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.MENU_TABLE_NAME = MENU_TABLE_NAME;
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

    public void createTable(String tableName){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                MENU_COLUMN_ID + " INTEGER PRIMARY KEY, " + MENU_COLUMN_DAY + " TEXT, " +
                MENU_COLUMN_BREAKFAST + " TEXT, "+ MENU_COLUMN_LUNCH + " TEXT, " +
                MENU_COLUMN_DINNER + " TEXT)";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public boolean insertMenuItem(String day, String breakfast, String lunch, String dinner){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MENU_COLUMN_DAY, day);
        contentValues.put(MENU_COLUMN_BREAKFAST, breakfast);
        contentValues.put(MENU_COLUMN_LUNCH, lunch);
        contentValues.put(MENU_COLUMN_DINNER, dinner);
        sqLiteDatabase.insert(MENU_TABLE_NAME,null, contentValues);
        sqLiteDatabase.close();
        return true;
    }

    public boolean updateMenuItem(Integer id, String day, String breakfast, String lunch, String dinner){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MENU_COLUMN_DAY, day);
        contentValues.put(MENU_COLUMN_BREAKFAST, breakfast);
        contentValues.put(MENU_COLUMN_LUNCH, lunch);
        contentValues.put(MENU_COLUMN_DINNER, dinner);
        sqLiteDatabase.update(MENU_TABLE_NAME, contentValues, MENU_COLUMN_ID + " = ?", new String[] {Integer.toString(id)});
        sqLiteDatabase.close();
        return true;
    }

    public Cursor getMenuItem(Integer id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + MENU_TABLE_NAME + " WHERE " + MENU_COLUMN_ID + "= ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[] {Integer.toString(id)});
        return cursor;
    }

    public Cursor getAllItems(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + MENU_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public Integer deleteItem(Integer id){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Integer ret = sqLiteDatabase.delete(MENU_TABLE_NAME, "WHERE " + MENU_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        sqLiteDatabase.close();
        return ret;
    }

    public int getDatabaseCount(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + MENU_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int ret = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return ret;
    }
}
