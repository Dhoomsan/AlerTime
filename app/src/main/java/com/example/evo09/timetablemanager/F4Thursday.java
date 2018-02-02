package com.example.evo09.timetablemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class F4Thursday extends Fragment implements AdapterView.OnItemClickListener,AbsListView.MultiChoiceModeListener {
    SQLiteHelper SQLITEHELPER;
    MyTask MyTask;
    SQLiteDatabase SQLITEDATABASE;
    Cursor cursor;
    SQLiteListAdapter ListAdapter ;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> STIME_ArrayList = new ArrayList<String>();
    ArrayList<String> ETIME_ArrayList = new ArrayList<String>();
    ArrayList<String> SUBJECT_ArrayList = new ArrayList<String>();
    ArrayList<String> VENUE_ArrayList = new ArrayList<String>();
    ArrayList<String> ALARM_ArrayList = new ArrayList<String>();
    ListView LISTVIEW;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPREFERENCES" ;
    public static final String StoreId = "StoreId";
    public static final String AddUpdateFlag = "AddUpdateFlag";
    String updatedata="UPDATE";
    LinearLayout layout;
    Animation slideUp,slideDown;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.f4_thursday, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        LISTVIEW = (ListView) view.findViewById(R.id.DynamiclistView);

        SQLITEHELPER = new SQLiteHelper(getActivity());

        MyTask =new MyTask();

        LISTVIEW.setAdapter(ListAdapter);
        LISTVIEW.setOnItemClickListener(this);
        LISTVIEW.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        LISTVIEW.setMultiChoiceModeListener(this);
        return view;
    }@Override
    public void onResume() {

        ShowSQLiteDBdata() ;

        super.onResume();
    }
    private void ShowSQLiteDBdata() {
        SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
        String CREATE_WEEKTABLE = "CREATE TABLE IF NOT EXISTS " + SQLITEHELPER.TABLE_NAME + " (" + SQLITEHELPER.KEY_ID + " INTEGER PRIMARY KEY NOT NULL, "+ SQLITEHELPER.KEY_DOWeek + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_STime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_ETime + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Subject + " VARCHAR NOT NULL, " + SQLITEHELPER.KEY_Venue + " VARCHAR NOT NULL , " + SQLITEHELPER.KEY_AlermBefor + " VARCHAR)";
        SQLITEDATABASE.execSQL(CREATE_WEEKTABLE);

        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_DOWeek + " = 'Thursday' ORDER BY " + SQLITEHELPER.KEY_STime + " ASC ", null);

        ID_ArrayList.clear();
        STIME_ArrayList.clear();
        ETIME_ArrayList.clear();
        SUBJECT_ArrayList.clear();
        VENUE_ArrayList.clear();
        ALARM_ArrayList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ID)));
                STIME_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_STime)));
                ETIME_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ETime)));
                SUBJECT_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Subject)));
                VENUE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Venue)));
                ALARM_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_AlermBefor)));

            } while (cursor.moveToNext());
        }

        ListAdapter = new SQLiteListAdapter(getContext(),

                ID_ArrayList,
                STIME_ArrayList,
                ETIME_ArrayList,
                SUBJECT_ArrayList,
                VENUE_ArrayList,
                ALARM_ArrayList

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

        MyTask.Subject.setText("");
        MyTask.Venue.setText("");
        MyTask.StartTime.setText("");
        MyTask.EndTime.setText("");
        MyTask.Subject.setText(((TextView)view.findViewById(R.id.textViewSubject)).getText().toString());
        MyTask.Venue.setText(((TextView)view.findViewById(R.id.textViewVenue)).getText().toString());
        MyTask.StartTime.setText(((TextView)view.findViewById(R.id.textViewSTime)).getText().toString());
        MyTask.EndTime.setText(((TextView)view.findViewById(R.id.textViewETime)).getText().toString());
        MyTask.Allday.setVisibility(View.GONE);

        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_AlermBefor+ " != '" + "00" + "' AND " + SQLITEHELPER.KEY_ID + " = '"+ data +"'" , null);
        MyTask.AlermBefore.setText("");
        MyTask.AlermRepeat.setChecked(false);
        while (cursor != null && cursor.moveToNext()) {

            MyTask.AlermBefore.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_AlermBefor)));
            MyTask.AlermRepeat.setChecked(true);
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(StoreId, data);
        editor.putString(AddUpdateFlag, updatedata);


        editor.commit();
        Button b =(Button)layout.findViewById(R.id.ButtonAddUpdate);
        b.setText(R.string.Update);
        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(slideUp);
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
                final int checkedCount = ID_ArrayList.size();
                ListAdapter.removeSelection();
                for (int i = 0; i < checkedCount; i++) {
                    LISTVIEW.setItemChecked(i, true);
                }
                actionMode.setTitle(checkedCount + "  Selected");
                return true;
            case R.id.delete:

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
                                SQLITEDATABASE = getActivity().openOrCreateDatabase(SQLITEHELPER.DATABASE_NAME, MODE_PRIVATE, null);
                                String sql = "DELETE FROM " + SQLITEHELPER.TABLE_NAME + " WHERE  " + SQLITEHELPER.KEY_ID + " = '" + selecteditem + "'";
                                try {
                                    SQLITEDATABASE.execSQL(sql);
                                } catch (SQLException e) {
                                }
                                ShowSQLiteDBdata();
                            }
                        }

                        actionMode.finish();
                        selected.clear();

                    }
                });
                AlertDialog alert = builder.create();
                alert.setIcon(R.drawable.logo);
                alert.setTitle("Confirmation");
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
        mode.setTitle(checkedCount + "  Selected");
        ListAdapter.toggleSelection(position);
    }
    @Override
    public void onDestroyActionMode (ActionMode actionMode){
        ListAdapter.removeSelection();
    }

}
