package com.rroycsdev.bingtools;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
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
import static com.rroycsdev.bingtools.BingDiningMenu.getDeviceInternetStatus;
import static com.rroycsdev.bingtools.BingDiningMenu.loadCurrentDate;

/**
 * A simple {@link Fragment} subclass.
 */
public class CryptoFragment extends Fragment {
    private TextView etherText, timeText, selectedCryptoText;
    private Button etherButton, bitcoinButton, rippleButton;
    private EditText editT;
    private double currentCryptoVal = 0.00;
    private Context context;
    private ProgressBar progressBar, progressBar2;
    Menu menu;

    public CryptoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        if(savedInstanceState != null){
            progressBar.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            selectedCryptoText.setText(savedInstanceState.getString("selectCryptoView"));
            timeText.setText(savedInstanceState.getString("lastUpdated"));
            final double res = savedInstanceState.getDouble("resultView");
            currentCryptoVal = res;
            //must wait for config changes such as orientation change
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    double res2;
                    if(editT.getText().toString().compareTo("") == 0) res2 = res;
                    else res2 = res * Integer.parseInt(editT.getText().toString());
                    NumberFormat.getInstance().format(res2);
                    final String result = String.format("%s%s", '$', String.format(Locale.US, "%.2f",res2));
                    etherText.setText(result);
                }
            }, 200);

        }else {
            if (getSavedEther("Ether") == 4.04) {
                if (getDeviceInternetStatus(context) != null) {
                    etherText.setText("");
                    etherText.setHint("");
                    new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ethereum/", "0");
                    currentCryptoVal = getSavedEther("Ether");
                } else {
                    progressBar.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            } else {

                progressBar.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                currentCryptoVal = getSavedEther("Ether");
                etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", currentCryptoVal)));
                String ret = "Last updated: " + getSavedDate();
                timeText.setText(ret);
            }
        }


        etherButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(getDeviceInternetStatus(context) != null) {
                    timeText.setText("Last updated: ");
                    currentCryptoVal = -1;
                    etherText.setText("");
                    etherText.setHint("");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ethereum/", "0");
                    editT.getText().clear();
                    selectedCryptoText.setText("1 Ethereum");
                }else{
                    etherText.setText("");
                    etherText.setHint("$0.00");
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bitcoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getDeviceInternetStatus(context) != null) {
                    currentCryptoVal = -1;
                    etherText.setText("");
                    etherText.setHint("");
                    timeText.setText("Last updated: ");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/bitcoin/", "1");
                    editT.getText().clear();
                    selectedCryptoText.setText("1 Bitcoin");
                }else {
                    etherText.setText("");
                    etherText.setHint("$0.00");
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rippleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getDeviceInternetStatus(context) != null) {
                    currentCryptoVal = -1;
                    etherText.setText("");
                    etherText.setHint("");
                    timeText.setText("Last updated: ");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    new getEther(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ripple/", "2");
                    editT.getText().clear();
                    selectedCryptoText.setText("1 Ripple");
                }else{
                    etherText.setText("");
                    etherText.setHint("$0.00");
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
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
                    } else res = Double.parseDouble(editT.getText().toString());

                    res = res * currentCryptoVal;
                    NumberFormat.getInstance().format(res);

                    String result = String.format("%s%s", '$', String.format(Locale.US, "%.2f",res));
                    etherText.setText(result);
                    etherText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                    String currentString = selectedCryptoText.getText().toString();
                    if(currentString.contains("Bitcoin")){
                        currentString = editT.getText().toString() + " Bitcoin";
                        selectedCryptoText.setText(currentString);
                    }else if(currentString.contains("Ethereum")){
                        currentString = editT.getText().toString() + " Ethereum";
                        selectedCryptoText.setText(currentString);
                    }else if(currentString.contains("Ripple")){
                        currentString = editT.getText().toString() + " Ripple";
                        selectedCryptoText.setText(currentString);
                    }

                }else{
                    if(currentCryptoVal == -1) etherText.setText("");
                    else etherText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", currentCryptoVal)));
                    if(selectedCryptoText.getText().toString().contains("Bitcoin")) {
                        if (editT.getText().toString().isEmpty()) {
                            String currentString = "1 Bitcoin";
                            selectedCryptoText.setText(currentString);
                        }
                    }else if(selectedCryptoText.getText().toString().contains("Ethereum")){
                        if (editT.getText().toString().isEmpty()) {
                            String currentString = "1 Ethereum";
                            selectedCryptoText.setText(currentString);
                        }
                    }else if(selectedCryptoText.getText().toString().contains("Ripple")){
                        if (editT.getText().toString().isEmpty()) {
                            String currentString = "1 Ripple";
                            selectedCryptoText.setText(currentString);
                        }
                    }
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
        protected void onPreExecute() {
            super.onPreExecute();
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
                    cryptoFragment.progressBar.setVisibility(View.GONE);
                    switch(cryptoType){
                        case 0:
                            cryptoFragment.saveCryptoData("Ether", cryptoVal);
                            break;
                        case 1:
                            cryptoFragment.saveCryptoData("Bitcoin", cryptoVal);
                            break;
                        case 2:
                            cryptoFragment.saveCryptoData("Ripple", cryptoVal);
                            break;
                    }

                    StringBuilder month = new StringBuilder(), day = new StringBuilder(), year = new StringBuilder();
                    loadCurrentDate(month, day, year);

                    StringBuilder retString = new StringBuilder();
                    retString.append("Last updated: ");
                    retString.append(month.toString()).append(':').append(day.toString()).append(':').append(year.toString());

                    cryptoFragment.timeText.setText(retString);
                    cryptoFragment.progressBar2.setVisibility(View.GONE);
                } else {
                    Toast.makeText(cryptoFragment.context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                    cryptoFragment.progressBar.setVisibility(View.GONE);
                    cryptoFragment.progressBar2.setVisibility(View.GONE);
                }
            }catch (Exception e){
                //Toast.makeText(cryptoFragment.context, "error:" +e, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            getActivity().setTitle(R.string.crypto);
            if(menu != null){
                menu.findItem(R.id.refresh_Bing).setVisible(false);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.refresh_Bing).setVisible(false);
        this.menu = menu;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.crypto);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("resultView", currentCryptoVal);
        outState.putString("selectCryptoView", selectedCryptoText.getText().toString());
        outState.putString("lastUpdated", timeText.getText().toString());
    }
}
