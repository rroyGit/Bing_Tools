package com.example.roy.navapp;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.HashMap;
import java.util.List;


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
        return radioButtons.get(titles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return radioButtons.get(titles.get(groupPosition)).get(childPosition);
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
        String radioName = (String) this.getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_item_s,null);
        }
        RadioButton radioButton1 = (RadioButton) convertView.findViewById(R.id.radioButton);
        radioButton1.setText(radioName);

        radioButton1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                radioOnClick(v);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void radioOnClick(View v){
        RadioButton radioButton = (RadioButton) v.findViewById(R.id.radioButton);

        if(radioButton.getText().equals("Red")){
            Toast.makeText(context, "Red selected.", Toast.LENGTH_SHORT).show();
        }else if(radioButton.getText().equals("Blue")){
            Toast.makeText(context, "Blue selected.", Toast.LENGTH_SHORT).show();
        }






    }
}
