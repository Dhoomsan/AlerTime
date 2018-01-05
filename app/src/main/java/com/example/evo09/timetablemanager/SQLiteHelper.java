 package com.example.evo09.timetablemanager;

 import android.content.Context;
 import android.database.sqlite.SQLiteDatabase;
 import android.database.sqlite.SQLiteOpenHelper;

 public class SQLiteHelper extends SQLiteOpenHelper {

     public static String DATABASE_NAME="TIMEMANAGER";

     public static final String KEY_ID="id";
     public static final String KEY_IA="id";

     public static final String TABLE_NAME="WEEKTABLE";
     public static final String TABLE_ALERM="ALERMTABLE";
     public static final String KEY_Before="ALERMTABLE";
     public static final String KEY_DOWeek="Dofweek";

     public static final String KEY_STime="Stime";

     public static final String KEY_ETime="Etime";

     public static final String KEY_Subject="subject";

     public static final String KEY_Venue="venue";
     public static final String KEY_AlermBefor="Alermbefore";
     public static final String KEY_Status="Status";

     public SQLiteHelper(Context context) {

         super(context, DATABASE_NAME, null, 1);

     }

     @Override
     public void onCreate(SQLiteDatabase database) {
             String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, "+ KEY_DOWeek + " VARCHAR, " + KEY_STime + " VARCHAR, " + KEY_ETime + " VARCHAR, " + KEY_Subject + " VARCHAR, " + KEY_Venue + " VARCHAR)";
             database.execSQL(CREATE_TABLE);

            database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ALERM + " (" + KEY_IA + " INTEGER PRIMARY KEY , " + KEY_AlermBefor + " VARCHAR, "+ KEY_Status +" VARCHAR");

     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
         onCreate(db);

         db.execSQL("DROP TABLE IF EXISTS "+TABLE_ALERM);
         onCreate(db);

     }

 }