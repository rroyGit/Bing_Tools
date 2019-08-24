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

public class C4Dining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public AppCompatTextView toolbarTitle;

    private BingDiningMenu c4_hall;
    private String c4Url;
    private String title;
    private View view;
    private TabLayout tabLayout;

    public C4Dining(TabLayout tabLayout) {
       this.tabLayout = tabLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: C4");

        Context context = this.getContext();
        toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        c4Url = getString(R.string.c4Url);
        title =  getString(R.string.c4);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewC4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (savedInstanceState != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName.compareTo("bing") != 0) c4_hall = new BingDiningMenu(c4Url, title, context, listItems, false, view);
            else c4_hall = new BingDiningMenu(c4Url, title, context, listItems, true, view);
        } else c4_hall = new BingDiningMenu(c4Url, title, context, listItems, true, view);

        c4_hall.setRecyclerView(recyclerView);
        c4_hall.setAdapter(listItems);
        c4_hall.setToolbarTitle(toolbarTitle);
        c4_hall.setTabLayout(tabLayout);
        c4_hall.makeRequest();

        if (getUserVisibleHint()) c4_hall.setView();

        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh () {
        c4_hall.refreshData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (c4_hall != null && isVisibleToUser) {
            c4_hall.setView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.c4_dining, container, false);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Saved", 1);
    }
}
