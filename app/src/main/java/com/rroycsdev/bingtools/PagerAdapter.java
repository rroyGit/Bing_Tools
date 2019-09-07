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
    int numTabs;
    private FragmentManager fragmentManager;
    private SparseArray<Fragment> fragmentSparseArray = new SparseArray<>(4);
    private TabLayout tabLayout;

    public PagerAdapter(FragmentManager fm, TabLayout tabLayout, int numTabs) {
        super(fm);
        fragmentManager = fm;
        this.numTabs = numTabs;
        this.tabLayout = tabLayout;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentSparseArray.get(position);
        Log.d(TAG, "getItem: pos" + position);

        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new C4Dining(tabLayout);
                    break;
                case 1:
                    fragment = new AppalachianDining(tabLayout);
                    break;
                case 2:
                    fragment = new CIWDining(tabLayout);
                    break;
                case 3:
                    fragment = new HinmanDining(tabLayout);
                    break;
            }
            fragmentSparseArray.put(position, fragment);


            Log.d(TAG, "getItem: " + fragmentSparseArray.get(position).toString());
        }

        return fragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: " + position);
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
