package com.rroycsdev.bingtools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class C4Dining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    private AppCompatTextView toolbarTitle;

    public BingDiningMenu c4_hall;
    private String c4Url;
    private String title;

    public C4Dining() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        Context context = this.getContext();
        toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        List<ListItem> listItems = new ArrayList<>();

        c4Url = getString(R.string.c4Url);
        title =  getString(R.string.c4);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewC4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(savedInstanceState != null){
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName.compareTo("bing") != 0) c4_hall = new BingDiningMenu(c4Url, title, context, listItems, false);
            else c4_hall = new BingDiningMenu(c4Url, title, context, listItems, true);
        }else c4_hall = new BingDiningMenu(c4Url, title, context, listItems, true);

        c4_hall.setRecyclerView(recyclerView);
        c4_hall.setAdapter(listItems);
        c4_hall.makeRequest();
        setToolbarDate();

        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.c4_dining, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerView != null) recyclerView.getAdapter().notifyDataSetChanged();
    }
    //--------------------------------------------------------------------------------------------//

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

        if(c4_hall.getBingWeekDate(getString(R.string.c4)).compareTo(BingDiningMenu.NO_DATE) != 0) {
            if(toolbarTitle != null) toolbarTitle.setText(c4_hall.getBingWeekDate(getString(R.string.c4)));
        }else{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(isAdded()) {
                        if (c4_hall.getBingWeekDate(getString(R.string.c4)).compareTo(BingDiningMenu.NO_DATE) == 0) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null && isAdded()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(c4_hall.getBingWeekDate(getString(R.string.c4)).compareTo(BingDiningMenu.NO_DATE) == 0) {
                                                    toolbarTitle.setText("No Menu Found");
                                                }else toolbarTitle.setText(c4_hall.getBingWeekDate(getString(R.string.c4)));
                                            }
                                        });
                                    }
                                }
                            }, 3500);
                        } else {
                            if (getActivity() != null && isAdded()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(toolbarTitle != null) toolbarTitle.setText(c4_hall.getBingWeekDate(getString(R.string.c4)));
                                    }
                                });
                            }
                        }
                    }
                }

            },2200);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Saved", 1);
    }
}
