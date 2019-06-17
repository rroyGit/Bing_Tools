package com.rroycsdev.bingtools;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.Objects;




public class BingDiningFragment extends Fragment implements HinmanDining.OnFragmentInteractionListener, C4Dining.OnFragmentInteractionListener,
AppalachianDining.OnFragmentInteractionListener, CIWDining.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    public BingDiningFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return false;
        }else if(id == R.id.refresh_Bing){
            if(CommonUtilities.getDeviceInternetStatus(Objects.requireNonNull(getContext())) == null){
                Toast.makeText(getContext(),"No Internet", Toast.LENGTH_SHORT).show();
                return false;
            }

            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    if(pagerAdapter != null && pagerAdapter.getHinmanRefs() != null) {
                        pagerAdapter.getHinmanRefs().setToolbarDate();
                        pagerAdapter.getHinmanRefs().hinman_hall.refreshData();
                    }
                    else {
                        pagerAdapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(pagerAdapter.getHinmanRefs() != null && pagerAdapter.getHinmanRefs().hinman_hall != null) {
                                    pagerAdapter.getHinmanRefs().setToolbarDate();
                                    pagerAdapter.getHinmanRefs().hinman_hall.refreshData();
                                }
                            }
                        },100);

                    }
                    return true;
                case 1:
                    if(pagerAdapter != null && pagerAdapter.getC4Refs() != null) {
                        pagerAdapter.getC4Refs().setToolbarDate();
                        pagerAdapter.getC4Refs().c4_hall.refreshData();
                    }
                    else {
                        pagerAdapter = new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount());
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(pagerAdapter.getC4Refs() != null && pagerAdapter.getC4Refs().c4_hall != null) {
                                    pagerAdapter.getC4Refs().setToolbarDate();
                                    pagerAdapter.getC4Refs().c4_hall.refreshData();
                                }
                            }
                        },100);

                    }
                    return true;
                case 2:
                    if(pagerAdapter != null && pagerAdapter.getAppRefs() != null) {
                        pagerAdapter.getAppRefs().setToolbarDate();
                        pagerAdapter.getAppRefs().appalachian_hall.refreshData();
                    }
                    else {
                        pagerAdapter = new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount());
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(pagerAdapter.getAppRefs() != null && pagerAdapter.getAppRefs().appalachian_hall != null) {
                                    pagerAdapter.getAppRefs().setToolbarDate();
                                    pagerAdapter.getAppRefs().appalachian_hall.refreshData();

                                }
                            }
                        }, 100);

                    }
                    return true;
                case 3:
                    if(pagerAdapter != null && pagerAdapter.getCIWRefs() != null) {
                        pagerAdapter.getCIWRefs().setToolbarDate();
                        pagerAdapter.getCIWRefs().ciw_hall.refreshData();

                    }
                    else {
                        pagerAdapter = new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount());
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(pagerAdapter.getCIWRefs() != null && pagerAdapter.getCIWRefs().ciw_hall != null) {
                                    pagerAdapter.getCIWRefs().setToolbarDate();
                                    pagerAdapter.getCIWRefs().ciw_hall.refreshData();

                                }
                            }
                        }, 100);
                    }
                    return true;
                    default: return false;
            }
        }
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.hinman));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.c4));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.appalachian));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.ciw));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(CommonUtilities.getDeviceInternetStatus(Objects.requireNonNull(getContext())) == null) return;
                viewPager.setCurrentItem(tab.getPosition(), true);
                updateToolbar(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void updateToolbar (int position) {
        Fragment fragment = pagerAdapter.getFragmentInstance(position);
        if (fragment instanceof C4Dining) {
            ((C4Dining) fragment).setToolbarDate();
        } else if (fragment instanceof HinmanDining) {
            ((HinmanDining) fragment).setToolbarDate();
        } else if (fragment instanceof AppalachianDining) {
            ((AppalachianDining) fragment).setToolbarDate();
        } else if (fragment instanceof CIWDining) {
            ((CIWDining) fragment).setToolbarDate();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bing_dining, container, false);
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Objects.requireNonNull(getActivity()).setTitle(R.string.bing_dining);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Saved", 1);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            Objects.requireNonNull(getActivity()).setTitle(R.string.bing_dining);
        }
    }

}
