package com.evolvan.timegrid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.relex.circleindicator.CircleIndicator;


public class Instruction extends Fragment {
    private ViewPager viewPager;
    CircleIndicator indicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(com.evolvan.timegrid.R.string.app_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(com.evolvan.timegrid.R.layout.fragment_instruction, container, false);

        viewPager = (ViewPager)rootview. findViewById(com.evolvan.timegrid.R.id.view_pager);
        viewPager.setAdapter(new ViewInstAdapter(getChildFragmentManager()));
        viewPager.getAdapter().notifyDataSetChanged();
        indicator = (CircleIndicator)rootview. findViewById(com.evolvan.timegrid.R.id.indicator);
        indicator.setViewPager(viewPager);
        return rootview;
    }
}
