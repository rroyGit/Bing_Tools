package com.example.roy.navapp;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    TextView result;
    EditText num1, num2;
    Button aB, mB, mulB,divB, cB;

    double res_num, n1, n2;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Home");


        result = (TextView) getActivity().findViewById(R.id.resultB);
        result.setTextIsSelectable(true);
        num1 = (EditText) getActivity().findViewById(R.id.num1);
        num2 = (EditText) getActivity().findViewById(R.id.num2);
        aB = (Button) getActivity().findViewById(R.id.addB);
        mB = (Button) getActivity().findViewById(R.id.minusB);
        cB = (Button) getActivity().findViewById(R.id.clear);
        mulB = (Button) getActivity().findViewById(R.id.mulB);
        divB = (Button) getActivity().findViewById(R.id.divB);


        cB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                result.setText("");
                num1.getText().clear();
                num2.getText().clear();
            }
        });

        mulB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(num1.getText().toString().trim().isEmpty() ||
                        num2.getText().toString().trim().isEmpty()) {
                    result.setText(getString(R.string.error));
                    hideKeyboard(getActivity(),getView());;
                    return;
                }

                double a  = Double.parseDouble(num1.getText().toString());
                double b  = Double.parseDouble(num2.getText().toString());
                if(a > Integer.MAX_VALUE || b > Integer.MAX_VALUE){
                    result.setText(R.string.error3);
                    hideKeyboard(getActivity(),getView());
                    return;
                }

                n1 = Double.parseDouble(num1.getText().toString());
                n2 = Double.parseDouble(num2.getText().toString());
                double res = n1 * n2;

                if(res < 0 || res > Integer.MAX_VALUE) {
                    result.setText(R.string.error3);
                    hideKeyboard(getActivity(),getView());
                    return;
                }

                res_num = n1 * n2;
                result.setText(NumberFormat.getInstance().format(res_num));
                hideKeyboard(getActivity(),getView());
            }
        });

        divB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num1.getText().toString().trim().isEmpty() ||
                        num2.getText().toString().trim().isEmpty()) {
                    result.setText(getString(R.string.error));
                    hideKeyboard(getActivity(),getView());
                    return;
                }

                double a  = Double.parseDouble(num1.getText().toString());
                double b  = Double.parseDouble(num2.getText().toString());
                if(a > Integer.MAX_VALUE || b > Integer.MAX_VALUE){
                    result.setText(R.string.error3);
                    hideKeyboard(getActivity(),getView());
                    return;

                }

                n1 = Double.parseDouble(num1.getText().toString());
                n2 = Double.parseDouble(num2.getText().toString());
                if(n2 == 0) {
                    result.setText(getString(R.string.error2));
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                res_num = n1 / n2;
                result.setText(NumberFormat.getInstance().format(res_num));
                hideKeyboard(getActivity(),getView());
            }
        });

        aB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(num1.getText().toString().trim().isEmpty() ||
                        num2.getText().toString().trim().isEmpty()) {
                    result.setText(getString(R.string.error));
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                double a  = Double.parseDouble(num1.getText().toString());
                double b  = Double.parseDouble(num2.getText().toString());
                if(a > Integer.MAX_VALUE || b > Integer.MAX_VALUE){
                    result.setText(R.string.error3);
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                n1 = Double.parseDouble(num1.getText().toString());
                n2 = Double.parseDouble(num2.getText().toString());
                if(n1 > Double.MAX_VALUE - n2){
                    result.setText(getString(R.string.error3));
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                res_num = n1 + n2;
                result.setText(NumberFormat.getInstance().format(res_num));

                hideKeyboard(getActivity(),getView());

            }

        });

        mB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(num1.getText().toString().trim().isEmpty() ||
                        num2.getText().toString().trim().isEmpty()) {
                    result.setText(getString(R.string.error));
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                double a  = Double.parseDouble(num1.getText().toString());
                double b  = Double.parseDouble(num2.getText().toString());
                if(a > Integer.MAX_VALUE || b > Integer.MAX_VALUE){
                    result.setText(R.string.error3);
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                if(n1 > Double.MAX_VALUE - n2){
                    result.setText(getString(R.string.error3));
                    hideKeyboard(getActivity(),getView());
                    return;
                }
                n1 = Double.parseDouble(num1.getText().toString());
                n2 = Double.parseDouble(num2.getText().toString());
                res_num = n1 - n2;
                result.setText(NumberFormat.getInstance().format(res_num));
                hideKeyboard(getActivity(),getView());
            }
        });

    }

    public static void hideKeyboard(Activity thisActivity, View view){
        if(view != null) {
            InputMethodManager imm;
            imm = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
