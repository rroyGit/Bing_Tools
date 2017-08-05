package com.example.roy.navapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ActionBarDrawerToggle mToggle;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkGray2));



        toolBar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(toolBar);

        final DrawerLayout dLayout;
        dLayout = (DrawerLayout) findViewById(R.id.nav_drawer_main);
        mToggle = new ActionBarDrawerToggle(this, dLayout, toolBar, R.string.open,R.string.close);

        dLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        NavigationView nView = (NavigationView) findViewById(R.id.nav_men);
        nView.setNavigationItemSelectedListener(this);

        Fragment home = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, home).commit();
        nView.getMenu().findItem(R.id.Home).setChecked(true);

        getPic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.currency);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
        this.setTaskDescription(taskDesc);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id, MenuItem item){
        Fragment f = null;

        switch(id){
            case R.id.Home:
                f = new HomeFragment();
                item.setChecked(true);

                break;
            case R.id.Crypto:
                f = new CryptoFragment();
                item.setChecked(true);
                break;
            case R.id.Bing_Dining:
                f = new Bing_Dining();
                item.setChecked(true);
                break;
        }

        if(f != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, f).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_main);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id, item);
        return true;
    }

    private void getPic () {


    }
}
