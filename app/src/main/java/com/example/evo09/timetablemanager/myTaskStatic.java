package com.example.evo09.timetablemanager;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import static android.content.Context.MODE_PRIVATE;

public class myTaskStatic extends Fragment implements View.OnClickListener{
    private ProgressDialog csprogress;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    Date STimedate,ETimedate,BSTimedate;

    EditText StartTime,EndTime,BreakStartTime,PeriodDuration,BreakDuration,Alarmbefore;
    Button buttonSubmit,backstack;
    int TimeFlag=0,shour,smint,ehour,emint,intStartTime,intEndTime,intBreakStartTime;
    String getStartTime,getEndTime,getBreakStartTime,getPeriodDuration,getBreakDuration,getAlarmbefore,StrStartTime,StrEndTime;
    Snackbar snackbar1;
    String getTime;
    private String Strday[] = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday","Friday","Saturday","Sunday" };
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview= inflater.inflate(R.layout.fragment_staticform, container, false);
        SQLITEHELPER = new SQLiteHelper(getActivity());
        csprogress = new ProgressDialog(getContext());

        StartTime=(EditText)rootview.findViewById(R.id.StartTime);
        EndTime=(EditText)rootview.findViewById(R.id.EndTime);
        BreakStartTime=(EditText)rootview.findViewById(R.id.BreakStartTime);
        Alarmbefore=(EditText) rootview.findViewById(R.id.Alarmbefore);
        BreakDuration=(EditText)rootview.findViewById(R.id.BreakDuration);
        PeriodDuration=(EditText)rootview.findViewById(R.id.PeriodDuration);

        buttonSubmit=(Button)rootview.findViewById(R.id.buttonSubmit);
        backstack=(Button)rootview.findViewById(R.id.backstack);

