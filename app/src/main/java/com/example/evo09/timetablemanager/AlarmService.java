package com.example.evo09.timetablemanager;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class AlarmService extends Service  {

    private int mInterval = 40000;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    private static final int NOTIFICATION_ID = 1;
    public int counter=0;
    public AlarmService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        SQLITEHELPER = new SQLiteHelper(this);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.example.evo09.timetablemanager.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, mInterval, mInterval); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                new AsyncTaskRunner().execute();
            }
        };
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp;
        private String Stime,Abefore,Sday,sub,ven,StineSplitStime,CSTime,tableTime,tableAlerm,tableId,CREATE_WEEKTABLE,CREATE_ALERMTABLE;
        String appname = getString(R.string.app_name);
        int notificationStatus=0;
        private int hour,mint;
        Date date1,date3;
        Calendar cal = Calendar.getInstance();
        int shour = cal.get(Calendar.HOUR_OF_DAY);
        int smint = cal.get(Calendar.MINUTE);
        String ctime = String.format("%02d:%02d %s", shour == 0 ? 12 : shour, smint, shour < 12 ? "AM" : "PM");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        final String dayOfTheWeek = sdf.format(d);
        @Override
        protected void onPreExecute() {
            SQLITEDATABASE = getApplicationContext().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
            CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL)";
            SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
            CREATE_ALERMTABLE ="CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_ALERM + " (" + SQLITEHELPER.KEY_IA + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR, "+ SQLITEHELPER.KEY_Status +" VARCHAR)";
            SQLITEDATABASE.execSQL(CREATE_ALERMTABLE);
        }
        @Override
        protected String doInBackground(String... params) {
            tableTime=SQLITEHELPER.TABLE_NAME;
            tableAlerm=SQLITEHELPER.TABLE_ALERM;
            tableId=SQLITEHELPER.KEY_ID;
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " INNER JOIN  "+SQLITEHELPER.TABLE_ALERM+" on "+ SQLITEHELPER.TABLE_NAME +"."+SQLITEHELPER.KEY_ID+"="+SQLITEHELPER.TABLE_ALERM+"."+SQLITEHELPER.KEY_Status, null);
            while (cursor != null && cursor.moveToNext()) {
                Stime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime));
                Abefore = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_AlermBefor));
                Sday = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DOWeek));
                sub=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Subject));
                ven=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Venue));
                String[] SplitStime = Stime.split(" ");
                StineSplitStime = SplitStime[0];
                date1 = new Date();
                date1.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
                date3 = new Date();
                date3.setTime(date1.getTime() - (Integer.parseInt(Abefore) * 60000));
                hour = date3.getHours();
                mint = date3.getMinutes();
                CSTime=String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM");
                //Log.d("TodayTask",dayOfTheWeek+"-"+Sday+" -"+ctime+"-"+CSTime+"-"+Abefore);
                if ((dayOfTheWeek.equals(Sday)) && (ctime.equals(CSTime))){
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentTitle(appname)
                        .setContentText("Today Task is "+sub+" ( "+ven+" ) at "+Stime)
                        .setVibrate(new long[] { 150, 300, 150, 600})
                        .setSound(defaultSoundUri)
                        .setSmallIcon(R.drawable.ic_alarm_clock)
                        .setAutoCancel(true);

                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);
                builder.setContentIntent(pendingIntent);

                NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, builder.build());

                //play current Ringtone
                // currentRingtone.play();
                }
                ////Log.d("respresp", resp);
            }
            return resp;
        }
        @Override
        protected void onPostExecute(String result ) {
            // execution of result of Long time consuming operation
        }
        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), AlarmService.class);
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}