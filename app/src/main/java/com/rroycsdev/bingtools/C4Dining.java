package com.rroycsdev.bingtools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class C4Dining extends Fragment {
    private boolean firstLaunched = true;
    private RecyclerView recyclerView;
    private BingDiningMenu c4_hall;
    private String c4Url;
    private String title;
    private AppCompatTextView toolbarTextView;

    public C4Dining() {
        //empty
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c4Url = getString(R.string.c4Url);
        title =  getString(R.string.c4);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = this.getContext();
        List<ListItem> listItems = new ArrayList<>();

        TabLayout tabLayout = view.getRootView().findViewById(R.id.tabLayout);
        toolbarTextView = view.getRootView().findViewById(R.id.toolbarTextView);

        recyclerView = view.findViewById(R.id.recycleViewC4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (toolbarTextView == null) Log.d(TAG, "onViewCreated: c4 toolbarText null");

        if (savedInstanceState != null) {
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if (Objects.requireNonNull(fragmentName).compareTo("bing") != 0) c4_hall = new BingDiningMenu(c4Url, title, context, listItems, false, view);
            else c4_hall = new BingDiningMenu(c4Url, title, context, listItems, true, view);
        } else c4_hall = new BingDiningMenu(c4Url, title, context, listItems, true, view);

        c4_hall.setRecyclerView(recyclerView);
        c4_hall.setToolbarTitle(toolbarTextView);
        c4_hall.setTabLayout(tabLayout);
        c4_hall.makeRequest();

        if (getUserVisibleHint()) c4_hall.setView(false);

        //set empty adapter due to waiting for data
        RecyclerView.Adapter adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh () {
        c4_hall.refreshData();
    }

    public void showDialog () { c4_hall.showDialog(); }

    public void clearToolbarTextView () { toolbarTextView.setText(""); }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (c4_hall != null && isVisibleToUser) {
            firstLaunched = firstLaunched ?  !c4_hall.setView(true) : !c4_hall.setView(false);
        }
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
