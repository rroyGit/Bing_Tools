package com.example.roy.navapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by rroy6 on 12/20/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numTabs;

    public PagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HinmanDining();
            case 1:
                return new C4Dining();
            case 2:
                return new AppalachianDining();
            case 3:
                return new CIWDining();
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
