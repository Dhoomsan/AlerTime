package com.example.evo09.timetablemanager;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MyStaticSchedules extends Fragment implements View.OnClickListener {

    private ProgressDialog csprogress;
    static TextView StartTime,EndTime,BreakStartTime;
    static EditText PeriodDuration,BreakDuration;
    Button buttonSubmit;
    Snackbar snackbar1;
    Toolbar.LayoutParams lp;
    LinearLayout Showstatictable,createstatictable;
    static int TimeFlag=0,intstart=0,intend=0,intBstart,TimeSBmin,TimeTotal,Timeplus,TimeABEdiv,Timebbtal,TimeAbtal;
    String getTime,StrStartTime,StrEndTime,StrBreakStartTime,StrPeriodDuration,StrBreakDuration,StrbuttonSubmit;
    Date date;

    SharedPreferences Staticsharedpreferences;
    public static final String Staticmypreference = "Staticmypreference";
    public static final String StaticStartTime = "StaticStartTime";
    public static final String StaticEndTime = "StaticEndTime";
    public static final String StaticBreakStartTime = "StaticBreakStartTime";
    public static final String StaticPeriodDuration = "StaticPeriodDuration";
    public static final String StaticBreakDuration = "StaticBreakDuration";
    public static final String StaticbuttonSubmit = "StaticbuttonSubmit";
    public static final String StaticTotalClass = "StaticTotalClass";
    private final String DefaultbuttonSubmitValue = "";
    private final String DefaultStartTimeValue = "";
    private final String DefaultEndTimeValue = "";
    private final String DefaultBreakStartTimeValue = "";
    private final String DefaultPeriodDurationValue = "";
    private final String DefaultBreakDurationValue = "";
    private final String DefaultTotalClassValue = "";
    String getbuttonSubmitStatus,getStaticStartTime,getStaticEndTime,getStaticBreakStartTime,getStaticPeriodDuration,getStaticBreakDuration,getStaticTotalClass;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Schedules");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_my_static_schedules, container, false);
        csprogress=new ProgressDialog(getActivity());

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

        Staticsharedpreferences = getActivity().getSharedPreferences(Staticmypreference, Context.MODE_PRIVATE);
        getbuttonSubmitStatus= Staticsharedpreferences.getString(StaticbuttonSubmit, DefaultbuttonSubmitValue);
        getStaticStartTime= Staticsharedpreferences.getString(StaticStartTime, DefaultStartTimeValue);
        getStaticEndTime= Staticsharedpreferences.getString(StaticEndTime, DefaultEndTimeValue);
        getStaticBreakStartTime= Staticsharedpreferences.getString(StaticBreakStartTime, DefaultBreakStartTimeValue);
        getStaticPeriodDuration= Staticsharedpreferences.getString(StaticPeriodDuration, DefaultPeriodDurationValue);
        getStaticBreakDuration=Staticsharedpreferences.getString(StaticBreakDuration, DefaultBreakDurationValue);
        getStaticTotalClass=Staticsharedpreferences.getString(StaticTotalClass, DefaultTotalClassValue);

        Showstatictable = (LinearLayout) rootview.findViewById(R.id.Showstatictable);
        createstatictable=(LinearLayout) rootview.findViewById(R.id.createstatictable);

        StartTime=(TextView)rootview.findViewById(R.id.StartTime);
        EndTime=(TextView)rootview.findViewById(R.id.EndTime);
        BreakStartTime=(TextView)rootview.findViewById(R.id.BreakStartTime);

        PeriodDuration=(EditText)rootview.findViewById(R.id.PeriodDuration);
        BreakDuration=(EditText)rootview.findViewById(R.id.BreakDuration);

        buttonSubmit=(Button)rootview.findViewById(R.id.buttonSubmit);

        //set Listener
        StartTime.setOnClickListener(this);
        EndTime.setOnClickListener(this);
        BreakStartTime.setOnClickListener(this);

        PeriodDuration.setOnClickListener(this);
        BreakDuration.setOnClickListener(this);

        buttonSubmit.setOnClickListener(this);

        return rootview;
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        csprogress.setMessage("Loading...");
        csprogress.show();
        csprogress.setCancelable(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(getbuttonSubmitStatus.length()==0){
                    Log.d("getbuttonSubmitStatus","dhgsd"+getbuttonSubmitStatus);
                    Showstatictable.setVisibility(View.GONE);
                    createstatictable.setVisibility(View.VISIBLE);
                }
                else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    createstatictable.setVisibility(View.GONE);
                    Showstatictable.setVisibility(View.VISIBLE);
                    StaticTableView();
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        csprogress.dismiss();
                    }
                }, 200);
            }
        }, 600);//just mention the time when you want to launch your action
    }
    public void onPrepareOptionsMenu(Menu main ) {
        //menu.clear();
        //menu.clear();
        MenuItem item = main.findItem(R.id.action_add);
        item.setVisible(false);
        MenuItem item2 = main.findItem(R.id.action_LANDSCAPE);
        item2.setVisible(false);
        MenuItem item3=main.findItem(R.id.action_PORTRAIT);
        item3.setVisible(false);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                    int hour = selectedHour;
                    int minute=selectedMinute;
                    getTime=String.format("%02d:%02d %s", hour == 0 ? 12 : hour, selectedMinute, selectedHour < 12 ? "AM" : "PM");
                    if(TimeFlag==789) {
                        StartTime.setText(getTime);
                        intstart = hour * 60 + minute;
                        //Log.d("hourss123", String.valueOf(intstart)+" s "+getTime);
                    }
                    else if(TimeFlag==456) {
                        EndTime.setText(getTime);
                        intend=hour*60+minute;
                        //Log.d("hourss456", String.valueOf(intend)+" e "+getTime);
                    }
                    else if( TimeFlag==123){
                        BreakStartTime.setText(getTime);
                        intBstart=hour*60+minute;
                        //Log.d("hourss456", String.valueOf(intBstart)+" e "+getTime);
                    }

                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
    }
    public void ButtonSubmit() {
        // //Log.d("getStoreId",getStoreId);
        StrStartTime=StartTime.getText().toString();
        StrEndTime=EndTime.getText().toString();
        StrBreakStartTime=BreakStartTime.getText().toString();
        StrPeriodDuration=PeriodDuration.getText().toString();
        StrBreakDuration=BreakDuration.getText().toString();
        StrbuttonSubmit=buttonSubmit.getText().toString();
        if(StrStartTime.length()==0){
            snackbar1 = Snackbar.make(getView(), "Start Time Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrEndTime.length()==0){
            snackbar1 = Snackbar.make(getView(), "End Time Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(intstart>=intend || intstart==0){
            //Log.d("hourss", String.valueOf(intstart)+"-"+ String.valueOf(intend));
            snackbar1 = Snackbar.make(getView(), "Start Time Cannot be higher than or Equals to End Time!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrPeriodDuration.length()==0) {
            snackbar1 = Snackbar.make(getView(), "Period Duration Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrBreakStartTime.length()==0){
            snackbar1 = Snackbar.make(getView(), "Break Start Time Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(intBstart<=intstart || intBstart>=intend ){
            //Log.d("hourss", String.valueOf(intstart)+"-"+ String.valueOf(intend));
            snackbar1 = Snackbar.make(getView(), "Break Start Time Cannot be Less than Start Time or higher than  End Time!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else if(StrBreakDuration.length()==0) {
            snackbar1 = Snackbar.make(getView(), "Break Duration Cannot be empty!", Snackbar.LENGTH_SHORT);snackbar1.show();
        }
        else {
            TimeSBmin=intBstart-intstart;
            Timeplus=intBstart+Integer.parseInt(StrBreakDuration);
            TimeABEdiv=intend-Timeplus;
            Timebbtal=TimeSBmin / Integer.parseInt(StrPeriodDuration);
            TimeAbtal=TimeABEdiv/ Integer.parseInt(StrPeriodDuration);
            TimeTotal=Timebbtal+TimeAbtal+2;
            if((TimeSBmin % Integer.parseInt(StrPeriodDuration)==0) &&(TimeABEdiv % Integer.parseInt(StrPeriodDuration)==0 )) {
                Log.d("Timemin", String.valueOf(TimeSBmin)+"-"+TimeABEdiv+"-"+TimeTotal);

            SharedPreferences.Editor editor = Staticsharedpreferences.edit();
            editor.putString(StaticStartTime, StrStartTime);
            editor.putString(StaticEndTime, StrEndTime);
            editor.putString(StaticBreakStartTime, StrBreakStartTime);
            editor.putString(StaticPeriodDuration, StrPeriodDuration);
            editor.putString(StaticBreakDuration, StrBreakDuration);
            editor.putString(StaticbuttonSubmit, StrbuttonSubmit);
            editor.putString(StaticTotalClass, String.valueOf(TimeTotal));
            editor.commit();
            //snackbar1 = Snackbar.make(getView(), "Created Successfully", Snackbar.LENGTH_SHORT);snackbar1.show();

            csprogress.setMessage("Loading...");
            csprogress.show();
            csprogress.setCancelable(false);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    StaticTableView();
                    createstatictable.setVisibility(View.GONE);
                    Showstatictable.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            csprogress.dismiss();
                        }
                    }, 200);
                }
            }, 600);//just mention the time when you want to launch your action*/
            }
            else
            {
                snackbar1 = Snackbar.make(getView(), "it doesn't match with standers!"+"\n"+"Please Choose Dynamic Schedule!", Snackbar.LENGTH_SHORT);snackbar1.show();
            }
        }
    }

    public void StaticTableView(){

        TimeTotal=Integer.parseInt(getStaticTotalClass);

        String[] SplitStime = getStaticStartTime.split(" ");
        //String[] SplitEtime = getStaticEndTime.split(" ");
        String StineSplitStime = SplitStime[0];
        //String EtineSplitEtime = SplitEtime[0];
        // Log.d("StineSplitStime",StineSplitStime+ " e "+EtineSplitEtime);

        Log.d("totalc", String.valueOf(TimeTotal));
        date = new Date();
        date.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + date.getTimezoneOffset()) * 60000);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(50, 100);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.linearLayout1);
        Display display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth() / 7;
        int height = display.getHeight() / TimeTotal;

        for (int i = 0; i < TimeTotal; i++) {
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 7; j++) {
                EditText et = new EditText(getContext());
                TextView et1 = new TextView(getContext());
                et.setBackgroundColor(Color.TRANSPARENT);
                et1.setBackgroundColor(Color.TRANSPARENT);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
                et1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
                //et.setLayoutParams(param1);
                et.setGravity(Gravity.CENTER_VERTICAL);
                et1.setGravity(Gravity.CENTER_VERTICAL);
                et.setPadding(5, 5, 5, 5);
                et1.setPadding(5, 5, 5, 5);

                if (i == 0 && j == 0) {
                    int hour = date.getHours();
                    int mint = date.getMinutes();
                    et1.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM"));
                    et1.setBackgroundResource(R.drawable.gradientbottom);
                } else if (i > 0 && j == 0) {
                    date.setTime(date.getTime() + Integer.parseInt(getStaticPeriodDuration) * 60000);
                    int hour = date.getHours();
                    int mint = date.getMinutes();
                    et1.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM"));
                    et1.setBackgroundResource(R.drawable.gradientbottom);
                } else {
                    et.setText(" Tap to write ");
                    et.setBackgroundResource(R.drawable.gradientbottom);
                }
                Toolbar.LayoutParams lp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, height);
                    l.addView(et, lp);
                    l.addView(et1, lp);
            }
            ll.addView(l);
        }

    }
}
