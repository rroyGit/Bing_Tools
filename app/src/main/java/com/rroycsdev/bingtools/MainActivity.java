package com.rroycsdev.bingtools;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

import static com.rroycsdev.bingtools.BingDiningMenu.getDeviceInternetStatus;
import static com.rroycsdev.bingtools.CalculatorFragment.hideKeyboard;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public NavigationView nView;
    private ImageView bingImage;
    private Activity activity;
    private Context context;
    private Menu menu;
    private String saveImagePath;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    Bundle bundle = new Bundle();
    ActionBarDrawerToggle mToggle;
    Toolbar toolBar;
    Bitmap bitmapWallpaper;
    private String TAG = "YO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if app launched from Play store, don't create a new instance; bring front previous instance
        if (!isTaskRoot()){
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        activity = this;
        context = getApplicationContext();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(context, R.color.darkGray2));
        nView = (NavigationView) findViewById(R.id.nav_menu_view);
        nView.getLayoutParams().width = (int)(width/1.45);

        View headerView = nView.getHeaderView(0);
        headerView.getLayoutParams().height = (int)(height/3.2);
        bingImage = (ImageView) headerView.findViewById(R.id.DailyImage);
        toolBar = (Toolbar) findViewById(R.id.nav_action_toolbar);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(getShowStoragePermission()) ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            else {
                makeBingWallRequest(false);
            }
        }else{
            createImageDir();
            makeBingWallRequest(true);
        }

        clearCryptoData();
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


        //fragmentManager.getFragments().size() == 0
        if(savedInstanceState == null){
            displaySelectedScreen(R.id.Bing_Dining_Nav, nView.getMenu().findItem(R.id.Bing_Dining_Nav), true);
        }else{
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            switch (fragmentName){
                case "timer":
                    setTitle(getResources().getString(R.string.auto_launch));
                    break;
                case "bing":
                    setTitle(getResources().getString(R.string.bing_dining));
                    break;
                case "calc":
                    setTitle(getResources().getString(R.string.calculator));
                    break;
                case "crypto":
                    setTitle(getResources().getString(R.string.crypto));
                    break;
                case "about":
                    setTitle(getResources().getString(R.string.about));
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createImageDir();

                    makeBingWallRequest(true);
                    setShowStoragePermission(false);
                } else {
                    setShowStoragePermission(false);
                    makeBingWallRequest(false);
                    setToast("Permission Denied: Unable to save image to storage");
                }
            }
        }
    }

    private void setShowStoragePermission(boolean permission){
        SharedPreferences sharedPreferences  = getSharedPreferences("storagePermission", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("permission", permission).apply();
    }

    private boolean getShowStoragePermission(){
        SharedPreferences sharedPreferences  = getSharedPreferences("storagePermission", MODE_PRIVATE);
        return sharedPreferences.getBoolean("permission", true);
    }


    private void createImageDir(){
        String dataPath = "/storage/emulated/0/Android/data/";

        String dirPath = dataPath + File.separator + getPackageName();
        File packageDir = new File(dirPath);

        boolean packageDirSuccess = true;
        if (!packageDir.exists()) packageDirSuccess = packageDir.mkdirs();
        if(packageDirSuccess){
            String dirPathImage = dirPath + File.separator + "Image";
            File imageDir = new File(dirPathImage);
            if (!imageDir.exists()) packageDirSuccess = imageDir.mkdirs();
            if(!packageDirSuccess) {
                setToast("Unable to create file");
                return;
            }
            saveImagePath = dirPathImage;
        }
    }

    private void setToast(String string){
        if(string != null) Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.bing_icon_new5);
                ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
                setTaskDescription(taskDesc);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(fragmentManager.getBackStackEntryCount() == 0) {
                finish();
                return;
            }
            String fragmentName  = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            Fragment chooseFragment;
            switch (fragmentName){
                case "bing":
                    setTitle(getResources().getString(R.string.bing_dining));
                    nView.getMenu().getItem(0).setChecked(true);
                    chooseFragment = fragmentManager.findFragmentByTag("calc");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("crypto");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("about");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("timer");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag(fragmentName);
                    if(chooseFragment != null)fragmentTransaction.show(chooseFragment).commit();
                    break;
                case "timer":
                    setTitle(getResources().getString(R.string.auto_launch));
                    nView.getMenu().getItem(1).setChecked(true);
                    chooseFragment = fragmentManager.findFragmentByTag("calc");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("crypto");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("about");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("bing");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag(fragmentName);
                    if(chooseFragment != null)fragmentTransaction.show(chooseFragment).commit();
                    break;
                case "calc":
                    setTitle(getResources().getString(R.string.calculator));
                    nView.getMenu().getItem(2).setChecked(true);
                    chooseFragment = fragmentManager.findFragmentByTag("bing");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("crypto");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("about");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("timer");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag(fragmentName);
                    if(chooseFragment != null)fragmentTransaction.show(chooseFragment).commit();
                    break;
                case "crypto":
                    setTitle(getResources().getString(R.string.crypto));
                    nView.getMenu().getItem(3).setChecked(true);
                    chooseFragment = fragmentManager.findFragmentByTag("calc");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("bing");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("about");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("timer");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag(fragmentName);
                    if(chooseFragment != null)fragmentTransaction.show(chooseFragment).commit();
                    break;
                case "about":
                    setTitle(getResources().getString(R.string.about));
                    nView.getMenu().getItem(4).setChecked(true);
                    chooseFragment = fragmentManager.findFragmentByTag("calc");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("crypto");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("bing");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag("timer");
                    if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                    chooseFragment = fragmentManager.findFragmentByTag(fragmentName);
                    if(chooseFragment != null)fragmentTransaction.show(chooseFragment).commit();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        this.menu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
            startActivity(settingsIntent);
            return true;
        }else if(id == R.id.refresh_Bing){
            return false;
        }
        return false;
    }

    private void displaySelectedScreen(int id, final MenuItem item, boolean firstRun) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_main);
        MenuItem refreshItem = null;
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment chooseFragment = null;

        if(!firstRun) {
            if(menu != null) refreshItem = menu.findItem(R.id.refresh_Bing);
        }
        String tag = "";
        int title = 0;

        switch (id) {
            case R.id.Timer_Nav:
                if (refreshItem != null) refreshItem.setVisible(false);
                tag = "timer";
                title = R.string.auto_launch;
                if (item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                break;
            case R.id.Calculator_Nav:
                if (refreshItem != null) refreshItem.setVisible(false);
                tag = "calc";
                title = R.string.calculator;
                if (item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                break;
            case R.id.Crypto_Nav:
                if(refreshItem != null) refreshItem.setVisible(false);
                tag = "crypto";
                title = R.string.crypto;
                if (item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                break;
            case R.id.Bing_Dining_Nav:
                tag = "bing";
                title = R.string.bing_dining;
                if(refreshItem != null && !refreshItem.isVisible()) refreshItem.setVisible(true);
                if (item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                break;
            case R.id.About_Nav:
                if(refreshItem != null) refreshItem.setVisible(false);
                tag = "about";
                title = R.string.about;
                if (item.isChecked()) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                break;
            default:
                drawer.closeDrawer(GravityCompat.START);
        }

        switch(tag){
            case "bing":
                chooseFragment = fragmentManager.findFragmentByTag("calc");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("crypto");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("about");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("timer");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                break;
            case "calc":
                chooseFragment = fragmentManager.findFragmentByTag("bing");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("crypto");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("about");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("timer");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                break;
            case "crypto":
                chooseFragment = fragmentManager.findFragmentByTag("calc");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("bing");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("about");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("timer");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                break;
            case "about":
                chooseFragment = fragmentManager.findFragmentByTag("calc");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("crypto");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("bing");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("timer");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                break;
            case "timer":
                chooseFragment = fragmentManager.findFragmentByTag("calc");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("crypto");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("about");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                chooseFragment = fragmentManager.findFragmentByTag("bing");
                if(chooseFragment != null) fragmentTransaction.hide(chooseFragment);
                break;
            default:
        }

        chooseFragment = fragmentManager.findFragmentByTag(tag);
        if(chooseFragment != null) {
            fragmentTransaction.show(chooseFragment);
            fragmentTransaction.addToBackStack(tag).commit();
        }else{
            switch (id){
                case R.id.Bing_Dining_Nav:
                    chooseFragment = new BingDiningFragment();
                    break;
                case R.id.Calculator_Nav:
                    chooseFragment = new CalculatorFragment();
                    break;
                case R.id.Crypto_Nav:
                    chooseFragment = new CryptoFragment();
                    break;
                case R.id.About_Nav:
                    chooseFragment = new AboutFragment();
                    break;
                case R.id.Timer_Nav:
                    chooseFragment = TimerFragment.newInstance();
                    break;
            }

            fragmentTransaction.add(R.id.fragmentHolder, chooseFragment, tag);
            fragmentTransaction.addToBackStack(tag).commit();
        }

        Handler handler = new Handler();
        final int finalTitle = title;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(finalTitle != 0) setTitle(finalTitle);
                item.setChecked(true);
                drawer.closeDrawer(GravityCompat.START);
            }
        }, 35);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId(), item, false);
        return true;
    }


    private static class BingWallpaper extends AsyncTask<Boolean, Void, Void> {
        StringRequest stringRequest;
        final String URL_DATA = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
        private WeakReference<MainActivity> reference;
        private String retVal;

        BingWallpaper(MainActivity context){
            reference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            final boolean storagePermission = params[0];
            final MainActivity mainActivity = reference.get();
            stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray array = jsonObject.getJSONArray("images");
                        JSONObject urlObj = array.getJSONObject(0);
                        retVal = "https://www.bing.com/"+ urlObj.getString("url");
                        assert (mainActivity.bingImage != null);
                        Picasso.with(mainActivity.context).load(retVal).fit().into(mainActivity.bingImage);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mainActivity.bitmapWallpaper = Picasso.with(mainActivity.context).load(retVal).get();
                                    if(storagePermission) mainActivity.saveBitmap();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (JSONException e) {
                        Toast.makeText(mainActivity.context, "Image JSON error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mainActivity.context, "Image Volley error", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            final MainActivity mainActivity = reference.get();
            RequestQueue rQ = Volley.newRequestQueue(mainActivity.context);
            rQ.add(stringRequest);
        }
    }

    private void saveBitmap(){
        if(bitmapWallpaper == null || saveImagePath == null){
            return;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapWallpaper.compress(Bitmap.CompressFormat.JPEG, 40, bytes);


        //create a new file if one does not exist already
        File imageFile = new File(saveImagePath + File.separator + "savedBingImage.jpg");
        if(!imageFile.exists()) {
            try {
                if (!imageFile.createNewFile()) {
                    setToast("Could not create new file");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //write the bytes in file
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fo != null) {
                fo.write(bytes.toByteArray());
            }else{
                setToast("Could not write to file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //close file FileOutput
        try {
            if (fo != null) {
                fo.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadBitmap(){
        String path = "/storage/emulated/0/Android/data/" + getPackageName() + "/Image" + File.separator + "savedBingImage.jpg";

        File file = new File(path);
        int size  = (int)(int)file.length();
        byte[] b = new byte[size];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
               int bytesRead =  fileInputStream.read(b);
                if(BuildConfig.DEBUG && !(bytesRead>0)){
                    throw new AssertionError();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bitMap = BitmapFactory.decodeByteArray(b, 0, b.length);
            bingImage.setImageBitmap(bitMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void makeBingWallRequest(boolean storagePermission){
        if(getDeviceInternetStatus(context) == null && getBingWallDay() == 0){
            bingImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bearcats));
            return;
        }
        if(!storagePermission){
           new BingWallpaper(MainActivity.this).execute(false);
        }else {
            Calendar now = Calendar.getInstance(TimeZone.getDefault());
            int day = now.get(Calendar.DAY_OF_MONTH);
            if (day != getBingWallDay()) {
                saveBingWallDay();
                new BingWallpaper(MainActivity.this).execute(true);
            } else {
                loadBitmap();
            }
        }
    }


    private void saveBingWallDay(){
        SharedPreferences sharedPreferences = getSharedPreferences("bingWall", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        int day = now.get(Calendar.DAY_OF_MONTH);
        editor.putInt("dayInt", day);
        editor.apply();
    }

    private int getBingWallDay(){
        SharedPreferences sharedPreferences = getSharedPreferences("bingWall", MODE_PRIVATE);
        return sharedPreferences.getInt("dayInt", 0);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("Saved", 1);
    }

    public Menu getMenu(){
        return menu;
    }

    private void clearCryptoData(){
        SharedPreferences sP = this.getSharedPreferences("Crypto", MODE_PRIVATE);
        sP.edit().remove("Crypto").clear().apply();
    }
}
