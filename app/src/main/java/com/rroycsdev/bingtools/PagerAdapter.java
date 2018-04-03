package com.rroycsdev.bingtools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numTabs;
    private FragmentManager fragmentManager;
    private HinmanDining hinmanDining = null;
    private C4Dining c4Dining = null;
    private AppalachianDining appalachianDining = null;
    private CIWDining ciwDining = null;

    SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
       Fragment fragment =  (Fragment) super.instantiateItem(container, position);
       fragmentSparseArray.put(position, fragment);
       return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentSparseArray.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragmentInstance(int position){
        return  fragmentSparseArray.get(position);
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
