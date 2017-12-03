package com.example.roy.navapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

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

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class Bing_Dining extends Fragment {

    private ProgressDialog pD;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    private Context context;
    private Toolbar toolbar;

    private List<ListItem> listItems;
    private String month, date, year;
    private String[] savedDate;
    private StringTokenizer sT;
    private final String days[] = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    public Bing_Dining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle("Bing Dining (Hinman)");

        context = this.getContext();

        savedDate = new String[3];
        toolbar = (Toolbar) view.findViewById(R.id.bing_toolbar);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();

        pD = new ProgressDialog(getActivity());

        //gets current date
        getDate(savedDate);
        //break date into three parts
        month = savedDate[0];
        date = savedDate[1];
        year = savedDate[2];

        startBingDiningRequest();
        //set empty adapter due to waiting for data
        adapter = new MyAdapter(listItems,getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bing__dining, container, false);
    }

    private class getBingDiningData extends AsyncTask<Void, Void, Void> {
        private List<String> temp;
        private List<String> temp2;
        private List<String> temp3;

        private List<List<String>> breakF = new ArrayList<>();
        private List<List<String>> lunch = new ArrayList<>();
        private List<List<String>> dinner = new ArrayList<>();

        private String urlStrings[] = new String[2];
        private String weekStrings[] = new String[2];

        int i,j = 0;
        final String hinmanUrl = "https://binghamton.sodexomyway.com/dining-choices/resident/residentrestaurants/hinman.html";

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
                Document doc = Jsoup.connect(hinmanUrl).get();
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

                toolbar.setTitleMarginStart(225);
                toolbar.setTitle(getWeekDate());
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
                sortMenu_byDay(res, res2, res3, resImg);

                adapter = new MyAdapter(listItems, getContext());
                recyclerView.setAdapter(adapter);
                pD.dismiss();
            }
        }
    }
    private void saveBingWeekData(String date){
        SharedPreferences sP = getContext().getSharedPreferences("BingDining", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();

        sEditor.putString("weekDate", date);
        sEditor.apply();
    }

    private String getWeekDate(){
        SharedPreferences sP = getContext().getSharedPreferences("BingDining", MODE_PRIVATE);
        return sP.getString("weekDate", "noDate");
    }
    private void formatString(List<List<String>> listStr, String[]a){
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

    private void saveBingData(String day,String breakF, String lunch, String dinner){
        SharedPreferences sP = getContext().getSharedPreferences("BingDining", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();

        sEditor.putString("Break"+day, breakF);
        sEditor.putString("Lunch"+day, lunch);
        sEditor.putString("Dinner"+day, dinner);
        sEditor.apply();
    }

    private String getSavedBingData(String key){
        SharedPreferences sP = getContext().getSharedPreferences("BingDining", MODE_PRIVATE);
        return sP.getString(key, "error");
    }

    private void loadData(){
        int resImg[] = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday, R.drawable.thursday,
                R.drawable.friday, R.drawable.saturday, R.drawable.sunday};
        //clear previous remnants
        listItems.clear();

        for(int i = findStartIndex(); i < 7; i++) {
            String res, res2, res3;
            res = getSavedBingData("Break"+days[i]);
            res2 = getSavedBingData("Lunch"+days[i]);
            res3 = getSavedBingData("Dinner"+days[i]);

            ListItem listItem = new ListItem(res, res2, res3, resImg[i]);
            listItems.add(listItem);
        }
        for(int i = 0; i < findStartIndex(); i++) {
            String res, res2, res3;
            res = getSavedBingData("Break"+days[i]);
            res2 = getSavedBingData("Lunch"+days[i]);
            res3 = getSavedBingData("Dinner"+days[i]);
            ListItem listItem = new ListItem(res, res2, res3, resImg[i]);
            listItems.add(listItem);
        }
        adapter = new MyAdapter(listItems, getContext());
        recyclerView.setAdapter(adapter);
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

    private void startBingDiningRequest(){
        //Async Task for getting bing dining data from the web
        getBingDiningData bing;
        //determine whether to load data from saved storage or from the web
        while(true) {
            if (getSavedBingData("Breakmonday").equals("error")) {

                if(getDeviceInternetStatus(context) == null){
                    Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                bing = new getBingDiningData();
                bing.execute();
                break;
            } else {
                sT = new StringTokenizer(getWeekDate(), " ");
                String firstDate = sT.nextToken();
                sT.nextToken();
                String secDate = sT.nextToken();

                sT = new StringTokenizer(firstDate, "/");
                String month1 = sT.nextToken();
                String date1 = sT.nextToken();
                String year1 = sT.nextToken();

                sT = new StringTokenizer(secDate, "/");
                String month2 = sT.nextToken();
                String date2 = sT.nextToken();
                String year2 = sT.nextToken();

                if (Integer.parseInt(month) < Integer.parseInt(month1) || Integer.parseInt(month) > Integer.parseInt(month2)) {
                    bing = new getBingDiningData();
                    bing.execute();
                    break;
                } else if (((month.equals(month1)) && (Integer.parseInt(date) < Integer.parseInt(date1))) ||
                        ((month.equals(month2)) && (Integer.parseInt(date) > Integer.parseInt(date2)))) {
                    bing = new getBingDiningData();
                    bing.execute();
                    break;
                } else if (Integer.parseInt(year) < Integer.parseInt(year1) || Integer.parseInt(year) > Integer.parseInt(year2)) {
                    bing = new getBingDiningData();
                    bing.execute();
                    break;
                } else {
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    int display_width = displayMetrics.widthPixels;
                    toolbar.setTitleMarginStart((display_width/2)-350);
                    toolbar.setTitle(getWeekDate());

                    Thread loadDataThread = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            loadData();
                        }
                    };
                    loadDataThread.start();
                    try {
                        loadDataThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void sortMenu_byDay(String[] res, String[] res2, String[] res3, int[] resImg){
        String breakf, lunch, dinner;

        //clear all previous remnants
        listItems.clear();

        for(int i = findStartIndex() ;i < 7; i++) {
            breakf = res[i];
            lunch = res2[i];
            dinner = res3[i];
            if (breakf.equals("")) breakf = "01. Time to visit the Marketplace\n\n";
            if(lunch.equals("")) lunch = "01. Time to visit the Marketplace\n\n";
            if (dinner.equals("")) dinner = "01. Time to visit the Marketplace\n\n";

            ListItem listItem = new ListItem(breakf, lunch, dinner, resImg[i]);
            listItems.add(listItem);
            saveBingData(days[i], breakf, lunch, dinner);
        }
        for(int i = 0 ; i < findStartIndex(); i++) {
            breakf = res[i];
            lunch = res2[i];
            dinner = res3[i];
            if (breakf.equals("")) breakf = "01. Time to visit the Marketplace\n\n";
            if(lunch.equals("")) lunch = "01. Time to visit the Marketplace\n\n";
            if (dinner.equals("")) dinner = "01. Time to visit the Marketplace\n\n";

            ListItem listItem = new ListItem(breakf, lunch, dinner, resImg[i]);
            listItems.add(listItem);
            saveBingData(days[i], breakf, lunch, dinner);
        }
    }

    private int findStartIndex(){
        int index = 0;
        Date date =  new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E", Locale.US);
        String dayWeek = dateFormatter.format(date);
        StringBuilder day_3char = new StringBuilder();

        for(int i = 0; i < days.length; i++){
            String first_char = String.valueOf(days[i].charAt(0));
            day_3char.append(first_char.toUpperCase());
            day_3char.append(days[i].substring(1,3));

            //Log.e(TAG,day_3char.toString() + " " + dayWeek);
            if(dayWeek.compareTo(day_3char.toString()) == 0){
                index = i;
                day_3char = null;
                dateFormatter = null;
                break;
            }else day_3char.delete(0, day_3char.length());
        }
        return index;
    }
}
