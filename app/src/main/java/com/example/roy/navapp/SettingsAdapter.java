package com.example.roy.navapp;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private final static int TIMER_LAYOUT = 2;
    private final static int RADIO_LAYOUT = 0;
    private final static int RADIO_LAYOUT_2 = 1;
    Activity activity;
    Button button;
    Holder holder;

    public SettingsAdapter(HashMap<String,List<String>> colorsMap, List<String> titles, Context context, Activity activity) {
        this.context = context;
        this.colorsMap = colorsMap;
        this.titles = titles;
        this.activity = activity;
        holder = new Holder();
    }

    public class Holder{
        ScheduledExecutorService scheduledExecutorService;
        Holder(){

        }
        private void set(ScheduledExecutorService scheduledExecutorService){
            this.scheduledExecutorService = scheduledExecutorService;
        }
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
                button = holder.buttonStart;
                if(getTimerStatus()) {
                    holder.buttonStart.setClickable(false);
                    holder.buttonStart.setPressed(true);
                }else {
                    holder.buttonStart.setClickable(true);
                    holder.buttonStart.setPressed(false);
                }
                break;
       }


    }


    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        super.getItemViewType(section, relativePosition, absolutePosition);
        // VIEW_TYPE_FOOTER is -3, VIEW_TYPE_HEADER is -2, VIEW_TYPE_ITEM is -1.
        // You can return 0 or greater.
        switch(section){
            case 0:
                return RADIO_LAYOUT;
            case 1:
                return RADIO_LAYOUT_2;
            case 2:
                return TIMER_LAYOUT;
            default:
                return 404;
        }
    }

    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout = 0;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                layout = R.layout.header_list;
                break;
            case RADIO_LAYOUT:
                layout = R.layout.radio_list_1;
                break;
            case RADIO_LAYOUT_2:
                layout = R.layout.radio_list_2;
                break;
            case TIMER_LAYOUT:
                layout = R.layout.timer_settings;
                break;
            default:
                Log.e("layout", " "+viewType);
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


        private MainVH(View itemView, SettingsAdapter myAdapter, int viewType) {
            super(itemView);
            if(viewType == 2){
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
                button = buttonStart;

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
                    editTextTimerSettings.clearFocus();
                    if(editTextTimerSettings.getText().toString().compareTo("") != 0){
                        if(Integer.parseInt(editTextTimerSettings.getText().toString()) > 0){
                            holder.set(timer(buttonStart, editTextTimerSettings));
                            Toast.makeText(context, "Timer for " +editTextTimerSettings.getText().toString()+" minutes has started", Toast.LENGTH_SHORT).show();
                            saveTimerStatus(true);
                            saveTimerValue(editTextTimerSettings.getText().toString());
                        }
                    }
                    return;
                case R.id.buttonStop:
                    editTextTimerSettings.clearFocus();
                    if (holder.scheduledExecutorService != null && getTimerStatus()) {
                        holder.scheduledExecutorService.shutdownNow();
                        buttonStart.setClickable(true);
                        buttonStart.setPressed(false);
                        Toast.makeText(context, "Timer for " + getTimerValue() + " minutes has stopped", Toast.LENGTH_SHORT).show();
                        saveTimerStatus(false);
                    }
                    editTextTimerSettings.getText().clear();
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

    public void saveTimerStatus(boolean bool){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", bool);
        editor.apply();
    }

    public void saveTimerValue(String string){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("value", string);
        editor.apply();
    }

    public boolean getTimerStatus(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        return sharedPreferences.getBoolean("status", false);
    }

    public String getTimerValue(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        return sharedPreferences.getString("value", "404");
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
        }, 75);
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
                        Toast.makeText(context, "It has been "+minutes+" minutes", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.setAction("android.intent.action.VIEW");
                        intent.setComponent(ComponentName.unflattenFromString("com.example.roy.navapp/com.example.roy.navapp.MainActivity"));
                        activity.startActivity(intent);

                        buttonStart.setClickable(true);
                        buttonStart.setPressed(false);
                        saveTimerStatus(false);
                    }
                });

            }
        },minutes,TimeUnit.MINUTES);
        return scheduledExecutorService;
    }

}
