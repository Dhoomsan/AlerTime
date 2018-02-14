package com.example.evo09.timetablemanager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class MyAdapter extends PagerAdapter {

    private Context mContext;

    public MyAdapter(Context context, ArrayList<Integer> images) {
        mContext = context;
    }
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);
        return layout;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}