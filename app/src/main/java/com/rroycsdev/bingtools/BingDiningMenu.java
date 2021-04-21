package com.rroycsdev.bingtools;

import com.rroycsdev.bingtools.BingDiningScrapper.DiningDataScrapper;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class BingDiningMenu {
    private static final String[] days = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday",
            "saturday"};

    private static final int[] resImg = {R.drawable.sunday, R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday,
            R.drawable.thursday, R.drawable.friday, R.drawable.saturday};

    private static final String SAMPLE_MENU = "Sample Menu";
    private static final String NO_DATE = "noDate";
    private static final int MENU_CHECK_ERROR_CODE = 404;
    static final String FAILED_MENU_DATE = "failedMenuDate";

    private DiningDataScrapper diningDataScrapper = null;
    private List<ListItem> listItems;
    private RecyclerView recyclerView;
    protected boolean showSavedMsg = true;

    boolean isShowProgressDialog;
    private AppCompatTextView toolbarTitle;
    DiningDatabase diningDatabase;
    View diningMenuView;
    TabLayout tabLayout;
    Context context;
    String title;
    String link;

    BingDiningMenu(String link, String title, Context context, List<ListItem> listItems,
                   boolean isShowProgressDialog, View view) {
        this.context = context;
        this.title = title;
        this.link = link;
        this.listItems = listItems;

        this.isShowProgressDialog = isShowProgressDialog;
        this.diningMenuView = view;
    }

    private void createDataTable() {
        diningDatabase = getDiningDatabase();
        diningDatabase.createTable(title);
    }

    private DiningDatabase getDiningDatabase() {
        if (diningDatabase == null) {
            diningDatabase = new DiningDatabase(context);
        }
        return diningDatabase;
    }

    private void makeBingDiningRequest() {
        StringBuilder currentMonth = new StringBuilder(), currentDay = new StringBuilder(),
                currentYear = new StringBuilder();
        CommonUtilities.loadCurrentDate(currentMonth, currentDay, currentYear);

        WebCrawler webCrawler;
        StringTokenizer sT;

        final int dayInt = Integer.parseInt(currentDay.toString());

        //TODO - alternate getMenuUpdateStatus() result to reset database on a pushed update

        //load data from SQLite database and check if a new menu is found, if yes then load new menu
        if (!getMenuUpdateStatus() && diningDatabase.getDatabaseCount() > 0
                && getMenuWeekDate(title).compareTo(NO_DATE) != 0
                && getMenuWeekDate(title).compareTo(FAILED_MENU_DATE) != 0) {

            //saved start and end dates per dining menu
            String month2, date2, year2;

            try {
                //sample menu check
                if (getMenuWeekDate(title).compareTo(SAMPLE_MENU) == 0) {

                    //if daily menu check is empty, make a new request else record the currentDay checked
                    //if saved currentDay does not equal current currentDay, make new request
                    if (getDayMenuCheck() == MENU_CHECK_ERROR_CODE || dayInt != getDayMenuCheck()) {
                        new BingDiningScrapper(BingDiningMenu.this).makeDailyRequest(link);
                        setDayMenuCheck(dayInt);
                    } else {
                        loadSortedData();
                    }
                } else {
                    sT = new StringTokenizer(getMenuWeekDate(title), " ");
                    sT.nextToken(); //first date
                    sT.nextToken(); //hyphen
                    String secDate = sT.nextToken(); //second date

                    sT = new StringTokenizer(secDate, "/");
                    month2 = sT.nextToken();
                    date2 = sT.nextToken();
                    year2 = sT.nextToken();

                    //if current currentDay does not fall in saved dates then make new request
                    if ( (Integer.parseInt(currentMonth.toString()) > Integer.parseInt(month2))
                        || ((currentMonth.toString().equals(month2)) &&
                            (Integer.parseInt(currentDay.toString()) > Integer.parseInt(date2)))
                        || (Integer.parseInt(currentYear.toString()) > Integer.parseInt(year2)) ) {

                            webCrawler = new BingDiningScrapper(this);
                            diningDataScrapper = webCrawler.getDiningMenuData(false);

                    } else {
                        loadSortedData();
                    }
                }
            } catch (Exception e){
                Toast.makeText(context, "Error parsing: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else { //nothing stored in database so make http request

            //load default image and toast when no internet
            if(CommonUtilities.getDeviceInternetStatus(context) == null){
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                diningMenuView.setBackground(ContextCompat.getDrawable(context, R.drawable.cloud_2));
            } else {

                if (getMenuWeekDate(title).equals(FAILED_MENU_DATE) && dayInt == getDayMenuCheck()) {
                    diningMenuView.setBackground(ContextCompat.getDrawable(context, R.drawable.cloud_2));
                } else {
                    //make a new http request to grab data from web
                    webCrawler = new BingDiningScrapper(this);
                    diningDataScrapper = webCrawler.getDiningMenuData(true);
                }
                saveMenuUpdateStatus(!getMenuUpdateStatus());
                setDayMenuCheck(dayInt);
            }
        }
        //close database connection
        diningDatabase.close();
    }

    boolean setView(boolean listUpdated) {
        setToolbarText();
        showErrorMsg();

        if (listUpdated && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            recyclerView.setAdapter(new MenuAdapter(listItems, context, recyclerView));
        }
        recyclerView.requestLayout();
        return true;
    }

    void clearView () {
        listItems.clear();
        recyclerView.setAdapter(null);
    }

    void setToolbarTitle (AppCompatTextView toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    void setTabLayout (TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    private void showErrorMsg () {
        if (showSavedMsg) {
            String msg = getMenuMsg();
            if (!msg.isEmpty() && !msg.equals("noMsg")) {
                if (diningMenuView.isShown())
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setToolbarText() {
        String textToSet;

        if (getMenuWeekDate(title).compareTo(NO_DATE) == 0) {
            textToSet = "";

        } else if (getMenuWeekDate(title).compareTo(FAILED_MENU_DATE) == 0){
            textToSet = "No Menu Found";

        } else {
            textToSet = getMenuWeekDate(title);
        }

        if (toolbarTitle != null) {
            toolbarTitle.setText(textToSet);
        }
    }

    private void saveMenuUpdateStatus(boolean isUpdate) {
        SharedPreferences sP = context.getSharedPreferences("updateStatus", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putBoolean(title, isUpdate);
        sEditor.apply();
    }

    private boolean getMenuUpdateStatus () {
        if (context == null) return false;
        SharedPreferences sP = context.getSharedPreferences("updateStatus", MODE_PRIVATE);
        return sP.getBoolean(title, false);
    }

    void saveMenuMsg(String msg) {
        SharedPreferences sP = context.getSharedPreferences("menuMsg", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString(title, msg);
        sEditor.apply();
    }

    private String getMenuMsg () {
        if (context == null) return "error";
        SharedPreferences sP = context.getSharedPreferences("menuMsg", MODE_PRIVATE);
        return sP.getString(title, "noMsg");
    }

    void saveMenuWeekDate(String date) {
        SharedPreferences sP = context.getSharedPreferences("menuWeekDate", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString(title, date);
        sEditor.apply();
    }

    private String getMenuWeekDate(String title) {
        if (context == null) return "error";
        SharedPreferences sP = context.getSharedPreferences("menuWeekDate", MODE_PRIVATE);
        return sP.getString(title, "noDate");
    }

    private void setDayMenuCheck(int day) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("menuCounter", MODE_PRIVATE);
        sharedPreferences.edit().putInt(title, day).apply();
    }

    private int getDayMenuCheck() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("menuCounter", MODE_PRIVATE);
        return sharedPreferences.getInt(title, MENU_CHECK_ERROR_CODE);
    }

    void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    void makeRequest() {
        createDataTable();
        makeBingDiningRequest();
    }

    void refreshData() {
        if (CommonUtilities.getDeviceInternetStatus(context) == null) {
            Toast.makeText(context,"No Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Refreshing " + title + " Menu", Toast.LENGTH_SHORT).show();
            isShowProgressDialog = true;
            new BingDiningScrapper(this).getDiningMenuData(true);
        }
    }

    void loadSortedData() {
        String breakfast, lunch, afternoon, dinner;
        //clear all previous remnants
        assert listItems != null;
        listItems.clear();
        // add one for database access with id starting @ 1
        int index = findStartIndex() + 1;

        Cursor cursor;
        for(int id = index; id <= days.length; id++) {
            cursor = diningDatabase.getMenuItem(id);
            if (cursor.moveToFirst()) {
                breakfast = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_BREAKFAST));
                lunch = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_LUNCH));
                afternoon = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_AFTERNOON_SNACK));
                dinner = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_DINNER));
                ListItem listItem = new ListItem(breakfast, lunch, afternoon, dinner, resImg[id-1]);
                listItems.add(listItem);
            }
            cursor.close();
        }

        for(int id = 1; id < index; id++) {
            cursor = diningDatabase.getMenuItem(id);
            if (cursor.moveToFirst()) {
                breakfast = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_BREAKFAST));
                lunch = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_LUNCH));
                afternoon = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_AFTERNOON_SNACK));
                dinner = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_DINNER));
                ListItem listItem = new ListItem(breakfast, lunch, afternoon, dinner, resImg[id-1]);
                listItems.add(listItem);
            }
            cursor.close();
        }
    }

    private int findStartIndex() {
        int index = 0;
        Date date =  new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        String dayWeek = dateFormatter.format(date);
        StringBuilder day_3char = new StringBuilder();

        for (String day: days) {
            String first_char = String.valueOf(day.charAt(0));
            day_3char.append(first_char.toUpperCase());
            day_3char.append(day.substring(1,3));
            if(dayWeek.compareTo(day_3char.toString()) == 0) {
                return index;
            } else day_3char.delete(0, day_3char.length());
            index++;
        }
        return index;
    }

    void showDialog() {
        if (diningDataScrapper != null && diningDataScrapper.pD !=  null && isShowProgressDialog
                && diningMenuView.isShown())
            diningDataScrapper.pD.show();
    }
}