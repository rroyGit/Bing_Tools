package com.example.roy.navapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.roy.navapp.BingDiningMenu.getDate;

/**
 * A simple {@link Fragment} subclass.
 */
public class CryptoFragment extends Fragment {
    private TextView etherText, timeText;
    private Button etherB;
    private EditText editT;
    private String[] retTime;
    private double etherVal = 0.00;
    private Context context;

    private ProgressBar progressBar, progressBar2;

    public CryptoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Crypto");

        context = this.getContext();
        progressBar = (ProgressBar) getActivity().findViewById(R.id.proBar);
        progressBar2 = (ProgressBar) getActivity().findViewById(R.id.proBar2);
        timeText = (TextView) getActivity().findViewById(R.id.timeText);
        etherText = (TextView) getActivity().findViewById(R.id.etherText);
        etherB = (Button) getActivity().findViewById(R.id.etherButton);
        editT = (EditText) getActivity().findViewById(R.id.editT);
        etherText.setMaxLines(2);
        etherText.setTextIsSelectable(true);


        if(getSavedEther() == 0.00){
            new getEther().execute();
        }else{
            progressBar.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            etherVal = getSavedEther();
            etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f",etherVal)));
            String ret = "Last updated: " + getSavedDate();
            timeText.setText(ret);
        }


        etherB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                timeText.setText("Last updated: ");
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                new getEther().execute();
            }
        });

        editT.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(editT.getText().length() != 0){
                    double res;
                    if(editT.getText().toString().charAt(0) == '.' && editT.getText().length() == 1) res = 1;
                    else if(editT.getText().toString().charAt(0) == '.' && editT.getText().length() > 1){
                        res = Double.parseDouble(editT.getText().toString());
                    }
                    else res = Double.parseDouble(editT.getText().toString());
                    res = res * etherVal;
                    NumberFormat.getInstance().format(res);

                    String result = String.format("%s%s", '$', String.format(Locale.US, "%.2f",res));
                    etherText.setText(result);
                    etherText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                }else{
                    etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f",etherVal)));
                }
                changeTextView();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crypto, container, false);
    }

    private class getEther extends AsyncTask<Void, Void, Void> {
        String words;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://coinmarketcap.com/currencies/ethereum/").get();
                words = doc.select("span#quote_price").text();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (words != null) {
                    super.onPostExecute(aVoid);
                    String value;
                    try {
                        value = words.substring(0, words.length() - 4);
                        etherVal = Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        value = "0.00";
                        etherVal = Double.parseDouble(value);
                        Toast.makeText(context, "Parsing error", Toast.LENGTH_LONG).show();
                    }

                    etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", etherVal)));
                    saveCryptoData();

                    retTime = new String[3];
                    getDate(retTime);

                    StringBuilder retString = new StringBuilder();
                    retString.append("Last updated: ");
                    retString.append(retTime[0]).append(':').append(retTime[1]).append(':').append(retTime[2]);

                    timeText.setText(retString);
                    progressBar.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                }
            }catch (Exception e){
                //Toast.makeText(context, "error:" +e, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void changeTextView() {
        if(etherText.length() > 17){
            etherText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            etherText.setTextSize(30);
        }else {
            etherText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            etherText.setTextSize(40);
        }
    }

    private void saveCryptoData(){
        SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        retTime = new String[3];
        getDate(retTime);
        String date =  retTime[0]+':'+retTime[1]+':'+retTime[2];

        sEditor.putString("Date",date);
        sEditor.putString("Ether", String.valueOf(etherVal));
        sEditor.apply();
    }

    private double getSavedEther(){
        SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
        return Double.parseDouble(sP.getString("Ether", "0.00"));
    }

    private String getSavedDate(){
        SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
        return sP.getString("Date", "0.00");
    }

}
