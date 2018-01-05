package com.example.evo09.timetablemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_LONG;

public class Alarm extends Fragment {

    private  ProgressDialog csprogress;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    Toolbar.LayoutParams lp;
    LinearLayout.LayoutParams  param1;
    LinearLayout ll ;
    TextView ed ,et ,eb ;
    Display display;
    boolean refreshcheck=false;
    String Stime,AlermBefore,dweek;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Alarms");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_alerm, container, false);
        csprogress=new ProgressDialog(getActivity());
        SQLITEHELPER = new SQLiteHelper(getActivity());
        return view;
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(refreshcheck==false) {
            refreshcheck = true;
            csprogress.setMessage("Loading...");
            csprogress.show();
            csprogress.setCancelable(false);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    AlarmDataShow();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            csprogress.dismiss();
                        }
                    }, 500);
                }
            },1500);//just mention the time when you want to launch your action
        }
    }
    @Override
    public void onPrepareOptionsMenu(Menu main ) {
        main.clear();

    }
    public void  AlarmDataShow(){

       // Log.d("cickme","ok");
        String tableTime=SQLITEHELPER.TABLE_NAME;
        String tableAlerm=SQLITEHELPER.TABLE_ALERM;
        String tableId=SQLITEHELPER.KEY_ID;
        SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
        String CREATE_ALERMTABLE ="CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_ALERM + " (" + SQLITEHELPER.KEY_IA + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR, "+ SQLITEHELPER.KEY_Status +" VARCHAR)";
        SQLITEDATABASE.execSQL(CREATE_ALERMTABLE);
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + tableTime + " INNER JOIN  "+tableAlerm+" on "+ tableTime +"."+tableId+"="+tableAlerm+"."+SQLITEHELPER.KEY_Status + " ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        param1 = new LinearLayout.LayoutParams(50,100);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth()/3;
        ll = (LinearLayout) getActivity().findViewById(R.id.linearLayout1);
        Log.d("AlermBeforedweek", String.valueOf(cursor.getCount()-1));
        while (cursor != null && cursor.moveToNext()) {
            Stime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime));
            AlermBefore = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_AlermBefor));
            dweek = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DOWeek));
            Log.d("SQLvaralarm", cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Status)));
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setBackgroundResource(R.drawable.gradientbottom);
            ed = new TextView(getContext());
            et = new TextView(getContext());
            eb = new TextView(getContext());
            et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            et.setLayoutParams(param1);
            et.setGravity(Gravity.CENTER_VERTICAL);
            et.setPadding(5, 5, 5, 5);
            et.setHeight(100);
            et.setTextColor(getResources().getColor(R.color.colorPrimary));
            ed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            ed.setLayoutParams(param1);
            ed.setGravity(Gravity.CENTER_VERTICAL);
            ed.setPadding(5, 5, 5, 5);
            ed.setHeight(100);
            ed.setTextColor(getResources().getColor(R.color.orange));
            eb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            eb.setLayoutParams(param1);
            eb.setGravity(Gravity.CENTER_VERTICAL);
            eb.setPadding(5, 5, 5, 5);
            eb.setHeight(100);
            eb.setTextColor(getResources().getColor(R.color.red));
            ed.setText(dweek);
            et.setText(Stime);
            eb.setText(AlermBefore);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lp = new Toolbar.LayoutParams(width, Toolbar.LayoutParams.MATCH_PARENT);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                l.addView(ed, lp);
                l.addView(et, lp);
                l.addView(eb, lp);
            }
            ll.addView(l);
        }
    }
}
