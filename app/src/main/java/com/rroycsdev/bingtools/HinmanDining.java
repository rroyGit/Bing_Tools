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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HinmanDining extends Fragment{
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public BingDiningMenu hinman_hall;
    private AppCompatTextView toolbarTitle;

    public HinmanDining() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = getContext();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (AppCompatTextView) getActivity().findViewById(R.id.toolbarTitle);

        List<ListItem> listItems = new ArrayList<>();

        String hinmanUrl = getString(R.string.hinmanUrl);
        String title = getString(R.string.hinman);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewHinman);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(savedInstanceState != null){
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(fragmentName.compareTo("bing") != 0)  hinman_hall = new BingDiningMenu(hinmanUrl, title, context, listItems, false);
            else hinman_hall = new BingDiningMenu(hinmanUrl, title, context, listItems, true);
        }else hinman_hall = new BingDiningMenu(hinmanUrl, title, context, listItems, true);

        hinman_hall.setRecyclerView(recyclerView);
        hinman_hall.setAdapter(listItems);
        hinman_hall.makeRequest();
        setToolbarDate();


        //set empty adapter due to waiting for data
        adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.hinman_dining, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerView != null) recyclerView.getAdapter().notifyDataSetChanged();
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
    public void setToolbarDate(){

        if(hinman_hall.getBingWeekDate(getString(R.string.hinman)).compareTo(BingDiningMenu.NO_DATE) != 0) {
            if(toolbarTitle != null) toolbarTitle.setText(hinman_hall.getBingWeekDate(getString(R.string.hinman)));
        }else{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(isAdded()) {
                        if (hinman_hall.getBingWeekDate(getString(R.string.hinman)).compareTo(BingDiningMenu.NO_DATE) == 0) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null && !isAdded()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(toolbarTitle != null) {
                                                    if(hinman_hall.getBingWeekDate(getString(R.string.hinman)).compareTo(BingDiningMenu.NO_DATE) == 0) {
                                                        toolbarTitle.setText("No Menu Found");
                                                    }else toolbarTitle.setText(hinman_hall.getBingWeekDate(getString(R.string.hinman)));
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 3500);
                        } else {
                            if (getActivity() != null && isAdded()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(toolbarTitle != null) toolbarTitle.setText(hinman_hall.getBingWeekDate(getString(R.string.hinman)));
                                    }
                                });
                            }
                        }
                    }
                }

            },2200);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Saved", 1);
    }
}
