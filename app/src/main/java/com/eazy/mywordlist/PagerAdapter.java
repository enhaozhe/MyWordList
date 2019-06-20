package com.eazy.mywordlist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private newFragment newFragment;
    private famFragment famFragment;
    private knownFragment knownFragment;

    public PagerAdapter(FragmentManager fm, int numOfTabs, newFragment nf, famFragment ff, knownFragment kf) {
        super(fm);
        this.numOfTabs = numOfTabs;
        newFragment = nf;
        famFragment = ff;
        knownFragment = kf;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0: return newFragment;
            case 1: return famFragment;
            case 2: return knownFragment;
            default: return null;
        }

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
