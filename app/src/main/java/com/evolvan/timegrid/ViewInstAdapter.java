package com.evolvan.timegrid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewInstAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    Fragment fragment=null;
    public ViewInstAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Scrach scrach = new Scrach();
                return  scrach;
            case 1:
                Skeleton skeleton = new Skeleton();
                return  skeleton;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}