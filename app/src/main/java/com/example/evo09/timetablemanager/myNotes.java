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

    private static final int FILE_RESULT_CODER= 111;
    private ProgressDialog csprogress;
    Button newButton, saveButton, openButton,readButton,searchButton;
    private EditText EditextReadWrite;
    private int result=0;
    //TextView titlefile;
    EditText fileName;
    private TextToSpeech tts;
    Intent intent ;
    File extStore,myFile;
    FileOutputStream fOut;
    FileInputStream fIn;
    OutputStreamWriter myOutWriter;
    BufferedReader myReader;
    DataInputStream din;
    AlertDialog.Builder ad;
    String str,path,s,fileContent,DNAME,state,getfilename,storefilename;
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
        csprogress=new ProgressDialog(getActivity());
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
        //openButton=(Button) getActivity().findViewById(R.id.openButton);
        readButton=(Button) getActivity().findViewById(R.id.readButton);
        searchButton=(Button) getActivity().findViewById(R.id.searchButton);
        //titlefile=(TextView) getActivity().findViewById(R.id.titlefile);
        newButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        //openButton.setOnClickListener(this);
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
        // ==> /storage/emulated/0/note.txt
        fileName = new EditText(getActivity());
        ad = new AlertDialog.Builder(getActivity());
        ad.setView(fileName);
        switch (v.getId()) {
            case R.id.newButton:
                EditextReadWrite.setText("");
                //titlefile.setText("");
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
                            //Log.i("ExternalStorageDemo", "Save to: " + path);
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
                                /*FileOutputStream fout = getActivity().openFileOutput(fileName.getText().toString() + ".txt", MODE_WORLD_READABLE);
                                fout.write(text.getText().toString().getBytes());*/
                                    snackbar = Snackbar.make(getView(), "Saved!", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    //titlefile.setText(path);
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
                        //titlefile.setText("");
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
                            //titlefile.setText(path);
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
            //case R.id.openButton:
            //intent = new Intent(Intent.ACTION_GET_CONTENT);
            //intent.setType("*/*");
            //startActivityForResult(intent, FILE_RESULT_CODER);
            // break;
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
    //call this method to speak text
    private void speakOut() {
        str = EditextReadWrite.getText().toString();
        if(result!=tts.setLanguage(Locale.US))
        {
            snackbar = Snackbar.make(getView(), "Oops! special characters not allowed.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else
        {
            //speak given text
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    //shutdown tts when activity destroy
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        //check status for TTS is initialized or not
        if (status == TextToSpeech.SUCCESS) {
            //if TTS initialized than set language
            result = tts.setLanguage(Locale.US);

            // tts.setPitch(5); // you can set pitch level
            // tts.setSpeechRate(2); //you can set speech speed rate

            //check language is supported or not
            //check language data is available or not
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar snackbar1 = Snackbar.make(getView(), "Oops! Missing data.", Snackbar.LENGTH_SHORT);
                snackbar1.show();
                //disable button
                readButton.setEnabled(false);
            } else {
                //if all is good than enable button convert text to speech
                readButton.setEnabled(true);
            }
        }
    }
   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==FILE_RESULT_CODER && resultCode == Activity.RESULT_OK && data!=null){
            state = Environment.getExternalStorageState();
            if (!(state.equals(Environment.MEDIA_MOUNTED))) {
                Toast.makeText(getActivity(), "There is no any sd card", Toast.LENGTH_LONG).show();
            }
            path =data.getData().getPath();

            EditextReadWrite.setText("");
            //titlefile.setText("");
            s = "";
            fileContent = "";
            Log.d("titlefile",path);
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
                //titlefile.setText(path);
            } catch (Exception e) {
                //titlefile.setText("Error"+path);
            }
        }
        else {
            snackbar = Snackbar.make(getView(),"Oops! Something went wrong.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }*/
}