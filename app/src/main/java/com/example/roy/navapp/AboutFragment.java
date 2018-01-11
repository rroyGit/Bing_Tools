package com.example.roy.navapp;

import android.content.Context;
import android.content.Intent;
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
        listView = (ListView) view.findViewById(R.id.listView);

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> desc = new ArrayList<>();
        title.add("Bing Wallpaper");
        title.add("Binghamton Campus Menu");
        title.add("Crypto");

        desc.add("Daily wallpaper in navigation menu is retrieved from Microsoft Bing homepage");
        desc.add("Dining data are retrieved from Binghamton University Sodexo website");
        desc.add("Crypto currency exchange rate data are retrieved from www.coinmarketcap.com");

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
