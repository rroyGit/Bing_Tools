package com.example.roy.navapp;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class Bing_Dining extends Fragment {

    private ProgressDialog pD;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;


    public Bing_Dining() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle("Bing Dining");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();

        pD = new ProgressDialog(getActivity());
        pD.setMessage("Loading, please wait...");
        pD.setCancelable(false);
        pD.show();

        getBingDiningData bing = new getBingDiningData();

        if(getSavedBingData("Breakmonday").equals("error")){
            bing.execute();
        }else{
            loadData();
        }
        
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
        private String days[] ={"monday", "tuesday", "wednesday"};
        int i,j = 0;

        final String hinmanUrl = "https://binghamton.sodexomyway.com/dining-choices/resident/residentrestaurants/hinman.html";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(hinmanUrl).get();
                Elements weekUrl = doc.getElementsByClass("accordionBody");

                for (Element link : weekUrl) {
                    Elements f = link.getElementsByTag("a");
                    for(Element a: f) {
                        urlStrings[i++] = a.attr("href");
                        weekStrings[j++] = a.text();
                    }
                }

                String firstUrl = "https://binghamton.sodexomyway.com"+urlStrings[0];
                Document doc2 = Jsoup.connect(firstUrl).get();
                for(int i = 0; i < 3; i++) {
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
            String res[],  res2[],  res3[];
            int resImg[] = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday};
            res = new String[7];
            res2 = new String[7];
            res3 = new String[7];

            formatString(breakF, res);
            formatString(lunch, res2);
            formatString(dinner, res3);

            for(int i = 0; i < 3; i++) {
                ListItem listItem = new ListItem(res[i], res2[i], res3[i], resImg[i]);
                listItems.add(listItem);

                saveBingData(days[i],res[i], res2[i], res3[i]);
            }

            adapter = new MyAdapter(listItems, getContext());
            recyclerView.setAdapter(adapter);
            pD.dismiss();
        }
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

        sEditor.putString("Break"+day, String.valueOf(breakF));
        sEditor.putString("Lunch"+day, String.valueOf(lunch));
        sEditor.putString("Dinner"+day, String.valueOf(dinner));

        sEditor.apply();
    }

    private String getSavedBingData(String key){
        SharedPreferences sP = getContext().getSharedPreferences("BingDining", MODE_PRIVATE);
        return sP.getString(key, "error");
    }

    private void loadData(){
        int resImg[] = {R.drawable.monday, R.drawable.tuesday, R.drawable.wednesday};
        final String days[] ={"monday", "tuesday", "wednesday"};
        listItems = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            String res, res2, res3;

            res = getSavedBingData("Break"+days[i]);
            res2 = getSavedBingData("Lunch"+days[i]);
            res3 = getSavedBingData("Dinner"+days[i]);

            ListItem listItem = new ListItem(res, res2, res3, resImg[i]);
            listItems.add(listItem);
        }

        adapter = new MyAdapter(listItems, getContext());
        recyclerView.setAdapter(adapter);
        pD.setMessage("Loaded form mem");
        pD.dismiss();
    }
}
