package com.rroycsdev.bingtools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by rroy6 on 12/20/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numTabs;
    private FragmentManager fragmentManager;
    private HinmanDining hinmanDining = null;
    private C4Dining c4Dining = null;
    private AppalachianDining appalachianDining = null;
    private CIWDining ciwDining = null;

    public PagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        fragmentManager = fm;
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                hinmanDining =  new HinmanDining();
                return hinmanDining;
            case 1:
                c4Dining = new C4Dining();
                return c4Dining;
            case 2:
                appalachianDining = new AppalachianDining();
                return appalachianDining;
            case 3:
                ciwDining = new CIWDining();
                return ciwDining;
            default:
                return  null;
        }
    }

    public HinmanDining getHinmanRefs(){
        return hinmanDining;
    }

    public C4Dining getC4Refs(){
        return c4Dining;
    }

    public CIWDining getCIWRefs(){
        return ciwDining;
    }
    public AppalachianDining getAppRefs(){
        return appalachianDining;
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
