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


public class CIWDining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    public BingDiningMenu ciw_hall;
    private String ciwUrl;
    private String title;
    private AppCompatTextView toolbarTitle;

    public CIWDining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        ciwUrl = getString(R.string.ciwUrl);
        title = getString(R.string.ciw);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewCIW);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(savedInstanceState != null){
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName.compareTo("bing") != 0)  ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, false);
            else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true);
        }else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true);

        ciw_hall.setRecyclerView(recyclerView);
        ciw_hall.setAdapter(listItems);
        ciw_hall.makeRequest();

        if(toolbarTitle.getText().length() ==0){
            setToolbarDate();
        }

        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ciw_dining, container, false);
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
            if(ciw_hall.getBingWeekDate(getString(R.string.ciw)).compareTo(BingDiningMenu.NO_DATE) != 0) {
                toolbarTitle.setText(ciw_hall.getBingWeekDate(getString(R.string.ciw)));
            }else{
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(ciw_hall.getBingWeekDate(getString(R.string.ciw)).compareTo(BingDiningMenu.NO_DATE) == 0){
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toolbarTitle.setText(ciw_hall.getBingWeekDate(getString(R.string.ciw)));
                                }
                            },2000);
                        }else {
                            toolbarTitle.setText(ciw_hall.getBingWeekDate(getString(R.string.ciw)));
                        }
                    }

                },1900);
            }
        }
    }

}
