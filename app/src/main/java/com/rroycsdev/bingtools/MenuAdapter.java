package com.rroycsdev.bingtools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.Serializable;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> implements Serializable {

    private List<ListItem> listItems;
    private Context context;

    public MenuAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);

        holder.mealB.setText(listItem.getMealB());
        holder.mealL.setText(listItem.getMealL());
        holder.mealD.setText(listItem.getMealD());
        holder.weekdayImage.setImageResource(listItem.getResInt());


        String color = getSavedColors("ColorSpace3", context);
        if(!color.equals("error")){
            changeHeaderColors(holder, Integer.parseInt(color));
            changeExpandTextColors(holder, Integer.parseInt(color));
            return;
        }
        color = getSavedColors("ColorSpace1", context);
        if(!color.equals("error")){
            changeExpandTextColors(holder, Integer.parseInt(color));
        }
        color = getSavedColors("ColorSpace0", context);
        if(!color.equals("error")){
            changeHeaderColors(holder, Integer.parseInt(color));
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ExpandableTextView mealB;
        ExpandableTextView mealL;
        ExpandableTextView mealD;
        ImageView weekdayImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mealB = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            mealL = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view2);
            mealD = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view3);
            weekdayImage = (ImageView) itemView.findViewById(R.id.dayImage);
        }
    }

    public static String getSavedColors(String key, Context context){
        SharedPreferences sP = context.getSharedPreferences("Colors", MODE_PRIVATE);
        return sP.getString(key, "error");
    }
    private void changeExpandTextColors(ViewHolder v, int color){
        ExpandableTextView expandableTextView = v.mealB;
        ExpandableTextView expandableTextView2 = v.mealL;
        ExpandableTextView expandableTextView3 = v.mealD;

        expandableTextView.setBackgroundColor(color);
        expandableTextView2.setBackgroundColor(color);
        expandableTextView3.setBackgroundColor(color);

        TextView textView = (TextView) expandableTextView.findViewById(R.id.expandable_text);
        TextView textView2 = (TextView) expandableTextView2.findViewById(R.id.expandable_text);
        TextView textView3 = (TextView) expandableTextView3.findViewById(R.id.expandable_text);
        ImageButton imageButton = expandableTextView.findViewById(R.id.expand_collapse);
        ImageButton imageButton1 = expandableTextView2.findViewById(R.id.expand_collapse);
        ImageButton imageButton2 = expandableTextView3.findViewById(R.id.expand_collapse);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_focused},
        };

        int[] colors = new int[] {
                Color.BLACK,
        };
        ColorStateList myList = new ColorStateList(states, colors);

        if(isColorDark(color)){
            textView.setTextColor(Color.WHITE);
            textView2.setTextColor(Color.WHITE);
            textView3.setTextColor(Color.WHITE);
        }else{
            textView.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
            imageButton.setImageTintList(myList);
            imageButton1.setImageTintList(myList);
            imageButton2.setImageTintList(myList);
        }
    }

    private void changeHeaderColors(ViewHolder v, int color){
        TextView textView = (TextView) v.itemView.findViewById(R.id.mealTime);
        TextView textView2 = (TextView) v.itemView.findViewById(R.id.mealTime2);
        TextView textView3 = (TextView) v.itemView.findViewById(R.id.mealTime3);

        textView.setBackgroundColor(color);
        textView2.setBackgroundColor(color);
        textView3.setBackgroundColor(color);

        if(isColorDark(color)){
            textView.setTextColor(Color.WHITE);
            textView2.setTextColor(Color.WHITE);
            textView3.setTextColor(Color.WHITE);
        }else{
            textView.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
        }
    }

    //credit: adboco from StackOverflow
    private boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        return !(darkness < 0.5);
    }
}
