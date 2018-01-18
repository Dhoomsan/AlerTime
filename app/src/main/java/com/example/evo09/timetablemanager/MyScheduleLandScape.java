package com.example.evo09.timetablemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import java.util.ArrayList;
import java.util.Date;
import static android.content.Context.MODE_PRIVATE;

public class MyScheduleLandScape extends Fragment {
    private ProgressDialog csprogress;
    Fragment fragment=null;
    Fragment frag;
    FragmentManager fm1;
    FragmentTransaction ft1;

    static int t = 0,j, jump = 0, ST, ET, DStandEt, sizetime, sizemon, sizetue, sizewed, sizethu, sizefri, sizesat, sizesun, Shour, SnextHour, Sminutes,width=0,height=0,storDayId=0;
    static String[] Timedata, Mondata, Tuedata, Weddata, Thudata, Fridata, Satdata, Sundata, SplitMondSTCompare, SplitMonETCompare;
    static String[] MondST, MonET, TueST, TueET, WedST, WedET, ThuST, ThuET, FriST, FriET, SatST, SatET, SunST, SunET;
    static String[] MonId, TueId, WedId, ThuId, FriId, SatId, SunId;
    String DaySTCompare, MonETCompare, shourSplitMondSTCompare, shourSplitMonETCompare,strMondata,strDayId=null;
    Date dateSTime, date1, date2, dateETime;
    ArrayList<String> Timedatalist;

    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor,cursortime, cursormon, cursortue, cursorwed, cursorthu, cursorfri, cursorsat, cursorsun;

    Toolbar.LayoutParams lp;
    LinearLayout.LayoutParams param1;
    LinearLayout Layouttime, LayoutMon, LayoutTue, LayoutWed, LayoutThu, LayoutFri, LayoutSat, LayoutSun, container1;
    LinearLayout LDay,datafield;
    TextView Day,gotoViewPager;
    Display display;
    String[] CStimeId,CStime, CEtime,CMonId, CMon, CMonSTime, CMonETime,CTueId, CTue, CTueETime, CTueTime,CWedId, CWed, CWedTime, CWedETime,CThuId, CThu, CThuTime, CThuETime,CFriId, CFri, CFriTime, CFriETime,CSatId, CSat, CSatTime, CSatETime,CSunId, CSun, CSunTime, CSunETime;
    String StrSubject,StrVenue,StrAlembefor,Error="Field Cannot be empty!";
    android.support.v7.app.AlertDialog show;

