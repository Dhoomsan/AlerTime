package com.example.evo09.timetablemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Scrach extends Fragment implements View.OnClickListener {

    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;

    TextView dynamic;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.scratch, container, false);

        SQLITEHELPER = new SQLiteHelper(getActivity());

        dynamic=(TextView) rootview.findViewById(R.id.dynamic);
        dynamic.setOnClickListener(this);
        return rootview;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dynamic: {
                dynamicdata();
                break;
            }
        }
    }
    public void dynamicdata(){
        Date d = new Date();
        String stime = String.format("%02d:%02d %s", d.getHours() == 0 ? 12 : d.getHours(), d.getMinutes(), d.getHours() < 12 ? "AM" : "PM");
        String etime = String.format("%02d:%02d %s", d.getHours()+1 == 0 ? 12 : d.getHours()+1, d.getMinutes(), d.getHours()+1 < 12 ? "AM" : "PM");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        final String dayOfTheWeek = sdf.format(d);

        SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
        SQLITEDATABASE.execSQL("INSERT or replace INTO " + SQLITEHELPER.TABLE_NAME + " " + "(" + SQLITEHELPER.KEY_DOWeek + "," + SQLITEHELPER.KEY_STime + "," + SQLITEHELPER.KEY_ETime + "," + SQLITEHELPER.KEY_Subject + "," + SQLITEHELPER.KEY_Venue + "," + SQLITEHELPER.KEY_AlermBefor + ")" + " VALUES('" + dayOfTheWeek + "', '" + stime + "', '" + etime + "', '" + "Math" + "', '" + "Room 101" + "' , '" + "5" + "');");
        ((MainActivity)getActivity()).WhenRecord();
    }
}