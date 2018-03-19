package com.evolvan.timegrid;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SQLiteListAdapter extends BaseAdapter {

    int count;
    Calendar cal;
    int sshour,ssmint,ctime,storeshour,storesmint,storestime,storeehour,storeemint,storeetime;
    String shour,ehour,st,et,cb;
    String[] SplitStime,Splitetime;
    Date date1,date2;
    Holder holder = null;
    LayoutInflater layoutInflater;
    Context context;
    ArrayList<String> userID;
    ArrayList<String> UserSTime;
    ArrayList<String> UserETime;
    ArrayList<String> UserSubject ;
    ArrayList<String> User_Venue;
    ArrayList<String> User_Alarm;
    private SparseBooleanArray mSelectedItemsIds=new SparseBooleanArray();
    public SQLiteListAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> stime,
            ArrayList<String> etime,
            ArrayList<String> subject,
            ArrayList<String> venue,
            ArrayList<String> alarm
    )
    {

        this.context = context2;
        this.userID = id;
        this.UserSTime = stime;
        this.UserETime = etime;
        this.UserSubject = subject ;
        this.User_Venue = venue;
        this.User_Alarm = alarm;
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

    private  class Holder {
        TextView textviewstime;
        TextView textviewetime;
        TextView textviewsubject;
        TextView textviewvenue;
        TextView textviewalarm;
    }
    @Override
    public int getViewTypeCount() {
        count=getCount();
        if(count<1){
            count=1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View child, ViewGroup parent) {
        if (child == null) {
            holder = new Holder();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(com.evolvan.timegrid.R.layout.display, null, true);

            holder.textviewstime = (TextView) child.findViewById(com.evolvan.timegrid.R.id.textViewSTime);
            holder.textviewetime = (TextView) child.findViewById(com.evolvan.timegrid.R.id.textViewETime);
            holder.textviewsubject = (TextView) child.findViewById(com.evolvan.timegrid.R.id.textViewSubject);
            holder.textviewvenue = (TextView) child.findViewById(com.evolvan.timegrid.R.id.textViewVenue);
            holder.textviewalarm = (TextView) child.findViewById(com.evolvan.timegrid.R.id.textViewAlarm);

            child.setTag(holder);

        } else {
            holder = (Holder) child.getTag();
        }

        cb = User_Alarm.get(position);

        if (cb.equals("00") || cb.equals("")) {
            holder.textviewstime.setText(UserSTime.get(position));
            holder.textviewetime.setText(UserETime.get(position));
            holder.textviewsubject.setText(UserSubject.get(position));
            holder.textviewvenue.setText(User_Venue.get(position));
        } else{
            holder.textviewalarm.setBackgroundResource(com.evolvan.timegrid.R.drawable.ic_alarm);
            holder.textviewstime.setText(UserSTime.get(position));
            holder.textviewetime.setText(UserETime.get(position));
            holder.textviewsubject.setText(UserSubject.get(position));
            holder.textviewvenue.setText(User_Venue.get(position));
            holder.textviewalarm.setText(User_Alarm.get(position));
        }
        highLight(position,child);
        return child;
    }


    public void highLight(int position, View child){
        cal = Calendar.getInstance();
        sshour = cal.get(Calendar.HOUR_OF_DAY);
        ssmint = cal.get(Calendar.MINUTE);
        ctime = sshour * 60 + ssmint;
        shour = UserSTime.get(position);
        ehour = UserETime.get(position);
        SplitStime = shour.split(" ");
        Splitetime = ehour.split(" ");
        st = SplitStime[0];
        et = Splitetime[0];
        date1 = new Date();
        date1.setTime((((Integer.parseInt(st.split(":")[0])) * 60 + (Integer.parseInt(st.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
        storeshour = date1.getHours();
        storesmint = date1.getMinutes();
        storestime = storeshour * 60 + storesmint;
        date2 = new Date();
        date2.setTime((((Integer.parseInt(et.split(":")[0])) * 60 + (Integer.parseInt(et.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
        storeehour = date2.getHours();
        storeemint = date2.getMinutes();
        storeetime = storeehour * 60 + storeemint;

        if (ctime > storestime && ctime < storeetime){
            child.setBackgroundResource(com.evolvan.timegrid.R.color.colorGrey);
        }
        if(User_Venue.get(position).equals("Break") || User_Venue.get(position).equals("") || UserSubject.get(position).equals("Break") || UserSubject.get(position).equals("")){
            child.setBackgroundResource(com.evolvan.timegrid.R.color.colorTransparent);
        }
    }

    public void  toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void  removeSelection() {
        mSelectedItemsIds = new  SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position,  value);

        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int  getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public  SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}