    Button ButtonAddUpdate,ButtonDelete;
    static EditText AlermBefore;
    static AutoCompleteTextView Subject,Venue;
    static CheckBox AlermRepeat;
    Snackbar snackbar1;
    boolean refreshcheck = false;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("TableView");
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
                }, 200);//just mention the Day when you want to launch your action*/
            }
        }else {
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
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR)";
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

        CStimeId=new String[sizetime];
        CStime = new String[sizetime];
        CEtime = new String[sizetime];

        CMon = new String[sizemon];
        CTue = new String[sizetue];
        CWed = new String[sizewed];
        CThu = new String[sizethu];
        CFri = new String[sizefri];
        CSat = new String[sizesat];
        CSun = new String[sizesun];

        CMonId = new String[sizemon];
        CMonSTime = new String[sizemon];
        CMonETime = new String[sizemon];

        CTueId = new String[sizetue];
        CTueTime = new String[sizetue];
        CTueETime = new String[sizetue];

        CWedId = new String[sizewed];
        CWedTime = new String[sizewed];
        CWedETime = new String[sizewed];

        CThuId = new String[sizethu];
        CThuTime = new String[sizethu];
        CThuETime = new String[sizethu];

        CFriId = new String[sizefri];
        CFriTime = new String[sizefri];
        CFriETime = new String[sizefri];

        CSatId = new String[sizesat];
        CSatTime = new String[sizesat];
        CSatETime = new String[sizesat];

        CSunId = new String[sizesun];
        CSunTime = new String[sizesun];
        CSunETime = new String[sizesun];

        //time
        int a = 0;
        while (cursortime != null && cursortime.moveToNext()) {
            String stime = cursortime.getString(cursortime.getColumnIndex(SQLiteHelper.KEY_STime));
            String etime = cursortime.getString(cursortime.getColumnIndex(SQLiteHelper.KEY_ETime));
            CStime[a] = stime;
            CEtime[a] = etime;
            a++;

        }
        //Mon
        a = 0;
        while (cursormon != null && cursormon.moveToNext()) {
            String times = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursormon.getString(cursormon.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDM",id);
            CMonId[a]=id;
            CMonSTime[a] = times;
            CMonETime[a] = timee;
            CMon[a] = SubVen;
            a++;
        }
        //Tue
        a = 0;
        while (cursortue != null && cursortue.moveToNext()) {
            String Day = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursortue.getString(cursortue.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDT",id);
            CTueId[a]=id;
            CTueTime[a] = Day;
            CTueETime[a] = timee;
            CTue[a] = SubVen;
            a++;
        }
        //Wed
        a = 0;
        while (cursorwed != null && cursorwed.moveToNext()) {
            String Day = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursorwed.getString(cursorwed.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDW",id);
            CWedId[a]=id;
            CWedTime[a] = Day;
            CWed[a] = SubVen;
            CWedETime[a] = timee;
            a++;
        }
        //Thu
        a = 0;
        while (cursorthu != null && cursorthu.moveToNext()) {
            String Day = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursorthu.getString(cursorthu.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDTh",id);
            CThuId[a]=id;
            CThuTime[a] = Day;
            CThu[a] = SubVen;
            CThuETime[a] = timee;
            a++;
        }
        //Fri
        a = 0;
        while (cursorfri != null && cursorfri.moveToNext()) {
            String Day = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursorfri.getString(cursorfri.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDF",id);
            CFriId[a]=id;
            CFriTime[a] = Day;
            CFri[a] = SubVen;
            CFriETime[a] = timee;
            a++;
        }
        //Sat
        a = 0;
        while (cursorsat != null && cursorsat.moveToNext()) {
            String Day = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursorsat.getString(cursorsat.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDSa",id);
            CSatId[a]=id;
            CSatTime[a] = Day;
            CSat[a] = SubVen;
            CSatETime[a] = timee;
            //Log.d("ahdsfgahs",SubVen);
            a++;
        }
        //Sun
        a = 0;
        while (cursorsun != null && cursorsun.moveToNext()) {
            String Day = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_STime));
            String timee = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_ETime));
            String SubVen = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_Subject)) + "<br>(" + cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_Venue)) + ")";
            String id = cursorsun.getString(cursorsun.getColumnIndex(SQLiteHelper.KEY_ID));
           //Log.d("KEY_IDS",id);
            CSunId[a]=id;
            CSunTime[a] = Day;
            CSun[a] = SubVen;
            CSunETime[a] = timee;
            a++;
        }
        showdata(CStime, CEtime,CMonId, CMonSTime, CMonETime, CMon,CTueId, CTueTime, CTueETime, CTue,CWedId, CWedTime, CWedETime, CWed,CThuId, CThuTime, CThuETime, CThu,CFriId, CFriTime, CFriETime, CFri,CSatId, CSatTime, CSatETime, CSat,CSunId, CSunTime, CSunETime, CSun);
    }

    public void showdata(String[] CStime, String[] CEtime,String[] CMonId, String[] CMonSTime, String[] CMonETime, String[] CMon,String[] CTueId, String[] CTueTime, String[] CTueETime, String[] CTue,String[] CWedId, String[] CWedTime, String[] CWedETime, String[] CWed,String[] CThuId, String[] CThuTime, String[] CThuETime, String[] CThu,String[] CFriId, String[] CFriTime, String[] CFriETime, String[] CFri,String[] CSatId, String[] CSatTime, String[] CSatETime, String[] CSat,String[] CSunId, String[] CSunTime, String[] CSunETime, String[] CSun) {
        if(CStime.length==0){
            Toast.makeText(getContext(),"Sorry !"+"\n"+"Create First.",Toast.LENGTH_SHORT).show();
        }
        else {
            param1 = new LinearLayout.LayoutParams(50, 100);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            width = display.getWidth() / 8;
            height = 120;
            Layouttime = (LinearLayout) getActivity().findViewById(R.id.linearLayouttime);
            LayoutMon = (LinearLayout) getActivity().findViewById(R.id.linearLayoutmon);
            LayoutTue = (LinearLayout) getActivity().findViewById(R.id.linearLayouttue);
            LayoutWed = (LinearLayout) getActivity().findViewById(R.id.linearLayoutwed);
            LayoutThu = (LinearLayout) getActivity().findViewById(R.id.linearLayoutthu);
            LayoutFri = (LinearLayout) getActivity().findViewById(R.id.linearLayoutfri);
            LayoutSat = (LinearLayout) getActivity().findViewById(R.id.linearLayoutsat);
            LayoutSun = (LinearLayout) getActivity().findViewById(R.id.linearLayoutsun);
            container1 = (LinearLayout) getActivity().findViewById(R.id.container1);
            //container1.setOnClickListener(this);

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
                        LDay = new LinearLayout(getContext());
                        Day = new TextView(getContext());
                        Day.setText(StartFirstTime);
                        Day.setBackgroundResource(R.drawable.gradientbottom);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            lp = new Toolbar.LayoutParams(width, height - Sminutes * 2);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LDay.addView(Day, lp);
                        }
                        Layouttime.addView(LDay);
                    }
                }
                else {
                    date1.setTime(date1.getTime() + (1 * 60000));
                    int hour = date1.getHours();
                    int mint = date1.getMinutes();
                    if (mint == 0) {
                        LDay = new LinearLayout(getContext());
                        Day = new TextView(getContext());
                        Day.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM"));
                        Day.setBackgroundResource(R.drawable.gradientbottom);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            lp = new Toolbar.LayoutParams(width, height);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LDay.addView(Day, lp);
                        }
                        Layouttime.addView(LDay);
                    }
                }
                Timedatalist.add(String.format("%02d:%02d %s", date1.getHours() == 0 ? 12 : date1.getHours(), date1.getMinutes(), date1.getHours() < 12 ? "AM" : "PM"));
                SST = date1.getHours() * 60 + date1.getMinutes();
                t++;
            }

