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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CIWDining extends Fragment {
    private boolean firstLaunched = true;
    private RecyclerView recyclerView;
    private BingDiningMenu ciw_hall;
    private String ciwUrl;
    private String title;
    private AppCompatTextView toolbarTextView;

    public CIWDining() {
        // empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ciwUrl = getString(R.string.ciwUrl);
        title = getString(R.string.ciw);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        List<ListItem> listItems = new ArrayList<>();

        TabLayout tabLayout = view.getRootView().findViewById(R.id.tabLayout);
        toolbarTextView = view.getRootView().findViewById(R.id.toolbarTextView);

        recyclerView = view.findViewById(R.id.recycleViewCIW);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (savedInstanceState != null){
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            String fragmentName = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName != null && fragmentName.compareTo("bing") != 0)  ciw_hall = new BingDiningMenu(ciwUrl, title, context, listItems, false, view);
            else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true, view);
        } else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true, view);

        ciw_hall.setRecyclerView(recyclerView);
        ciw_hall.setToolbarTitle(toolbarTextView);
        ciw_hall.setTabLayout(tabLayout);
        ciw_hall.makeRequest();

        if (getUserVisibleHint()) ciw_hall.setView(false);

        //set empty adapter due to waiting for data
        RecyclerView.Adapter adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh () {
        ciw_hall.refreshData();
    }

    public void showDialog () { ciw_hall.showDialog(); }

    public void clearToolbarTextView () { toolbarTextView.setText(""); }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (ciw_hall != null && isVisibleToUser) {
            firstLaunched = firstLaunched ?  !ciw_hall.setView(true) : !ciw_hall.setView(false);
        }
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
