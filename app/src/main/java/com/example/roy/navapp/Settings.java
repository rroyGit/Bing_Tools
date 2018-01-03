package com.example.roy.navapp;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;


import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
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
    Context context;
    private List<String> titles;
    private List<String> colorsHead;
    private List<String> colorsBody;

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();

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

        recyclerView = (RecyclerView) findViewById(R.id.recycleView2);

        titles = new ArrayList<>();
        colorsHead = new ArrayList<>();
        colorsBody = new ArrayList<>();

        HashMap<String,List<String>> colorsMap = new HashMap<>();

        colorsHead.add("Red");
        colorsHead.add("Blue");
        colorsHead.add("Black");
        colorsHead.add("Light Gray");

        colorsBody.add("Red");
        colorsBody.add("Blue");
        colorsBody.add("Black");
        colorsBody.add("Light Gray");

        titles.add("Meal Time");
        titles.add("Menu List");

        colorsMap.put(titles.get(0), colorsHead);
        colorsMap.put(titles.get(1), colorsBody);

        settingsAdapter = new SettingsAdapter(colorsMap, titles, context);
        GridLayoutManager manager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(manager);
        settingsAdapter.setLayoutManager(manager);
        settingsAdapter.shouldShowHeadersForEmptySections(false);
        settingsAdapter.shouldShowFooters(false);
        settingsAdapter.collapseAllSections();
        recyclerView.setAdapter(settingsAdapter);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Get color reset state from device, custom color chosen then option is unlocked
        //if no custom color is chosen, option is locked
        if(getReset()) menu.getItem(0).setChecked(false);
        else menu.getItem(0).setChecked(true);
        return super.onPrepareOptionsMenu(menu);
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
        }
        if(id == R.id.resetColors){
            if(getReset()) {
                Toast.makeText(context, "Colors Reset", Toast.LENGTH_SHORT).show();
                SharedPreferences colors = getSharedPreferences("Colors", MODE_PRIVATE);
                colors.edit().clear().apply();
                recyclerView.setAdapter(settingsAdapter);
                item.setChecked(true);
                settingsAdapter.saveForReset(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean getReset(){
        SharedPreferences sP = context.getSharedPreferences("resetState", MODE_PRIVATE);
        return sP.getBoolean("colorReset", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.bing_tools_icon);
                ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
                setTaskDescription(taskDesc);
            }
        }).start();
    }
}


