package com.example.roy.navapp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        pD.show();

        getBingDiningData bing = new getBingDiningData();
        bing.execute();



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
        private List<String> temp = new ArrayList<>();
        private List<String> temp2 = new ArrayList<>();
        private List<String> temp3 = new ArrayList<>();



        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://binghamton.sodexomyway.com/images/sample_tcm975-112149.htm").get();
                Elements monB = doc.getElementById("monday").select("tr.brk").select("span.ul");
                Elements monL = doc.getElementById("monday").select("tr.lun").select("span.ul");
                Elements monD = doc.getElementById("monday").select("tr.din").select("span.ul");
                for(Element e: monB){
                    temp.add(e.text());
                }
                for(Element e: monL){
                    temp2.add(e.text());
                }
                for(Element e: monD){
                    temp3.add(e.text());
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String res[] = {formatString(temp)};
            String res2[] = {formatString(temp2)};
            String res3[] = {formatString(temp3)};


            for(int i = 0; i < 1; i++) {
                ListItem listItem = new ListItem(res[0], res2[0], res3[0]);
                listItems.add(listItem);
            }

            adapter = new MyAdapter(listItems, getContext());
            recyclerView.setAdapter(adapter);
            pD.dismiss();
        }
    }

    private String formatString(List<String> listStr){
        String res = "";
        for(int i = 0; i < listStr.size(); i++){
            if(i < 9) res = res +0+(i+1)+". "+listStr.get(i)+'\n';
            else res = res + (i+1)+". "+listStr.get(i)+'\n';
        }
        return res;
    }
}
