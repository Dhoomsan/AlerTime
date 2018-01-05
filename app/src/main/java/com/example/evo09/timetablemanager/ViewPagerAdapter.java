package com.example.evo09.timetablemanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 7;
    Fragment fragment=null;
    // Tab Titles
    private String tabtitles[] = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday","Friday","Saturday","Sunday" };
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                F1Monday mon = new F1Monday();
                return  mon;
            case 1:
                F2Tuesday tue = new F2Tuesday();
                return tue;
            case 2:
                F3Wednesday wed = new F3Wednesday();
                return wed;
            case 3:
                F4Thursday thu = new F4Thursday();
                return thu;
            case 4:
                F5Friday fri = new F5Friday();
                return fri;
            case 5:
                F6Saturday sat = new F6Saturday();
                return sat;
            case 6:
                F7Sunday sun = new F7Sunday();
                return sun;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}