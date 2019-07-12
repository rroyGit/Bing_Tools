package com.rroycsdev.bingtools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

class CommonUtilities {

    static void loadCurrentDate(StringBuilder... strings){
        Date dateNow = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("M-d-yyyy", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        StringTokenizer sT = new StringTokenizer(dateFormatter.format(dateNow), "-");

        strings[0].append(sT.nextToken());
        strings[1].append(sT.nextToken());
        strings[2].append(sT.nextToken());
    }

    //No Internet access if returns null
    static NetworkInfo getDeviceInternetStatus(Context context){
        //check if internet is enabled
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo();
        }
        return null;
    }

    static void hideKeyboard(Activity thisActivity, View view){
        if(view != null) {
            InputMethodManager imm;
            imm = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
