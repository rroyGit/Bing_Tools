package com.rroycsdev.bingtools;

import android.content.Context;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;

import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private SparseBooleanArray collapseBreakfast;
    private SparseBooleanArray collapseLunch;
    private SparseBooleanArray collapseDinner;
    private RecyclerView recyclerView;
    private final String AUTO_POSITION_SWITCH = "auto_position_switch";

    MenuAdapter(List<ListItem> listItems, Context context, RecyclerView recyclerView) {
        this.listItems = listItems;
        this.context = context;
        this.collapseBreakfast = new SparseBooleanArray();
        this.collapseLunch = new SparseBooleanArray();
        this.collapseDinner = new SparseBooleanArray();
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
       return new ViewHolder(v, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ExpandableTextView mealB;
        ExpandableTextView mealL;
        ExpandableTextView mealD;
        ImageView weekdayImage;
        LinearLayout linearLayout;


        ViewHolder(final View itemView, final MenuAdapter adapter) {
            super(itemView);
            mealB = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            mealL = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view2);
            mealD = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view3);
            weekdayImage = (ImageView) itemView.findViewById(R.id.dayImage);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.cardLayout);

            mealB.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    final int currentPosition = getAdapterPosition();
                    if(isExpanded){
                        adapter.collapseBreakfast.put(currentPosition, false);
                        //auto-position textView
                        if(getAutoPostionStatus()) {
                            Rect rect = new Rect();
                            if (mealB.getGlobalVisibleRect(rect)
                                    && mealB.getHeight() == rect.height()
                                    && mealB.getWidth() == rect.width()) {
                                //fully visible
                            } else {
                                //not fully visible
                                recyclerView.smoothScrollToPosition(getAdapterPosition());
                            }
                        }
                    }else{
                        adapter.collapseBreakfast.put(currentPosition, true);
                    }
                }
            });

            mealL.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    final int currentPosition = getAdapterPosition();
                    if(isExpanded){
                        adapter.collapseLunch.put(currentPosition, false);
                        //auto-position textView
                        if(getAutoPostionStatus()) {
                            Rect rect = new Rect();
                            if (mealL.getGlobalVisibleRect(rect)
                                    && mealL.getHeight() == rect.height()
                                    && mealL.getWidth() == rect.width()) {
                                //fully visible
                            } else {
                                //not fully visible
                                recyclerView.smoothScrollToPosition(getAdapterPosition());
                            }
                        }
                    }else{
                        adapter.collapseLunch.put(currentPosition, true);
                    }
                }
            });

            mealD.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    final int currentPosition = getAdapterPosition();
                    if(isExpanded){
                        adapter.collapseDinner.put(currentPosition, false);
                        //auto-position textView
                        if(getAutoPostionStatus()) {
                            Rect rect = new Rect();
                            if (mealD.findViewById(R.id.expandable_text).getGlobalVisibleRect(rect)
                                    && mealD.findViewById(R.id.expandable_text).getHeight() == rect.height()
                                    && mealD.findViewById(R.id.expandable_text).getWidth() == rect.width()) {
                                //fully visible
                            } else {
                                //not fully visible
                                recyclerView.smoothScrollToPosition(getAdapterPosition());
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Rect rect = new Rect();
                                        if (mealD.findViewById(R.id.expandable_text).getGlobalVisibleRect(rect) && rect.height() == mealD.findViewById(R.id.expandable_text).getHeight()
                                                && rect.width() == mealD.findViewById(R.id.expandable_text).getWidth()) {
                                            //fully visible
                                        } else {
                                            //not fully visible
                                            recyclerView.smoothScrollToPosition(getAdapterPosition());
                                        }
                                    }
                                }, 500);
                            }
                        }
                    }else{
                        adapter.collapseDinner.put(currentPosition, true);
                    }
                }
            });
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        String color;
        holder.weekdayImage.setImageResource(listItem.getResInt());
        holder.mealB.setText(listItem.getMealB(), collapseBreakfast, position);
        holder.mealL.setText(listItem.getMealL(), collapseLunch, position);
        holder.mealD.setText(listItem.getMealD(), collapseDinner, position);

        //default colors
        holder.linearLayout.setBackgroundColor(Color.DKGRAY);
        holder.weekdayImage.setBackgroundColor(Color.DKGRAY);
        holder.weekdayImage.setColorFilter(Color.WHITE);
        changeHeaderColors(holder, Color.DKGRAY);
        changeExpandViewColors(holder, Color.DKGRAY, true);


        //ColorSpace 0 --> Color from first Radio Group
        //ColorSpace 1 --> Color from second Radio Group
        //ColorSpace 2 --> Color from switch button
        //ColorSpace 3 --> Color copy of switch button
        //switch saves ColorSpace 2 and 3


        color = getSavedColors("ColorSpace3", context);
        if(!color.equals("error")){
            holder.linearLayout.setBackgroundColor(Integer.parseInt(color));
            holder.weekdayImage.setBackgroundColor(Integer.parseInt(color));
            if(isColorDark(Integer.parseInt(color))) {
                holder.weekdayImage.setColorFilter(Color.WHITE);
            }else holder.weekdayImage.setColorFilter(Color.BLACK);
            changeHeaderColors(holder, Integer.parseInt(color));
            changeExpandViewColors(holder, Integer.parseInt(color), true);
        }


        if(getSavedColors("ColorSpace0", context).compareTo("error") != 0 &&
                getSavedColors("ColorSpace1", context).compareTo("error") != 0){

            if(getSavedColors("ColorSpace2", context).compareTo("error") !=0 ) {
                holder.linearLayout.setBackgroundColor(Integer.parseInt(getSavedColors("ColorSpace2", context)));
                holder.weekdayImage.setBackgroundColor(Integer.parseInt(getSavedColors("ColorSpace2", context)));
                if (isColorDark(Integer.parseInt(getSavedColors("ColorSpace2", context)))) {
                    holder.weekdayImage.setColorFilter(Color.WHITE);
                } else holder.weekdayImage.setColorFilter(Color.BLACK);
            }

            changeHeaderColors(holder, Integer.parseInt(getSavedColors("ColorSpace0", context)));
            changeExpandViewColors(holder, Integer.parseInt(getSavedColors("ColorSpace1", context)), true);
        }else{
            if(getSavedColors("ColorSpace2", context).compareTo("error") !=0 ) {
                holder.linearLayout.setBackgroundColor(Integer.parseInt(getSavedColors("ColorSpace2", context)));
                holder.weekdayImage.setBackgroundColor(Integer.parseInt(getSavedColors("ColorSpace2", context)));

                if (isColorDark(Integer.parseInt(getSavedColors("ColorSpace2", context)))) {
                    holder.weekdayImage.setColorFilter(Color.WHITE);
                } else holder.weekdayImage.setColorFilter(Color.BLACK);
            }

            color = getSavedColors("ColorSpace0", context);
            if (!color.equals("error")) {
                changeHeaderColors(holder, Integer.parseInt(color));
                if (getSavedColors("ColorSpace2", context).compareTo("error") != 0) {
                    changeExpandViewColors(holder, Integer.parseInt(getSavedColors("ColorSpace2", context)), true);
                }
            }

            color = getSavedColors("ColorSpace1", context);
            if (!color.equals("error")) {
                changeExpandViewColors(holder, Integer.parseInt(color), true);
                if (getSavedColors("ColorSpace2", context).compareTo("error") != 0) {
                    changeHeaderColors(holder, Integer.parseInt(getSavedColors("ColorSpace2", context)));
                }
            }

        }

    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public static String getSavedColors(String key, Context context){
        SharedPreferences sP = context.getSharedPreferences("Colors", MODE_PRIVATE);
        return sP.getString(key, "error");
    }

    public boolean getAutoPostionStatus(){
        SharedPreferences sP = context.getSharedPreferences("switch", MODE_PRIVATE);
        return sP.getBoolean(AUTO_POSITION_SWITCH, true);
    }

    private void changeExpandViewColors(ViewHolder v, int color, boolean changeArrowColor){
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
            if(changeArrowColor) {
                imageButton.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
                imageButton1.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
                imageButton2.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            }

        }else{
            textView.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
            if(changeArrowColor) {
                imageButton.setColorFilter(Color.argb(255, 0, 0, 1)); // Black? Tint
                imageButton1.setColorFilter(Color.argb(255, 0, 0, 1)); // Black? Tint
                imageButton2.setColorFilter(Color.argb(255, 0, 0, 1)); // Black? Tint
            }
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
