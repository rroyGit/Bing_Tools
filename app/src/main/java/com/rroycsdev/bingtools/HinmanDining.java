package com.rroycsdev.bingtools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HinmanDining extends Fragment {
    private boolean firstLaunched = true;
    private RecyclerView recyclerView;
    private BingDiningMenu hinman_hall;
    private String title;
    private String hinmanUrl;
    private AppCompatTextView toolbarTextView;

    public HinmanDining() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hinmanUrl = getString(R.string.hinmanUrl);
        title = getString(R.string.hinman);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        List<ListItem> listItems = new ArrayList<>();

        TabLayout tabLayout = view.getRootView().findViewById(R.id.tabLayout);
        toolbarTextView = view.getRootView().findViewById(R.id.toolbarTextView);

        recyclerView = view.findViewById(R.id.recycleViewHinman);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (savedInstanceState != null){
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            if(Objects.requireNonNull(fragmentName).compareTo("bing") != 0)  hinman_hall = new BingDiningMenu(hinmanUrl, title, context, listItems, false, view);
            else hinman_hall = new BingDiningMenu(hinmanUrl, title, context, listItems, true, view);
        } else hinman_hall = new BingDiningMenu(hinmanUrl, title, context, listItems, true, view);

        hinman_hall.setRecyclerView(recyclerView);
        hinman_hall.setToolbarTitle(toolbarTextView);
        hinman_hall.setTabLayout(tabLayout);
        hinman_hall.makeRequest();

        if (getUserVisibleHint()) hinman_hall.setView(false);

        //set empty adapter due to waiting for data
        RecyclerView.Adapter adapter = new MenuAdapter(listItems, context, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void refresh () {
        hinman_hall.refreshData();
    }

    public void showDialog () { hinman_hall.showDialog(); }

    public void clearToolbarTextView () { toolbarTextView.setText(""); }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (hinman_hall != null && isVisibleToUser) {
            firstLaunched = firstLaunched ?  !hinman_hall.setView(true) : !hinman_hall.setView(false);
        }
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
        if(recyclerView != null && recyclerView.getAdapter() != null) recyclerView.getAdapter().notifyDataSetChanged();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Saved", 1);
    }
}
