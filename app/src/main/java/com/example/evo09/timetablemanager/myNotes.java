package com.example.evo09.timetablemanager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class myNotes extends Fragment implements View.OnClickListener,TextToSpeech.OnInitListener {

    Button newButton, saveButton, readButton,searchButton;
    private EditText EditextReadWrite;
    private int result=0;
    EditText fileName;
    private TextToSpeech tts;
    File extStore,myFile;
    FileOutputStream fOut;
    FileInputStream fIn;
    OutputStreamWriter myOutWriter;
    BufferedReader myReader;
    AlertDialog.Builder ad;
    String str,path,s,fileContent,DNAME,getfilename,storefilename;
    Snackbar snackbar;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.Notes);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View notes= inflater.inflate(R.layout.fragment_notes,container,false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 10);

            }
        }
        return notes;
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        AlarmDataShow();
    }
    public void AlarmDataShow(){
        setHasOptionsMenu(true);
        EditextReadWrite=(EditText)getActivity().findViewById(R.id.EditextReadWrite);
        tts = new TextToSpeech(getActivity(), this);
        newButton=(Button)getActivity().findViewById(R.id.newButton);
        saveButton=(Button)getActivity().findViewById(R.id.saveButton);
        readButton=(Button) getActivity().findViewById(R.id.readButton);
        searchButton=(Button) getActivity().findViewById(R.id.searchButton);
        newButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        readButton.setOnClickListener(this);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public void onClick(View v ) {
        DNAME = getString(R.string.app_name);
        extStore =new File( Environment.getExternalStorageDirectory()+File.separator+ DNAME);
        fileName = new EditText(getActivity());
        ad = new AlertDialog.Builder(getActivity());
        ad.setView(fileName);
        switch (v.getId()) {
            case R.id.newButton:
                EditextReadWrite.setText("");
                break;
            case R.id.saveButton:
                ad.setMessage("Save File");
                str = EditextReadWrite.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    snackbar = Snackbar.make(getView(), "Oops! Nothing to Save.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    ad.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!extStore.exists()) {
                                extStore.mkdirs();
                            }
                            storefilename = fileName.getText().toString();
                            getfilename=removeExtension(storefilename);
                            path = extStore.getAbsolutePath() + "/" + getfilename + ".txt";
                            try {
                                myFile = new File(path);
                                if(myFile.exists())
                                {
                                    snackbar = Snackbar.make(getView(), "Oops! File Name already existing.", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                                else {
                                    myFile.createNewFile();
                                    fOut = new FileOutputStream(myFile);
                                    myOutWriter = new OutputStreamWriter(fOut);
                                    myOutWriter.append(str);
                                    myOutWriter.close();
                                    fOut.close();
                                    snackbar = Snackbar.make(getView(), "Saved!", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            } catch (Exception e) {
                                snackbar = Snackbar.make(getView(), "Oops! Something went wrong.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    });

                    ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    ad.show();
                }
                break;
            case R.id.searchButton:
                ad.setMessage("Search File");

                ad.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storefilename = fileName.getText().toString();
                        getfilename=removeExtension(storefilename);
                        path = extStore.getAbsolutePath() + "/" + getfilename + ".txt";
                        Log.d("titlefile",path);
                        EditextReadWrite.setText("");
                        s = "";
                        fileContent = "";
                        try {
                            myFile = new File(path);
                            fIn = new FileInputStream(myFile);
                            myReader = new BufferedReader(
                                    new InputStreamReader(fIn));

                            while ((s = myReader.readLine()) != null) {
                                fileContent += s + "\n";
                            }
                            myReader.close();

                            EditextReadWrite.setText(fileContent);
                        } catch (Exception e) {
                            snackbar = Snackbar.make(getView(), "Oops! File Name not Existing.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });

                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                ad.show();
                break;
            case R.id.readButton:
                String str1 = EditextReadWrite.getText().toString().trim();
                if (TextUtils.isEmpty(str1)) {
                    snackbar = Snackbar.make(getView(), "Oops! Nothing to Read.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    speakOut();
                    break;
                }
        }
    }
    private static String removeExtension(String storefilename) {
        if (storefilename.indexOf(".") > 0) {
            return storefilename.substring(0, storefilename.lastIndexOf("."));
        } else {
            return storefilename;
        }
    }
    private void speakOut() {
        str = EditextReadWrite.getText().toString();
        if(result!=tts.setLanguage(Locale.US))
        {
            snackbar = Snackbar.make(getView(), "Oops! special characters not allowed.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else
        {
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar snackbar1 = Snackbar.make(getView(), "Oops! Missing data.", Snackbar.LENGTH_SHORT);
                snackbar1.show();
                readButton.setEnabled(false);
            } else {
                readButton.setEnabled(true);
            }
        }
    }

}
