package com.example.roy.navapp;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings extends AppCompatActivity{
    private Toolbar toolBar;
    private ExpandableListView expandableListView;

    private List<String> titles;
    private List<String> radioNames;
    private List<String> radioNames2;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkGray2));
        setTitle("Settings");

        toolBar = (Toolbar) findViewById(R.id.nav_action_toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_List);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton = (RadioButton) findViewById(R.id.radioButton);

        titles = new ArrayList<>();
        radioNames = new ArrayList<>();
        radioNames2 = new ArrayList<>();

        HashMap<String,List<String>> childList = new HashMap<>();

        radioNames.add("Red");
        radioNames.add("Blue");

        radioNames2.add("Black");
        radioNames2.add("Light Gray");

        titles.add("Bing Header");
        titles.add("Bing Body");


        childList.put(titles.get(0), radioNames);
        childList.put(titles.get(1), radioNames2);

        SettingsAdapter settingsAdapter = new SettingsAdapter(childList, titles, getApplicationContext());
        expandableListView.setAdapter(settingsAdapter);





    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent homeIntent = new Intent(Settings.this, MainActivity.class);
            startActivity(homeIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
