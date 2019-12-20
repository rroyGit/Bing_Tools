package com.rroycsdev.bingtools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private SQLiteDatabase sqLiteDatabaseAllItems = null;
    private SQLiteDatabase sqLiteDatabaseItem = null;
    private Context context = null;


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

    public void setTableName(String tableName) {
        MENU_TABLE_NAME = tableName;
    }

    public void createTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = getDBHelper().getWritableDatabase();
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
        SQLiteDatabase sqLiteDatabase = getDBHelper().getWritableDatabase();
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
        SQLiteDatabase sqLiteDatabase = getDBHelper().getWritableDatabase();
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
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + MENU_TABLE_NAME + " WHERE " + MENU_COLUMN_ID + "= ?";
        return sqLiteDatabase.rawQuery(query, new String[] {Integer.toString(id)});
    }

    public SQLiteDatabase getSqLiteDatabaseItemRef(){
        return sqLiteDatabaseItem;
    }

    public Cursor getAllItems() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabaseAllItems = sqLiteDatabase;
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
        SQLiteDatabase sqLiteDatabase = getDBHelper().getWritableDatabase();
        int numItems = sqLiteDatabase.delete(MENU_TABLE_NAME, null, null);
        sqLiteDatabase.close();
        return numItems;
    }

    public int getDatabaseCount() {
        SQLiteDatabase sqLiteDatabase = getDBHelper().getReadableDatabase();
        String query = "SELECT * FROM " + MENU_TABLE_NAME;

        int ret;
        //cursor operations are NOT protected by reference counting
        //https://stackoverflow.com/questions/23293572/android-
        //cannot-perform-this-operation-because-the-connection-pool-has-been-clos
        synchronized (sqLiteDatabase) {
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            ret = cursor.getCount();
            cursor.close();
        }

        sqLiteDatabase.close();
        return ret;
    }

    private DiningDatabase getDBHelper() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        if (!sqLiteDatabase.isOpen()) {
            DiningDatabase a = new DiningDatabase(context);
            a.setTableName(MENU_TABLE_NAME);
            return a;
        }
        return this;
    }

}
