package com.example.roy.navapp;


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
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class CryptoFragment extends Fragment {

    private TextView etherText;
    private Button etherB;
    private EditText editT;

    private double etherVal = 0.00;

    public CryptoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Crypto");


        etherText = (TextView) getActivity().findViewById(R.id.etherText);
        etherB = (Button) getActivity().findViewById(R.id.etherButton);
        editT = (EditText) getActivity().findViewById(R.id.editT);
        etherText.setMaxLines(2);
        editT.setTextIsSelectable(true);

        new getEther().execute();
        etherB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                    String result = '$'+NumberFormat.getInstance().format(res);
                    etherText.setText(result);
                    etherText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                }else{
                    String result = '$'+ String.valueOf(etherVal);
                    etherText.setText(result);
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
            super.onPostExecute(aVoid);
            etherText.setText(words);
            etherVal = Double.parseDouble(words.substring(1,words.length()));
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

}
