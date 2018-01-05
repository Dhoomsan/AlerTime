package com.example.evo09.timetablemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class F2Tuesday extends Fragment implements AdapterView.OnItemClickListener,AbsListView.MultiChoiceModeListener {
    SQLiteHelper SQLITEHELPER;
    MySchedules mySchedules;
    SQLiteDatabase SQLITEDATABASE;
    Cursor cursor;
    SQLiteListAdapter ListAdapter ;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> STIME_ArrayList = new ArrayList<String>();
    ArrayList<String> ETIME_ArrayList = new ArrayList<String>();
    ArrayList<String> SUBJECT_ArrayList = new ArrayList<String>();
    ArrayList<String> VENUE_ArrayList = new ArrayList<String>();
    ListView LISTVIEW;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPREFERENCES" ;
    public static final String StoreId = "StoreId";
    public static final String AddUpdateFlag = "AddUpdateFlag";
    String updatedata="UPDATE";
    LinearLayout layout;
    Animation slideUp,slideDown;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f2_tuesday, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        LISTVIEW = (ListView) view.findViewById(R.id.DynamiclistView);

        SQLITEHELPER = new SQLiteHelper(getActivity());

        mySchedules=new MySchedules();

        LISTVIEW.setAdapter(ListAdapter);
        LISTVIEW.setOnItemClickListener(this);
        LISTVIEW.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click
        LISTVIEW.setMultiChoiceModeListener(this);
        return view;
    }@Override
    public void onResume() {

        ShowSQLiteDBdata() ;

        super.onResume();
    }
    private void ShowSQLiteDBdata() {
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR, " + SQLITEHELPER.KEY_STime + " VARCHAR, " + SQLITEHELPER.KEY_ETime + " VARCHAR, " + SQLITEHELPER.KEY_Subject + " VARCHAR, " + SQLITEHELPER.KEY_Venue + " VARCHAR)";
        SQLITEDATABASE.execSQL(CREATE_TABLE);
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Tuesday' ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);

        ID_ArrayList.clear();
        STIME_ArrayList.clear();
        ETIME_ArrayList.clear();
        SUBJECT_ArrayList.clear();
        VENUE_ArrayList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            //Log.d("tabledata","ok");
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ID)));
                STIME_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime)));
                ETIME_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ETime)));
                SUBJECT_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Subject)));
                VENUE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Venue)));

            } while (cursor.moveToNext());
        }

        ListAdapter = new SQLiteListAdapter(getContext(),

                ID_ArrayList,
                STIME_ArrayList,
                ETIME_ArrayList,
                SUBJECT_ArrayList,
                VENUE_ArrayList

        );

        LISTVIEW.setAdapter(ListAdapter);

        cursor.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        layout = (LinearLayout) getActivity().findViewById(R.id.updatelayout);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        String data=(String)adapterView.getItemAtPosition(position);

        mySchedules.Subject.setText("");
        mySchedules.Venue.setText("");
        mySchedules.StartTime.setText("");
        mySchedules.EndTime.setText("");
        mySchedules.Subject.setText(((TextView)view.findViewById(R.id.textViewSubject)).getText().toString());
        mySchedules.Venue.setText(((TextView)view.findViewById(R.id.textViewVenue)).getText().toString());
        mySchedules.StartTime.setText(((TextView)view.findViewById(R.id.textViewSTime)).getText().toString());
        mySchedules.EndTime.setText(((TextView)view.findViewById(R.id.textViewETime)).getText().toString());

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(StoreId, data);
        editor.putString(AddUpdateFlag, updatedata);

        AlarmStatus(data);

        editor.commit();
        Button b =(Button)layout.findViewById(R.id.ButtonAddUpdate);
        b.setText("Update");
        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(slideUp);
    }
    public void AlarmStatus(String data){
        String getid=data;
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
        String CREATE_ALERMTABLE ="CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_ALERM + " (" + SQLITEHELPER.KEY_IA + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR, "+ SQLITEHELPER.KEY_Status +" VARCHAR)";
        SQLITEDATABASE.execSQL(CREATE_ALERMTABLE);
        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_ALERM + " WHERE  " + SQLITEHELPER.KEY_Status + " = '"+ getid +"'", null);
        //Log.d("abcdefghijk","kdghysid"+getid);
        mySchedules.AlermBefore.setText("");
        mySchedules.AlermRepeat.setChecked(false);
        while (cursor != null && cursor.moveToNext()) {

            mySchedules.AlermBefore.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_AlermBefor)));
            mySchedules.AlermRepeat.setChecked(true);
        }
    }

    @Override
    public boolean onCreateActionMode (ActionMode actionMode, Menu menu){
        actionMode.getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode (ActionMode actionMode, Menu menu){
        return false;
    }

    @Override
    public boolean onActionItemClicked ( final ActionMode actionMode, MenuItem menuItem){
        // TODO  Auto-generated method stub
        switch (menuItem.getItemId()) {
            case R.id.selectAll:
                //
                final int checkedCount = ID_ArrayList.size();
                // If item  is already selected or checked then remove or
                // unchecked  and again select all
                ListAdapter.removeSelection();
                for (int i = 0; i < checkedCount; i++) {
                    LISTVIEW.setItemChecked(i, true);
                }
                // Set the  CAB title according to total checked items

                // Calls  toggleSelection method from ListViewAdapter Class

                // Count no.  of selected item and print it
                actionMode.setTitle(checkedCount + "  Selected");
                return true;
            case R.id.delete:
                // Add  dialog for confirmation to delete selected item
                // record.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you  want to delete selected record(s)?");

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO  Auto-generated method stub

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO  Auto-generated method stub
                        SparseBooleanArray selected = ListAdapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                String selecteditem = (String) ListAdapter.getItem(selected.keyAt(i));
                                // Remove  selected items following the ids
                                SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                                String sql = "DELETE FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_ID + " = '" + selecteditem + "'";
                                String sql1 = "DELETE FROM " + SQLITEHELPER.TABLE_ALERM + " WHERE  " + SQLITEHELPER.KEY_Status + " = '" + selecteditem + "'";
                                try {
                                    SQLITEDATABASE.execSQL(sql);
                                    SQLITEDATABASE.execSQL(sql1);
                                } catch (SQLException e) {
                                }
                                //Toast.makeText(getContext(),selecteditem,Toast.LENGTH_LONG).show();
                                ShowSQLiteDBdata();
                            }
                        }

                        // Close CAB
                        actionMode.finish();
                        selected.clear();

                    }
                });
                AlertDialog alert = builder.create();
                alert.setIcon(R.drawable.ic_alarm_clock);// dialog  Icon
                alert.setTitle("Confirmation"); // dialog  Title
                alert.show();
                return true;
            default:
                return false;
        }
    }
    @Override
    public void onItemCheckedStateChanged (ActionMode mode,int position, long id, boolean checked){
        // TODO  Auto-generated method stub
        final int checkedCount = LISTVIEW.getCheckedItemCount();
        // Set the  CAB title according to total checked items
        mode.setTitle(checkedCount + "  Selected");
        // Calls  toggleSelection method from ListViewAdapter Class
        ListAdapter.toggleSelection(position);
    }
    @Override
    public void onDestroyActionMode (ActionMode actionMode){
        ListAdapter.removeSelection();
    }

}
