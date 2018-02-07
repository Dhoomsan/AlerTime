package com.example.evo09.timetablemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Instruction extends Fragment implements ViewPager.OnPageChangeListener{
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_instruction, container, false);

        viewPager = (ViewPager)rootview. findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewInstAdapter(getChildFragmentManager()));
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.addOnPageChangeListener(this);

        dotsLayout = (LinearLayout) rootview.findViewById(R.id.layoutDots);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.scratch_layout,
                R.layout.grid_layout};

        // adding bottom dots
        addBottomDots(0);

        setHasOptionsMenu(true);
        return rootview;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            //dots[i].setTextSize(20);
            dots[i].setPadding(5,0,5,0);
            dots[i].setGravity(Gravity.CENTER_HORIZONTAL);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        addBottomDots(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
