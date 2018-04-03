package com.rroycsdev.bingtools;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalculatorFragment extends Fragment implements View.OnClickListener {

    TextView result;
    EditText num1, num2;
    Button addB, subB, mulB, divB, clearB, leftCopyB, rightCopyB;
    ScrollView scrollView;
    final int space_btw_editText = 15;
    final int editText1_width = 500, editText2_width = 500;
    double res_num, n1, n2;
    Menu menu;


    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        result = (TextView) view.findViewById(R.id.resultB);
        result.setTextIsSelectable(true);
        num1 = (EditText) view.findViewById(R.id.num1);
        num2 = (EditText) view.findViewById(R.id.num2);
        addB = (Button) view.findViewById(R.id.addB);
        subB = (Button) view.findViewById(R.id.minusB);
        clearB = (Button) view.findViewById(R.id.clear);
        mulB = (Button) view.findViewById(R.id.mulB);
        divB = (Button) view.findViewById(R.id.divB);
        leftCopyB =(Button) view.findViewById(R.id.leftCopy);
        rightCopyB =(Button) view.findViewById(R.id.rightCopy);
        scrollView = (ScrollView) view.findViewById(R.id.scrollViewCalculator);

        positionEditText();
        controlScroll();
        clearB.setOnClickListener(this);
        mulB.setOnClickListener(this);
        divB.setOnClickListener(this);
        addB.setOnClickListener(this);
        subB.setOnClickListener(this);
        leftCopyB.setOnClickListener(this);
        rightCopyB.setOnClickListener(this);

        if(savedInstanceState != null){
            result.setText(savedInstanceState.getString("resultView"));
        }

    }

    public static void hideKeyboard(Activity thisActivity, View view){
        if(view != null) {
            InputMethodManager imm;
            imm = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    private void positionEditText(){

        //position the EditTexts correctly
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int totalTextWidth =  editText1_width+editText2_width+space_btw_editText;
        int device_width = displayMetrics.widthPixels;
        int startIndex= (device_width-totalTextWidth)/2;

        RelativeLayout.LayoutParams editT1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams editT2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editT1.setMargins(startIndex,400,0,0);
        editT2.setMargins(startIndex+editText1_width+space_btw_editText,400, 0, 0);
        num1.setLayoutParams(editT1);
        num1.setWidth(editText1_width);
        num2.setLayoutParams(editT2);
        num2.setWidth(editText2_width);
    }
    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        positionEditText();
    }

    private void multiplyNumbers(){
        if (num1.getText().toString().trim().isEmpty() ||
                num2.getText().toString().trim().isEmpty()) {
            result.setText(getString(R.string.error));
            hideKeyboard(getActivity(), getView());
            return;
        }

        double a = Double.parseDouble(num1.getText().toString());
        double b = Double.parseDouble(num2.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            result.setText(R.string.error3);
            hideKeyboard(getActivity(), getView());
            return;
        }

        n1 = Double.parseDouble(num1.getText().toString());
        n2 = Double.parseDouble(num2.getText().toString());
        double res = n1 * n2;

        if (res < 0 || res > Integer.MAX_VALUE) {
            result.setText(R.string.error3);
            hideKeyboard(getActivity(), getView());
            return;
        }

        res_num = n1 * n2;
        result.setText(NumberFormat.getInstance().format(res_num));
    }

    private void divideNumbers(){
        if (num1.getText().toString().trim().isEmpty() ||
                num2.getText().toString().trim().isEmpty()) {
            result.setText(getString(R.string.error));
            hideKeyboard(getActivity(), getView());
            return;
        }

        double a = Double.parseDouble(num1.getText().toString());
        double b = Double.parseDouble(num2.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            result.setText(R.string.error3);
            hideKeyboard(getActivity(), getView());
            return;

        }

        n1 = Double.parseDouble(num1.getText().toString());
        n2 = Double.parseDouble(num2.getText().toString());
        if (n2 == 0) {
            result.setText(getString(R.string.error2));
            hideKeyboard(getActivity(), getView());
            return;
        }
        res_num = n1 / n2;
        result.setText(NumberFormat.getInstance().format(res_num));
    }

    private void addNumbers(){
        if (num1.getText().toString().trim().isEmpty() ||
                num2.getText().toString().trim().isEmpty()) {
            result.setText(getString(R.string.error));
            hideKeyboard(getActivity(), getView());
            return;
        }
        double a = Double.parseDouble(num1.getText().toString());
        double b = Double.parseDouble(num2.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            result.setText(R.string.error3);
            hideKeyboard(getActivity(), getView());
            return;
        }
        n1 = Double.parseDouble(num1.getText().toString());
        n2 = Double.parseDouble(num2.getText().toString());
        if (n1 > Double.MAX_VALUE - n2) {
            result.setText(getString(R.string.error3));
            hideKeyboard(getActivity(), getView());
            return;
        }
        res_num = n1 + n2;
        result.setText(NumberFormat.getInstance().format(res_num));
    }

    private void subtractNumbers(){
        if (num1.getText().toString().trim().isEmpty() ||
                num2.getText().toString().trim().isEmpty()) {
            result.setText(getString(R.string.error));
            hideKeyboard(getActivity(), getView());
            return;
        }
        double a = Double.parseDouble(num1.getText().toString());
        double b = Double.parseDouble(num2.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            result.setText(R.string.error3);
            hideKeyboard(getActivity(), getView());
            return;
        }
        if (n1 > Double.MAX_VALUE - n2) {
            result.setText(getString(R.string.error3));
            hideKeyboard(getActivity(), getView());
            return;
        }
        n1 = Double.parseDouble(num1.getText().toString());
        n2 = Double.parseDouble(num2.getText().toString());
        res_num = n1 - n2;
        result.setText(NumberFormat.getInstance().format(res_num));
    }

    private void clearNumbers(){
        result.setText("");
        num1.getText().clear();
        num2.getText().clear();
    }

    private void leftCopyPaste(){
        String retString = result.getText().toString();
        if(retString.compareTo("Too large") !=0 & retString.compareTo("Error") != 0 & retString.compareTo("Undefined") !=0 & retString.compareTo("") != 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(result.getText().toString(), ",");
            if(stringTokenizer.countTokens() == 1){
                num1.setText(retString);
            }else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= stringTokenizer.countTokens() + 1; i++) {
                    stringBuilder.append(stringTokenizer.nextToken());
                }
                num1.setText(stringBuilder.toString());
            }
        }
    }
    private void rightCopyPaste(){
        String retString = result.getText().toString();
        if(retString.compareTo("Too large") !=0 & retString.compareTo("Error") != 0 & retString.compareTo("Undefined") !=0 & retString.compareTo("") != 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(result.getText().toString(), ",");
            if(stringTokenizer.countTokens() == 1){
                num2.setText(retString);
            }else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= stringTokenizer.countTokens() + 1; i++) {
                    stringBuilder.append(stringTokenizer.nextToken());
                }
                num2.setText(stringBuilder.toString());
            }
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.clear:
                clearNumbers();
                break;
            case R.id.mulB:
                multiplyNumbers();
                break;
            case R.id.divB:
                divideNumbers();
                break;
            case R.id.addB:
                addNumbers();
                break;
            case R.id.minusB:
                subtractNumbers();
                break;
            case R.id.leftCopy:
                leftCopyPaste();
                break;
            case R.id.rightCopy:
                rightCopyPaste();
                break;
            default:
        }
        hideKeyboard(getActivity(), getView());
    }

    private void controlScroll(){
        num1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        scrollView.pageScroll(View.FOCUS_DOWN);

                    }
                };
                handler.postDelayed(runnable, 250);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        num2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        scrollView.pageScroll(View.FOCUS_DOWN);
                    }
                };
                handler.postDelayed(runnable, 250);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    num2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.calculator);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            getActivity().setTitle(R.string.calculator);
            if(menu != null){
                menu.findItem(R.id.refresh_Bing).setVisible(false);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.refresh_Bing).setVisible(false);
        this.menu =  menu;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("resultView", result.getText().toString());
    }
}
