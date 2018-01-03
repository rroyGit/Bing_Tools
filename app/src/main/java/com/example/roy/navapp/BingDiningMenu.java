package com.example.roy.navapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;


public class BingDiningMenu {

    private static final String days[] = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private static final int resImg[] = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday, R.drawable.thursday,
                                  R.drawable.friday, R.drawable.saturday, R.drawable.sunday};
    private String month, date, year, title;
    private String link;
    private final String[] savedDate;
    private List<ListItem> listItems;

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private AppCompatTextView toolbarTitle;
    private DiningDatabase diningDatabase;

    public BingDiningMenu(String link, String title, Context context, List<ListItem> listItems){
        this.context = context;
        this.title = title;
        this.link = link;
        this.listItems = listItems;
        savedDate = new String[3];
        diningDatabase = new DiningDatabase(context, title);
        diningDatabase.createTable(title);
    }

    private void startBingDiningRequest(){
        //Async Task for getting bing dining data from the web
        getSavedDate();
        DiningDataScrapper bing;
        StringTokenizer sT;

        //load data from saved storage or from the web
        if(diningDatabase.getDatabaseCount() > 0) {
            String month1, month2, date1, date2, year1, year2;
            try {
                sT = new StringTokenizer(getWeekDate(), " ");
                String firstDate = sT.nextToken();
                sT.nextToken();
                String secDate = sT.nextToken();

                sT = new StringTokenizer(firstDate, "/");
                month1 = sT.nextToken();
                date1 = sT.nextToken();
                year1 = sT.nextToken();

                sT = new StringTokenizer(secDate, "/");
                month2 = sT.nextToken();
                date2 = sT.nextToken();
                year2 = sT.nextToken();
            } catch (Exception e){
                if(getWeekDate().compareTo("Sample Menu") == 0){
                    toolbarTitle.setText(getWeekDate());
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                           loadSortedData(BingDiningMenu.this);
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
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    return;
                }else Toast.makeText(context, "Error loading saved data " + e.toString(), Toast.LENGTH_LONG).show();
                return;
            }
            if(Integer.parseInt(month) < Integer.parseInt(month1) || Integer.parseInt(month) > Integer.parseInt(month2)) {
                bing = new DiningDataScrapper(this);
                bing.execute();
            }else if (((month.equals(month1)) && (Integer.parseInt(date) < Integer.parseInt(date1))) ||
                    ((month.equals(month2)) && (Integer.parseInt(date) > Integer.parseInt(date2)))) {
                bing = new DiningDataScrapper(this);
                bing.execute();
            }else if (Integer.parseInt(year) < Integer.parseInt(year1) || Integer.parseInt(year) > Integer.parseInt(year2)) {
                bing = new DiningDataScrapper(this);
                bing.execute();
            }else {
                toolbarTitle.setText(getWeekDate());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadSortedData(BingDiningMenu.this);
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
        }else{
            if(getDeviceInternetStatus(context) == null){
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            bing = new DiningDataScrapper(this);
            bing.execute();
        }
        adapter = new MyAdapter(listItems, context);
        recyclerView.setAdapter(adapter);
    }

    private boolean setData(){
        assert listItems != null;
        if(listItems.isEmpty()) {
            Log.e("SetData ", "Must populate listItems before calling setData");
            return false;
        }
        adapter = new MyAdapter(listItems, context);
        recyclerView.setAdapter(adapter);
        return true;
    }

    public void setRecycleAdapter(){
        recyclerView.setAdapter(adapter);
    }

    /*
    private class MenuDate extends AsyncTask<Void, Void, Void>{
        private String weekStrings[] = new String[2];

        int i = 0;
        @Override
        protected Void doInBackground(Void ... voids) {
            Document doc;
            Elements weekUrl;
            try {
                doc = Jsoup.connect(link).get();
                weekUrl = doc.getElementsByClass("accordionBody");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            //get updated links to Bing dining data
            assert weekUrl != null;
            for (Element link : weekUrl) {
                Elements f = link.getElementsByTag("a");
                for (Element a : f) {
                    weekStrings[i] = a.text();
                    i++;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(weekStrings[0] == null) {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            if(weekStrings[0].compareTo("Sample Menu") != 0){
                DiningDataScrapper diningDataScrapper = new DiningDataScrapper(get);
                diningDataScrapper.execute();
            }else {
                Toast.makeText(context, "Just Checked: No new menu yet.", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/
    private static class DiningDataScrapper extends AsyncTask<Void, Void, Void> {
        private StringBuilder stringBuilderBreakfast = new StringBuilder();
        private StringBuilder stringBuilderLunch = new StringBuilder();
        private StringBuilder stringBuilderDinner = new StringBuilder();

        private String urlStrings[] = new String[2];
        private String weekStrings[] = new String[2];
        private ProgressDialog pD;

        //weak reference
        private WeakReference <BingDiningMenu> activityReference;
        DiningDataScrapper(BingDiningMenu context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            BingDiningMenu bingDiningMenu = activityReference.get();
            Log.e("onPreExecute ", bingDiningMenu.title);

            if(bingDiningMenu.title.compareTo(bingDiningMenu.context.getString(R.string.c4)) == 0 ||
                    bingDiningMenu.title.compareTo(bingDiningMenu.context.getString(R.string.ciw)) == 0) {
                pD = new ProgressDialog(bingDiningMenu.context);
                pD.setCancelable(false);
                pD.setMessage("Loading, please wait...");
                pD.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final BingDiningMenu bingDiningMenu = activityReference.get();

            if(bingDiningMenu == null){
                Log.e("weakRef is ", "null");
                return null;
            }
            if(pD != null) pD.dismiss();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(bingDiningMenu.link).get();
                        Elements weekUrl = doc.getElementsByClass("accordionBody");

                        //get updated links to Bing dining data
                        int i = 0;
                        for (Element link : weekUrl) {
                            Elements f = link.getElementsByTag("a");
                            for(Element a: f) {
                                urlStrings[i] = a.attr("href");
                                weekStrings[i] = a.text();
                                i++;
                            }
                        }

                        String firstUrl = "https://binghamton.sodexomyway.com"+urlStrings[0];
                        Document doc2 = Jsoup.connect(firstUrl).get();
                        String stringToAppend;
                        int index;
                        for(i = 0; i < 7; i++) {
                            Elements B = doc2.getElementById(days[i]).select("tr.brk").select("span.ul");
                            Elements L = doc2.getElementById(days[i]).select("tr.lun").select("span.ul");
                            Elements D = doc2.getElementById(days[i]).select("tr.din").select("span.ul");

                            index = 1;
                            for (Element e : B) {
                                stringToAppend = ((index < 10)? "0"+index:index) +". "+ e.text()+'\n';
                                stringBuilderBreakfast.append(stringToAppend);
                                index++;
                            }
                            index = 1;
                            for (Element e : L) {
                                stringToAppend = ((index < 10)? "0"+index:index)+". "+ e.text()+'\n';
                                stringBuilderLunch.append(stringToAppend);
                                index++;
                            }
                            index = 1;
                            for (Element e : D) {
                                stringToAppend = ((index < 10)? "0"+index:index)+". "+ e.text()+'\n';
                                stringBuilderDinner.append(stringToAppend);
                                index++;
                            }

                            stringBuilderBreakfast.append((stringBuilderBreakfast.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                            stringBuilderLunch.append((stringBuilderLunch.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");
                            stringBuilderDinner.append((stringBuilderDinner.toString().length() == 0)? "01. Time to visit Marketplace\n\n\n": "");

                            bingDiningMenu.diningDatabase.insertMenuItem(days[i],stringBuilderBreakfast.toString(), stringBuilderLunch.toString(),stringBuilderDinner.toString());
                            stringBuilderBreakfast.delete(0, stringBuilderBreakfast.length());
                            stringBuilderLunch.delete(0, stringBuilderLunch.length());
                            stringBuilderDinner.delete(0, stringBuilderDinner.length());

                        }

                    }catch(IOException e){
                        e.printStackTrace();
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
            final BingDiningMenu bingDiningMenu = activityReference.get();
            if(bingDiningMenu == null){
                Log.e("weakRef is ", "null"); return;
            }
            bingDiningMenu.toolbarTitle.setText(weekStrings[0]);
            bingDiningMenu.saveBingWeekData(weekStrings[0]);
            bingDiningMenu.loadSortedData(bingDiningMenu);
            bingDiningMenu.adapter = new MyAdapter(bingDiningMenu.listItems, bingDiningMenu.context);
            bingDiningMenu.recyclerView.setAdapter(bingDiningMenu.adapter);

        }
    }

    private void saveBingWeekData(String date){
        SharedPreferences sP = context.getSharedPreferences("BingDining"+title, MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString("weekDate", date);
        sEditor.apply();
    }

    private String getWeekDate(){
        SharedPreferences sP = context.getSharedPreferences("BingDining"+title, MODE_PRIVATE);
        return sP.getString("weekDate", "noDate");
    }


    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setAdapter(List<ListItem> listItems){
       adapter = new MyAdapter(listItems,context);
   }

    public void setToolbar(AppCompatTextView toolbarTitle){
       this.toolbarTitle = toolbarTitle;
    }

    public void makeRequest(){
       startBingDiningRequest();
    }


    private void loadSortedData(BingDiningMenu bingDiningMenu){
        String breakfast, lunch, dinner;
        //clear all previous remnants
        listItems.clear();
        int index = findStartIndex() +1;

        Cursor cursor = null;
        for(int id = index; id <= days.length; id++) {
            cursor = bingDiningMenu.diningDatabase.getMenuItem(id);
            if (cursor.moveToFirst()) {
                breakfast = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_BREAKFAST));
                lunch = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_LUNCH));
                dinner = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_DINNER));
                ListItem listItem = new ListItem(breakfast, lunch, dinner, resImg[id-1]);
                assert bingDiningMenu.listItems != null;
                bingDiningMenu.listItems.add(listItem);
            }
        }

        for(int id = 1; id < index; id++) {
            cursor = bingDiningMenu.diningDatabase.getMenuItem(id);
            if (cursor.moveToFirst()) {
                breakfast = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_BREAKFAST));
                lunch = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_LUNCH));
                dinner = cursor.getString(cursor.getColumnIndex(DiningDatabase.MENU_COLUMN_DINNER));
                ListItem listItem = new ListItem(breakfast, lunch, dinner, resImg[id-1]);
                assert bingDiningMenu.listItems != null;
                bingDiningMenu.listItems.add(listItem);
            }
        }
        assert cursor != null;
        cursor.close();
    }

    private int findStartIndex(){
        int index = 0;
        Date date =  new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E", Locale.US);
        String dayWeek = dateFormatter.format(date);
        StringBuilder day_3char = new StringBuilder();

        for(String day: days){
            String first_char = String.valueOf(day.charAt(0));
            day_3char.append(first_char.toUpperCase());
            day_3char.append(day.substring(1,3));
            if(dayWeek.compareTo(day_3char.toString()) == 0){
                return index;
            }else day_3char.delete(0, day_3char.length());
            index++;
        }
        return index;
    }

    private void getSavedDate(){
        //get current date
        getDate(savedDate);
        month = savedDate[0];
        date = savedDate[1];
        year = savedDate[2];
    }

    public static void getDate(String[] ret){
        Date dateNow = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("M-d-yyyy", Locale.US);
        StringTokenizer sT = new StringTokenizer(dateFormatter.format(dateNow), "-");
        ret[0] = sT.nextToken();
        ret[1] = sT.nextToken();
        ret[2] = sT.nextToken();
    }

    public static NetworkInfo getDeviceInternetStatus(Context context){
        //check if internet is enabled
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo();
        }
        return null;
    }
}