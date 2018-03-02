package com.rroycsdev.bingtools;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.Serializable;


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
            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    HinmanDining hinmanDining = pagerAdapter.getHinmanRefs();
                    if(hinmanDining != null) hinmanDining.hinman_hall.refreshData();
                    else {
                        pagerAdapter = new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount());
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), true);
                        pagerAdapter.getHinmanRefs().hinman_hall.refreshData();
                        Toast.makeText(getContext(), "Missing ref to BingDining class, got new Hinman ref", Toast.LENGTH_LONG).show();
                    }
                    return true;
                case 1:
                    C4Dining c4Dining = pagerAdapter.getC4Refs();
                    if(c4Dining != null) c4Dining.c4_hall.refreshData();
                    else Toast.makeText(getContext(), "Missing ref to BingDining class", Toast.LENGTH_SHORT).show();
                    return true;
                case 2:
                    AppalachianDining appalachianDining = pagerAdapter.getAppRefs();
                    if(appalachianDining != null) appalachianDining.appalachian_hall.refreshData();
                    else Toast.makeText(getContext(), "Missing ref to BingDining class", Toast.LENGTH_SHORT).show();
                    return true;
                case 3:
                    CIWDining ciwDining = pagerAdapter.getCIWRefs();
                    if(ciwDining != null) ciwDining.ciw_hall.refreshData();
                    else Toast.makeText(getContext(), "Missing ref to BingDining class", Toast.LENGTH_SHORT).show();
                    return true;
                    default: return false;
            }
        }
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.hinman));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.c4));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.appalachian));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.ciw));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               viewPager.setCurrentItem(tab.getPosition(), true);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bing_dining, container, false);
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.bing_dining);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            getActivity().setTitle(R.string.bing_dining);
        }
    }

}
