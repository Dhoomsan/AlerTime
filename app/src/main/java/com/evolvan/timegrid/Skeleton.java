package com.evolvan.timegrid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

public class Skeleton extends Fragment implements View.OnClickListener {
    TextView statically;

    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;

    EditText StartTime,EndTime;
    WebView skeletonWebView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(com.evolvan.timegrid.R.layout.grid_layout, container, false);

        SQLITEHELPER = new SQLiteHelper(getActivity());

        statically = (TextView) rootview.findViewById(com.evolvan.timegrid.R.id.statically);
        statically.setOnClickListener(this);
        skeletonWebView=(WebView)rootview.findViewById(com.evolvan.timegrid.R.id.skeletonWebView);
        skeletonWebView.getSettings();
        skeletonWebView.setBackgroundColor(Color.TRANSPARENT);

        String slide_2_desc = getString(com.evolvan.timegrid.R.string.grid_data);
        String myHtmlString = "<html><body align='justify'>" +
                "<p>" + slide_2_desc+"</p>\n" +
                "</body></html>";
        skeletonWebView.loadData(myHtmlString, "text/html", null);


        return rootview;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.evolvan.timegrid.R.id.statically: {
                ((MainActivity)getActivity()).WhenStatic();
                break;
            }
        }
    }
}