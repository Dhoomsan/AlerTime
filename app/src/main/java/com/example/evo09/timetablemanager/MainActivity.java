package com.example.evo09.timetablemanager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


//#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end #parse("File Header.java") public class ${NAME} { }
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Intent mServiceIntent;
    private AlarmService mSensorService;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }


    SharedPreferences Staticsharedpreferences;
    public static final String StaticTotalClass = "StaticTotalClass";
    String getStaticTotalClass;
    private final String DefaultTotalClassValue = "";

    Fragment fragment=null;
    Fragment frag;
    FragmentManager fm1;
    boolean permissiongrant=false;
    TextView evolvan;
    FragmentTransaction ft1;
    LinearLayout layout;
    LinearLayout Showstatictable,createstatictable;

    Animation slideUp,slideDown;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPREFERENCES" ;
    public static final String AddUpdateFlag = "AddUpdateFlag";
    String insertdata="INSERTDATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Servises
        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new AlarmService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
        startService(new Intent(this, AlarmService.class));

        Staticsharedpreferences =getSharedPreferences("Staticmypreference", Context.MODE_PRIVATE);
        getStaticTotalClass= Staticsharedpreferences.getString(StaticTotalClass, DefaultTotalClassValue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //permissions
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
       /* ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setIcon(R.drawable.ic_clock);*/
        sharedpreferences =getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
        if(permissiongrant=true){
            fm1 = MainActivity.this.getSupportFragmentManager();
            ft1 = fm1.beginTransaction();
            frag = new MySchedules();
            ft1.replace(R.id.content_frame, frag);
            ft1.commit();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
            /*String manufacturer = "xiaomi";
            if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {

                //this will open auto start screen where user can enable permission for your app
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(intent);
            }*/
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissiongrant=true;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Permission denied to read your External storage"+"\n"+"Allow it")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
        //moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        layout = (LinearLayout) findViewById(R.id.updatelayout);
        Showstatictable = (LinearLayout) findViewById(R.id.Showstatictable);
        createstatictable=(LinearLayout) findViewById(R.id.createstatictable);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        switch(item.getItemId()) {
            case R.id.action_LANDSCAPE: {
                fm1 = MainActivity.this.getSupportFragmentManager();
                ft1 = fm1.beginTransaction();
                frag = new MyScheduleLandScape();
                ft1.replace(R.id.content_frame, frag);
                ft1.commit();
                break;
            }
            case R.id.action_PORTRAIT: {
                fm1 = MainActivity.this.getSupportFragmentManager();
                ft1 = fm1.beginTransaction();
                frag = new MySchedules();
                ft1.replace(R.id.content_frame, frag);
                ft1.commit();
                break;
            }
            case R.id.action_add: {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(AddUpdateFlag, insertdata);
                editor.commit();
                Button b = (Button) layout.findViewById(R.id.ButtonAddUpdate);
                b.setText("Add");
                layout.setVisibility(View.VISIBLE);
                layout.startAnimation(slideUp);
                break;
            }
            case R.id.action_deleteStatic: {
                if(getStaticTotalClass.length()==0) {
                    Toast.makeText(getApplication(),"Sorry !"+"\n"+"Create First.",Toast.LENGTH_SHORT).show();

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to Delete Static Schedule?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Staticsharedpreferences.edit().clear().commit();
                                    Showstatictable.setVisibility(View.GONE);
                                    createstatictable.setVisibility(View.VISIBLE);
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
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
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case  R.id.My_schedule:
               fragment=new MySchedules();
                break;
            case  R.id.DMy_schedule:
                fragment=new MyStaticSchedules();
                break;
            case  R.id.nav_notes:
                fragment= new NotesActivity();
                break;
            case  R.id.nav_Alerm:
                fragment= new Alarm();
                break;
            case  R.id.nav_share:
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, "MockMe");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=mockme.evolvan.com.mockme&hl=en \n\n";
                    share.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(share, "Share Using..."));
                } catch(Exception e) {
                    //e.toString();
                }
                return true;
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
        startService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
