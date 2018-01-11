package com.example.evo09.timetablemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MyScheduleLandScape extends Fragment implements View.OnClickListener {
    private ProgressDialog csprogress;

    Fragment fragment=null;
    Fragment frag;
    FragmentManager fm1;
    FragmentTransaction ft1;

    static int t = 0, jump = 0, ST, ET, DStandEt, sizetime, sizemon, sizetue, sizewed, sizethu, sizefri, sizesat, sizesun, Shour, SnextHour, Sminutes;
    static String[] Timedata, Mondata, Tuedata, Weddata, Thudata, Fridata, Satdata, Sundata, SplitMondSTCompare, SplitMonETCompare;
    static String[] MondST, MonET, TueST, TueET, WedST, WedET, ThuST, ThuET, FriST, FriET, SatST, SatET, SunST, SunET;
    String MondSTCompare, MonETCompare, shourSplitMondSTCompare, shourSplitMonETCompare;
    Date dateSTime, date1, date2, dateETime;
    ArrayList<String> Timedatalist;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor,cursortime, cursormon, cursortue, cursorwed, cursorthu, cursorfri, cursorsat, cursorsun;
    Toolbar.LayoutParams lp;
    LinearLayout.LayoutParams param1;
    LinearLayout Layouttime, LayoutMon, LayoutTue, LayoutWed, LayoutThu, LayoutFri, LayoutSat, LayoutSun, container1;
    LinearLayout LTime, LMon, LTue, LWed, LThu, LFri, LSat, LSun;
    TextView time, mon, tue, wed, thu, fri, sat, sun;
    Display display;
    boolean refreshcheck = false;
    String[] CStime, CEtime, CMon, CMonSTime, CMonETime, CTue, CTueETime, CTueTime, CWed, CWedTime, CWedETime, CThu, CThuTime, CThuETime, CFri, CFriTime, CFriETime, CSat, CSatTime, CSatETime, CSun, CSunTime, CSunETime;
    private String tabtitles[] = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getActivity().setTitle("Schedules");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_landscape_layout, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        csprogress = new ProgressDialog(getActivity());
        SQLITEHELPER = new SQLiteHelper(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        DBCreate();
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
        if(cursor.getCount()!=0) {
            if (refreshcheck == false) {
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
                        }, 50);
                    }
                }, 200);//just mention the time when you want to launch your action
            }
        }
        else {
            fm1 = getActivity().getSupportFragmentManager();
            ft1 = fm1.beginTransaction();
            frag = new MyStaticSchedules();
            ft1.replace(R.id.content_frame, frag);
            ft1.commit();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu main) {
        //menu.clear();
        MenuItem item = main.findItem(R.id.action_add);
        item.setVisible(false);
        MenuItem item2 = main.findItem(R.id.action_LANDSCAPE);
        item2.setVisible(false);
        MenuItem item3=main.findItem(R.id.action_createStatic);
        item3.setVisible(false);
    }
    public void DBCreate(){
        SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR NOT NULL)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
        if(SQLITEDATABASE.isOpen()) {
            //Log.d("SQ", "open");
        }
        else {
            //Log.d("SLV", "not open");
        }
    }
    public void AlarmDataShow() {
        cursortime = SQLITEDATABASE.rawQuery("SELECT DISTINCT " + SQLITEHELPER.KEY_STime + " , " + SQLITEHELPER.KEY_ETime + " FROM " + SQLITEHELPER.TABLE_NAME + " ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursormon = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Monday' ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursortue = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Tuesday' ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursorwed = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Wednesday'  ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursorthu = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Thursday'  ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursorfri = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Friday'  ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursorsat = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Saturday'  ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        cursorsun = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Sunday'  ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        sizetime = cursortime.getCount();
        sizemon = cursormon.getCount();
        sizetue = cursortue.getCount();
        sizewed = cursorwed.getCount();
        sizethu = cursorthu.getCount();
        sizefri = cursorfri.getCount();
        sizesat = cursorsat.getCount();
        sizesun = cursorsun.getCount();

        CStime = new String[sizetime];
        CEtime = new String[sizetime];
        CMon = new String[sizemon];
        CTue = new String[sizetue];
        CWed = new String[sizewed];
        CThu = new String[sizethu];
        CFri = new String[sizefri];
        CSat = new String[sizesat];
        CSun = new String[sizesun];

        CMonSTime = new String[sizemon];
        CMonETime = new String[sizemon];

        CTueTime = new String[sizetue];
        CTueETime = new String[sizetue];

        CWedTime = new String[sizewed];
        CWedETime = new String[sizewed];

        CThuTime = new String[sizethu];
        CThuETime = new String[sizethu];

        CFriTime = new String[sizefri];
        CFriETime = new String[sizefri];

        CSatTime = new String[sizesat];
        CSatETime = new String[sizesat];

        CSunTime = new String[sizesun];
        CSunETime = new String[sizesun];

        int a = 0;
        while (cursortime != null && cursortime.moveToNext()) {
            String stime = cursortime.getString(cursortime.getColumnIndex(SQLiteHelper.KEY_STime));
            String etime = cursortime.getString(cursortime.getColumnIndex(SQLiteHelper.KEY_ETime));
            CStime[a] = stime;
            CEtime[a] = etime;
            a++;

        }
        //mon
        int b = 0;
        while (cursormon != null && cursormon.moveToNext()) {
            String times = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CMonSTime[b] = times;
            CMonETime[b] = timee;
            CMon[b] = SubVen;
            b++;
        }
        //tue
        int c = 0;
        while (cursortue != null && cursortue.moveToNext()) {
            String time = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CTueTime[c] = time;
            CTueETime[c] = timee;
            CTue[c] = SubVen;
            c++;
        }
        //wed
        int d = 0;
        while (cursorwed != null && cursorwed.moveToNext()) {
            String time = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CWedTime[d] = time;
            CWed[d] = SubVen;
            CWedETime[d] = timee;
            d++;
        }
        //thu
        int e = 0;
        while (cursorthu != null && cursorthu.moveToNext()) {
            String time = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CThuTime[e] = time;
            CThu[e] = SubVen;
            CThuETime[e] = timee;
            e++;
        }
        //fri
        int f = 0;
        while (cursorfri != null && cursorfri.moveToNext()) {
            String time = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CFriTime[f] = time;
            CFri[f] = SubVen;
            CFriETime[f] = timee;
            f++;
        }
        //sat
        int g = 0;
        while (cursorsat != null && cursorsat.moveToNext()) {
            String time = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CSatTime[g] = time;
            CSat[g] = SubVen;
            CSatETime[g] = timee;
            //Log.d("ahdsfgahs",SubVen);
            g++;
        }
        //sun
        int h = 0;
        while (cursorsun != null && cursorsun.moveToNext()) {
            String time = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            CSunTime[h] = time;
            CSun[h] = SubVen;
            CSunETime[h] = timee;
            h++;
        }
        showdata(CStime, CEtime, CMonSTime, CMonETime, CMon, CTueTime, CTueETime, CTue, CWedTime, CWedETime, CWed, CThuTime, CThuETime, CThu, CFriTime, CFriETime, CFri, CSatTime, CSatETime, CSat, CSunTime, CSunETime, CSun);
    }


    public void showdata(String[] CStime, String[] CEtime, String[] CMonSTime, String[] CMonETime, String[] CMon, String[] CTueTime, String[] CTueETime, String[] CTue, String[] CWedTime, String[] CWedETime, String[] CWed, String[] CThuTime, String[] CThuETime, String[] CThu, String[] CFriTime, String[] CFriETime, String[] CFri, String[] CSatTime, String[] CSatETime, String[] CSat, String[] CSunTime, String[] CSunETime, String[] CSun) {
        if(CStime.length==0){
            Toast.makeText(getContext(),"Sorry !"+"\n"+"Create First.",Toast.LENGTH_SHORT).show();
        }
        else {
            param1 = new LinearLayout.LayoutParams(50, 100);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth() / 8;
            int height = 120;
            Layouttime = (LinearLayout) getActivity().findViewById(R.id.linearLayouttime);
            LayoutMon = (LinearLayout) getActivity().findViewById(R.id.linearLayoutmon);
            LayoutTue = (LinearLayout) getActivity().findViewById(R.id.linearLayouttue);
            LayoutWed = (LinearLayout) getActivity().findViewById(R.id.linearLayoutwed);
            LayoutThu = (LinearLayout) getActivity().findViewById(R.id.linearLayoutthu);
            LayoutFri = (LinearLayout) getActivity().findViewById(R.id.linearLayoutfri);
            LayoutSat = (LinearLayout) getActivity().findViewById(R.id.linearLayoutsat);
            LayoutSun = (LinearLayout) getActivity().findViewById(R.id.linearLayoutsun);
            container1 = (LinearLayout) getActivity().findViewById(R.id.container1);
            container1.setOnClickListener(this);

            LinearLayout.LayoutParams Margin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Margin.setMargins(0, 5, 0, 0);
            //main for loop
            String StartFirstTime = CStime[0];
            String[] SplitStime = StartFirstTime.split(" ");
            String shourminute = SplitStime[0];
            date1 = new Date();
            date1.setTime((((Integer.parseInt(shourminute.split(":")[0])) * 60 + (Integer.parseInt(shourminute.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
            Shour = date1.getHours() * 60;
            Sminutes = date1.getMinutes();
            int SST = date1.getHours() * 60 + date1.getMinutes();
            SnextHour = date1.getHours() * 60 + 60;

            Timedatalist = new ArrayList<String>();
            String LastEndTime = CEtime[CEtime.length - 1];
            String[] SplitEtime = LastEndTime.split(" ");
            String SplitEtimeFirst = SplitEtime[0];
            date2 = new Date();
            date2.setTime((((Integer.parseInt(SplitEtimeFirst.split(":")[0])) * 60 + (Integer.parseInt(SplitEtimeFirst.split(":")[1]))) + date2.getTimezoneOffset()) * 60000);
            int SET = date2.getHours() * 60 + date2.getMinutes();


            t = 0;
            while (SST < SET-1) {
                if (t == 0) {
                    if (SST >= Shour || SST < SnextHour) {
                        LTime = new LinearLayout(getContext());
                        time = new TextView(getContext());
                        time.setText(StartFirstTime);
                        time.setBackgroundResource(R.drawable.gradientbottom);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            lp = new Toolbar.LayoutParams(width, height - Sminutes * 2);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LTime.addView(time, lp);
                        }
                        Layouttime.addView(LTime);
                    }
                }
                else {
                    date1.setTime(date1.getTime() + (1 * 60000));
                    int hour = date1.getHours();
                    int mint = date1.getMinutes();
                    if (mint == 0) {
                        LTime = new LinearLayout(getContext());
                        time = new TextView(getContext());
                        time.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM"));
                        time.setBackgroundResource(R.drawable.gradientbottom);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            lp = new Toolbar.LayoutParams(width, height);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LTime.addView(time, lp);
                        }
                        Layouttime.addView(LTime);
                    }
                }
                Timedatalist.add(String.format("%02d:%02d %s", date1.getHours() == 0 ? 12 : date1.getHours(), date1.getMinutes(), date1.getHours() < 12 ? "AM" : "PM"));
                SST = date1.getHours() * 60 + date1.getMinutes();
                t++;
            }

//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
            Timedata = new String[Timedatalist.size()];
            Timedata = Timedatalist.toArray(Timedata);

            Mondata = new String[Timedatalist.size()];
            MonET = new String[Timedatalist.size()];
            MondST = new String[Timedatalist.size()];

            Tuedata = new String[Timedatalist.size()];
            TueST = new String[Timedatalist.size()];
            TueET = new String[Timedatalist.size()];

            Weddata = new String[Timedatalist.size()];
            WedST = new String[Timedatalist.size()];
            WedET = new String[Timedatalist.size()];

            Thudata = new String[Timedatalist.size()];
            ThuST = new String[Timedatalist.size()];
            ThuET = new String[Timedatalist.size()];

            Fridata = new String[Timedatalist.size()];
            FriST = new String[Timedatalist.size()];
            FriET = new String[Timedatalist.size()];

            Satdata = new String[Timedatalist.size()];
            SatST = new String[Timedatalist.size()];
            SatET = new String[Timedatalist.size()];

            Sundata = new String[Timedatalist.size()];
            SunST = new String[Timedatalist.size()];
            SunET = new String[Timedatalist.size()];

            //monday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CMonSTime.length; i++) {
                    if (Timedata[j].equals(CMonSTime[i])) {
                        Mondata[j] = CMon[i];
                        MondST[j] = CMonSTime[i];
                        MonET[j] = CMonETime[i];
                    }
                }
                MondSTCompare = MondST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = MonET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LMon = new LinearLayout(getContext());
                    mon = new TextView(getContext());
                    //mon.setId(j);
                    mon.setText(Html.fromHtml("<small><font color=\"#47a842\">" + MondST[j] + "</font></small>" + "<br>" + Mondata[j]));
                    mon.setGravity(Gravity.CENTER_HORIZONTAL);
                    mon.setBackgroundResource(R.drawable.gradientbottom);
                    mon.setEllipsize(TextUtils.TruncateAt.END);
                    mon.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LMon.addView(mon, lp);
                    }
                    LayoutMon.addView(LMon);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    LMon = new LinearLayout(getContext());
                    mon = new TextView(getContext());
                    mon.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LMon.addView(mon, lp);
                    }
                    LayoutMon.addView(LMon);
                }
            }

            //Tuesday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CTueTime.length; i++) {
                    if (Timedata[j].equals(CTueTime[i])) {
                        Tuedata[j] = CTue[i];
                        TueST[j] = CTueTime[i];
                        TueET[j] = CTueETime[i];
                    }
                }
                MondSTCompare = TueST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = TueET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LTue = new LinearLayout(getContext());
                    tue = new TextView(getContext());
                    tue.setText(Html.fromHtml("<small><font color=\"#47a842\">" + TueST[j] + "</font></small>" + "<br>" + Tuedata[j]));
                    tue.setGravity(Gravity.CENTER_HORIZONTAL);
                    tue.setBackgroundResource(R.drawable.gradientbottom);
                    tue.setEllipsize(TextUtils.TruncateAt.END);
                    tue.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LTue.addView(tue, lp);
                    }
                    LayoutTue.addView(LTue);
                    j = j + (DStandEt / 2) - 1;
                    ////Log.d("JumpCheck",DStandEt+"-"+jump+"-"+ET+"-"+ST+"-"+j);
                } else {
                    LTue = new LinearLayout(getContext());
                    tue = new TextView(getContext());
                    tue.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LTue.addView(tue, lp);
                    }
                    LayoutTue.addView(LTue);
                }
            }
            //........................


            //Wednesday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CWedTime.length; i++) {
                    if (Timedata[j].equals(CWedTime[i])) {
                        Weddata[j] = CWed[i];
                        WedST[j] = CWedTime[i];
                        WedET[j] = CWedETime[i];
                    }
                }
                MondSTCompare = WedST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = WedET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LWed = new LinearLayout(getContext());
                    wed = new TextView(getContext());
                    wed.setText(Html.fromHtml("<small><font color=\"#47a842\">" + WedST[j] + "</font></small>" + "<br>" + Weddata[j]));
                    wed.setGravity(Gravity.CENTER_HORIZONTAL);
                    wed.setBackgroundResource(R.drawable.gradientbottom);
                    wed.setEllipsize(TextUtils.TruncateAt.END);
                    wed.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LWed.addView(wed, lp);
                    }
                    LayoutWed.addView(LWed);
                    j = j + (DStandEt / 2) - 1;
                    ////Log.d("JumpCheck",DStandEt+"-"+jump+"-"+ET+"-"+ST+"-"+j);
                } else {
                    LWed = new LinearLayout(getContext());
                    wed = new TextView(getContext());
                    wed.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LWed.addView(wed, lp);
                    }
                    LayoutWed.addView(LWed);
                }
            }
            //........................


            //Thursday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CThuTime.length; i++) {
                    if (Timedata[j].equals(CThuTime[i])) {
                        Thudata[j] = CThu[i];
                        ThuST[j] = CThuTime[i];
                        ThuET[j] = CThuETime[i];
                    }
                }
                MondSTCompare = ThuST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = ThuET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LThu = new LinearLayout(getContext());
                    thu = new TextView(getContext());
                    thu.setText(Html.fromHtml("<small><font color=\"#47a842\">" + ThuST[j] + "</font></small>" + "<br>" + Thudata[j]));
                    thu.setGravity(Gravity.CENTER_HORIZONTAL);
                    thu.setBackgroundResource(R.drawable.gradientbottom);
                    thu.setEllipsize(TextUtils.TruncateAt.END);
                    thu.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LThu.addView(thu, lp);
                    }
                    LayoutThu.addView(LThu);
                    j = j + (DStandEt / 2) - 1;
                    // //Log.d("JumpCheck",DStandEt+"-"+jump+"-"+ET+"-"+ST+"-"+j);
                } else {
                    LThu = new LinearLayout(getContext());
                    tue = new TextView(getContext());
                    tue.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LThu.addView(tue, lp);
                    }
                    LayoutThu.addView(LThu);
                }
            }
            //........................

            //Frieday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CFriTime.length; i++) {
                    if (Timedata[j].equals(CFriTime[i])) {
                        Fridata[j] = CFri[i];
                        FriST[j] = CFriTime[i];
                        FriET[j] = CFriETime[i];
                    }
                }
                MondSTCompare = FriST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = FriET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LFri = new LinearLayout(getContext());
                    fri = new TextView(getContext());
                    fri.setText(Html.fromHtml("<small><font color=\"#47a842\">" + FriST[j] + "</font></small>" + "<br>" + Fridata[j]));
                    fri.setGravity(Gravity.CENTER_HORIZONTAL);
                    fri.setBackgroundResource(R.drawable.gradientbottom);
                    fri.setEllipsize(TextUtils.TruncateAt.END);
                    fri.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LFri.addView(fri, lp);
                    }
                    LayoutFri.addView(LFri);
                    j = j + (DStandEt / 2) - 1;
                    ////Log.d("JumpCheck",DStandEt+"-"+jump+"-"+ET+"-"+ST+"-"+j);
                } else {
                    LFri = new LinearLayout(getContext());
                    fri = new TextView(getContext());
                    fri.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LFri.addView(fri, lp);
                    }
                    LayoutFri.addView(LFri);
                }
            }
            //........................

            //Saturday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CSatTime.length; i++) {
                    if (Timedata[j].equals(CSatTime[i])) {
                        Satdata[j] = CSat[i];
                        SatST[j] = CSatTime[i];
                        SatET[j] = CSatETime[i];
                    }
                }
                MondSTCompare = SatST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = SatET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LSat = new LinearLayout(getContext());
                    sat = new TextView(getContext());
                    sat.setText(Html.fromHtml("<small><font color=\"#47a842\">" + SatST[j] + "</font></small>" + "<br>" + Satdata[j]));
                    Log.d("ahdsfgahs", String.valueOf(Satdata[j]));
                    sat.setGravity(Gravity.CENTER_HORIZONTAL);
                    sat.setBackgroundResource(R.drawable.gradientbottom);
                    sat.setEllipsize(TextUtils.TruncateAt.END);
                    sat.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LSat.addView(sat, lp);
                    }
                    LayoutSat.addView(LSat);
                    j = j + (DStandEt / 2) - 1;
                    ////Log.d("JumpCheck",DStandEt+"-"+jump+"-"+ET+"-"+ST+"-"+j);
                } else {
                    LSat = new LinearLayout(getContext());
                    sat = new TextView(getContext());
                    sat.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LSat.addView(sat, lp);
                    }
                    LayoutSat.addView(LSat);
                }
            }
            //........................


            //Sunday
            for (int j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CSunTime.length; i++) {
                    if (Timedata[j].equals(CSunTime[i])) {
                        Sundata[j] = CSun[i];
                        SunST[j] = CSunTime[i];
                        SunET[j] = CSunETime[i];
                    }
                }
                MondSTCompare = SunST[j];
                if (MondSTCompare != null && MondSTCompare.length() != 0) {
                    SplitMondSTCompare = MondSTCompare.split(" ");
                    shourSplitMondSTCompare = SplitMondSTCompare[0];
                    dateSTime = new Date();
                    dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
                    ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

                    MonETCompare = SunET[j];
                    SplitMonETCompare = MonETCompare.split(" ");
                    shourSplitMonETCompare = SplitMonETCompare[0];
                    dateETime = new Date();
                    dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
                    ET = dateETime.getHours() * 60 + dateETime.getMinutes();
                    DStandEt = ((ET - ST) * 2);
                    jump = DStandEt / height;
                    LSun = new LinearLayout(getContext());
                    sun = new TextView(getContext());
                    sun.setText(Html.fromHtml("<small><font color=\"#008CBA\">" + SunST[j] + "</font></small>" + "<br>" + Sundata[j]));
                    sun.setGravity(Gravity.CENTER_HORIZONTAL);
                    sun.setBackgroundResource(R.drawable.gradientbottom);
                    sun.setEllipsize(TextUtils.TruncateAt.END);
                    sun.setMaxLines(DStandEt / 30);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, DStandEt);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LSun.addView(sun, lp);
                    }
                    LayoutSun.addView(LSun);
                    j = j + (DStandEt / 2) - 1;
                    ////Log.d("JumpCheck",DStandEt+"-"+jump+"-"+ET+"-"+ST+"-"+j);
                } else {
                    LSun = new LinearLayout(getContext());
                    sun = new TextView(getContext());
                    sun.setText(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        lp = new Toolbar.LayoutParams(width, 2);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LSun.addView(sun, lp);
                    }
                    LayoutSun.addView(LSun);
                }
            }
        }
        //........................
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container1: {
                // Add  dialog for confirmation to delete selected item
                // record.
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setMessage("Add click on +Plus icon"+"\n"+"  or  "+"\n"+"Update click on list below !")
                        .setPositiveButton(Html.fromHtml("<font color=\"#47a842\">Add  / Update</font>"), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fm1 = getActivity().getSupportFragmentManager();
                                ft1 = fm1.beginTransaction();
                                frag = new MyStaticSchedules();
                                ft1.replace(R.id.content_frame, frag);
                                ft1.commit();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setIcon(R.drawable.ic_alarm_clock);// dialog  Icon
                alert.setTitle("Change"); // dialog  Title
                alert.show();
            }
        }
    }
}