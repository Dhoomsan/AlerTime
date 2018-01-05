package com.example.evo09.timetablemanager;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SQLiteListAdapter extends BaseAdapter {
    //0.5 seconds
    private String tabtitles[] = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday","Friday","Saturday","Sunday" };
    Context context;
    ArrayList<String> userID;
    ArrayList<String> UserSTime;
    ArrayList<String> UserETime;
    ArrayList<String> UserSubject ;
    ArrayList<String> User_Venue;
    private SparseBooleanArray mSelectedItemsIds=new SparseBooleanArray();
    public SQLiteListAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> stime,
            ArrayList<String> etime,
            ArrayList<String> subject,
            ArrayList<String> venue
    )
    {

        this.context = context2;
        this.userID = id;
        this.UserSTime = stime;
        this.UserETime = etime;
        this.UserSubject = subject ;
        this.User_Venue = venue;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return userID.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return userID.get(position);
    }
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public View getView(int position, View child, ViewGroup parent) {
        Holder holder;
        LayoutInflater layoutInflater;
        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.display, null,true);

            holder = new Holder();

            holder.textviewstime = (TextView) child.findViewById(R.id.textViewSTime);
            holder.textviewetime = (TextView) child.findViewById(R.id.textViewETime);
            holder.textviewsubject = (TextView) child.findViewById(R.id.textViewSubject);
            holder.textviewvenue = (TextView) child.findViewById(R.id.textViewVenue);

            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }
            Calendar cal = Calendar.getInstance();
            int sshour = cal.get(Calendar.HOUR_OF_DAY);
            int ssmint = cal.get(Calendar.MINUTE);
            int ctime = sshour*60+ssmint;
            String shour= UserSTime.get(position);
            String ehour=UserETime.get(position);
            String[] SplitStime=shour.split(" ");
            String[] Splitetime = ehour.split(" ");
            String st = SplitStime[0];
            String et = Splitetime[0];
            Date date1 = new Date();
            date1.setTime((((Integer.parseInt(st.split(":")[0]))*60 + (Integer.parseInt(st.split(":")[1])))+ date1.getTimezoneOffset())*60000);
            int storeshour = date1.getHours();
            int storesmint = date1.getMinutes();
            int storestime=storeshour*60+storesmint;
            Date date2 = new Date();
            date2.setTime((((Integer.parseInt(et.split(":")[0]))*60 + (Integer.parseInt(et.split(":")[1])))+ date1.getTimezoneOffset())*60000);
            int storeehour = date2.getHours();
            int storeemint = date2.getMinutes();
            int storeetime=storeehour*60+storeemint;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            final String dayOfTheWeek = sdf.format(d);
            if (ctime >= storestime && ctime < storeetime) {
                child.setBackgroundColor(Color.parseColor("#E6E8FA"));
            }
            holder.textviewstime.setText(UserSTime.get(position));
            holder.textviewetime.setText(UserETime.get(position));
            holder.textviewsubject.setText(UserSubject.get(position));
            holder.textviewvenue.setText(User_Venue.get(position));
        return child;
    }

    public class Holder {
        TextView textviewstime;
        TextView textviewetime;
        TextView textviewsubject;
        TextView textviewvenue;
    }
    public void  toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
    // Remove selection after unchecked
    public void  removeSelection() {
        mSelectedItemsIds = new  SparseBooleanArray();
        notifyDataSetChanged();
    }
    // Item checked on selection
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position,  value);

        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
    // Get number of selected item
    public int  getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public  SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}