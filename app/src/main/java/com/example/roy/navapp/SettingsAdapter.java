package com.example.roy.navapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SettingsAdapter extends SectionedRecyclerViewAdapter<SettingsAdapter.MainVH> {

    private List<String> titles;
    private HashMap<String,List<String>> colorsMap;
    private Context context;


    public SettingsAdapter(HashMap<String,List<String>> colorsMap, List<String> titles, Context context) {
        this.context = context;
        this.colorsMap = colorsMap;
        this.titles = titles;
    }

    @Override
    public int getSectionCount() {
        return colorsMap.get(titles.get(0)).size();
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

               radioListener(holder.radioButton,"ColorSpace0", Color.RED);
               radioListener(holder.radioButton2,"ColorSpace0", Color.BLUE);

               break;
           case 1:
               holder.radioButton.setText(colorsMap.get(titles.get(1)).get(0));
               holder.radioButton2.setText(colorsMap.get(titles.get(1)).get(1));

               radioListener(holder.radioButton,"ColorSpace1", Color.BLACK);
               radioListener(holder.radioButton2,"ColorSpace1", Color.LTGRAY);

               break;
       }
        //preserve saved state
        preseveRadioCheckState(section, holder);
    }
    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        if (section == 1) {
            // VIEW_TYPE_FOOTER is -3, VIEW_TYPE_HEADER is -2, VIEW_TYPE_ITEM is -1.
            // You can return 0 or greater.
            return 0;
        }
        return super.getItemViewType(section, relativePosition, absolutePosition);
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
            default:
                layout = R.layout.body_list;
                break;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MainVH(v, this);
    }


    public class MainVH extends SectionedViewHolder implements View.OnClickListener{
        TextView textView;
        ImageView imageView;
        RadioButton radioButton;
        RadioButton radioButton2;
        SettingsAdapter adapter;
        Toast toast;

        private MainVH(View itemView, SettingsAdapter myAdapter) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.Bing_header);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio);
            radioButton2 = (RadioButton) itemView.findViewById(R.id.radio2);
            adapter = myAdapter;
            imageView = (ImageView) itemView.findViewById(R.id.arrow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

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

    void preseveRadioCheckState(int section, final MainVH holder){
        String saveLocation[] = {"ColorSpace0", "ColorSpace1"};
        SharedPreferences colors = context.getSharedPreferences("Colors", MODE_PRIVATE);
        String ret = colors.getString(saveLocation[section], "error");

        if (ret.compareTo("error") != 0) {
            int colorInt = Integer.parseInt(ret);
            switch (section) {
                case 0:
                    if (colorInt == Color.RED) {
                        holder.radioButton.setChecked(true);
                    } else if (colorInt == Color.BLUE) {
                        holder.radioButton2.setChecked(true);
                    }
                    break;
                case 1:
                    if (colorInt == Color.BLACK) {
                        holder.radioButton.setChecked(true);
                    } else if (colorInt == Color.LTGRAY) {
                        holder.radioButton2.setChecked(true);
                    }
                    break;
                default:
                    Toast.makeText(context, "Radio Button State Preservation Error", Toast.LENGTH_LONG).show();
            }
        }
    }

}
