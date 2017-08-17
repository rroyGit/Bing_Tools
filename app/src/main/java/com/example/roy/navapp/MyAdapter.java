package com.example.roy.navapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;


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
}
