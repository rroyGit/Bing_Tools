package com.example.roy.navapp;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;


public class SettingsAdapter extends SectionedRecyclerViewAdapter<SettingsAdapter.MainVH> {

    private List<String> titles;
    private HashMap<String,List<String>> colorsMap;
    private Context context;
    private final static int TIMER_LAYOUT = 1;
    Activity activity;


    public SettingsAdapter(HashMap<String,List<String>> colorsMap, List<String> titles, Context context, Activity activity) {
        this.context = context;
        this.colorsMap = colorsMap;
        this.titles = titles;
        this.activity = activity;
    }

    @Override
    public int getSectionCount() {
        return titles.size();
    }

    @Override
    public int getItemCount(int section) {
        return 1;
    }

    @Override
    public void onBindHeaderViewHolder(MainVH holder, int section, boolean expanded) {
        switch (section){
            case 0:
                holder.textView.setText(titles.get(0));
                break;
            case 1:
                holder.textView.setText(titles.get(1));
                break;
            case 2:
                holder.textView.setText(titles.get(2));
                break;
        }
        holder.imageView.setImageResource(expanded ? R.drawable.arrow_up: R.drawable.arrow_down);
    }

    @Override
    public void onBindFooterViewHolder(MainVH holder, int section) {

    }

    @Override
    public void onBindViewHolder(final MainVH holder, int section, int relativePosition, int absolutePosition) {

       switch (section){
           case 0:
               holder.radioButton.setText(colorsMap.get(titles.get(0)).get(0));
               holder.radioButton2.setText(colorsMap.get(titles.get(0)).get(1));
               holder.radioButton3.setText(colorsMap.get(titles.get(0)).get(2));
               holder.radioButton4.setText(colorsMap.get(titles.get(0)).get(3));

               radioListener(holder.radioButton,"ColorSpace0", Color.RED);
               radioListener(holder.radioButton2,"ColorSpace0", Color.BLUE);
               radioListener(holder.radioButton3,"ColorSpace0", Color.BLACK);
               radioListener(holder.radioButton4,"ColorSpace0", Color.LTGRAY);
               preserveRadioCheckState(section, holder);
               break;
           case 1:
               holder.radioButton.setText(colorsMap.get(titles.get(1)).get(0));
               holder.radioButton2.setText(colorsMap.get(titles.get(1)).get(1));
               holder.radioButton3.setText(colorsMap.get(titles.get(1)).get(2));
               holder.radioButton4.setText(colorsMap.get(titles.get(1)).get(3));

               radioListener(holder.radioButton,"ColorSpace1", Color.RED);
               radioListener(holder.radioButton2,"ColorSpace1", Color.BLUE);
               radioListener(holder.radioButton3,"ColorSpace1", Color.BLACK);
               radioListener(holder.radioButton4,"ColorSpace1", Color.LTGRAY);
               preserveRadioCheckState(section, holder);
               break;
           case 2:




       }


    }




    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        super.getItemViewType(section, relativePosition, absolutePosition);
        // VIEW_TYPE_FOOTER is -3, VIEW_TYPE_HEADER is -2, VIEW_TYPE_ITEM is -1.
        // You can return 0 or greater.
        switch(section){

            case 2:
                return TIMER_LAYOUT;
        }
        return 0;
    }

    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                layout = R.layout.header_list;
                break;
            case VIEW_TYPE_ITEM:
                layout = R.layout.body_list;
                break;
            case TIMER_LAYOUT:
                layout = R.layout.timer_settings;
                break;
            default:
                layout = R.layout.body_list;
                break;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MainVH(v, this, viewType);
    }

    public class MainVH extends SectionedViewHolder implements View.OnClickListener{
        TextView textView;
        ImageView imageView;
        RadioButton radioButton, radioButton2, radioButton3, radioButton4;
        EditText editTextTimerSettings;
        Button button15, button30, buttonStart, buttonStop;
        SettingsAdapter adapter;
        Toast toast;
        ScheduledExecutorService scheduledExecutorService;

        private MainVH(View itemView, SettingsAdapter myAdapter, int viewType) {
            super(itemView);
            if(viewType == 1){
                    textView = (TextView) itemView.findViewById(R.id.Bing_header);
                    editTextTimerSettings = (EditText) itemView.findViewById(R.id.editTextTimer);
                    button15 = (Button) itemView.findViewById(R.id.button15);
                    button30 = (Button) itemView.findViewById(R.id.button30);
                    buttonStart = (Button) itemView.findViewById(R.id.buttonStart);
                    buttonStop = (Button) itemView.findViewById(R.id.buttonStop);

                button15.setOnClickListener(this);
                button30.setOnClickListener(this);
                buttonStart.setOnClickListener(this);
                buttonStop.setOnClickListener(this);

            }else {
                textView = (TextView) itemView.findViewById(R.id.Bing_header);
                radioButton = (RadioButton) itemView.findViewById(R.id.radio);
                radioButton2 = (RadioButton) itemView.findViewById(R.id.radio2);
                radioButton3 = (RadioButton) itemView.findViewById(R.id.radio3);
                radioButton4 = (RadioButton) itemView.findViewById(R.id.radio4);
                imageView = (ImageView) itemView.findViewById(R.id.arrow);
                adapter = myAdapter;
                itemView.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            int delayBy;

            switch (v.getId()){
                case R.id.button15:
                    if(editTextTimerSettings.getText().toString().compareTo("") == 0) delayBy = 0;
                    else delayBy = Integer.parseInt(editTextTimerSettings.getText().toString());
                    editTextTimerSettings.setText(String.valueOf(delayBy+15));
                    return;
                case R.id.button30:
                    if(editTextTimerSettings.getText().toString().compareTo("") == 0) delayBy = 0;
                    else delayBy = Integer.parseInt(editTextTimerSettings.getText().toString());
                    editTextTimerSettings.setText(String.valueOf(delayBy+30));
                    return;
                case R.id.buttonStart:
                    scheduledExecutorService = timer(buttonStart, editTextTimerSettings);
                    return;
                case R.id.buttonStop:
                    scheduledExecutorService.shutdownNow();
                    buttonStart.setClickable(true);
                    buttonStart.setPressed(false);
                    Toast.makeText(context, "Timer for " + editTextTimerSettings.getText().toString()+ " minutes has stopped", Toast.LENGTH_LONG).show();
                    return;
            }


            if (isFooter()){
                return;
            }
            if (isHeader()) {
                adapter.toggleSectionExpanded(getRelativePosition().section());
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(v.getContext(), getRelativePosition().toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void saveColors(String col, int color){
        SharedPreferences sP = context.getSharedPreferences("Colors", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString(col, String.valueOf(color));
        sEditor.apply();
    }

    protected void saveForReset(boolean bool){
        SharedPreferences sP = context.getSharedPreferences("resetState", MODE_PRIVATE);
        sP.edit().putBoolean("colorReset", bool).apply();
    }

    private void radioListener(final RadioButton radioButton, final String saveLocation, final int color){
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveColors(saveLocation, color);
                saveForReset(true);
            }
        });
    }

    private void preserveRadioCheckState(int section, final MainVH holder){

        if(section == 0 || section == 1) {
            String saveLocation[] = {"ColorSpace0", "ColorSpace1"};
            SharedPreferences colors = context.getSharedPreferences("Colors", MODE_PRIVATE);
            String ret = colors.getString(saveLocation[section], "error");

            if (ret.compareTo("error") != 0) {
                int colorInt = Integer.parseInt(ret);
                switch (section) {
                    case 0:
                        if (colorInt == Color.RED) holder.radioButton.setChecked(true);
                        else if (colorInt == Color.BLUE) holder.radioButton2.setChecked(true);
                        else if (colorInt == Color.BLACK) holder.radioButton3.setChecked(true);
                        else if (colorInt == Color.LTGRAY) holder.radioButton4.setChecked(true);
                        break;
                    case 1:
                        if (colorInt == Color.RED) holder.radioButton.setChecked(true);
                        else if (colorInt == Color.BLUE) holder.radioButton2.setChecked(true);
                        else if (colorInt == Color.BLACK) holder.radioButton3.setChecked(true);
                        else if (colorInt == Color.LTGRAY) holder.radioButton4.setChecked(true);
                        break;
                    default:
                        Toast.makeText(context, "Radio Button State Preservation Error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private ScheduledExecutorService timer(final Button buttonStart, final EditText editText){
        buttonStart.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonStart.setPressed(true);
            }
        }, 100);
        int temp = 0;
        if(editText.getText().toString().compareTo("") != 0) temp = Integer.parseInt(editText.getText().toString());
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        final int minutes = temp;
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "It has been "+minutes+" minutes", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.setAction("android.intent.action.VIEW");
                        intent.setComponent(ComponentName.unflattenFromString("com.example.roy.navapp/com.example.roy.navapp.MainActivity"));
                        activity.startActivity(intent);

                        buttonStart.setClickable(true);
                        buttonStart.setPressed(false);
                    }
                });

            }
        },minutes,TimeUnit.MINUTES);
        return scheduledExecutorService;
    }

}