        backstack.setOnClickListener(this);
        StartTime.setOnClickListener(this);
        EndTime.setOnClickListener(this);
        BreakStartTime.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);

        return rootview;
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
    public void onPrepareOptionsMenu(Menu main ) {
        main.clear();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backstack:{
                ((MainActivity) getActivity()).WhenNullRecord();
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
            case R.id.BreakStartTime:{
                TimeFlag=123;
                SetTime();
                break;
            }
            case R.id.buttonSubmit:{
                ButtonSubmit();
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
                int inthour = selectedHour;
                int intminute=selectedMinute;
                getTime=String.format("%02d:%02d %s", inthour == 0 ? 12 : inthour, intminute, inthour < 12 ? "AM" : "PM");
                if(TimeFlag==789) {
                    StartTime.setText(getTime);
                    intStartTime = inthour * 60 + intminute;
                }
                else if(TimeFlag==456) {
                    EndTime.setText(getTime);
                    intEndTime=inthour * 60 + intminute;
                }
                else if( TimeFlag==123){
                    BreakStartTime.setText(getTime);
                    intBreakStartTime=inthour * 60 + intminute;
                }

            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
    public void ButtonSubmit(){
        getStartTime=StartTime.getText().toString();
        getEndTime=EndTime.getText().toString();
        getBreakStartTime=BreakStartTime.getText().toString();
        getPeriodDuration=PeriodDuration.getText().toString();
        getBreakDuration=BreakDuration.getText().toString();
        getAlarmbefore=Alarmbefore.getText().toString();
        CheckEmpty(getStartTime,getEndTime,getBreakStartTime,getPeriodDuration,getBreakDuration,getAlarmbefore,intStartTime,intEndTime,intBreakStartTime);
    }
    public void  CheckEmpty(String getStartTime,String getEndTime,String getBreakStartTime,String getPeriodDuration,String getBreakDuration,String getAlarmbefore,int intStartTime,int intEndTime,int intBreakStartTime){
        if(TextUtils.isEmpty(getStartTime) || getStartTime.length()==0 || TextUtils.isEmpty(getEndTime)  || getEndTime.length()==0 || TextUtils.isEmpty(getBreakStartTime) || getBreakStartTime.length()==0 || TextUtils.isEmpty(getPeriodDuration) || getPeriodDuration.length()==0  || TextUtils.isEmpty(getBreakDuration) || getBreakDuration.length()==0 ){
            snackbar1 = Snackbar.make(getView(), "Error: all fields are required", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(getPeriodDuration.length()==1){
            snackbar1 = Snackbar.make(getView(), "Error! Check Period Duration!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(intStartTime>=intBreakStartTime ) {
            snackbar1 = Snackbar.make(getView(), "Error! Break-Start-time must be greater then Start-time!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(intBreakStartTime<intStartTime+Integer.parseInt(getPeriodDuration)){
            snackbar1 = Snackbar.make(getView(), "Error! Break-Start-time overlapping Period Duration !", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(getBreakDuration.length()==1){
            snackbar1 = Snackbar.make(getView(), "Error! Check Break Duration!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if( intEndTime<=intBreakStartTime){
            snackbar1 = Snackbar.make(getView(), "Error! End-time must be greater then Break-Start-time!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(intEndTime<intBreakStartTime+Integer.parseInt(getBreakDuration)){
            snackbar1 = Snackbar.make(getView(), "Error! End-time overlapping Break Duration !", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else {
            if((intBreakStartTime-intStartTime)%Integer.parseInt(getPeriodDuration)==0){
                if((intEndTime-(intBreakStartTime+Integer.parseInt(getBreakDuration)))%Integer.parseInt(getPeriodDuration)==0){
                    StaticDBCreate(getStartTime,getEndTime,getBreakStartTime,getPeriodDuration,getBreakDuration,getAlarmbefore);
                }
            }
            else {
                snackbar1 = Snackbar.make(getView(), "Error! Provided data is not valid!", Snackbar.LENGTH_SHORT);snackbar1.show();
            }
        }
    }
    public void StaticDBCreate(String getStartTime,String getEndTime,String getBreakStartTime,String getPeriodDuration,String getBreakDuration,String getAlarmbefore){
        SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
        if(SQLITEDATABASE.isOpen()) {
            insertCreateddata(getStartTime,getEndTime,getBreakStartTime,getPeriodDuration,getBreakDuration,getAlarmbefore);
        }
        else {
            snackbar1 = Snackbar.make(getView(), "Error! Something went wrong!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
    }
    public void insertCreateddata(String getStartTime,String getEndTime,String getBreakStartTime,String getPeriodDuration,String getBreakDuration,String getAlarmbefore){

        String[] SplitStime = getStartTime.split(" ");
        String StineSplitStime = SplitStime[0];
        STimedate = new Date();
        STimedate.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + STimedate.getTimezoneOffset()) * 60000);
        int SST = STimedate.getHours() * 60 + STimedate.getMinutes();

        String[] SplitEtime = getEndTime.split(" ");
        String EtineSplitEtime = SplitEtime[0];
        ETimedate = new Date();
        ETimedate.setTime((((Integer.parseInt(EtineSplitEtime.split(":")[0])) * 60 + (Integer.parseInt(EtineSplitEtime.split(":")[1]))) + ETimedate.getTimezoneOffset()) * 60000);
        int SET = ETimedate.getHours() * 60 + ETimedate.getMinutes();

        String[] SplitBreakTime=getBreakStartTime.split(" ");
        String BreakTimeSplitBreakTime = SplitBreakTime[0];
        BSTimedate = new Date();
        BSTimedate.setTime((((Integer.parseInt(BreakTimeSplitBreakTime.split(":")[0])) * 60 + (Integer.parseInt(BreakTimeSplitBreakTime.split(":")[1]))) + BSTimedate.getTimezoneOffset()) * 60000);
        int BST = (BSTimedate.getHours() * 60 + BSTimedate.getMinutes());

        if(getAlarmbefore.length()==0){
            getAlarmbefore="00";
        }

        while (SST <SET) {
            if (SST==BST) {
                shour = STimedate.getHours();
                smint = STimedate.getMinutes();
                StrStartTime=String.format("%02d:%02d %s", shour == 0 ? 12 : shour, smint, shour < 12 ? "AM" : "PM");

                STimedate.setTime(STimedate.getTime() + Integer.parseInt(getBreakDuration) * 60000);
                ehour = STimedate.getHours();
                emint = STimedate.getMinutes();
                StrEndTime=String.format("%02d:%02d %s", ehour == 0 ? 12 : ehour, emint, ehour < 12 ? "AM" : "PM");
                for(int i=0;i<7;i++) {
                    SQLITEDATABASE.execSQL("INSERT or replace INTO " + SQLITEHELPER.TABLE_NAME + " " + "(" + SQLITEHELPER.KEY_DOWeek + "," + SQLITEHELPER.KEY_STime + "," + SQLITEHELPER.KEY_ETime + "," + SQLITEHELPER.KEY_Subject + "," + SQLITEHELPER.KEY_Venue + "," + SQLITEHELPER.KEY_AlermBefor + ")" + " VALUES('" + Strday[i] + "', '" + StrStartTime + "', '" + StrEndTime + "', '" + "Break" + "', '" + "Break" + "' , '" + getAlarmbefore + "');");
                }
            }
            else {
                shour = STimedate.getHours();
                smint = STimedate.getMinutes();
                StrStartTime=String.format("%02d:%02d %s", shour == 0 ? 12 : shour, smint, shour < 12 ? "AM" : "PM");

                STimedate.setTime(STimedate.getTime() + Integer.parseInt(getPeriodDuration) * 60000);
                ehour = STimedate.getHours();
                emint = STimedate.getMinutes();
                StrEndTime=String.format("%02d:%02d %s", ehour == 0 ? 12 : ehour, emint, ehour < 12 ? "AM" : "PM");
                for(int i=0;i<7;i++) {
                    SQLITEDATABASE.execSQL("INSERT or replace INTO " + SQLITEHELPER.TABLE_NAME + " " + "(" + SQLITEHELPER.KEY_DOWeek + "," + SQLITEHELPER.KEY_STime + "," + SQLITEHELPER.KEY_ETime + "," + SQLITEHELPER.KEY_Subject + "," + SQLITEHELPER.KEY_Venue + "," + SQLITEHELPER.KEY_AlermBefor + ")" + " VALUES('" + Strday[i] + "', '" + StrStartTime + "', '" + StrEndTime + "', '" + "Subject" + "', '" + "Venue" + "' , '" + getAlarmbefore + "');");
                }
            }

            SST = STimedate.getHours() * 60 + STimedate.getMinutes();

        }
        csprogress.setMessage("Loading...");
        csprogress.show();
        csprogress.setCancelable(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                chechFragmentStatus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        csprogress.dismiss();
                    }
                }, 200);
            }
        }, 2000);
    }
    public void chechFragmentStatus(){
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME +" ORDER BY " + SQLITEHELPER.KEY_STime + " ASC", null);
        if(cursor.getCount()>0){
            ((MainActivity) getActivity()).WhenRecord();
        }
    }
}
