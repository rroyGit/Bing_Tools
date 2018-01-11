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

    private String title, link;
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
        diningDatabase = new DiningDatabase(context, title);
        diningDatabase.createTable(title);
    }

    private void startBingDiningRequest(){
        //Async Task for getting bing dining data from the web
        StringBuilder month = new StringBuilder(), day = new StringBuilder(), year = new StringBuilder();
        loadCurrentDate(month, day, year);

        DiningDataScrapper diningDataScrapper;
        StringTokenizer sT;

        //load data from saved storage or from the web
        if(diningDatabase.getDatabaseCount() > 0) {
            if(toolbarTitle != null) toolbarTitle.setText(getWeekDate());
            String month1, month2, date1, date2, year1, year2;
            try {
                if(getWeekDate().compareTo("Sample Menu") == 0){
                    final int dayInt = Integer.parseInt(day.toString());
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(getDayMenuCheck() == 404) {
                                new CheckForNewMenu(BingDiningMenu.this).execute(link);
                                setDayMenuCheck(dayInt);
                            }else{
                                if(dayInt != getDayMenuCheck()){
                                    new CheckForNewMenu(BingDiningMenu.this).execute(link);
                                    setDayMenuCheck(dayInt);
                                }
                            }
                        }
                    }).start();

                    if(getWeekDate().compareTo("Sample Menu") != 0){
                        diningDataScrapper = new DiningDataScrapper(this);
                        diningDataScrapper.execute();
                    }else {
                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                loadSortedData(BingDiningMenu.this);
                            }
                        });
                        thread.start();

                        assert listItems != null;
                        if (listItems.isEmpty()) {
                            try {
                                thread.join();
                                if (BuildConfig.DEBUG && !setData()) {
                                    Toast.makeText(context, "Data was not set", Toast.LENGTH_SHORT).show();
                                }
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                    return;
                }else {
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
                }
            } catch (Exception e){
                Toast.makeText(context, "Error loading saved data " + e.toString(), Toast.LENGTH_LONG).show();
                return;
            }
            if(Integer.parseInt(month.toString()) < Integer.parseInt(month1) || Integer.parseInt(month.toString()) > Integer.parseInt(month2)) {
                diningDataScrapper = new DiningDataScrapper(this);
                diningDataScrapper.execute();
            }else if (((month.equals(month1)) && (Integer.parseInt(day.toString()) < Integer.parseInt(date1))) ||
                    ((month.equals(month2)) && (Integer.parseInt(day.toString()) > Integer.parseInt(date2)))) {
                diningDataScrapper = new DiningDataScrapper(this);
                diningDataScrapper.execute();
            }else if (Integer.parseInt(year.toString()) < Integer.parseInt(year1) || Integer.parseInt(year.toString()) > Integer.parseInt(year2)) {
                diningDataScrapper = new DiningDataScrapper(this);
                diningDataScrapper.execute();
            }else {
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
            diningDataScrapper = new DiningDataScrapper(this);
            diningDataScrapper.execute();
            setDayMenuCheck(Integer.parseInt(day.toString()));
        }
        adapter = new MenuAdapter(listItems, context);
        recyclerView.setAdapter(adapter);
        diningDatabase.close();
    }

    private boolean setData(){
        assert listItems != null;
        if(listItems.isEmpty()) {
            Log.e("SetData ", "Must populate listItems before calling setData");
            return false;
        }
        adapter = new MenuAdapter(listItems, context);
        recyclerView.setAdapter(adapter);
        return true;
    }

    public void setRecycleAdapter(){
        recyclerView.setAdapter(adapter);
    }


    private static class CheckForNewMenu extends AsyncTask<String, Void, Void>{
        private String weekString;
        WeakReference<BingDiningMenu> weakReference;
        CheckForNewMenu(BingDiningMenu context){
            weakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String ... strings) {
            final String url = strings[0];
            Document doc;
            Elements weekUrl = null;
            try {
                doc = Jsoup.connect(url).get();
                weekUrl = doc.getElementsByClass("accordionBody");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //get updated links to Bing dining data
            assert weekUrl != null;
            Element link = weekUrl.first();
            Elements f = link.getElementsByTag("a");
            weekString = f.first().text();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            BingDiningMenu bingDiningMenu = weakReference.get();
            assert (weekString != null);
            if(weekString.compareTo("Sample Menu") != 0){
                bingDiningMenu.saveBingWeekData(weekString);
                DiningDataScrapper diningDataScrapper = new DiningDataScrapper(bingDiningMenu);
                diningDataScrapper.execute();
            }else {
                Toast.makeText(bingDiningMenu.context, "No new menu yet", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
            bingDiningMenu.adapter = new MenuAdapter(bingDiningMenu.listItems, bingDiningMenu.context);
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

    private void setDayMenuCheck(int day){
        SharedPreferences sharedPreferences = context.getSharedPreferences("menuCounter", MODE_PRIVATE);
        sharedPreferences.edit().putInt("day", day).apply();
    }
    private int getDayMenuCheck(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("menuCounter", MODE_PRIVATE);
        return sharedPreferences.getInt("day", 404);
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setAdapter(List<ListItem> listItems){
       adapter = new MenuAdapter(listItems,context);
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
        assert listItems != null;
        listItems.clear();
        int index = findStartIndex() +1;

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

    public static void loadCurrentDate(StringBuilder... strings){
        Date dateNow = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("M-d-yyyy", Locale.US);
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