//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
            /*Timedata = new String[Timedatalist.size()];
            Timedata = Timedatalist.toArray(Timedata);*/

            Timedata = new String[Timedatalist.size()];
            Timedata = Timedatalist.toArray(Timedata);

            //Monday
            Mondata = new String[Timedatalist.size()];
            MonET = new String[Timedatalist.size()];
            MondST = new String[Timedatalist.size()];
            MonId = new String[Timedatalist.size()];
            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CMonSTime.length; i++) {
                    if (Timedata[j].equals(CMonSTime[i])) {
                        Mondata[j] = CMon[i];
                        MondST[j] = CMonSTime[i];
                        MonET[j] = CMonETime[i];
                        MonId[j]=CMonId[i];
                    }
                }
                DaySTCompare = MondST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata = Mondata[j];
                    MonETCompare = MonET[j];
                    strDayId=MonId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width, LayoutMon);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutMon);
                }
            }


            //Tuesday
            Tuedata = new String[Timedatalist.size()];
            TueST = new String[Timedatalist.size()];
            TueET = new String[Timedatalist.size()];
            TueId = new String[Timedatalist.size()];
            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CTueTime.length; i++) {
                    if (Timedata[j].equals(CTueTime[i])) {
                        Tuedata[j] = CTue[i];
                        TueST[j] = CTueTime[i];
                        TueET[j] = CTueETime[i];
                        TueId[j] = CTueId[i];
                    }
                }

                DaySTCompare = TueST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata=Tuedata[j];
                    MonETCompare = TueET[j];
                    strDayId=TueId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width,LayoutTue);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutTue);
                }
            }
            //........................


            //Wednesday
            Weddata = new String[Timedatalist.size()];
            WedST = new String[Timedatalist.size()];
            WedET = new String[Timedatalist.size()];
            WedId = new String[Timedatalist.size()];
            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CWedTime.length; i++) {
                    if (Timedata[j].equals(CWedTime[i])) {
                        Weddata[j] = CWed[i];
                        WedST[j] = CWedTime[i];
                        WedET[j] = CWedETime[i];
                        WedId[j]=CWedId[i];
                    }
                }
                DaySTCompare = WedST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata=Weddata[j];
                    MonETCompare = WedET[j];
                    strDayId=WedId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width,LayoutWed);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutWed);
                }
            }
            //........................


            //Thursday
            Thudata = new String[Timedatalist.size()];
            ThuST = new String[Timedatalist.size()];
            ThuET = new String[Timedatalist.size()];
            ThuId = new String[Timedatalist.size()];
            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CThuTime.length; i++) {
                    if (Timedata[j].equals(CThuTime[i])) {
                        Thudata[j] = CThu[i];
                        ThuST[j] = CThuTime[i];
                        ThuET[j] = CThuETime[i];
                        ThuId[j]=CThuId[i];
                    }
                }
                DaySTCompare = ThuST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata=Thudata[j];
                    MonETCompare = ThuET[j];
                    strDayId=ThuId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width,LayoutThu);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutThu);
                }
            }
            //........................

            //Frieday
            Fridata = new String[Timedatalist.size()];
            FriST = new String[Timedatalist.size()];
            FriET = new String[Timedatalist.size()];
            FriId = new String[Timedatalist.size()];

            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CFriTime.length; i++) {
                    if (Timedata[j].equals(CFriTime[i])) {
                        Fridata[j] = CFri[i];
                        FriST[j] = CFriTime[i];
                        FriET[j] = CFriETime[i];
                        FriId[j]=CFriId[i];
                    }
                }
                DaySTCompare = FriST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata=Fridata[j];
                    MonETCompare = FriET[j];
                    strDayId=FriId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width,LayoutFri);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutFri);
                }
            }
            //........................

            //Saturday
            Satdata = new String[Timedatalist.size()];
            SatST = new String[Timedatalist.size()];
            SatET = new String[Timedatalist.size()];
            SatId = new String[Timedatalist.size()];
            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CSatTime.length; i++) {
                    if (Timedata[j].equals(CSatTime[i])) {
                        Satdata[j] = CSat[i];
                        SatST[j] = CSatTime[i];
                        SatET[j] = CSatETime[i];
                        SatId[j]=CSatId[i];
                    }
                }
                DaySTCompare = SatST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata=Satdata[j];
                    MonETCompare = SatET[j];
                    strDayId=SatId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width,LayoutSat);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutSat);
                }
            }
            //........................


            //Sunday
            Sundata = new String[Timedatalist.size()];
            SunST = new String[Timedatalist.size()];
            SunET = new String[Timedatalist.size()];
            SunId = new String[Timedatalist.size()];
            for (j = 0; j < Timedatalist.size(); j++) {
                for (int i = 0; i < CSunTime.length; i++) {
                    if (Timedata[j].equals(CSunTime[i])) {
                        Sundata[j] = CSun[i];
                        SunST[j] = CSunTime[i];
                        SunET[j] = CSunETime[i];
                        SunId[j]=CSunId[i];
                    }
                }
                DaySTCompare = SunST[j];
                if (DaySTCompare != null && DaySTCompare.length() != 0) {
                    strMondata=Sundata[j];
                    MonETCompare = SunET[j];
                    strDayId=SunId[j];
                    AddDataInLandscape(DaySTCompare, strMondata, MonETCompare,strDayId, height, width,LayoutSun);
                    j = j + (DStandEt / 2) - 1;
                } else {
                    AddSpaceInLandscape(LayoutSun);
                }
            }
        }
        //........................
    }

    public void AddDataInLandscape(String DaySTCompare,String strMondata,String MonETCompare,String strDayId,int height,int width,LinearLayout LayoutDay) {
        SplitMondSTCompare = DaySTCompare.split(" ");
        shourSplitMondSTCompare = SplitMondSTCompare[0];
        dateSTime = new Date();
        dateSTime.setTime((((Integer.parseInt(shourSplitMondSTCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMondSTCompare.split(":")[1]))) + dateSTime.getTimezoneOffset()) * 60000);
        ST = dateSTime.getHours() * 60 + dateSTime.getMinutes();

        SplitMonETCompare = MonETCompare.split(" ");
        shourSplitMonETCompare = SplitMonETCompare[0];
        dateETime = new Date();
        dateETime.setTime((((Integer.parseInt(shourSplitMonETCompare.split(":")[0])) * 60 + (Integer.parseInt(shourSplitMonETCompare.split(":")[1]))) + dateETime.getTimezoneOffset()) * 60000);
        ET = dateETime.getHours() * 60 + dateETime.getMinutes();
        DStandEt = ((ET - ST) * 2);
        jump = DStandEt / height;
        LDay = new LinearLayout(getContext());
        Day = new TextView(getContext());
        Day.setBackgroundResource(R.drawable.gradientbottom);
        Day.setId(Integer.parseInt(strDayId));
       //Log.d("strDayId",strDayId);
        Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView ss=(TextView) view;
                ConfirmUpdate(ss.getId());
            }
        });
        Day.setPadding(0,5,0,5);
        Day.setText(Html.fromHtml("<small><font size=\"10 \" color=\"#008080\">" + DaySTCompare + "</font></small>" + "<br>" + strMondata));
        Day.setGravity(Gravity.CENTER_HORIZONTAL);
        Day.setEllipsize(TextUtils.TruncateAt.END);
        Day.setMaxLines(DStandEt / 30);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lp = new Toolbar.LayoutParams(width, DStandEt);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LDay.addView(Day, lp);
        }
        LayoutDay.addView(LDay);

    }

    public void AddSpaceInLandscape(LinearLayout LayoutDay) {
        LDay = new LinearLayout(getContext());
        Day = new TextView(getContext());
        Day.setText(" ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lp = new Toolbar.LayoutParams(width, 2);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LDay.addView(Day, lp);
        }
        LayoutDay.addView(LDay);
    }

    public void ConfirmUpdate(final int storDayId){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
        LayoutInflater li = LayoutInflater.from(getContext());
        final View promptsView = li.inflate(R.layout.updatelandscapedata, null);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        show = alertDialogBuilder.show();

        datafield=(LinearLayout)promptsView.findViewById(R.id.datafield);
        datafield.setVisibility(promptsView.GONE);
        ButtonAddUpdate=(Button) promptsView.findViewById(R.id.ButtonAddUpdate);
        ButtonDelete=(Button)promptsView.findViewById(R.id.ButtonDelete);
        Subject=(AutoCompleteTextView) promptsView.findViewById(R.id.Subject);
        Venue=(AutoCompleteTextView) promptsView.findViewById(R.id.Venue);
        AlermBefore=(EditText)promptsView.findViewById(R.id.AlermBefore);
        AlermRepeat=(CheckBox)promptsView.findViewById(R.id.AlermRepeat);
        gotoViewPager=(TextView)promptsView.findViewById(R.id.gotoViewPager);
        ButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to Delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                                SQLITEDATABASE.execSQL("DELETE FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_ID + " = '" + storDayId + "'");

                                fm1 = getActivity().getSupportFragmentManager();
                                ft1 = fm1.beginTransaction();
                                frag = new MyScheduleLandScape();
                                ft1.replace(R.id.content_frame, frag);
                                ft1.commit();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();

                show.dismiss();
            }
        });
        gotoViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm1 = getActivity().getSupportFragmentManager();
                ft1 = fm1.beginTransaction();
                frag = new MySchedules();
                ft1.replace(R.id.content_frame, frag);
                ft1.commit();
                show.dismiss();
            }
        });
        ButtonAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonDelete.setVisibility(promptsView.GONE);
                datafield.setVisibility(promptsView.VISIBLE);
                StrSubject=Subject.getText().toString();
                StrVenue=Venue.getText().toString();
                StrAlembefor=AlermBefore.getText().toString();
                if(StrSubject.length()==0) {
                    Subject.requestFocus();
                    Subject.setError(Error);
                }
                else if(StrVenue.length()==0) {
                    Venue.requestFocus();
                    Venue.setError(Error);
                }
                else if((StrAlembefor.length()==0) &&(AlermRepeat.isChecked())) {
                    AlermBefore.requestFocus();
                    AlermBefore.setError(Error);
                }
                else if(!StrAlembefor.equals("") && !AlermRepeat.isChecked()){
                    snackbar1 = Snackbar.make(getView(), "Tick Checkbox!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                }
                else {
                    if(StrAlembefor.length()==0){
                        StrAlembefor="00";
                    }
                    SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                    SQLITEDATABASE.execSQL(" UPDATE " + SQLITEHELPER.TABLE_NAME + " SET "  + SQLITEHELPER.KEY_Subject + "= '" + StrSubject + "' ," + SQLITEHELPER.KEY_Venue + "= '" + StrVenue + "' ," + SQLITEHELPER.KEY_AlermBefor + "= '" + StrAlembefor + "' WHERE " + SQLITEHELPER.KEY_ID + " = '" + storDayId + "'");

                    fm1 = getActivity().getSupportFragmentManager();
                    ft1 = fm1.beginTransaction();
                    frag = new MyScheduleLandScape();
                    ft1.replace(R.id.content_frame, frag);
                    ft1.commit();

                    show.dismiss();
                }
            }
        });
    }
}