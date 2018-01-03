package com.example.roy.navapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class C4Dining extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    private BingDiningMenu c4_hall;
    private String c4Url;
    private String title;

    public C4Dining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        Context context = this.getContext();
        List<ListItem> listItems = new ArrayList<>();

        AppCompatTextView toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewC4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        c4_hall = new BingDiningMenu(c4Url, title, context, listItems);
        c4_hall.setRecyclerView(recyclerView);
        c4_hall.setAdapter(listItems);
        c4_hall.setToolbar(toolbarTitle);
        c4_hall.makeRequest();


        //set empty adapter due to waiting for data
        adapter = new MyAdapter(listItems, context);
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
        c4_hall.setRecycleAdapter();
    }
    //--------------------------------------------------------------------------------------------//
    private OnFragmentInteractionListener mListener;

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
        c4Url = getString(R.string.c4Url);
        title = getString(R.string.c4);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
