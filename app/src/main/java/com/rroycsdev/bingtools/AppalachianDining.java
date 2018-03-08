package com.rroycsdev.bingtools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;


public class AppalachianDining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    public BingDiningMenu appalachian_hall;
    private String appalachianUrl;
    private String title;
    private AppCompatTextView toolbarTitle;
    public AppalachianDining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        appalachianUrl = getString(R.string.appalachianUrl);
        title = getString(R.string.appalachian);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewAppalachian);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appalachian_hall = new BingDiningMenu(appalachianUrl,title,context, listItems);
        appalachian_hall.setRecyclerView(recyclerView);
        appalachian_hall.setAdapter(listItems);
        appalachian_hall.makeRequest();


        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.appalachian_dining, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void setToolbarDate(){
        if(toolbarTitle != null) {
            if(appalachian_hall.getBingWeekDate().compareTo(BingDiningMenu.NO_DATE) != 0) {
                toolbarTitle.setText(appalachian_hall.getBingWeekDate());
            }else{
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(appalachian_hall.getBingWeekDate().compareTo(BingDiningMenu.NO_DATE) == 0){
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toolbarTitle.setText(appalachian_hall.getBingWeekDate());
                                }
                            },2000);
                        }else {
                            toolbarTitle.setText(appalachian_hall.getBingWeekDate());
                        }
                    }

                },1900);
            }
        }
    }
}
