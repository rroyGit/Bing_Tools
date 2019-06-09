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
    private View view;

    public CIWDining() {
        //empty constructor
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

        if(savedInstanceState != null){
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName.compareTo("bing") != 0)  ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, false, view);
            else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true, view);
        }else ciw_hall = new BingDiningMenu(ciwUrl,title,context, listItems, true, view);

        ciw_hall.setRecyclerView(recyclerView);
        ciw_hall.setAdapter(listItems);
        ciw_hall.makeRequest();


        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
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

    public void setToolbarDate() {
        ciw_hall.setToolbar(toolbarTitle);
    }

}
