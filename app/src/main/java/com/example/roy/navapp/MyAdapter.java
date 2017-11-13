package com.example.roy.navapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    public MyAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);

        holder.mealB.setText(listItem.getMealB());
        holder.mealL.setText(listItem.getMealL());
        holder.mealD.setText(listItem.getMealD());

        holder.weekdayImage.setImageResource(listItem.getresInt());

        String color = getSavedColors("ColorSpace1", context);
        if(!color.equals("error")){
            changeExpandTextColors(holder, Integer.parseInt(color));
        }
        color =  getSavedColors("ColorSpace0", context);
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
            mealB.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener(){

                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {

                }
            });
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

        if(color == Color.LTGRAY) {
            TextView textView = (TextView) expandableTextView.findViewById(R.id.expandable_text);
            TextView textView2 = (TextView) expandableTextView2.findViewById(R.id.expandable_text);
            TextView textView3 = (TextView) expandableTextView3.findViewById(R.id.expandable_text);
            textView.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
        }
    }

    private void changeHeaderColors(ViewHolder v, int color){
        TextView textView = (TextView) v.itemView.findViewById(R.id.mealTime);
        TextView textView2 = (TextView) v.itemView.findViewById(R.id.mealTime2);
        TextView textView3 = (TextView) v.itemView.findViewById(R.id.mealTime3);

        textView.setBackgroundColor(color);
        textView2.setBackgroundColor(color);
        textView3.setBackgroundColor(color);

        if (color == Color.BLUE){
            textView.setTextColor(Color.WHITE);
            textView2.setTextColor(Color.WHITE);
            textView3.setTextColor(Color.WHITE);
        }
    }

}
