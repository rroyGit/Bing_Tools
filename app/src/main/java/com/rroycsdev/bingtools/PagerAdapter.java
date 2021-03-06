package com.rroycsdev.bingtools;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "PagerAdapter" ;
    private SparseArray<Fragment> fragmentSparseArray;
    private int numTabs;

    public PagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        fragmentSparseArray = new SparseArray<>(numTabs);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentSparseArray.get(position);

        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new C4Dining();
                    break;
                case 1:
                    fragment = new AppalachianDining();
                    break;
                case 2:
                    fragment = new CIWDining();
                    break;
                case 3:
                    fragment = new HinmanDining();
                    break;
            }
            fragmentSparseArray.put(position, fragment);
        }

        return fragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment =  (Fragment) super.instantiateItem(container, position);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    Fragment getFragmentInstance(int position){
        return fragmentSparseArray.get(position);
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
