package com.evolvan.timegrid;

import android.app.AlarmManager;
import android.app.Notification;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service  {

    private int mInterval = 40000;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    private static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder notification;
    Intent mainIntent;
    PendingIntent pendingIntent;
    NotificationManager manager;
    int notificationId = new Random().nextInt();
    public AlarmService(Context applicationContext) {
        super();
    }

    public AlarmService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = new Notification();
        startForeground(123456, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        SQLITEHELPER = new SQLiteHelper(this);
        startTimer();
       return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, mInterval, mInterval); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                new AsyncTaskRunner().execute();
            }
        };
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        @Override
        protected void onPreExecute() {
            SQLITEDATABASE = getApplicationContext().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
            String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR NOT NULL)";
            SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
            if(SQLITEDATABASE.isOpen()) {
            }
            else {
            }
        }
        @Override
        protected String doInBackground(String... params) {
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_AlermBefor + " != '" + "00" + "' AND " + SQLITEHELPER.KEY_DOWeek + " = '"+ dayOfTheWeek +"'" , null);
            while (cursor != null && cursor.moveToNext()) {
                String Stime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime));
                String  Abefore = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_AlermBefor));
                String  Sday = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DOWeek));
                String sub=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Subject));
                String ven=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Venue));

                if((!Abefore.equals("00") || Abefore.length()!=0)&&(dayOfTheWeek.equals(Sday))){

                    String[] SplitStime = Stime.split(" ");
                    String StineSplitStime = SplitStime[0];
                    Date date1 = new Date();
                    date1.setTime((((Integer.parseInt(StineSplitStime.split(":")[0])) * 60 + (Integer.parseInt(StineSplitStime.split(":")[1]))) + date1.getTimezoneOffset()) * 60000);
                    Date date3 = new Date();
                    date3.setTime(date1.getTime() - (Integer.parseInt(Abefore) * 60000));
                    int hour = date3.getHours();
                    int mint = date3.getMinutes();
                    String CSTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, mint, hour < 12 ? "AM" : "PM");

                    Calendar cal = Calendar.getInstance();
                    int shour = cal.get(Calendar.HOUR_OF_DAY);
                    int smint = cal.get(Calendar.MINUTE);
                    String ctime = String.format("%02d:%02d %s", shour == 0 ? 12 : shour, smint, shour < 12 ? "AM" : "PM");

                    if (ctime.equals(CSTime)) {
                        String appname = getString(com.evolvan.timegrid.R.string.app_name);
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        notification = new NotificationCompat.Builder(getApplicationContext());
                        notification.setContentTitle(appname)
                                .setContentText("Today Task is " + sub + " ( " + ven + " ) at " + Stime)
                                .setVibrate(new long[]{150, 300, 150, 600})
                                .setSound(defaultSoundUri)
                                .setSmallIcon(com.evolvan.timegrid.R.drawable.logo)
                                .setAutoCancel(true);

                        mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);
                        notification.setContentIntent(pendingIntent);

                        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(NOTIFICATION_ID, notification.build());
                    }
                }
            }
            return resp;
        }
        @Override
        protected void onPostExecute(String result ) {}
        @Override
        protected void onProgressUpdate(String... text) {}
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Calendar calendar = Calendar.getInstance();
        rootIntent = new Intent(getApplicationContext(), this.getClass());
        rootIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, rootIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                mInterval, restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(alarm.RTC_WAKEUP, System.currentTimeMillis() + mInterval, PendingIntent.getService(this, 0, new Intent(this, AlarmService.class), 0));
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }
}