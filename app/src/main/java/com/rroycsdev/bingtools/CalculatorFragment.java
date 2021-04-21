package com.rroycsdev.bingtools;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalculatorFragment extends Fragment implements View.OnClickListener {

    TextView resultView;
    EditText num1EditView, num2EditView;
    Button addB, subB, mulB, divB, clearB, leftCopyB, rightCopyB, modB;
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


        resultView = (TextView) view.findViewById(R.id.resultB);
        resultView.setTextIsSelectable(true);
        num1EditView = (EditText) view.findViewById(R.id.num1);
        num2EditView = (EditText) view.findViewById(R.id.num2);
        addB = (Button) view.findViewById(R.id.addB);
        subB = (Button) view.findViewById(R.id.minusB);
        clearB = (Button) view.findViewById(R.id.clear);
        mulB = (Button) view.findViewById(R.id.mulB);
        divB = (Button) view.findViewById(R.id.divB);
        leftCopyB =(Button) view.findViewById(R.id.leftCopy);
        rightCopyB =(Button) view.findViewById(R.id.rightCopy);
        modB = (Button) view.findViewById(R.id.mod);
        scrollView = (ScrollView) view.findViewById(R.id.scrollViewCalculator);

        //positionEditText();
        modB.setOnClickListener(this);
        clearB.setOnClickListener(this);
        mulB.setOnClickListener(this);
        divB.setOnClickListener(this);
        addB.setOnClickListener(this);
        subB.setOnClickListener(this);
        leftCopyB.setOnClickListener(this);
        rightCopyB.setOnClickListener(this);

        if(savedInstanceState != null){
            resultView.setText(savedInstanceState.getString("resultView"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    private void positionEditText(){

        //position the EditTexts center
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
        num1EditView.setLayoutParams(editT1);
        num1EditView.setWidth(editText1_width);
        num2EditView.setLayoutParams(editT2);
        num2EditView.setWidth(editText2_width);
    }
    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        positionEditText();
    }

    private void multiplyNumbers() throws NumberFormatException {
        if (num1EditView.getText().toString().trim().isEmpty() ||
                num2EditView.getText().toString().trim().isEmpty()) {
            resultView.setText(getString(R.string.error));
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }

        double a = Double.parseDouble(num1EditView.getText().toString());
        double b = Double.parseDouble(num2EditView.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            resultView.setText(R.string.error3);
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }

        n1 = Double.parseDouble(num1EditView.getText().toString());
        n2 = Double.parseDouble(num2EditView.getText().toString());
        double res = n1 * n2;

        if (res < 0 || res > Integer.MAX_VALUE) {
            resultView.setText(R.string.error3);
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }

        res_num = n1 * n2;
        resultView.setText(NumberFormat.getInstance().format(res_num));
    }

    private void divideNumbers() throws NumberFormatException {
        if (num1EditView.getText().toString().trim().isEmpty() ||
                num2EditView.getText().toString().trim().isEmpty()) {
            resultView.setText(getString(R.string.error));
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }

        double a = Double.parseDouble(num1EditView.getText().toString());
        double b = Double.parseDouble(num2EditView.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            resultView.setText(R.string.error3);
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;

        }

        n1 = Double.parseDouble(num1EditView.getText().toString());
        n2 = Double.parseDouble(num2EditView.getText().toString());
        if (n2 == 0) {
            resultView.setText(getString(R.string.error2));
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        res_num = n1 / n2;
        resultView.setText(NumberFormat.getInstance().format(res_num));
    }

    private void addNumbers() throws NumberFormatException {
        if (num1EditView.getText().toString().trim().isEmpty() ||
                num2EditView.getText().toString().trim().isEmpty()) {
            resultView.setText(getString(R.string.error));
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        double a = Double.parseDouble(num1EditView.getText().toString());
        double b = Double.parseDouble(num2EditView.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            resultView.setText(R.string.error3);
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        n1 = Double.parseDouble(num1EditView.getText().toString());
        n2 = Double.parseDouble(num2EditView.getText().toString());
        if (n1 > Double.MAX_VALUE - n2) {
            resultView.setText(getString(R.string.error3));
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        res_num = n1 + n2;
        resultView.setText(NumberFormat.getInstance().format(res_num));
    }

    private void subtractNumbers() throws NumberFormatException {
        if (num1EditView.getText().toString().trim().isEmpty() ||
                num2EditView.getText().toString().trim().isEmpty()) {
            resultView.setText(getString(R.string.error));
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        double a = Double.parseDouble(num1EditView.getText().toString());
        double b = Double.parseDouble(num2EditView.getText().toString());
        if (a > Integer.MAX_VALUE || b > Integer.MAX_VALUE) {
            resultView.setText(R.string.error3);
            CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        if (n1 > Double.MAX_VALUE - n2) {
            resultView.setText(getString(R.string.error3));
           CommonUtilities.hideKeyboard(getActivity(), getView());
            return;
        }
        n1 = Double.parseDouble(num1EditView.getText().toString());
        n2 = Double.parseDouble(num2EditView.getText().toString());
        res_num = n1 - n2;
        resultView.setText(NumberFormat.getInstance().format(res_num));
    }

    private void clearNumbers(){
        resultView.setText("");
        num1EditView.getText().clear();
        num2EditView.getText().clear();
    }

    private void leftCopyPaste(){
        String retString = resultView.getText().toString();
        if(retString.compareTo("Too large") !=0 & retString.compareTo("Error") != 0 & retString.compareTo("NaN") !=0 &
                retString.compareTo("Undefined") !=0 & retString.compareTo("") != 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(resultView.getText().toString(), ",");
            if(stringTokenizer.countTokens() == 1){
                num1EditView.setText(retString);
            }else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= stringTokenizer.countTokens() + 1; i++) {
                    stringBuilder.append(stringTokenizer.nextToken());
                }
                num1EditView.setText(stringBuilder.toString());
            }
        }
        num1EditView.requestFocus();
    }
    private void rightCopyPaste(){
        String retString = resultView.getText().toString();
        if(retString.compareTo("Too large") !=0 & retString.compareTo("Error") != 0 & retString.compareTo("NaN") != 0 &
                retString.compareTo("Undefined") !=0 & retString.compareTo("") != 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(resultView.getText().toString(), ",");
            if(stringTokenizer.countTokens() == 1){
                num2EditView.setText(retString);
            }else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= stringTokenizer.countTokens() + 1; i++) {
                    stringBuilder.append(stringTokenizer.nextToken());
                }
                num2EditView.setText(stringBuilder.toString());
            }

        }
        num2EditView.requestFocus();
    }

    private void doMod() throws NumberFormatException {

        if(num1EditView.getText().toString().compareTo("") != 0 && num2EditView.getText().toString().compareTo("") != 0) {
            n1 = Double.parseDouble(num1EditView.getText().toString());
            n2 = Double.parseDouble(num2EditView.getText().toString());
            res_num = n1 % n2;
            resultView.setText(NumberFormat.getInstance().format(res_num));
        }
    }

    @Override
    public void onClick(View view){
        try {
            switch (view.getId()) {
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
                case R.id.mod:
                    doMod();
                    break;
                default:
            }
        } catch (NumberFormatException exception) {
            Toast.makeText(view.getContext(), "Error converting input into a number!", Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Toast.makeText(view.getContext(), "Unknown error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            CommonUtilities.hideKeyboard(getActivity(), getView());
        }
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
        outState.putString("resultView", resultView.getText().toString());
    }
}
