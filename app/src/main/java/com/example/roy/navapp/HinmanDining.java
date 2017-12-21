package com.example.roy.navapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.StringTokenizer;

public class HinmanDining extends Fragment {

    private ProgressDialog pD;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    private BingDiningMenu hinman_hall;
    private String month, date, year;
    private String[] savedDate;
    private StringTokenizer sT;
    private final String days[] = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private final String hinmanUrl = "https://binghamton.sodexomyway.com/dining-choices/resident/residentrestaurants/hinman.html";

    public HinmanDining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        Toolbar toolbar;
        AppCompatTextView toolbarTitle;

        List<ListItem> listItems = new ArrayList<>();
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
        return inflater.inflate(R.layout.hinman_dining, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        hinman_hall.setRecycleAdapter();
    }
    //--------------------------------------------------------------------------------------------//
    private OnFragmentInteractionListener mListener;

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
