package com.rroycsdev.bingtools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class AppalachianDining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public AppCompatTextView toolbarTitle;

    private BingDiningMenu appalachian_hall;
    private String appalachianUrl;
    private String title;

    private View view;
    private TabLayout tabLayout;


    public AppalachianDining(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        toolbarTitle = getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        appalachianUrl = getString(R.string.appalachianUrl);
        title = getString(R.string.appalachian);
        recyclerView = view.findViewById(R.id.recycleViewAppalachian);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (savedInstanceState != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName.compareTo("bing") != 0)  appalachian_hall = new BingDiningMenu(appalachianUrl,title,context, listItems, false, view);
            else appalachian_hall = new BingDiningMenu(appalachianUrl,title,context, listItems, true, view);
        } else appalachian_hall = new BingDiningMenu(appalachianUrl,title,context, listItems, true, view);

        appalachian_hall.setRecyclerView(recyclerView);
        appalachian_hall.setAdapter(listItems);
        appalachian_hall.setToolbarTitle(toolbarTitle);
        appalachian_hall.setTabLayout(tabLayout);
        appalachian_hall.makeRequest();

        if (getUserVisibleHint()) appalachian_hall.setView();

        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh () {
        appalachian_hall.refreshData();
    }

    public void showDialog () { appalachian_hall.showDialog(); }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (appalachian_hall != null && isVisibleToUser) {
            appalachian_hall.setView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.appalachian_dining, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerView != null && recyclerView.getAdapter() != null) recyclerView.getAdapter().notifyDataSetChanged();
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
}
