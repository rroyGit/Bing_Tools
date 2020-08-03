package com.rroycsdev.bingtools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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

    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private final int[] TABS = {R.string.c4, R.string.appalachian, R.string.ciw, R.string.hinman};

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
        } else if (id == R.id.refresh_Bing) {
            if (CommonUtilities.getDeviceInternetStatus(Objects.requireNonNull(getContext())) == null) {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (pagerAdapter != null) {
                Fragment fragment = pagerAdapter.getFragmentInstance(tabLayout.getSelectedTabPosition());
                if (fragment != null) {
                    switch (tabLayout.getSelectedTabPosition()) {
                        case 0:
                            ((C4Dining) fragment).refresh();
                            break;
                        case 1:
                            ((AppalachianDining) fragment).refresh();
                            break;
                        case 2:
                            ((CIWDining) fragment).refresh();
                            break;
                        case 3:
                            ((HinmanDining) fragment).refresh();
                            break;
                        default:
                            return false;
                    }
                }
            }
        }
        return true;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.pager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = pagerAdapter.getFragmentInstance(tab.getPosition());
                ((View) Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).view).setSelected(true);

                if (fragment != null) {
                    if (fragment instanceof C4Dining)
                        ((C4Dining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).showDialog();
                    if (fragment instanceof AppalachianDining)
                        ((AppalachianDining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).showDialog();
                    if (fragment instanceof CIWDining)
                        ((CIWDining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).showDialog();
                    if (fragment instanceof HinmanDining)
                        ((HinmanDining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).showDialog();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Fragment fragment = pagerAdapter.getFragmentInstance(tab.getPosition());

                if (fragment != null) {
                    if (fragment instanceof C4Dining)
                    ((C4Dining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).clearToolbarTextView();
                    if (fragment instanceof AppalachianDining)
                        ((AppalachianDining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).clearToolbarTextView();
                    if (fragment instanceof CIWDining)
                        ((CIWDining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).clearToolbarTextView();
                    if (fragment instanceof HinmanDining)
                        ((HinmanDining)(pagerAdapter.getFragmentInstance(tab.getPosition()))).clearToolbarTextView();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pagerAdapter = new PagerAdapter(getChildFragmentManager(), TABS.length);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        for (int i = 0; i < TABS.length; i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setText(TABS[i]);
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
