package com.evolvan.timegrid;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
import static android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS;
import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int TIME_DELAY = 2000;
    public static final int RequestPermissionCode = 741;
    private static long back_pressed;

    private ProgressDialog csprogress;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteHelper SQLITEHELPER;
    Cursor cursor;
    Intent mServiceIntent;
    private AlarmService mSensorService;
    Context context=null;
    public Context getContext() {
        return context;
    }

    Fragment fragment=null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    LinearLayout layout;

    ImageView ButtonCancel;
    private static ViewPager DialogViewPager;
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    CircleIndicator indicator;

    Animation slideUp;
    SharedPreferences sharedpreferences;
    private SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPREFERENCES" ;

    private final String send_dialogValue = "";
    public static final String send_dialog = "send_dialog";
    String get_send_dialog,appname ;

    NavigationView navigationView;

    boolean permissiongrant=false;
    static

    Intent intent;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(com.evolvan.timegrid.R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECEIVE_BOOT_COMPLETED, ACTION_MANAGE_OVERLAY_PERMISSION, ACTION_MANAGE_WRITE_SETTINGS,ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, RequestPermissionCode);

        Toolbar toolbar = (Toolbar) findViewById(com.evolvan.timegrid.R.id.toolbar);
        setSupportActionBar(toolbar);

        appname = getString(com.evolvan.timegrid.R.string.app_name);

        sharedpreferences =getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        DrawerLayout drawer = (DrawerLayout) findViewById(com.evolvan.timegrid.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.evolvan.timegrid.R.string.navigation_drawer_open, com.evolvan.timegrid.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SQLITEHELPER = new SQLiteHelper(this);
        DBCreate();

        csprogress = new ProgressDialog(this);
        navigationView = (NavigationView) findViewById(com.evolvan.timegrid.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(permissiongrant=true) {
            SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
            if (cursor.getCount() != 0) {
                WhenRecord();
            } else {
                WhenNullRecord();
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            enableAutoStart();
        }

    }

    public void DBCreate(){
        SQLITEDATABASE =openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR NOT NULL)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);
        if(SQLITEDATABASE.isOpen()) {}
        else {DBCreate();}
    }

    private void hideItem() {
        SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
        navigationView = (NavigationView) findViewById(com.evolvan.timegrid.R.id.nav_view);
        Menu action_menu = navigationView.getMenu();
        if(cursor.getCount()==0) {
            action_menu.findItem(com.evolvan.timegrid.R.id.action_deleteStatic).setVisible(false);
        }
        else {
            action_menu.findItem(com.evolvan.timegrid.R.id.action_deleteStatic).setVisible(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode: {
                if (grantResults.length > 0) {

                    boolean WRITE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean READ = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean RECEIVE = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean OVERLAY = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean SETTINGS = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean BATTERY = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean BATTERY1 = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean BATTERY2 = grantResults[7] == PackageManager.PERMISSION_GRANTED;

                    if (WRITE || READ || RECEIVE || OVERLAY || SETTINGS || BATTERY) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                permissiongrant = true;
                            }
                        }, 2000);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Permission is necessary " + "\n" + "Allow it")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECEIVE_BOOT_COMPLETED, ACTION_MANAGE_OVERLAY_PERMISSION, ACTION_MANAGE_WRITE_SETTINGS,ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, RequestPermissionCode);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setIcon(com.evolvan.timegrid.R.drawable.logo);// dialog  Icon
                        alert.setTitle("Confirmation"); // dialog  Title
                        alert.show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case com.evolvan.timegrid.R.id.action_helpinfo: {
                instruction_dialog();
                break;
            }
            case com.evolvan.timegrid.R.id.action_deleteStatic: {
                SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
                if(cursor.getCount()!=0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setMessage("Do you really want to Delete All Record?")
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
                    alert.setIcon(com.evolvan.timegrid.R.drawable.logo);// dialog  Icon
                    alert.setTitle("Confirmation"); // dialog  Title
                    alert.show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error! Something went wrong!",Toast.LENGTH_LONG).show();
                }
                break;
            }
            case com.evolvan.timegrid.R.id.nav_share: {
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, "TimeGrid");
                    String sAux = "\nI recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.evolvan.timegrid \n\n";
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
            ft.replace(com.evolvan.timegrid.R.id.content_frame,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(com.evolvan.timegrid.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void WhenNullRecord(){
        hideItem();
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Instruction();
        fragmentTransaction.replace(com.evolvan.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void WhenRecord(){

        hideItem();

        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new MyTask();
        fragmentTransaction.replace(com.evolvan.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();

        startService();
    }

    public void startService(){
        mSensorService = new AlarmService(getContext());
        mServiceIntent = new Intent(getContext(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);

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

    public void WhenLandScape(){
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new myTaskLandScape();
        fragmentTransaction.replace(com.evolvan.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void WhenStatic(){
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new myTaskStatic();
        fragmentTransaction.replace(com.evolvan.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void layoutUpdate(){
        layout = (LinearLayout) findViewById(com.evolvan.timegrid.R.id.updatelayout);
        slideUp = AnimationUtils.loadAnimation(this, com.evolvan.timegrid.R.anim.slide_up);
        Button b =(Button)layout.findViewById(com.evolvan.timegrid.R.id.ButtonAddUpdate);
        b.setText(com.evolvan.timegrid.R.string.Update);
        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(slideUp);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void instruction_dialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(com.evolvan.timegrid.R.layout.fragment_help_info, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog notification
        alertDialogBuilder.setView(promptsView);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonCancel=(ImageView)promptsView.findViewById(com.evolvan.timegrid.R.id.ButtonCancel);

        DialogViewPager = (ViewPager) promptsView.findViewById(com.evolvan.timegrid.R.id.DialogViewPager);
        DialogViewPager.setAdapter(new MyAdapter(MainActivity.this,XMENArray));
        indicator = (CircleIndicator)promptsView. findViewById(com.evolvan.timegrid.R.id.indicator);
        indicator.setViewPager(DialogViewPager);

        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void enableAutoStart() {
        try {
            intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent = new Intent("com.coloros.safecenter.permission.PermissionAppAllPermissionActivity");
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer) || "huawei".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            }
            else {
                get_send_dialog= sharedpreferences.getString(send_dialog, send_dialogValue);
                if(!get_send_dialog.equals(send_dialog)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("instruction")
                            .setMessage("Change setting if notification is not receiving." + "\n" + "1.On the settings screen, scroll down a little and then tap on Security to open the security related settings." + "\n" + "2.In the security section you will find many settings related to apps like app permissions, app verification, whether to install from unknown sources etc. You have to tap on Auto-start Management to in this list." +
                                    "\n" + "3.In the auto-start management screen, it will display a list of all the apps that are being auto-started in your Android phone. You can simply uncheck any apps that you want to disable from being auto-started. Similarly, checking an app will enable it to be auto-started with Android bootup.")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setIcon(com.evolvan.timegrid.R.drawable.logo);// dialog  Icon
                    alert.show();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(send_dialog, send_dialog);
                    editor.commit();
                }

            }
            List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enable AutoStart")
                        .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                        .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setIcon(com.evolvan.timegrid.R.drawable.logo);// dialog  Icon
                alert.show();
            }
        } catch (ActivityNotFoundException e) {e.printStackTrace();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService();
    }
}
