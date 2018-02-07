package com.example.evo09.timetablemanager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    private ProgressDialog csprogress;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    Intent mServiceIntent;
    private AlarmService mSensorService;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    MyTask MyTask;
    Fragment fragment=null;
    Fragment frag;
    FragmentManager fm1;
    FragmentTransaction ft1;

    boolean permissiongrant=false;
    TextView evolvan;
    LinearLayout layout;

    ImageView ButtonCancel;
    private static ViewPager DialogViewPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.img_grid,R.drawable.img_add,R.drawable.img_updata,R.drawable.img_delete,R.drawable.img_landscape};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    CircleIndicator indicator;
    Handler handler;
    Runnable Update;

    Animation slideUp,slideDown;
    SharedPreferences sharedpreferences;
    private SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPREFERENCES" ;
    public static final String AddUpdateFlag = "AddUpdateFlag";
    String insertdata="INSERTDATA";

    NavigationView navigationView;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new AlarmService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
        startService(new Intent(this, AlarmService.class));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        sharedpreferences =getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SQLITEHELPER = new SQLiteHelper(this);
        DBCreate();
        MyTask =new MyTask();


        csprogress = new ProgressDialog(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        evolvan = (TextView) headerview.findViewById(R.id.evolvan);
        evolvan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://evolvan.com/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        if(permissiongrant=true) {
            SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
            if(cursor.getCount()!=0) {
                WhenRecord();
            }
            else {
                WhenNullRecord();
            }
        }

        check_autorun_permission();
    }


    public void DBCreate(){
        SQLITEDATABASE =openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR NOT NULL)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
        if(SQLITEDATABASE.isOpen()) {
        }
        else {
            DBCreate();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void hideItem() {
        SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu action_menu = navigationView.getMenu();
        if(cursor.getCount()==0) {
            action_menu.findItem(R.id.action_deleteStatic).setVisible(false);
        }
        else {
            action_menu.findItem(R.id.action_deleteStatic).setVisible(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            permissiongrant=true;
                        }
                    }, 2000);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Permission is necessary "+"\n"+"Allow it")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECEIVE_BOOT_COMPLETED}, 1);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {getMenuInflater().inflate(R.menu.main, menu);return true;}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        layout = (LinearLayout) findViewById(R.id.updatelayout);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        switch(item.getItemId()) {
            case R.id.action_LANDSCAPE: {
                WhenLandScape();
                break;
            }
            case R.id.action_PORTRAIT: {
                WhenRecord();
                break;
            }
            case R.id.action_add: {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(AddUpdateFlag, insertdata);
                editor.commit();
                Button b = (Button) layout.findViewById(R.id.ButtonAddUpdate);
                b.setText(R.string.Add);
                layout.setVisibility(View.VISIBLE);
                layout.startAnimation(slideUp);
                MyTask.Allday.setVisibility(View.VISIBLE);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_helpinfo: {
                instruction_dialog();
                break;
            }
            case R.id.action_deleteStatic: {
                SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
                if(cursor.getCount()!=0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Do you really want to Delete All Record?" + "\n" + "We Recommend you to update Your Record")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
                                    SQLITEDATABASE.delete(SQLITEHELPER.TABLE_NAME, null, null);
                                    SQLITEDATABASE.close();

                                    mEditor.clear();
                                    mEditor.commit();

                                    csprogress.setMessage("Deleting...");
                                    csprogress.show();
                                    csprogress.setCancelable(false);
                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            WhenNullRecord();
                                            new Handler().postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    csprogress.dismiss();
                                                }
                                            }, 300);
                                        }
                                    }, 1500);//just mention the time when you want to launch your action

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
                else {
                    Toast.makeText(getApplicationContext(),"Error! Something went wrong!",Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.nav_share: {
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, "MockMe");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=mockme.evolvan.com.mockme&hl=en \n\n";
                    share.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(share, "Share Using..."));
                } catch (Exception e) {
                    //e.toString();
                }
                return true;
            }
        }
        if(fragment !=null)
        {
            FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
    }

    public void WhenNullRecord(){
        hideItem();
        fm1 = this.getSupportFragmentManager();
        ft1 = fm1.beginTransaction();
        frag = new Instruction();
        ft1.replace(R.id.content_frame, frag);
        ft1.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void instruction_dialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.fragment_help_info, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonCancel=(ImageView)promptsView.findViewById(R.id.ButtonCancel);

        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        DialogViewPager = (ViewPager) promptsView.findViewById(R.id.DialogViewPager);
        DialogViewPager.setAdapter(new MyAdapter(MainActivity.this,XMENArray));
        indicator = (CircleIndicator)promptsView. findViewById(R.id.indicator);
        indicator.setViewPager(DialogViewPager);

        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        /*// Auto start of viewpager
        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                DialogViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, TIME_DELAY*5, TIME_DELAY*5);*/

        alertDialog.show();
    }
    public void WhenRecord(){
        hideItem();
        fm1 = this.getSupportFragmentManager();
        ft1 = fm1.beginTransaction();
        frag = new MyTask();
        ft1.replace(R.id.content_frame, frag);
        ft1.commit();
    }

    public void WhenLandScape(){
        fm1 = this.getSupportFragmentManager();
        ft1 = fm1.beginTransaction();
        frag = new myTaskLandScape();
        ft1.replace(R.id.content_frame, frag);
        ft1.commit();
    }

    public void WhenStatic(){
        fm1 = this.getSupportFragmentManager();
        ft1 = fm1.beginTransaction();
        frag = new myTaskStatic();
        ft1.replace(R.id.content_frame, frag);
        ft1.commit();
    }

    public void check_autorun_permission(){
        //'''''''''''
        Log.d("Build.BRAND",android.os.Build.MANUFACTURER);
        if(android.os.Build.MANUFACTURER.equalsIgnoreCase("xiaomi") ){
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }
        }else if(android.os.Build.MANUFACTURER.equalsIgnoreCase("Letv")){
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }

        } else if(android.os.Build.MANUFACTURER.equalsIgnoreCase("Honor")){
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }

        } else if (android.os.Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }
        }
        //''''''''''''
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, AlarmService.class);
        startService(startServiceIntent);
    }

}
