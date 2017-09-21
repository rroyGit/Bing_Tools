package com.example.roy.navapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SettingsAdapter extends BaseExpandableListAdapter {


    private List<String> titles;
    private HashMap<String,List<String>> radioButtons;
    private Context context;

    public SettingsAdapter(HashMap<String,List<String>> radioButtons, List<String> titles, Context context) {
        this.context = context;
        this.radioButtons = radioButtons;
        this.titles = titles;
    }

    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return radioButtons.get(titles.get(groupPosition)).size()-1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return radioButtons.get(titles.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) this.getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_item_s,null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.titleContent);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        @SuppressWarnings("unchecked")
        List<String> radioNames = (List<String>) this.getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_item_s,null);
        }

        final RadioGroup radioGroup = (RadioGroup) convertView.findViewById(R.id.radioGroup);
        final RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
        final RadioButton radioButton1 = (RadioButton) convertView.findViewById(R.id.radioButton1);

        radioButton.setText(radioNames.get(0));
        radioButton1.setText(radioNames.get(1));

        if(groupPosition == 0) {
            radioButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "RED, restart to see changes", Toast.LENGTH_SHORT).show();
                    saveColors("ColorSpace"+0, Color.RED);


                }
            });

            radioButton1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Blue, restart to see changes", Toast.LENGTH_SHORT).show();
                    saveColors("ColorSpace"+0, Color.BLUE);
                }
            });
        }else if(groupPosition == 1){
            radioButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Black, restart to see changes", Toast.LENGTH_SHORT).show();
                    saveColors("ColorSpace"+1, Color.BLACK);

                }
            });

            radioButton1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Light Gray, restart to see changes", Toast.LENGTH_SHORT).show();
                    saveColors("ColorSpace"+1, Color.LTGRAY);
                }
            });

        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void saveColors(String col, int color){
        SharedPreferences sP = context.getSharedPreferences("Colors", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString(col, String.valueOf(color));
        sEditor.apply();
    }



}
