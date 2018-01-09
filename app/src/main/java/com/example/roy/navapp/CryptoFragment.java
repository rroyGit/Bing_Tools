package com.example.roy.navapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.roy.navapp.BingDiningMenu.loadCurrentDate;

/**
 * A simple {@link Fragment} subclass.
 */
public class CryptoFragment extends Fragment {
    private TextView etherText, timeText, selectedCryptoText;
    private Button etherButton, bitcoinButton, rippleButton;
    private EditText editT;
    private String[] retTime;
    private double currentCryptoVal = 0.00;
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
        progressBar = (ProgressBar) view.findViewById(R.id.proBar);
        progressBar2 = (ProgressBar) view.findViewById(R.id.proBar2);
        timeText = (TextView) view.findViewById(R.id.timeText);
        etherText = (TextView) view.findViewById(R.id.etherText);
        selectedCryptoText = (TextView) view.findViewById(R.id.selectedCrypto);
        etherButton = (Button) view.findViewById(R.id.etherButton);
        bitcoinButton = (Button) view.findViewById(R.id.bitcoinButton);
        rippleButton = (Button) view.findViewById(R.id.rippleButton);
        editT = (EditText) view.findViewById(R.id.editT);
        etherText.setMaxLines(2);
        etherText.setTextIsSelectable(true);


        if(getSavedEther("Ether") == 4.04){
            new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ethereum/", "0");
            currentCryptoVal = getSavedEther("Ether");
        }else{
            progressBar.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            currentCryptoVal = getSavedEther("Ether");
            etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", currentCryptoVal)));
            String ret = "Last updated: " + getSavedDate();
            timeText.setText(ret);
        }
        etherButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                timeText.setText("Last updated: ");
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ethereum/", "0");
                editT.setText("");
                selectedCryptoText.setText("1 Ethereum");
            }
        });

        bitcoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeText.setText("Last updated: ");
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/bitcoin/", "1");
                editT.setText("");
                selectedCryptoText.setText("1 Bitcoin");
            }
        });

        rippleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeText.setText("Last updated: ");
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ripple/", "2");
                editT.setText("");
                selectedCryptoText.setText("1 Ripple");
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
                    res = res * currentCryptoVal;
                    NumberFormat.getInstance().format(res);

                    String result = String.format("%s%s", '$', String.format(Locale.US, "%.2f",res));
                    etherText.setText(result);
                    etherText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                }else{
                    etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", currentCryptoVal)));
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

    private static class getEther extends AsyncTask<String, Void, Void> {
        String words;
        int cryptoType = 0;
        WeakReference<CryptoFragment> weakReference;

        getEther(CryptoFragment context){
            weakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                cryptoType = Integer.parseInt(strings[1]);
                Document doc = Jsoup.connect(strings[0]).get();
                words = doc.select("span#quote_price").text();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            CryptoFragment cryptoFragment = weakReference.get();
            double cryptoVal;
            try {
                if (words != null) {
                    super.onPostExecute(aVoid);
                    String value;
                    try {
                        value = words.substring(0, words.length() - 4);
                        cryptoVal = Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        value = "0.00";
                        cryptoVal = Double.parseDouble(value);
                        Toast.makeText(cryptoFragment.context, "Parsing error", Toast.LENGTH_LONG).show();
                    }

                    cryptoFragment.currentCryptoVal = cryptoVal;
                    cryptoFragment.etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", cryptoVal)));
                    switch(cryptoType){
                        case 0:
                            cryptoFragment.saveCryptoData("Ether", cryptoVal);
                            break;
                        case 1:
                            cryptoFragment.saveCryptoData("Bitcoin", cryptoVal);
                            break;
                    }

                    StringBuilder month = new StringBuilder(), day = new StringBuilder(), year = new StringBuilder();
                    loadCurrentDate(month, day, year);

                    StringBuilder retString = new StringBuilder();
                    retString.append("Last updated: ");
                    retString.append(month.toString()).append(':').append(day.toString()).append(':').append(year.toString());

                    cryptoFragment.timeText.setText(retString);
                    cryptoFragment.progressBar.setVisibility(View.GONE);
                    cryptoFragment.progressBar2.setVisibility(View.GONE);
                } else {
                    Toast.makeText(cryptoFragment.context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                    cryptoFragment.progressBar.setVisibility(View.GONE);
                    cryptoFragment.progressBar2.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Toast.makeText(cryptoFragment.context, "error:" +e, Toast.LENGTH_SHORT).show();
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

    private void saveCryptoData(String name, double value){
        SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        StringBuilder month = new StringBuilder(), day = new StringBuilder(), year = new StringBuilder();
        loadCurrentDate(month, day, year);
        String date = month.toString()+':'+day.toString()+':'+year.toString();

        sEditor.putString("Date", date);
        sEditor.putString(name, String.valueOf(value));
        sEditor.apply();
    }

    private double getSavedEther(String type){
        SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
        return Double.parseDouble(sP.getString(type, "4.04"));
    }

    private String getSavedDate(){
        SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
        return sP.getString("Date", "0.00");
    }

}
