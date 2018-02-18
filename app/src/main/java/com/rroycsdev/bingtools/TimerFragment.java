package com.rroycsdev.bingtools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;


public class TimerFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private Menu menu;
    private int buttonID[] = {R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight,
                        R.id.nine, R.id.plus5, R.id.plus15, R.id.plus30, R.id.plus45};
    private Button[] buttonArray = new Button[buttonID.length];
    private TextView resultTextView;
    private ImageButton deleteOne, deleteAll;
    private FloatingActionButton addFab, startFab, stopFab;
    private Animation animClose, animOpen, animForward, animBackward;
    private boolean isOpen = false;
    private ScheduledExecutorService scheduledExecutorService = null;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.auto_launch));
        resultTextView = (TextView) view.findViewById(R.id.resultTextView);
        deleteOne = (ImageButton) view.findViewById(R.id.deleteOne);
        deleteAll = (ImageButton) view.findViewById(R.id.deleteAll);

        deleteOne.setOnClickListener(this);
        deleteAll.setOnClickListener(this);
        for(int i = 0; i < buttonArray.length; i++){
            buttonArray[i] = view.findViewById(buttonID[i]);
            buttonArray[i].setOnClickListener(this);
        }

        addFab = (FloatingActionButton) view.findViewById(R.id.addFab);
        startFab = (FloatingActionButton) view.findViewById(R.id.startFab);
        stopFab = (FloatingActionButton) view.findViewById(R.id.stopFab);
        animOpen = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_open);
        animClose = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_close);
        animForward = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_rotate_forward);
        animBackward = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_rotate_backward);
        addFab.setRippleColor(Color.CYAN);

        addFab.setOnClickListener(this);
        startFab.setOnClickListener(this);
        stopFab.setOnClickListener(this);

    }

    private void animateFab(){

        if(isOpen){
            addFab.startAnimation(animForward);
            startFab.startAnimation(animClose);
            stopFab.startAnimation(animClose);
            startFab.setClickable(false);
            stopFab.setClickable(false);
            isOpen = false;
        }else{
            addFab.startAnimation(animBackward);
            startFab.startAnimation(animOpen);
            stopFab.startAnimation(animOpen);
            if(scheduledExecutorService == null) startFab.setClickable(true);
            stopFab.setClickable(true);
            isOpen = true;

        }
    }

    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            getActivity().setTitle(R.string.auto_launch);
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
    public void onClick(View view) {
        String temp;
        switch (view.getId()){
            case R.id.addFab:
                animateFab();
                break;
            case R.id.startFab:
                long minutes;
                if (resultTextView.getText().toString().compareTo("") != 0) {
                    BigInteger bigInteger = new BigInteger(resultTextView.getText().toString());
                    minutes = bigInteger.intValue();
                    scheduledExecutorService = timer(startFab, resultTextView, minutes);
                    Toast.makeText(getContext(), "Timer has been set to " + minutes + " minutes", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stopFab:
                resultTextView.setText("");

                if(scheduledExecutorService != null && !startFab.isClickable()){
                    startFab.clearColorFilter();
                    scheduledExecutorService.shutdownNow();
                    startFab.setPressed(false);
                    startFab.setClickable(true);
                    scheduledExecutorService = null;
                    Toast.makeText(getContext(), "Timer has been canceled", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.zero:
                temp = resultTextView.getText().toString() +"0";
                resultTextView.setText(temp);
                break;
            case R.id.one:
                temp = resultTextView.getText().toString() +"1";
                resultTextView.setText(temp);
                break;
            case R.id.two:
                temp = resultTextView.getText().toString() +"2";
                resultTextView.setText(temp);
                break;
            case R.id.three:
                temp = resultTextView.getText().toString() +"3";
                resultTextView.setText(temp);
                break;
            case R.id.four:
                temp = resultTextView.getText().toString() +"4";
                resultTextView.setText(temp);
                break;
            case R.id.five:
                temp = resultTextView.getText().toString() +"5";
                resultTextView.setText(temp);
                break;
            case R.id.six:
                temp = resultTextView.getText().toString() +"6";
                resultTextView.setText(temp);
                break;
            case R.id.seven:
                temp = resultTextView.getText().toString() +"7";
                resultTextView.setText(temp);
                break;
            case R.id.eight:
                temp = resultTextView.getText().toString() +"8";
                resultTextView.setText(temp);
                break;
            case R.id.nine:
                temp = resultTextView.getText().toString() +"9";
                resultTextView.setText(temp);
                break;
            case R.id.plus5:
                if(resultTextView.getText().toString().isEmpty()){
                    resultTextView.setText("5");
                }else {
                    BigInteger bigInteger = new BigInteger(resultTextView.getText().toString());
                    temp = String.valueOf(bigInteger.longValue() + 5);
                    resultTextView.setText(temp);
                }
                break;
            case R.id.plus15:
                if(resultTextView.getText().toString().isEmpty()){
                    resultTextView.setText("15");
                }else {
                    BigInteger bigInteger = new BigInteger(resultTextView.getText().toString());
                    temp = String.valueOf(bigInteger.longValue() + 15);
                    resultTextView.setText(temp);
                }
                break;
            case R.id.plus30:
                if(resultTextView.getText().toString().isEmpty()){
                    resultTextView.setText("30");
                }else {
                    BigInteger bigInteger = new BigInteger(resultTextView.getText().toString());
                    temp = String.valueOf(bigInteger.longValue() + 30);
                    resultTextView.setText(temp);
                }
                break;
            case R.id.plus45:
                if(resultTextView.getText().toString().isEmpty()){
                    resultTextView.setText("45");
                }else {
                    BigInteger bigInteger = new BigInteger(resultTextView.getText().toString());
                    temp = String.valueOf(bigInteger.longValue() + 45);
                    resultTextView.setText(temp);
                }
                break;
            case R.id.deleteOne:
                if(!resultTextView.getText().toString().isEmpty()) {
                    temp = resultTextView.getText().toString();
                    temp = temp.substring(0, temp.length() - 1);
                    resultTextView.setText(temp);
                }
                break;
            case R.id.deleteAll:
                resultTextView.setText("");
                break;
        }
    }

    private ScheduledExecutorService timer(final FloatingActionButton startFab, final TextView textView, final long minutes) {
        startFab.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        startFab.setClickable(false);

        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startFab.setClickable(true);
                        startFab.setPressed(false);
                        Toast.makeText(getContext(), "It has been " + minutes + " minutes", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.setAction("android.intent.action.VIEW");
                        intent.setComponent(ComponentName.unflattenFromString("com.rroycsdev.bingtools/com.rroycsdev.bingtools.MainActivity"));
                        getActivity().startActivity(intent);
                    }
                });
            }
        }, minutes, TimeUnit.MINUTES);
        return scheduledExecutorService;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
