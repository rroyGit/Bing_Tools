package com.example.roy.navapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rroy6 on 1/10/2018.
 */

public class AboutAdapter extends ArrayAdapter{

    ArrayList<String> descriptions;
    ArrayList<String> title;
    Context context;


    public AboutAdapter(@NonNull Context context, int resource, ArrayList<String> title, ArrayList<String> desc) {
        super(context, resource, title);
        this.context = context;
        this.title = title;
        this.descriptions = desc;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String titleString = title.get(position);
        String descString = descriptions.get(position);

        TextView titleView, descView;
        CheckBox checkBox;

        if(convertView == null){
            if(position == 1){
                convertView = LayoutInflater.from(context).inflate(R.layout.row2_about, parent,false);
                titleView = (TextView) convertView.findViewById(R.id.title);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                descView = (TextView) convertView.findViewById(R.id.desc);
                titleView.setText(titleString);
                descView.setText(descString);
               checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                       if (isChecked){
                           Intent intent = new Intent(context, Settings.class);
                           context.startActivity(intent);
                       }
                   }
               });

            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_about, parent,false);
                titleView = (TextView) convertView.findViewById(R.id.title);
                descView = (TextView) convertView.findViewById(R.id.desc);

                titleView.setText(titleString);
                descView.setText(descString);
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
