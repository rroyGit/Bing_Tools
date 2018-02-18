package com.rroycsdev.bingtools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.andexert.library.RippleView;

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
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String titleString = title.get(position);
        String descString = descriptions.get(position);
        final String[] tempBody = new String[1];
        final TextView titleView, descView, dialogBody;
        CheckBox checkBox = null;
        RippleView rippleView = null;

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        final View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_custom,parent, false);
        alertDialog.setView(dialogLayout);
        dialogBody = (TextView )dialogLayout.findViewById(R.id.body);

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });

        if(convertView == null){

            if(position == 404){
                convertView = LayoutInflater.from(context).inflate(R.layout.row2_about, parent,false);
                titleView = (TextView) convertView.findViewById(R.id.title);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                descView = (TextView) convertView.findViewById(R.id.desc);
                rippleView = (RippleView) convertView.findViewById(R.id.rippleViewRow2);

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
                rippleView = (RippleView) convertView.findViewById(R.id.rippleViewRow1);

                titleView.setText(titleString);
                descView.setText(descString);
            }
        }

        assert rippleView != null;
        final CheckBox finalCheckBox = checkBox;
        if(rippleView != null) {
            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View rippleView) {
                    switch (position) {
                        case 0:
                            tempBody[0] = "Click OK to visit https://www.bing.com/";
                            dialogBody.setText(tempBody[0]);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Uri uri = Uri.parse("https://www.bing.com/");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            context.startActivity(intent);
                                        }
                                    }).start();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.show();
                                }
                            }, 500);
                            break;
                        case 1:
                            tempBody[0] = "Click OK to visit https://binghamton.sodexomyway.com/";
                            dialogBody.setText(tempBody[0]);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Uri uri = Uri.parse("https://binghamton.sodexomyway.com/");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            context.startActivity(intent);
                                        }
                                    }).start();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.show();
                                }
                            }, 500);

                            break;
                        case 2:
                            tempBody[0] = "Click OK to visit https://coinmarketcap.com/";
                            dialogBody.setText(tempBody[0]);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Uri uri = Uri.parse("https://coinmarketcap.com/");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            context.startActivity(intent);
                                        }
                                    }).start();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.show();
                                }
                            }, 500);
                            break;
                        case 3:
                            tempBody[0] = "Click OK to visit https://material.io/icons/";
                            dialogBody.setText(tempBody[0]);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Uri uri = Uri.parse("https://material.io/icons/");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            context.startActivity(intent);
                                        }
                                    }).start();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.show();
                                }
                            }, 500);
                            break;

                        default:
                    }
                }
            });
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
