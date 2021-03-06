package com.rroycsdev.bingtools;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class CryptoFragment extends Fragment {
    private TextView priceText, timeText, selectedCryptoText;
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
        priceText = (TextView) view.findViewById(R.id.priceText);
        selectedCryptoText = (TextView) view.findViewById(R.id.selectedCrypto);
        etherButton = (Button) view.findViewById(R.id.etherButton);
        bitcoinButton = (Button) view.findViewById(R.id.bitcoinButton);
        rippleButton = (Button) view.findViewById(R.id.rippleButton);
        editT = (EditText) view.findViewById(R.id.editT);
        priceText.setMaxLines(2);
        priceText.setTextIsSelectable(true);

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
                    if (editT.getText().toString().compareTo("") == 0) res2 = res;
                    else res2 = res * Double.parseDouble(editT.getText().toString());
                    NumberFormat.getInstance().format(res2);
                    final String result = String.format("%s%s", '$', String.format(Locale.US, "%.2f",res2));
                    priceText.setText(result);
                }
            }, 200);

        }else {
            if (getSavedEther("Ether") == 4.04) {
                if (CommonUtilities.getDeviceInternetStatus(context) != null) {
                    priceText.setText("");
                    priceText.setHint("");
                    new CryptoScrapper(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ethereum/", "0");
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
                priceText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", currentCryptoVal)));
                String ret = "Last updated: " + getSavedDate();
                timeText.setText(ret);
            }
        }


        etherButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(CommonUtilities.getDeviceInternetStatus(context) != null) {
                    timeText.setText("Last updated: ");
                    currentCryptoVal = -1;
                    priceText.setText("");
                    priceText.setHint("");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    new CryptoScrapper(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ethereum/", "0");
                    editT.getText().clear();
                    selectedCryptoText.setText("1 Ethereum");
                }else{
                    priceText.setText("");
                    priceText.setHint("$0.00");
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bitcoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonUtilities.getDeviceInternetStatus(context) != null) {
                    currentCryptoVal = -1;
                    priceText.setText("");
                    priceText.setHint("");
                    timeText.setText("Last updated: ");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    new CryptoScrapper(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/bitcoin/", "1");
                    editT.getText().clear();
                    selectedCryptoText.setText("1 Bitcoin");
                }else {
                    priceText.setText("");
                    priceText.setHint("$0.00");
                    Toast.makeText(context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rippleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonUtilities.getDeviceInternetStatus(context) != null) {
                    currentCryptoVal = -1;
                    priceText.setText("");
                    priceText.setHint("");
                    timeText.setText("Last updated: ");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    new CryptoScrapper(CryptoFragment.this).execute("https://coinmarketcap.com/currencies/ripple/", "2");
                    editT.getText().clear();
                    selectedCryptoText.setText("1 Ripple");
                }else{
                    priceText.setText("");
                    priceText.setHint("$0.00");
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
                    priceText.setText(result);
                    priceText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

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
                    if(currentCryptoVal == -1) priceText.setText("");
                    else priceText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", currentCryptoVal)));
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

    private static class CryptoScrapper extends AsyncTask<String, Void, Void> {
        String priceQuote = "0.00";
        int cryptoType = 0;
        WeakReference<CryptoFragment> weakReference;

        CryptoScrapper(CryptoFragment context){
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
                Document doc = Jsoup.connect(strings[0]).timeout(5*1000).get();
                priceQuote = doc.select("div[class~=priceValue___11gHJ]").text();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            CryptoFragment cryptoFragment = weakReference.get();
            try {
                if (!priceQuote.equals("0.00")) {
                    String value;
                    double cryptoVal;
                    try {
                        value = priceQuote.replaceAll("[$|,]","");
                        cryptoVal = Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        value = "0.00";
                        cryptoVal = Double.parseDouble(value);
                        Toast.makeText(cryptoFragment.context, "Parsing error " + priceQuote, Toast.LENGTH_LONG).show();
                    } finally {
                        cryptoFragment.progressBar.setVisibility(View.GONE);
                    }

                    cryptoFragment.currentCryptoVal = cryptoVal;
                    cryptoFragment.priceText.setText(String.format("%s%s", '$', String.format(Locale.US, "%.2f", cryptoVal)));
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
                    CommonUtilities.loadCurrentDate(month, day, year);

                    StringBuilder retString = new StringBuilder();
                    retString.append("Last updated: ");
                    retString.append(month.toString()).append(':').append(day.toString()).append(':').append(year.toString());

                    cryptoFragment.timeText.setText(retString);
                } else {
                    Toast.makeText(cryptoFragment.context, "Could not connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(cryptoFragment.context, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                cryptoFragment.progressBar.setVisibility(View.GONE);
                cryptoFragment.progressBar2.setVisibility(View.GONE);
            }
        }
    }

    protected void changeTextView() {
        if(priceText.length() > 17){
            priceText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            priceText.setTextSize(30);
        }else {
            priceText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            priceText.setTextSize(40);
        }
    }

    private void saveCryptoData(String name, double value) {
        if (getContext() != null) {
            SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
            SharedPreferences.Editor sEditor = sP.edit();
            StringBuilder month = new StringBuilder(), day = new StringBuilder(), year = new StringBuilder();
            CommonUtilities.loadCurrentDate(month, day, year);
            String date = month.toString() + ':' + day.toString() + ':' + year.toString();

            sEditor.putString("Date", date);
            sEditor.putString(name, String.valueOf(value));
            sEditor.apply();
        } else Toast.makeText(context, "Failed to save crypto data", Toast.LENGTH_SHORT).show();
    }

    private double getSavedEther(String type) {
        if (getContext() != null) {
            SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
            return Double.parseDouble(sP.getString(type, "4.04"));
        } else Toast.makeText(context, "Failed to get saved Ether", Toast.LENGTH_SHORT).show();
        return 4.04;
    }

    private String getSavedDate() {
        if (getContext() != null) {
            SharedPreferences sP = getContext().getSharedPreferences("Crypto", MODE_PRIVATE);
            return sP.getString("Date", "0.00");
        } else Toast.makeText(context, "Failed to get saved date", Toast.LENGTH_SHORT).show();
        return "0.00";
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden && getActivity() != null){
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
        if (getActivity() != null)getActivity().setTitle(R.string.crypto);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("resultView", currentCryptoVal);
        outState.putString("selectCryptoView", selectedCryptoText.getText().toString());
        outState.putString("lastUpdated", timeText.getText().toString());
    }
}
