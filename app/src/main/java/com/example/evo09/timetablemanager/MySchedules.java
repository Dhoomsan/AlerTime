package com.example.evo09.timetablemanager;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MySchedules extends Fragment implements View.OnClickListener,ViewPager.OnPageChangeListener {

    Fragment fragment=null;
    Fragment frag;
    FragmentManager fm1;
    FragmentTransaction ft1;

    private ProgressDialog csprogress;
    Button ButtonAddUpdate,ButtonCancel;
    static TextView Dayofweek,StartTime,EndTime;
    int[] EStime,EEtime;

    Boolean timeExist;

    static EditText AlermBefore;
    static AutoCompleteTextView Subject,Venue;
    static CheckBox AlermRepeat,Allday;

    ViewPager pager;
    LinearLayout layout;
    Animation slideUp,slideDown;
    int TimeFlag=0,intstart,intend,pageposition;
    String getTime,Strday,StrStartTime,StrEndTime,StrSubject,StrVenue,StrAlembefor,Error="Field Cannot be empty!",getdataposition;
    Snackbar snackbar1;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    ArrayList<String> autosub=new ArrayList<String>();
    ArrayList<String> autoven=new ArrayList<String>();
    ArrayAdapter<String> sub;
    ArrayAdapter<String> ven;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPREFERENCES" ;
    private SharedPreferences.Editor editor;
    public static final String StoreId = "StoreId";
    public static final String AddUpdateFlag = "AddUpdateFlag";
    private final String DefaultUnameValue = "";
    private final String DefaultInsertUpdateValue = "";
    static  String getStoreId;
    private String InsertUpdateStoreId;
    String insertdata="INSERTDATA", updatedata="UPDATE";


    private String tabtitles[] = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday","Friday","Saturday","Sunday" };
    String cday;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Schedules");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View views= inflater.inflate(R.layout.fragment_my_schedules, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        csprogress=new ProgressDialog(getActivity());

        SQLITEHELPER = new SQLiteHelper(getActivity());
        pager=(ViewPager) views.findViewById(R.id.pager);
        pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        pager.getAdapter().notifyDataSetChanged();
        pager.addOnPageChangeListener(this);

        ButtonAddUpdate=(Button) views.findViewById(R.id.ButtonAddUpdate);
        ButtonCancel=(Button) views.findViewById(R.id.ButtonCancel);

        Dayofweek=(TextView)views.findViewById(R.id.Dayofweek);
        StartTime=(TextView)views.findViewById(R.id.StartTime);
        EndTime=(TextView)views.findViewById(R.id.EndTime);
        StartTime.setOnClickListener(this);
        EndTime.setOnClickListener(this);

        Subject=(AutoCompleteTextView) views.findViewById(R.id.Subject);
        Venue=(AutoCompleteTextView) views.findViewById(R.id.Venue);
        AlermBefore=(EditText)views.findViewById(R.id.AlermBefore);
        AlermBefore.setFilters(new InputFilter[]{new InputFilterMinMax("1", "60")});

        AlermRepeat=(CheckBox)views.findViewById(R.id.AlermRepeat);
        Allday=(CheckBox)views.findViewById(R.id.Allday);

        //Creating the instance of ArrayAdapter containing list of language names
        sub = new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_item,autosub);
        ven = new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_item,autoven);

        Subject.setThreshold(1);//will start working from first character
        Subject.setAdapter(sub);
        Venue.setThreshold(1);//will start working from first character
        Venue.setAdapter(ven);

        ButtonAddUpdate.setOnClickListener(this);
        ButtonCancel.setOnClickListener(this);

        Subject.setOnClickListener(this);
        Venue.setOnClickListener(this);
        Allday.setOnClickListener(this);

        return views;
    }
    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }
        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        DBCreate();
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
        if(cursor.getCount()!=0) {
            if (pager.getChildCount() > 0) {
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek > 1 && dayOfWeek <= 7) {
                    pager.setCurrentItem(dayOfWeek - 2);
                    Dayofweek.setText(tabtitles[dayOfWeek - 2]);
                    cday = tabtitles[dayOfWeek - 2];
                    getdataposition = tabtitles[dayOfWeek - 2];
                } else {
                    pager.setCurrentItem(0);
                }
            }
            autocomplete();
            AddData();
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
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item1=menu.findItem(R.id.action_PORTRAIT);
        item1.setVisible(false);
        /*MenuItem item2=menu.findItem(R.id.action_deleteStatic);
        item2.setVisible(false);*/
    }
    @Override
    public void onClick(View view) {
        layout = (LinearLayout) getActivity().findViewById(R.id.updatelayout);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        switch (view.getId())
        {
            case R.id.ButtonAddUpdate:{
                //Log.d("val","en");
                AddorUpdateData();
                break;
            }
            case R.id.ButtonCancel: {
                layout.startAnimation(slideDown);
                layout.setVisibility(View.GONE);
                break;
            }
            case R.id.StartTime:{
                TimeFlag=789;
                SetTime();
                break;
            }
            case R.id.EndTime:{
                TimeFlag=456;
                SetTime();
                break;
            }
            case R.id.Subject:{
                autocomplete();
                break;
            }
            case R.id.Venue:{
                autocomplete();
                break;
            }
            case R.id.Allday:{
                if(Allday.isChecked()){
                    Dayofweek.setVisibility(View.GONE);
                }
                else {
                    Dayofweek.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }
    public void SetTime(){
        final Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker= new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int hour = selectedHour;
                int minute=selectedMinute;
                getTime=String.format("%02d:%02d %s", hour == 0 ? 12 : hour, selectedMinute, selectedHour < 12 ? "AM" : "PM");
                if(TimeFlag==789) {
                        StartTime.setText(getTime);
                        intstart = hour * 60 + minute;
                   // //Log.d("hour", String.valueOf(intstart)+"  "+getTime);
                }
                if(TimeFlag==456) {
                    EndTime.setText(getTime);
                    intend=hour*60+minute;
                    //Log.d("hour", String.valueOf(intend));
                }

            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
    public void AddorUpdateData() {
        getStoreId="";
        Log.d("getStoreIds",getStoreId);
        getStoreId= sharedpreferences.getString(StoreId, DefaultUnameValue);
        InsertUpdateStoreId= sharedpreferences.getString(AddUpdateFlag, DefaultInsertUpdateValue);

        Log.d("getStoreId",getStoreId);
        Strday=Dayofweek.getText().toString();
        StrStartTime=StartTime.getText().toString();
        StrEndTime=EndTime.getText().toString();
        StrSubject=Subject.getText().toString();
        StrVenue=Venue.getText().toString();
        StrAlembefor=AlermBefore.getText().toString();
        //Log.d("val","yes");
        if(Strday.length()==0){
            snackbar1 = Snackbar.make(getView(), "Day of Week Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrStartTime.length()==0){
            snackbar1 = Snackbar.make(getView(), "Start Time Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrEndTime.length()==0){
            snackbar1 = Snackbar.make(getView(), "End Time Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(intstart>=intend || intstart==0 || StrStartTime.length()==0 || StrEndTime.length()==0){
            snackbar1 = Snackbar.make(getView(), "Start Time Cannot be higher than or Equals to End Time.", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrSubject.length()==0) {
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
           TimeExistOrNot(intstart,intend,StrStartTime,StrEndTime,InsertUpdateStoreId);
            if (insertdata.equals(InsertUpdateStoreId)) {
                if(timeExist==true){
                    Toast.makeText(getActivity(),"Timing  Already Exists in Table update it.", Toast.LENGTH_LONG).show();
                }
                else {
                    DBCreate();
                    InsertDataInTable(Strday, StrStartTime, StrEndTime, StrSubject, StrVenue, StrAlembefor);
                    layout.startAnimation(slideDown);
                    layout.setVisibility(View.GONE);
                    pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
                    pager.setCurrentItem(pageposition);
                }
            }
            else if (updatedata.equals(InsertUpdateStoreId)) {
                if (getStoreId.isEmpty()) {
                    snackbar1 = Snackbar.make(getView(), "Error Add data first!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                }
                else{
                    if(timeExist==true){
                        if(StrAlembefor.length()==0){
                            StrAlembefor="00";
                        }
                        AlertDialogShow(getStoreId, StrStartTime, StrEndTime, StrSubject, StrVenue, StrAlembefor);
                    }
                    else {
                        UpdateDataInTable(getStoreId, StrStartTime, StrEndTime, StrSubject, StrVenue, StrAlembefor);
                        layout.startAnimation(slideDown);
                        layout.setVisibility(View.GONE);
                        pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
                        pager.setCurrentItem(pageposition);
                    }
                }
            }
        }
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
    public void InsertDataInTable(String Strday,String StrStartTime,String StrEndTime,String StrSubject,String StrVenue,String StrAlembefor) {
        if(StrAlembefor.length()==0){
            StrAlembefor="00";
        }
        if(Allday.isChecked()){
                for(int i=0;i<7;i++) {
                    String SQLiteQueryWEEKTABLE = "INSERT or replace INTO " + SQLITEHELPER.TABLE_NAME + " " + "(" + SQLITEHELPER.KEY_DOWeek + "," + SQLITEHELPER.KEY_STime + "," + SQLITEHELPER.KEY_ETime + "," + SQLITEHELPER.KEY_Subject + "," + SQLITEHELPER.KEY_Venue + "," + SQLITEHELPER.KEY_AlermBefor + ")" + " VALUES('" + tabtitles[i] + "', '" + StrStartTime + "', '" + StrEndTime + "', '" + StrSubject + "', '" + StrVenue + "' , '" + StrAlembefor + "');";
                    SQLITEDATABASE.execSQL(SQLiteQueryWEEKTABLE);
                }
            }
            else {
                String SQLiteQueryWEEKTABLE = "INSERT or replace INTO " + SQLITEHELPER.TABLE_NAME + " " + "(" + SQLITEHELPER.KEY_DOWeek + "," + SQLITEHELPER.KEY_STime + "," + SQLITEHELPER.KEY_ETime + "," + SQLITEHELPER.KEY_Subject + "," + SQLITEHELPER.KEY_Venue + " ," + SQLITEHELPER.KEY_AlermBefor + ")" + " VALUES('" + Strday + "', '" + StrStartTime + "', '" + StrEndTime + "', '" + StrSubject + "', '" + StrVenue + "' , '" + StrAlembefor + "');";
                SQLITEDATABASE.execSQL(SQLiteQueryWEEKTABLE);
                snackbar1 = Snackbar.make(getView(), "Inserted Successfully", Snackbar.LENGTH_SHORT);
                snackbar1.show();
                //Log.d("SQV", "days");
            }
    }
    public void UpdateDataInTable(String getStoreId,String StrStartTime,String StrEndTime,String StrSubject,String StrVenue,String StrAlembefor) {
        if(StrAlembefor.length()==0){
            StrAlembefor="00";
        }
        String St=StartTime.getText().toString();
        if(Allday.isChecked()){
            for(int i=0;i<7;i++) {
                SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                SQLITEDATABASE.execSQL(" UPDATE " + SQLITEHELPER.TABLE_NAME + " SET " + SQLITEHELPER.KEY_STime + " = '" + StrStartTime + "' ," + SQLITEHELPER.KEY_ETime + "= '" + StrEndTime + "' ," + SQLITEHELPER.KEY_Subject + "= '" + StrSubject + "' ," + SQLITEHELPER.KEY_Venue + "= '" + StrVenue + "' ," + SQLITEHELPER.KEY_AlermBefor + "= '" + StrAlembefor + "' WHERE " + SQLITEHELPER.KEY_STime + " = '" + St + "'");
            }
        }
        else {
            SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
            SQLITEDATABASE.execSQL(" UPDATE " + SQLITEHELPER.TABLE_NAME + " SET " + SQLITEHELPER.KEY_STime + " = '" + StrStartTime + "' ," + SQLITEHELPER.KEY_ETime + "= '" + StrEndTime + "' ," + SQLITEHELPER.KEY_Subject + "= '" + StrSubject + "' ," + SQLITEHELPER.KEY_Venue + "= '" + StrVenue + "' ," + SQLITEHELPER.KEY_AlermBefor + "= '" + StrAlembefor + "' WHERE " + SQLITEHELPER.KEY_ID + " = '" + getStoreId + "'");
            snackbar1 = Snackbar.make(getView(), "Updation Successful", Snackbar.LENGTH_SHORT);
            snackbar1.show();
            SQLITEDATABASE.close();
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {

        pageposition=position;
        getdataposition=tabtitles[position];
        Dayofweek.setText(tabtitles[position]);
        autocomplete();
        AddData();
    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void autocomplete() {
        DBCreate();
        cursor = SQLITEDATABASE.rawQuery("SELECT " + SQLITEHELPER.KEY_Subject +"," + SQLITEHELPER.KEY_Venue + " FROM " + SQLITEHELPER.TABLE_NAME+" GROUP BY " + SQLITEHELPER.KEY_Subject + " ORDER BY " + SQLITEHELPER.KEY_Subject + " DESC", null);
        autosub.clear();
        autoven.clear();
        while (cursor != null && cursor.moveToNext()) {
            autosub.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Subject)));
            autoven.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Venue)));
            //Log.d("auto", String.valueOf(autoven)+"\n");
        }
    }
    public void AddData() {
        DBCreate();
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = '" + getdataposition + "' ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        while (cursor != null && cursor.moveToNext()) {
            String Stime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime));
            String Etime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ETime));
            String[] SplitStime = Stime.split(" ");
            String[] SplitEtime = Etime.split(" ");
            String StineSplitStime = SplitStime[0];
            String EtineSplitEtime = SplitEtime[0];
            Date date1 = new Date();
            date1.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
            Date date2 = new Date();
            date2.setTime((((Integer.parseInt(EtineSplitEtime.split(":")[0])) * 60 + (Integer.parseInt(EtineSplitEtime.split(":")[1]))) + date2.getTimezoneOffset()) * 60000);
            int shour = date1.getHours();
            int smint = date1.getMinutes();
            int ehour = date2.getHours();
            int emint = date2.getMinutes();
            int sdiff = shour * 60 + smint;
            int Ediff = ehour * 60 + emint;
            int diff = Ediff - sdiff;
            Date date3 = new Date();
           // //Log.d("Diff", String.valueOf(diff));
            date3.setTime(date2.getTime() + (diff * 60000));
            int hour = date3.getHours();
            int mint = date3.getMinutes();
            intstart = shour * 60 + smint;
            intend = hour * 60 + mint;
           // //Log.d("split", String.valueOf(hour));
            StartTime.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ETime)));
            EndTime.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM"));
            Subject.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Subject)));
            Venue.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Venue)));

        }
    }
    public void TimeExistOrNot(int intstart, int intend, String StrStartTime,String StrEndTime,String InsertUpdateStoreId) {
        DBCreate();
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = '" + getdataposition + "' ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);
        EStime=new int[cursor.getCount()];
        EEtime=new int[cursor.getCount()];
        int a = 0;
        while (cursor != null && cursor.moveToNext()) {
            String Stime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime));
            String Etime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ETime));
            String[] SplitStime = Stime.split(" ");
            String[] SplitEtime = Etime.split(" ");
            String StineSplitStime = SplitStime[0];
            String EtineSplitEtime = SplitEtime[0];
            Date date1 = new Date();
            date1.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
            Date date2 = new Date();
            date2.setTime((((Integer.parseInt(EtineSplitEtime.split(":")[0])) * 60 + (Integer.parseInt(EtineSplitEtime.split(":")[1]))) + date2.getTimezoneOffset()) * 60000);
            int shour = date1.getHours();
            int smint = date1.getMinutes();
            int ehour = date2.getHours();
            int emint = date2.getMinutes();
            int sdiff = shour * 60 + smint;
            int Ediff = ehour * 60 + emint;
            EStime[a]=sdiff;
            EEtime[a]=Ediff;
            a++;
        }
        if(cursor.getCount()==0){
            //Log.d("dhfdhf5", "on");
            timeExist = false;
        }
        /*else if(InsertUpdateStoreId.equals(updatedata)){

        }*/
        else {

            String[] SplitStime = StrStartTime.split(" ");
            String[] SplitEtime = StrEndTime.split(" ");
            String StineSplitStime = SplitStime[0];
            String EtineSplitEtime = SplitEtime[0];
            Date date1 = new Date();
            date1.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
            Date date2 = new Date();
            date2.setTime((((Integer.parseInt(EtineSplitEtime.split(":")[0])) * 60 + (Integer.parseInt(EtineSplitEtime.split(":")[1]))) + date2.getTimezoneOffset()) * 60000);
            intstart = date1.getHours()*60+date1.getMinutes();
            intend = date2.getHours()*60+date2.getMinutes();

            for (int j = 0; j < EStime.length; j++) {
                if (intstart >= EStime[j] && intstart < EEtime[j]) {
                    //Log.d("dhfdhf1", String.valueOf(intstart) + " >=" + EStime[j] + " && " + String.valueOf(intstart) + "<" + EEtime[j]);
                    timeExist = true;
                    break;
                } else if (intend > EStime[j] && intend < EEtime[j]) {
                   // Log.d("dhfdhf2", String.valueOf(intstart) + " >=" + EStime[j] + " && " + String.valueOf(intstart) + "<" + EEtime[j]);
                    timeExist = true;
                    break;
                } else if (intstart < EStime[j] && intend > EEtime[j]) {
                   // Log.d("dhfdhf3", String.valueOf(intstart) + " >=" + EStime[j] + " && " + String.valueOf(intstart) + "<" + EEtime[j]);
                    timeExist = true;
                    break;
                } else {
                    //Log.d("dhfdhf4", "one");
                    timeExist = false;
                }
            }
        }
    }
    public void AlertDialogShow(final String getStoreId, final String StrStartTime, final String StrEndTime, final String StrSubject, final String StrVenue, final String StrAlembefor){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Timing  Already Existing"+"\n"+"click ' Yes' to Push Timing below")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DBCreate();
                        String getdataposition=Dayofweek.getText().toString();
                        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = '" + getdataposition + "' AND " + SQLITEHELPER.KEY_STime + " >= '"+ StrStartTime +"' ", null);
                        while (cursor != null && cursor.moveToNext()) {
                            SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                            SQLITEDATABASE.execSQL(" UPDATE " + SQLITEHELPER.TABLE_NAME + " SET " + SQLITEHELPER.KEY_STime + " = '" + StrStartTime + "' ," + SQLITEHELPER.KEY_ETime + "= '" + StrEndTime + "' ," + SQLITEHELPER.KEY_Subject + "= '" + StrSubject + "' ," + SQLITEHELPER.KEY_Venue + "= '" + StrVenue + "' ," + SQLITEHELPER.KEY_AlermBefor + "= '" + StrAlembefor + "' WHERE " + SQLITEHELPER.KEY_ID + " = '" + getStoreId + "'");

                        }
                        snackbar1 = Snackbar.make(getView(), "Updated Successfully!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
