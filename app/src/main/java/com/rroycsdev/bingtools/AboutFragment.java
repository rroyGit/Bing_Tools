package com.rroycsdev.bingtools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.andexert.library.RippleView;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class AboutFragment extends Fragment {
    Context context;
    ListView listView;
    AboutAdapter aboutAdapter;
    Menu menu;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getContext();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.about);
        listView = view.findViewById(R.id.listView);

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> desc = new ArrayList<>();
        title.add("Daily Wallpaper");
        title.add("Bing Dining Menu");
        title.add("Crypto");
        title.add("Icons");
        title.add("App Version");

        desc.add("Wallpaper in navigation menu is retrieved from Microsoft Bing homepage https://www.bing.com/");
        desc.add("Binghamton campus dining data are retrieved from Binghamton University Sodexo website");
        desc.add("Crypto currency exchange rate data are retrieved from https://coinmarketcap.com/");
        desc.add("App icon created using Iconion and icons on navigation menu are retrieved from https://material.io/icons/");
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            desc.add(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            desc.add("Unknown");
        }

        aboutAdapter = new AboutAdapter(context, R.layout.row2_about, title, desc);
        listView.setAdapter(aboutAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }


    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Toast.makeText(context, "Attach", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Toast.makeText(context, "Detach", Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(context, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Toast.makeText(context, "onDestroy view", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Toast.makeText(context, "Saving onSaveInstance", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            getActivity().setTitle(R.string.about);
            if(menu != null){
                menu.findItem(R.id.refresh_Bing).setVisible(false);
            }else Log.e("null","menu");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.refresh_Bing).setVisible(false);
        this.menu = menu;
    }

}
