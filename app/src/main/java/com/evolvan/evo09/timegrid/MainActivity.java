package com.evolvan.evo09.timegrid;

import android.annotation.TargetApi;
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

import me.relex.circleindicator.CircleIndicator;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
import static android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int TIME_DELAY = 2000;
    int enableAutoStartCode=147;
    public static final int RequestPermissionCode = 741;
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
    String  appname ;

    NavigationView navigationView;

    boolean permissiongrant=false;

    Intent intent;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(com.evolvan.evo09.timegrid.R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(com.evolvan.evo09.timegrid.R.id.toolbar);
        setSupportActionBar(toolbar);

        appname = getString(com.evolvan.evo09.timegrid.R.string.app_name);

        sharedpreferences =getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        DrawerLayout drawer = (DrawerLayout) findViewById(com.evolvan.evo09.timegrid.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.evolvan.evo09.timegrid.R.string.navigation_drawer_open, com.evolvan.evo09.timegrid.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SQLITEHELPER = new SQLiteHelper(this);
        DBCreate();

        mSensorService = new AlarmService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

        csprogress = new ProgressDialog(this);
        navigationView = (NavigationView) findViewById(com.evolvan.evo09.timegrid.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE , RECEIVE_BOOT_COMPLETED,ACTION_MANAGE_OVERLAY_PERMISSION, ACTION_MANAGE_WRITE_SETTINGS}, RequestPermissionCode);

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
        navigationView = (NavigationView) findViewById(com.evolvan.evo09.timegrid.R.id.nav_view);
        Menu action_menu = navigationView.getMenu();
        if(cursor.getCount()==0) {
            action_menu.findItem(com.evolvan.evo09.timegrid.R.id.action_deleteStatic).setVisible(false);
        }
        else {
            action_menu.findItem(com.evolvan.evo09.timegrid.R.id.action_deleteStatic).setVisible(true);
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

                    if (WRITE || READ || RECEIVE || OVERLAY || SETTINGS) {
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
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECEIVE_BOOT_COMPLETED, ACTION_MANAGE_OVERLAY_PERMISSION, ACTION_MANAGE_WRITE_SETTINGS}, RequestPermissionCode);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
                        alert.setTitle("Confirmation"); // dialog  Title
                        alert.show();
                    }
                }
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 258: {
                if (Settings.canDrawOverlays(this)) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startService(new Intent(getApplicationContext(), AlarmService.class));
                        }
                    }, 2000);
                }
            }
            case 147: {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Permission granted!", Toast.LENGTH_LONG).show();
                        }
                    }, 2000);
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
            case com.evolvan.evo09.timegrid.R.id.action_helpinfo: {
                instruction_dialog();
                break;
            }
            case com.evolvan.evo09.timegrid.R.id.action_deleteStatic: {
                SQLITEDATABASE = openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME, null);
                if(cursor.getCount()!=0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Do you really want to Delete All Record?")
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
                    alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
                    alert.setTitle("Confirmation"); // dialog  Title
                    alert.show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error! Something went wrong!",Toast.LENGTH_LONG).show();
                }
                break;
            }
            case com.evolvan.evo09.timegrid.R.id.nav_share: {
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
            ft.replace(com.evolvan.evo09.timegrid.R.id.content_frame,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(com.evolvan.evo09.timegrid.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void WhenNullRecord(){
        hideItem();
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Instruction();
        fragmentTransaction.replace(com.evolvan.evo09.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void WhenRecord(){
        hideItem();
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new MyTask();
        fragmentTransaction.replace(com.evolvan.evo09.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
        if (checkDrawOverlayPermission()) {
            startService();
        }

    }
    public void startService(){
        mSensorService = new AlarmService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {

                startService(mServiceIntent);

        }
    }

    public void WhenLandScape(){
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new myTaskLandScape();
        fragmentTransaction.replace(com.evolvan.evo09.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void WhenStatic(){
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new myTaskStatic();
        fragmentTransaction.replace(com.evolvan.evo09.timegrid.R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void layoutUpdate(){
        layout = (LinearLayout) findViewById(com.evolvan.evo09.timegrid.R.id.updatelayout);
        slideUp = AnimationUtils.loadAnimation(this, com.evolvan.evo09.timegrid.R.anim.slide_up);
        Button b =(Button)layout.findViewById(com.evolvan.evo09.timegrid.R.id.ButtonAddUpdate);
        b.setText(com.evolvan.evo09.timegrid.R.string.Update);
        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(slideUp);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void instruction_dialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(com.evolvan.evo09.timegrid.R.layout.fragment_help_info, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonCancel=(ImageView)promptsView.findViewById(com.evolvan.evo09.timegrid.R.id.ButtonCancel);

        DialogViewPager = (ViewPager) promptsView.findViewById(com.evolvan.evo09.timegrid.R.id.DialogViewPager);
        DialogViewPager.setAdapter(new MyAdapter(MainActivity.this,XMENArray));
        indicator = (CircleIndicator)promptsView. findViewById(com.evolvan.evo09.timegrid.R.id.indicator);
        indicator.setViewPager(DialogViewPager);

        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable Overlay")
                    .setMessage("Please allow Overlay to always run the Notification,else our services can't be run."+"\n"+"Enable"+appname)
                    .setCancelable(false)
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 258);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();

            return false;
        } else {
            return true;
        }
    }

    private void enableAutoStart() {
        String manufacturer = android.os.Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable AutoStart")
                    .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivityForResult(intent,enableAutoStartCode);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();
        }  else if ("Letv".equalsIgnoreCase(manufacturer)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable AutoStart")
                    .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                            startActivityForResult(intent,enableAutoStartCode);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();
        }  else if ("Honor".equalsIgnoreCase(manufacturer)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable AutoStart")
                    .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivityForResult(intent,enableAutoStartCode);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();
        }  else if ("oppo".equalsIgnoreCase(manufacturer)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable AutoStart")
                    .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                                startActivityForResult(intent,enableAutoStartCode);
                            } catch (Exception e) {
                                try {
                                    intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                                    startActivityForResult(intent,enableAutoStartCode);
                                } catch (Exception ex) {
                                    try {
                                        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                                        startActivityForResult(intent,enableAutoStartCode);
                                    } catch (Exception exx) {
                                        try {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                if (!android.provider.Settings.System.canWrite(ctx)){
                                                    intent = new Intent(ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                                                    startActivityForResult(intent,enableAutoStartCode);
                                                }
                                            }
                                        }catch (Exception exxxxx){

                                        }
                                    }
                                }
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();
        }  else if ("vivo".contains(manufacturer)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable AutoStart")
                    .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                 intent = new Intent();
                                intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                                startActivityForResult(intent,enableAutoStartCode);
                            } catch (Exception e) {
                                try {
                                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                                    startActivityForResult(intent,enableAutoStartCode);
                                } catch (Exception ex) {
                                    try {
                                        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                                        startActivityForResult(intent,enableAutoStartCode);
                                    } catch (Exception exx) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();
        }
        else if ("huawei".equalsIgnoreCase(manufacturer)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable AutoStart")
                    .setMessage("Please allow QuickAlert to always run in the background,else our services can't be run")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivityForResult(intent,enableAutoStartCode);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(com.evolvan.evo09.timegrid.R.drawable.logo);// dialog  Icon
            alert.show();
        }
    }
}
