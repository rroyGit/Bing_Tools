package com.rroycsdev.bingtools;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;
import com.andexert.library.RippleView;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import yuku.ambilwarna.AmbilWarnaDialog;
import static android.content.Context.MODE_PRIVATE;

public class SettingsAdapter extends SectionedRecyclerViewAdapter<SettingsAdapter.MainVH> {

    private List<String> titles;
    private HashMap<String, List<String>> colorsMap;
    private Context context;
    private final static int RADIO_LAYOUT = 0;
    private final static int RADIO_LAYOUT_2 = 1;
    private final static int SAME_COLORS_LAYOUT = 2;
    private Settings settingsObject;
    Button button;
    private Switch colorSwitch;


    public SettingsAdapter(HashMap<String, List<String>> colorsMap, List<String> titles, Context context, Settings settings) {
        this.context = context;
        this.colorsMap = colorsMap;
        this.titles = titles;
        this.settingsObject = settings;
    }

    @Override
    public int getSectionCount() {
        return titles.size();
    }

    @Override
    public int getItemCount(int section) {
        return 1;
    }

    public Switch getColorSwitch(){
        return colorSwitch;
    }

    @Override
    public void onBindHeaderViewHolder(final MainVH holder, final int section, final boolean expanded) {

        switch (section) {
            case RADIO_LAYOUT:
                holder.textView.setText(titles.get(0));
                break;
            case RADIO_LAYOUT_2:
                holder.textView.setText(titles.get(1));
                break;
            case SAME_COLORS_LAYOUT:
                holder.textView.setText(titles.get(2));
                break;
        }

        holder.rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionExpanded(section);
            }
        });

        holder.imageView.setImageResource(isSectionExpanded(section) ? R.drawable.arrow_up : R.drawable.arrow_down);
    }

    @Override
    public void onBindFooterViewHolder(MainVH holder, int section) {

    }

    @Override
    public void expandSection(int section) {
        super.expandSection(section);
    }

    @Override
    public void toggleSectionExpanded(int section) {
        super.toggleSectionExpanded(section);
    }

    @Override
    public void onBindViewHolder(final MainVH holder, int section, int relativePosition, int absolutePosition) {
        switch (section) {
            case RADIO_LAYOUT:
                holder.radioButton.setText(colorsMap.get(titles.get(0)).get(0));
                holder.radioButton2.setText(colorsMap.get(titles.get(0)).get(1));
                holder.radioButton3.setText(colorsMap.get(titles.get(0)).get(2));
                holder.radioButton4.setText(colorsMap.get(titles.get(0)).get(3));

                radioListener(holder.radioButton, "ColorSpace0", Color.RED);
                radioListener(holder.radioButton2, "ColorSpace0", Color.BLUE);
                radioListener(holder.radioButton3, "ColorSpace0", Color.BLACK);
                radioListener(holder.radioButton4, "ColorSpace0", Color.LTGRAY);
                preserveRadioCheckState(section, holder);
                break;
            case RADIO_LAYOUT_2:
                holder.radioButton.setText(colorsMap.get(titles.get(1)).get(0));
                holder.radioButton2.setText(colorsMap.get(titles.get(1)).get(1));
                holder.radioButton3.setText(colorsMap.get(titles.get(1)).get(2));
                holder.radioButton4.setText(colorsMap.get(titles.get(1)).get(3));

                radioListener(holder.radioButton, "ColorSpace1", Color.RED);
                radioListener(holder.radioButton2, "ColorSpace1", Color.BLUE);
                radioListener(holder.radioButton3, "ColorSpace1", Color.BLACK);
                radioListener(holder.radioButton4, "ColorSpace1", Color.LTGRAY);
                preserveRadioCheckState(section, holder);
                break;
            case SAME_COLORS_LAYOUT:

                break;
        }
    }

    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        super.getItemViewType(section, relativePosition, absolutePosition);
        // VIEW_TYPE_FOOTER is -3, VIEW_TYPE_HEADER is -2, VIEW_TYPE_ITEM is -1.
        // You can return 0 or greater.
        switch (section) {
            case 0:
                return RADIO_LAYOUT;
            case 1:
                return RADIO_LAYOUT_2;
            case 2:
                return SAME_COLORS_LAYOUT;
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
            case SAME_COLORS_LAYOUT:
                layout = R.layout.assign_same_colors;
                break;
            default:
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MainVH(v, this, viewType);
    }

    public class MainVH extends SectionedViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;
        RadioButton radioButton, radioButton2, radioButton3, radioButton4;
        EditText editTextTimerSettings;
        Button colorPicker;
        SettingsAdapter adapter;
        RippleView rippleView;
        Toast toast;
        int viewType;
        boolean cancel = false;


        private MainVH(View itemView, SettingsAdapter myAdapter, int viewType) {
            super(itemView);
            this.viewType = viewType;


            switch (viewType){
                case VIEW_TYPE_HEADER:
                    rippleView = itemView.findViewById(R.id.rippleSettingsHeader);
                    textView = (TextView) itemView.findViewById(R.id.Bing_header);
                    imageView = (ImageView) itemView.findViewById(R.id.arrow);
                    break;
                case RADIO_LAYOUT:
                case RADIO_LAYOUT_2:
                    colorPicker = (Button) itemView.findViewById(R.id.colorPicker);
                    radioButton = (RadioButton) itemView.findViewById(R.id.radio);
                    radioButton2 = (RadioButton) itemView.findViewById(R.id.radio2);
                    radioButton3 = (RadioButton) itemView.findViewById(R.id.radio3);
                    radioButton4 = (RadioButton) itemView.findViewById(R.id.radio4);
                    if (colorPicker != null) colorPicker.setOnClickListener(this);
                    break;

                case SAME_COLORS_LAYOUT:
                    colorSwitch = itemView.findViewById(R.id.colorSwitch);
                    if(getSwitchStatus()) colorSwitch.setChecked(true);
                    else colorSwitch.setChecked(false);

                    colorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                            if(isChecked){
                                Toast.makeText(context, "Pick a color", Toast.LENGTH_SHORT).show();
                                final AmbilWarnaDialog dialog = new AmbilWarnaDialog(settingsObject, Integer.parseInt("ADD8E6", 16), new AmbilWarnaDialog.OnAmbilWarnaListener() {

                                    @Override
                                    public void onCancel(AmbilWarnaDialog dialog) {
                                        cancel = true;
                                        colorSwitch.setChecked(false);
                                    }

                                    @Override
                                    public void onOk(AmbilWarnaDialog dialog, int color) {
                                        removeSavedColors("ColorSpace0");
                                        removeSavedColors("ColorSpace1");
                                        settingsObject.recyclerView.setAdapter(settingsObject.settingsAdapter);
                                        saveColors("ColorSpace3", color);
                                        saveColors("ColorSpace2", color);
                                        saveForReset(true);
                                        saveSwitchStatus(true);
                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.show();
                                    }
                                }, 500);

                            }else {
                                if(!cancel) {
                                    if (getSavedColor("ColorSpace3").compareTo("error") == 0) {
                                        saveForReset(true);
                                    } else saveForReset(false);

                                    saveSwitchStatus(false);
                                    //Don't display toast if toast for reset colors is active
                                    if (settingsObject.menu != null && !settingsObject.menu.getItem(0).isChecked()) {
                                        Toast.makeText(context, "Colors Reverted", Toast.LENGTH_SHORT).show();
                                    }
                                    saveColors("ColorSpace3", 333333);
                                    settingsObject.recyclerView.setAdapter(settingsObject.settingsAdapter);
                                }
                            }
                        }
                    });
                    break;
            }
            adapter = myAdapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.editTextTimer:
                        editTextTimerSettings.setFocusable(true);
                return;

                case R.id.colorPicker:
                    colorPickerUpper(radioButton, radioButton2, radioButton3, radioButton4, viewType);
                    return;
            }

            if (isFooter()) {
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

    /*
    public void saveTimerStatus(boolean bool) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", bool);
        editor.apply();
    }
    public void saveTimerValue(String string) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("value", string);
        editor.apply();
    }
    public boolean getTimerStatus() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        return sharedPreferences.getBoolean("status", false);
    }
    public String getTimerValue() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Timer", MODE_PRIVATE);
        return sharedPreferences.getString("value", "404");
    }
    */

    private void saveColors(String col, int color) {
        SharedPreferences sP = context.getSharedPreferences("Colors", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putString(col, String.valueOf(color));
        sEditor.apply();
    }

    private String getSavedColor(String colorSpace){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Colors", MODE_PRIVATE);
        return sharedPreferences.getString(colorSpace, "error");
    }

    private void removeSavedColors(String colorSpace){
        SharedPreferences sP = context.getSharedPreferences("Colors", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.remove(colorSpace);
        sEditor.apply();
    }

    protected void saveSwitchStatus(boolean checked){
        SharedPreferences sP = context.getSharedPreferences("switch", MODE_PRIVATE);
        SharedPreferences.Editor sEditor = sP.edit();
        sEditor.putBoolean("status", checked);
        sEditor.apply();
    }

    private boolean getSwitchStatus(){
        SharedPreferences sP = context.getSharedPreferences("switch", MODE_PRIVATE);
        return sP.getBoolean("status", false);
    }


    protected void saveForReset(boolean bool) {
        SharedPreferences sP = context.getSharedPreferences("resetState", MODE_PRIVATE);
        sP.edit().putBoolean("colorReset", bool).apply();
    }

    private void radioListener(final RadioButton radioButton, final String saveLocation, final int color) {
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveColors(saveLocation, color);
                saveForReset(true);
                if(colorSwitch != null) colorSwitch.setChecked(false);
                if(settingsObject.menu != null) settingsObject.menu.getItem(0).setChecked(false);
                saveSwitchStatus(false);
                removeSavedColors("ColorSpace3");

            }
        });
    }

    private void preserveRadioCheckState(int section, final MainVH holder) {

        if (section == 0 || section == 1) {
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
            }else{
                holder.radioButton.setChecked(false);
                holder.radioButton2.setChecked(false);
                holder.radioButton3.setChecked(false);
                holder.radioButton4.setChecked(false);
            }
        }
    }

    private void colorPickerUpper(final RadioButton radioButton, final RadioButton radioButton2, final RadioButton radioButton3, final RadioButton radioButton4, final int viewType) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(settingsObject, Integer.parseInt("ADD8E6", 16), new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if(viewType == RADIO_LAYOUT) {
                    saveColors("ColorSpace0", color);
                }else {
                    saveColors("ColorSpace1", color);
                }
                radioButton.setChecked(false);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                removeSavedColors("ColorSpace3");
                if(colorSwitch != null) colorSwitch.setChecked(false);
                saveSwitchStatus(false);
                saveForReset(true);
            }
        });
        dialog.show();
    }

    /*
    private void removeUSB() {
        final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
        IntentFilter intentFilter = new IntentFilter("com.android.example.USB_PERMISSION");

        final BroadcastReceiver usbReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if(ACTION_USB_PERMISSION.equals(action))
                {
                    // broadcast is like an interrupt and works asynchronously with the class, it must be synced just in case
                    synchronized(this)
                    {
                        if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                        {
                            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
                {
                    Toast.makeText(context, "USB detached", Toast.LENGTH_SHORT).show();
                }
            }
        };
        context.registerReceiver(usbReceiver,intentFilter);

        // check if there's a connected usb device
        if(usbManager.getDeviceList().isEmpty())
        {
            Toast.makeText(context, "No connected devices", Toast.LENGTH_SHORT).show();
            return;
        }

        // get the first (only) connected device
        final UsbDevice usbDevice = usbManager.getDeviceList().values().iterator().next();
        // user must approve of connection
        usbManager.requestPermission(usbDevice, pendingIntent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "About to close USB " + usbDevice.getManufacturerName() + usbDevice.getProductName(), Toast.LENGTH_LONG).show();

                UsbDeviceConnection usbDeviceConnection = usbManager.openDevice(usbDevice);
                usbDeviceConnection.close();
                context.unregisterReceiver(usbReceiver);

            }
        }, 2000);
    }

*/

}
