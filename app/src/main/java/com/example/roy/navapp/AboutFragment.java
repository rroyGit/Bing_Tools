package com.example.roy.navapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class AboutFragment extends Fragment {

    Context context;
    ListView listView;
    AboutAdapter aboutAdapter;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("About");
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse("https://www.bing.com/");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }).start();

                        break;
                    case 1:
                        break;
                    case 2:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse("https://coinmarketcap.com/");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }).start();
                        break;
                    case 3:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse("https://material.io/icons/");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }).start();

                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
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
