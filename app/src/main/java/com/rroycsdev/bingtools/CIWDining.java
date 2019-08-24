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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class CIWDining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public AppCompatTextView toolbarTitle;

    private BingDiningMenu ciw_hall;
    private String ciwUrl;
    private String title;
    private TabLayout tabLayout;

    private View view;

    public CIWDining(TabLayout tabLayout) {
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
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        ciwUrl = getString(R.string.ciwUrl);
        title = getString(R.string.ciw);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewCIW);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (savedInstanceState != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName != null && fragmentName.compareTo("bing") != 0)  ciw_hall = new BingDiningMenu(ciwUrl, title, context, listItems, false, view);
            else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true, view);
        } else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true, view);

        ciw_hall.setRecyclerView(recyclerView);
        ciw_hall.setAdapter(listItems);
        ciw_hall.setToolbarTitle(toolbarTitle);
        ciw_hall.setTabLayout(tabLayout);
        ciw_hall.makeRequest();

        if (getUserVisibleHint()) ciw_hall.setView();

        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh () {
        ciw_hall.refreshData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (ciw_hall != null && isVisibleToUser) {
            ciw_hall.setView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.ciw_dining, container, false);
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
