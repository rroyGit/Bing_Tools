package com.rroycsdev.bingtools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;


public class BingDiningMenu {

    private static final String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private static final int[] resImg = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday, R.drawable.thursday,
                                  R.drawable.friday, R.drawable.saturday, R.drawable.sunday};
    private static final String SAMPLE_MENU = "Sample Menu";
    private static final String NO_DATE = "noDate";
    private static final String FAILED_MENU_DATE = "failedMenuDate";
    private static final int MENU_CHECK_ERROR_CODE = 404;

    private String title, link;
    private List<ListItem> listItems;

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DiningDatabase diningDatabase;
    private boolean showProgressDialog;
    private AppCompatTextView toolbar;
    private View diningMenuView;


    public BingDiningMenu(String link, String title, Context context, List<ListItem> listItems, boolean showProgressDialog, View view){
        this.context = context;
        this.title = title;
        this.link = link;
        this.listItems = listItems;
        this.diningDatabase = new DiningDatabase(context, title);
        this.diningDatabase.createTable(title);
        this.showProgressDialog = showProgressDialog;
        this.diningMenuView = view;
    }

    private void makeBingDiningRequest(){
        StringBuilder currentMonth = new StringBuilder(), currentDay = new StringBuilder(), currentYear = new StringBuilder();
        loadCurrentDate(currentMonth, currentDay, currentYear);

        DiningDataScrapper diningDataScrapper;
        StringTokenizer sT;

        //load data from SQLite database and check if a new menu is found, if yes then load new menu
        if(diningDatabase.getDatabaseCount() > 0 && getBingWeekDate(title).compareTo(NO_DATE) !=0 && getBingWeekDate(title).compareTo(FAILED_MENU_DATE) !=0) {

            //saved start and end dates per dining menu
            String month2, date2, year2;

            try {

                //sample menu check
                if(getBingWeekDate(title).compareTo(SAMPLE_MENU) == 0){
                    final int dayInt = Integer.parseInt(currentDay.toString());

                    //if shared pref for daily menu check is empty, make a new request else record the currentDay checked
                    //if saved currentDay does not equal current currentDay, make new request
                    if(getDayMenuCheck() == MENU_CHECK_ERROR_CODE || dayInt != getDayMenuCheck()) {
                        new MakeNewMenuRequest(BingDiningMenu.this).execute(link);
                        setDayMenuCheck(dayInt);
                    }else{
                        loadSortedData();
                        adapter = new MenuAdapter(listItems, context, recyclerView);
                        recyclerView.setAdapter(adapter);
                    }
                }else {
                    sT = new StringTokenizer(getBingWeekDate(title), " ");
                    sT.nextToken(); //first date
                    sT.nextToken(); //hyphen
                    String secDate = sT.nextToken(); //second date

                    sT = new StringTokenizer(secDate, "/");
                    month2 = sT.nextToken();
                    date2 = sT.nextToken();
                    year2 = sT.nextToken();


                    //if current currentDay does not fall in saved dates then make new request
                    if(Integer.parseInt(currentMonth.toString()) > Integer.parseInt(month2)) {
                        diningDataScrapper = new DiningDataScrapper(this);
                        diningDataScrapper.execute(true);

                    }else if (((currentMonth.toString().equals(month2)) && (Integer.parseInt(currentDay.toString()) > Integer.parseInt(date2)))) {
                        diningDataScrapper = new DiningDataScrapper(this);
                        diningDataScrapper.execute(true);

                    }else if (Integer.parseInt(currentYear.toString()) > Integer.parseInt(year2)) {
                        diningDataScrapper = new DiningDataScrapper(this);
                        diningDataScrapper.execute(true);

                    }else {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loadSortedData();
                            }
                        });
                        thread.start();
                        assert listItems != null;
                        if(listItems.isEmpty()){
                            try {
                                thread.join();
                                if(BuildConfig.DEBUG && !setData()){
                                    Toast.makeText(context,"Data was not set",Toast.LENGTH_SHORT).show();
                                }
                            } catch (InterruptedException e3) {
                                e3.printStackTrace();
                            }
                        }
                    }
                }

            } catch (Exception e){
                Toast.makeText(context, "Error parsing: " + e.toString(), Toast.LENGTH_SHORT).show();
                diningDatabase.close();
            }
        }else { //nothing stored in database so make http request

            //load default image and toast when no internet
            if(getDeviceInternetStatus(context) == null){
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                diningMenuView.setBackground(ContextCompat.getDrawable(context, R.drawable.cloud_2));
            }else {

                //make a new http request to grab data from web
                diningDataScrapper = new DiningDataScrapper(this);
                diningDataScrapper.execute(false);
                setDayMenuCheck(Integer.parseInt(currentDay.toString()));
            }
        }
        //close database connection
        diningDatabase.close();
    }

    private boolean setData(){
        assert listItems != null;
        if(listItems.isEmpty()) {
            //Log.e("SetData ", "Must populate listItems before calling setData");
            return false;
        }
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
        return true;
    }

    public void setToolbar(AppCompatTextView toolbar) {
        String textToSet;
        if(getBingWeekDate(title).compareTo(NO_DATE) == 0)
            toolbar.setText("");
        else if(getBingWeekDate(title).compareTo(FAILED_MENU_DATE) == 0){
            textToSet = "No Menu Found";
            toolbar.setText(textToSet);
        }else{
            toolbar.setText(getBingWeekDate(title));
        }

        this.toolbar = toolbar;
    }

    public AppCompatTextView getToolbar() {
        return toolbar;
    }

    //check if a new menu is available by checking on the week date of the menu
    private static class MakeNewMenuRequest extends AsyncTask<String, Void, Boolean> {
        private String weekString = "Not Sample Menu";
        WeakReference<BingDiningMenu> weakReference;
        MakeNewMenuRequest(BingDiningMenu context){
            weakReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(String ... strings) {
            final String url = strings[0];

            try {
                // check for valid connection
                Connection.Response response = Jsoup.connect(url).timeout(2000).execute();
                if (response.statusCode() != 200) {
                   return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean updatePassed) {
            super.onPostExecute(updatePassed);
            BingDiningMenu bingDiningMenu = weakReference.get();
            if(updatePassed) {
                if (weekString.compareTo("Sample Menu") != 0) {
                    DiningDataScrapper diningDataScrapper = new DiningDataScrapper(bingDiningMenu);
                    diningDataScrapper.execute(true);
                } else {
                    Toast.makeText(bingDiningMenu.context, "No new menu yet", Toast.LENGTH_SHORT).show();
                    bingDiningMenu.loadSortedData();
                    bingDiningMenu.adapter = new MenuAdapter(bingDiningMenu.listItems, bingDiningMenu.context, bingDiningMenu.recyclerView);
                    bingDiningMenu.recyclerView.setAdapter(bingDiningMenu.adapter);
                    bingDiningMenu.diningDatabase.close();
                }
            }else {
                Toast.makeText(bingDiningMenu.context, "No data found on server", Toast.LENGTH_SHORT).show();
                bingDiningMenu.diningMenuView.setBackground(ContextCompat.getDrawable(bingDiningMenu.context, R.drawable.cloud_2));
                bingDiningMenu.saveBingWeekData(FAILED_MENU_DATE);
            }
        }
    }
    //make async web request using JSOUP
    //first parameter (Boolean), if true update database - if false insert into database
    private static class DiningDataScrapper extends AsyncTask<Boolean, Void, Void> {
        private StringBuilder stringBuilderBreakfast = new StringBuilder();
        private StringBuilder stringBuilderLunch = new StringBuilder();
        private StringBuilder stringBuilderDinner = new StringBuilder();

        private String weekString;
        private ProgressDialog pD;
        private Boolean updateDatabase = false;
        private Boolean loadEmptyMenu = false;
        private String errorMessage = "";


        //weak reference
        private WeakReference <BingDiningMenu> activityReference;
        DiningDataScrapper(BingDiningMenu context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            BingDiningMenu bingDiningMenu = activityReference.get();

            if(bingDiningMenu.showProgressDialog) {
                pD = new ProgressDialog(bingDiningMenu.context);
                pD.setCancelable(false);
                pD.setMessage("Loading menu, please wait.");
                pD.show();
            }
        }

        @Override
        protected Void doInBackground(Boolean... params) {

            final BingDiningMenu bingDiningMenu = activityReference.get();
            if(params.length > 0) updateDatabase = params[0];

            if(bingDiningMenu == null){
                return null;
            }

            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // test for valid connection
                        Connection.Response response = Jsoup.connect(bingDiningMenu.link).timeout(5000).execute();
                        if (response.statusCode() != 200) {
                           loadEmptyMenu = true;
                           return;
                        }

                        Document doc2 = Jsoup.connect(bingDiningMenu.link).get();
                        Element body = doc2.body();
                        Element menuDiv = body.getElementById("bite-menu");
                        String menuActive = menuDiv.getElementById("bite-calc").select("p").text();

                        if(menuActive.equals("Sorry, no menu found")){
                            loadEmptyMenu = true;
                            errorMessage = "Dining hall is closed, no menu found";
                            return;
                        }

                        Elements daysAll = menuDiv.select("li[id~=menuid-\\d+$]");
                        Elements menuAll = menuDiv.select("div[id~=menuid-\\d+-day]");

                        List<String> dayNames = new ArrayList<>();
                        String startDay = null, endDay = "";

                        for (Element e: daysAll) {
                            String shortDay = e.text().substring(0, 3);

                            if (startDay == null) startDay = e.text().substring(4);
                            endDay = e.text().substring(4);

                            switch (shortDay) {
                                case "Mon": dayNames.add("monday");
                                    break;
                                case "Tue": dayNames.add("tuesday");
                                    break;
                                case "Wed": dayNames.add("wednesday");
                                    break;
                                case "Thu": dayNames.add("thursday");
                                    break;
                                case "Fri": dayNames.add("friday");
                                    break;
                                case "Sat": dayNames.add("saturday");
                                    break;
                                case "Sun": dayNames.add("sunday");
                                    break;
                            }
                        }

                        StringBuilder currentMonth = new StringBuilder(), currentDay = new StringBuilder(), currentYear = new StringBuilder();
                        loadCurrentDate(currentMonth, currentDay, currentYear);

                        // "1/10/2019 - 1/21/2019"
                        if (Integer.parseInt(startDay) < Integer.parseInt(endDay)) {
                            weekString = currentMonth.toString() + "/" + startDay + "/" + currentYear.toString() + " - " +
                                    currentMonth.toString() + "/" + endDay + "/" + currentYear.toString();

                        } else {
                            String nextMonth = String.valueOf(Integer.parseInt(currentMonth.toString()) + 1);
                            weekString = currentMonth.toString() + "/" + startDay + "/" + currentYear.toString() + " - " +
                                    nextMonth + "/" + endDay + "/" + currentYear.toString();
                        }

                        // let's skip year check yo

                        currentMonth.delete(0, currentMonth.length());
                        currentDay.delete(0, currentDay.length());
                        currentYear.delete(0, currentYear.length());

                        String stringToAppend;
                        int index, i;

                        for(i = 0; i < daysAll.size(); i++) {
                            Elements B = menuAll.get(i).select("div[class~=accordion-block breakfast]");
                            Elements L = menuAll.get(i).select("div[class~=accordion-block lunch]");
                            Elements D = menuAll.get(i).select("div[class~=accordion-block dinner]");
                            index = 1;

                            if (B.size() > 0) {
                                for (Element e : B.get(0).select("a[data-fooditemname]")) {
                                    stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                                    stringBuilderBreakfast.append(stringToAppend);
                                    index++;
                                }
                            }
                            index = 1;
                            if (L.size() > 0) {
                              for (Element e : L.get(0).select("a[data-fooditemname]")) {
                                  stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                                  stringBuilderLunch.append(stringToAppend);
                                  index++;
                              }
                            }
                            index = 1;
                            if(D.size() > 0) {
                              for (Element e : D.get(0).select("a[data-fooditemname]")) {
                                  stringToAppend = ((index < 10) ? "0" + index : index) + ". " + e.text() + '\n';
                                  stringBuilderDinner.append(stringToAppend);
                                  index++;
                              }
                            }

                            stringBuilderBreakfast.append((stringBuilderBreakfast.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                            stringBuilderLunch.append((stringBuilderLunch.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                            stringBuilderDinner.append((stringBuilderDinner.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");

                            if(updateDatabase) bingDiningMenu.diningDatabase.updateMenuItem(i+1,dayNames.get(i), stringBuilderBreakfast.toString(),
                                    stringBuilderLunch.toString(), stringBuilderDinner.toString());
                            else bingDiningMenu.diningDatabase.insertMenuItem(dayNames.get(i), stringBuilderBreakfast.toString(),
                                    stringBuilderLunch.toString(), stringBuilderDinner.toString());

                            stringBuilderBreakfast.delete(0, stringBuilderBreakfast.length());
                            stringBuilderLunch.delete(0, stringBuilderLunch.length());
                            stringBuilderDinner.delete(0, stringBuilderDinner.length());
                        }
                        dayNames.clear();
                    } catch(IOException e){
                        errorMessage = "Error occurred while processing data - most likely no data on server";
                        e.printStackTrace();
                        loadEmptyMenu = true;
                    } catch (NumberFormatException e) {
                        errorMessage = "Web data has changed on backend - dev will push an update soon";
                        e.printStackTrace();
                        loadEmptyMenu = true;
                    } catch (Exception e) {
                        errorMessage = "Unknown error has occurred - dev will push an update soon";
                        e.printStackTrace();
                        loadEmptyMenu = true;
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pD != null) pD.dismiss();

            final BingDiningMenu bingDiningMenu = activityReference.get();
            if(bingDiningMenu == null) return;


            if(loadEmptyMenu){
                Toast.makeText(activityReference.get().context, errorMessage, Toast.LENGTH_SHORT).show();

                bingDiningMenu.diningDatabase.deleteAllItems();
                bingDiningMenu.recyclerView.setAdapter(null);
                bingDiningMenu.adapter.notifyDataSetChanged();

                bingDiningMenu.diningMenuView.setBackground(ContextCompat.getDrawable(bingDiningMenu.context, R.drawable.cloud_2));

                bingDiningMenu.saveBingWeekData(FAILED_MENU_DATE);
                if (bingDiningMenu.getToolbar() != null) {
                    String textToSet = "No Menu Found";
                    bingDiningMenu.getToolbar().setText(textToSet);
                }
            }else {
                bingDiningMenu.saveBingWeekData(weekString);
                if (bingDiningMenu.getToolbar() != null) bingDiningMenu.getToolbar().setText(weekString);
                
                bingDiningMenu.loadSortedData();
                bingDiningMenu.adapter = new MenuAdapter(bingDiningMenu.listItems, bingDiningMenu.context, bingDiningMenu.recyclerView);
                bingDiningMenu.recyclerView.setAdapter(bingDiningMenu.adapter);
                bingDiningMenu.adapter.notifyDataSetChanged();
                bingDiningMenu.diningDatabase.close();
            }
        }
    }

    private void saveBingWeekData(String date){
        SharedPreferences sP = context.getSharedPreferences("BingDiningFragment"+title, MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString("weekDate", date);
        sEditor.apply();
    }

    public String getBingWeekDate(String title){
        SharedPreferences sP = context.getSharedPreferences("BingDiningFragment"+title, MODE_PRIVATE);
        return sP.getString("weekDate", "noDate");
    }

    private void setDayMenuCheck(int day){
        SharedPreferences sharedPreferences = context.getSharedPreferences("menuCounter", MODE_PRIVATE);
        sharedPreferences.edit().putInt("day", day).apply();
    }
    private int getDayMenuCheck(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("menuCounter", MODE_PRIVATE);
        return sharedPreferences.getInt("day", MENU_CHECK_ERROR_CODE);
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setAdapter(List<ListItem> listItems){
       adapter = new MenuAdapter(listItems,context, recyclerView);
   }

    public void makeRequest(){
        makeBingDiningRequest();
    }

    public void refreshData(){
        if(getDeviceInternetStatus(context) == null){
            Toast.makeText(context,"No Internet", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Refreshing " + title + " Menu", Toast.LENGTH_SHORT).show();
            new DiningDataScrapper(this).execute(true);
        }
    }

    private void loadSortedData(){
        String breakfast, lunch, dinner;
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
                dinner = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_DINNER));
                ListItem listItem = new ListItem(breakfast, lunch, dinner, resImg[id-1]);
                listItems.add(listItem);
            }
            cursor.close();
        }

        for(int id = 1; id < index; id++) {
            cursor = diningDatabase.getMenuItem(id);
            if (cursor.moveToFirst()) {
                breakfast = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_BREAKFAST));
                lunch = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_LUNCH));
                dinner = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_DINNER));
                ListItem listItem = new ListItem(breakfast, lunch, dinner, resImg[id-1]);
                listItems.add(listItem);
            }
            cursor.close();
        }
    }

    private int findStartIndex(){
        int index = 0;
        Date date =  new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        String dayWeek = dateFormatter.format(date);
        StringBuilder day_3char = new StringBuilder();

        for(String day: days){
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

    public static void loadCurrentDate(StringBuilder... strings){
        Date dateNow = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("M-d-yyyy", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        StringTokenizer sT = new StringTokenizer(dateFormatter.format(dateNow), "-");

        strings[0].append(sT.nextToken());
        strings[1].append(sT.nextToken());
        strings[2].append(sT.nextToken());
    }

    //No Internet access if returns null
    public static NetworkInfo getDeviceInternetStatus(Context context){
        //check if internet is enabled
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo();
        }
        return null;
    }


}