package com.example.roy.navapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rroy6 on 12/15/2017.
 */

public class BingDiningMenu {

    private final String days[] = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private String month, date, year;
    private String[] savedDate;
    private static String link;
    private List<ListItem> listItems;

    private Context context;
    private ProgressDialog pD;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Toolbar toolbar;
    private AppCompatTextView toolbarTitle;

    public BingDiningMenu(String link, Context context){
        this.link = link;
        this.context = context;
        savedDate = new String[3];
        listItems = new ArrayList<>();
    }

    private void startBingDiningRequest(){
        //Async Task for getting bing dining data from the web
        getSavedDate();
        DiningDataScrapper bing;
        StringTokenizer sT;
        //determine whether to load data from saved storage or from the web

        if (getSavedBingData("Breakmonday").equals("error")) {

            if(getDeviceInternetStatus(context) == null){
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            bing = new DiningDataScrapper();
            bing.execute();
        } else {
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
                    loadData();
                    return;
                }else Toast.makeText(context, "Error loading saved data " + e.toString(), Toast.LENGTH_LONG).show();
                return;
            }
            if (Integer.parseInt(month) < Integer.parseInt(month1) || Integer.parseInt(month) > Integer.parseInt(month2)) {
                bing = new DiningDataScrapper();
                bing.execute();
            } else if (((month.equals(month1)) && (Integer.parseInt(date) < Integer.parseInt(date1))) ||
                    ((month.equals(month2)) && (Integer.parseInt(date) > Integer.parseInt(date2)))) {
                bing = new DiningDataScrapper();
                bing.execute();
            } else if (Integer.parseInt(year) < Integer.parseInt(year1) || Integer.parseInt(year) > Integer.parseInt(year2)) {
                bing = new DiningDataScrapper();
                bing.execute();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        toolbarTitle.setText(getWeekDate());
                        loadData();
                    }
                }).start();
            }
        }
    }

    private void loadData(){
        int resImg[] = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday, R.drawable.thursday,
                R.drawable.friday, R.drawable.saturday, R.drawable.sunday};
        //clear previous remnants
        listItems.clear();
        int index = findStartIndex();

        for(int i = index; i < 7; i++) {
            String res, res2, res3;
            res = getSavedBingData("Break"+days[i]);
            res2 = getSavedBingData("Lunch"+days[i]);
            res3 = getSavedBingData("Dinner"+days[i]);

            ListItem listItem = new ListItem(res, res2, res3, resImg[i]);
            listItems.add(listItem);
        }
        for(int i = 0; i < index; i++) {
            String res, res2, res3;
            res = getSavedBingData("Break"+days[i]);
            res2 = getSavedBingData("Lunch"+days[i]);
            res3 = getSavedBingData("Dinner"+days[i]);
            ListItem listItem = new ListItem(res, res2, res3, resImg[i]);
            listItems.add(listItem);
        }
        adapter = new MyAdapter(listItems, context);
        recyclerView.setAdapter(adapter);
    }
    public void getDate(){
        final String urlStrings[] = new String[2];
        final String weekStrings[] = new String[2];
        final MenuDate menuDate = new MenuDate();

        //bing = new DiningDataScrapper();
        //bing.execute();

        if(getWeekDate().compareTo("Sample Menu") == 0){
            menuDate.execute(urlStrings,weekStrings);
            toolbarTitle.setText(getWeekDate());
            loadData();
            Log.e("YO: "+urlStrings[0], '\n'+weekStrings[0]);
            return;
        }


    }
    private static class MenuDate extends AsyncTask<String[], String[], Void>{
       // private String urlStrings[] = new String[2];
       // private String weekStrings[] = new String[2];
        int i = 0;
        @Override
        protected Void doInBackground(String[] ... strings) {

            Document doc = null;
            Elements weekUrl = null;
            try {
                doc = Jsoup.connect(link).get();
                weekUrl = doc.getElementsByClass("accordionBody");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //get updated links to Bing dining data
            for (Element link : weekUrl) {
                Elements f = link.getElementsByTag("a");
                for(Element a: f) {
                    strings[0][i] = a.attr("href");
                    strings[1][i] = a.text();
                    //Log.e("url",  strings[1][0]);
                    i++;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private class DiningDataScrapper extends AsyncTask<Void, Void, Void> {
        private List<String> temp;
        private List<String> temp2;
        private List<String> temp3;

        private List<List<String>> breakF = new ArrayList<>();
        private List<List<String>> lunch = new ArrayList<>();
        private List<List<String>> dinner = new ArrayList<>();

        private String urlStrings[] = new String[2];
        private String weekStrings[] = new String[2];

        int i,j = 0;

        @Override
        protected void onPreExecute() {
            pD = new ProgressDialog(context);
            pD.setCancelable(false);
            pD.setMessage("Loading, please wait...");
            pD.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(link).get();
                Elements weekUrl = doc.getElementsByClass("accordionBody");

                //get updated links to Bing dining data
                for (Element link : weekUrl) {
                    Elements f = link.getElementsByTag("a");
                    for(Element a: f) {
                        urlStrings[i++] = a.attr("href");
                        weekStrings[j++] = a.text();
                    }
                }

                String firstUrl = "https://binghamton.sodexomyway.com"+urlStrings[0];
                Document doc2 = Jsoup.connect(firstUrl).get();

                for(int i = 0; i < 7; i++) {
                    Elements B = doc2.getElementById(days[i]).select("tr.brk").select("span.ul");
                    Elements L = doc2.getElementById(days[i]).select("tr.lun").select("span.ul");
                    Elements D = doc2.getElementById(days[i]).select("tr.din").select("span.ul");

                    temp = new ArrayList<>();
                    temp2 = new ArrayList<>();
                    temp3 = new ArrayList<>();

                    for (Element e : B) {
                        temp.add(e.text());
                    }
                    for (Element e : L) {
                        temp2.add(e.text());
                    }
                    for (Element e : D) {
                        temp3.add(e.text());
                    }
                    breakF.add(temp);
                    lunch.add(temp2);
                    dinner.add(temp3);
                }

            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(weekStrings[0] == null){
                Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                pD.dismiss();
            }else {

                //save the date to device
                saveBingWeekData(weekStrings[0]);
                toolbarTitle.setText(getWeekDate());

                String res[], res2[], res3[];
                int resImg[] = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday, R.drawable.thursday,
                        R.drawable.friday, R.drawable.saturday, R.drawable.sunday};
                res = new String[7];
                res2 = new String[7];
                res3 = new String[7];

                formatString(breakF, res);
                formatString(lunch, res2);
                formatString(dinner, res3);

                //sort listItems by current day of week
                listItems = new ArrayList<>();
                sortMenu_byDay(res, res2, res3, resImg);

                adapter = new MyAdapter(listItems, context);
                recyclerView.setAdapter(adapter);
                pD.dismiss();
            }
        }
    }

    private void saveBingWeekData(String date){
        SharedPreferences sP = context.getSharedPreferences("BingDining", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString("weekDate", date);
        sEditor.apply();
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setAdapter(List<ListItem> listItems){
       adapter = new MyAdapter(listItems,context);
   }

   public void setToolbar(Toolbar toolbar, AppCompatTextView toolbarTitle){
       this.toolbar = toolbar;
       this.toolbarTitle = toolbarTitle;
   }

   public void makeRequest(){
       startBingDiningRequest();
   }

    private String getWeekDate(){
        SharedPreferences sP = context.getSharedPreferences("BingDining", MODE_PRIVATE);
        return sP.getString("weekDate", "noDate");
    }

    private void formatString(List<List<String>> listStr, String[] a){
        String res = "";
        for(int j = 0; j < listStr.size(); j++) {

            List<String> temp = listStr.get(j);
            for (int i = 0; i < temp.size(); i++) {
                if (i < 9) res = res + 0 + (i + 1) + ". " + temp.get(i) + '\n';
                else res = res + (i + 1) + ". " + temp.get(i) + '\n';
            }
            a[j] = res;
            res = "";
        }
    }

    private void sortMenu_byDay(String[] res, String[] res2, String[] res3, int[] resImg){
        String breakfast, lunch, dinner;
        //clear all previous remnants
        listItems.clear();
        int index = findStartIndex();

        for(int i = index; i < 7; i++) {
            breakfast = res[i];
            lunch = res2[i];
            dinner = res3[i];
            if (breakfast.equals("")) breakfast = "01. Time to visit the Marketplace\n\n\n";
            if(lunch.equals("")) lunch = "01. Time to visit the Marketplace\n\n\n";
            if (dinner.equals("")) dinner = "01. Time to visit the Marketplace\n\n\n";

            ListItem listItem = new ListItem(breakfast, lunch, dinner, resImg[i]);
            listItems.add(listItem);
            saveBingData(days[i], breakfast, lunch, dinner);
        }
        for(int i = 0; i < index; i++) {
            breakfast = res[i];
            lunch = res2[i];
            dinner = res3[i];
            if (breakfast.equals("")) breakfast = "01. Time to visit the Marketplace\n\n\n";
            if(lunch.equals("")) lunch = "01. Time to visit the Marketplace\n\n\n";
            if (dinner.equals("")) dinner = "01. Time to visit the Marketplace\n\n\n";

            ListItem listItem = new ListItem(breakfast, lunch, dinner, resImg[i]);
            listItems.add(listItem);
            saveBingData(days[i], breakfast, lunch, dinner);
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
        }
        return index;
    }
    private void saveBingData(String day,String breakF, String lunch, String dinner){
        SharedPreferences sP = context.getSharedPreferences("BingDining", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();

        sEditor.putString("Break"+day, breakF);
        sEditor.putString("Lunch"+day, lunch);
        sEditor.putString("Dinner"+day, dinner);
        sEditor.apply();
    }
    private String getSavedBingData(String key){
        SharedPreferences sP = context.getSharedPreferences("BingDining", MODE_PRIVATE);
        return sP.getString(key, "error");
    }

    private void getSavedDate(){
        //gets current date
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
