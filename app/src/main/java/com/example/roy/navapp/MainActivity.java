package com.example.roy.navapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.*;


import static android.content.ContentValues.TAG;
import static com.example.roy.navapp.Bing_Dining.getDeviceInternetStatus;
import static com.example.roy.navapp.HomeFragment.hideKeyboard;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ActionBarDrawerToggle mToggle;
    private Toolbar toolBar;
    private String retVal;

    ImageView bingImage;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        //clears saved data for ether crypto
        clearCryptoData();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkGray2));


        NavigationView nView = (NavigationView) findViewById(R.id.nav_menu_view);
        nView.getLayoutParams().width = (int)(width/1.45);

        View headerView = nView.getHeaderView(0);
        headerView.getLayoutParams().height = (int)(height/3.2);

        bingImage = (ImageView) headerView.findViewById(R.id.DailyImage);

        if(getDeviceInternetStatus(getApplicationContext()) == null){
            Toast.makeText(getApplicationContext(), "Could not get Bing Daily Image", Toast.LENGTH_SHORT).show();
        }else new getPic().execute();


        toolBar = (Toolbar) findViewById(R.id.nav_action_toolbar);
        setSupportActionBar(toolBar);


        final DrawerLayout dLayout;
        dLayout = (DrawerLayout) findViewById(R.id.nav_drawer_main);
        mToggle = new ActionBarDrawerToggle(this, dLayout, toolBar, R.string.open,R.string.close){

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(dLayout.isDrawerOpen(GravityCompat.START)) {
                    hideKeyboard(activity,dLayout);
                }
            }
        };

        dLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        nView.setNavigationItemSelectedListener(this);

        //set the initial homepage to Home
        Fragment home = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, home).commit();
        nView.getMenu().findItem(R.id.Home).setChecked(true);
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
            Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
            startActivity(settingsIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id, MenuItem item){
        Fragment f = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_main);

        switch(id){
            case R.id.Home:
                if(item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                }else {
                    f = new HomeFragment();
                    item.setChecked(true);
                    break;
                }
            case R.id.Crypto:
                if(item.isChecked()){
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                }else {
                    f = new CryptoFragment();
                    item.setChecked(true);
                    break;
                }
            case R.id.Bing_Dining:
                if(item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                }else {
                    f = new Bing_Dining();
                    item.setChecked(true);
                    break;
                }
        }

        if(f != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, f).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id, item);
        return true;
    }


    private class getPic extends AsyncTask<Void, Void, Void> {
        StringRequest sR;
        final String URL_DATA = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";

        @Override
        protected Void doInBackground(Void... params) {
            sR = new StringRequest(Request.Method.GET,
                    URL_DATA, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray array = jsonObject.getJSONArray("images");
                        JSONObject urlObj = array.getJSONObject(0);
                        retVal = "https://www.bing.com/"+ urlObj.getString("url");
                        if(bingImage == null) {
                            //Log.d(TAG, "image is null");
                        }else {
                            //Log.d(TAG, "image"+retVal);
                            Picasso.with(getApplicationContext()).load(retVal).fit().into(bingImage);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Could not connect to the Internet", Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RequestQueue rQ = Volley.newRequestQueue(getApplicationContext());
            rQ.add(sR);
        }
    }

    private void clearCryptoData(){
        SharedPreferences sP = this.getSharedPreferences("Crypto", MODE_PRIVATE);
        sP.edit().remove("Crypto").clear().apply();
    }
}
