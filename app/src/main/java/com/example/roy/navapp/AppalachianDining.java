package com.example.roy.navapp;

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


public class AppalachianDining extends Fragment {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    private BingDiningMenu appalachian_hall;
    private String appalachianUrl;
    private String title;

    public AppalachianDining() {
        //empty constructor
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        AppCompatTextView toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        appalachianUrl = getString(R.string.appalachianUrl);
        title = getString(R.string.appalachian);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewAppalachian);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appalachian_hall = new BingDiningMenu(appalachianUrl,title,context, listItems);
        appalachian_hall.setRecyclerView(recyclerView);
        appalachian_hall.setAdapter(listItems);
        appalachian_hall.setToolbar(toolbarTitle);
        appalachian_hall.makeRequest();

        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.appalachian_dining, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        appalachian_hall.setRecycleAdapter();
    }
    //--------------------------------------------------------------------------------------------//
    private OnFragmentInteractionListener mListener;

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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
