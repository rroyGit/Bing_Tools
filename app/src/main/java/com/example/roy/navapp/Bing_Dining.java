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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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

    BingDiningMenu hinman_hall;
    private List<ListItem> listItems;
    private String month, date, year;
    private String[] savedDate;
    private StringTokenizer sT;
    private final String days[] = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private final String hinmanUrl = "https://binghamton.sodexomyway.com/dining-choices/resident/residentrestaurants/hinman.html";
    private final String c4Url = "https://binghamton.sodexomyway.com/dining-choices/resident/residentrestaurants/index.html";
    public Bing_Dining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle("Hinman Dinning Hall");
        Context context = this.getContext();
        Toolbar toolbar;
        AppCompatTextView toolbarTitle;

        listItems = new ArrayList<>();
        savedDate = new String[3];

        pD = new ProgressDialog(getActivity());
        toolbar = (Toolbar) view.findViewById(R.id.bing_toolbar);
        toolbarTitle = (AppCompatTextView) view.findViewById(R.id.toolbarText);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hinman_hall = new BingDiningMenu(hinmanUrl,context);
        hinman_hall.setRecyclerView(recyclerView);
        hinman_hall.setAdapter(listItems);
        hinman_hall.setToolbar(toolbar, toolbarTitle);
        hinman_hall.makeRequest();

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

    @Override
    public void onResume() {
        super.onResume();
        hinman_hall.setRecycleAdapter();
    }
}
