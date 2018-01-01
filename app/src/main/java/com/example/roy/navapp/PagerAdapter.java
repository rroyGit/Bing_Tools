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
                HinmanDining hinmanDining = new HinmanDining();
                return  hinmanDining;
            case 1:
                C4Dining c4Dining = new C4Dining();
                return c4Dining;
            case 2:
                AppalachianDining appalachianDining = new AppalachianDining();
                return appalachianDining;
            case 3:
                CIWDining ciwDining = new CIWDining();
                return ciwDining;
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
