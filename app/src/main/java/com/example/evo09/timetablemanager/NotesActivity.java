package com.example.evo09.timetablemanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class NotesActivity extends Fragment implements View.OnClickListener,TextToSpeech.OnInitListener {
    private ProgressDialog csprogress;
    Button newButton, saveButton, openButton,readButton,searchButton;
    private EditText text;
    private int result=0;
    TextView titlefile;
    private TextToSpeech tts;
    private static final String DNAME = "TimeScheduler";
    Intent intent ;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Notes");
        csprogress=new ProgressDialog(getActivity());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes,container,false);
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
            csprogress.setMessage("Loading...");
            csprogress.show();
            csprogress.setCancelable(false);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    AlarmDataShow();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            csprogress.dismiss();
                        }
                    }, 200);
                }
            }, 600);//just mention the time when you want to launch your action
    }
    public void AlarmDataShow(){
        tts = new TextToSpeech(getActivity(), this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        newButton=(Button)getActivity().findViewById(R.id.newButton);
        saveButton=(Button)getActivity().findViewById(R.id.saveButton);
        openButton=(Button) getActivity().findViewById(R.id.openButton);
        readButton=(Button) getActivity().findViewById(R.id.readButton);
        searchButton=(Button) getActivity().findViewById(R.id.searchButton);
        text=(EditText) getActivity().findViewById(R.id.text);
        titlefile=(TextView) getActivity().findViewById(R.id.titlefile);
        newButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        openButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        readButton.setOnClickListener(this);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public void onClick(View v ) {
        final File extStore =new File( Environment.getExternalStorageDirectory(), DNAME);
        // ==> /storage/emulated/0/note.txt
        final EditText fileName = new EditText(getActivity());
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(fileName);
        switch (v.getId()) {
            case R.id.newButton:
                text.setText("");
                titlefile.setText("");
                break;
            case R.id.saveButton:
                ad.setMessage("Save File");
                final String str = text.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    Snackbar snackbar = Snackbar.make(getView(), "No data to Save", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    ad.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!extStore.exists()) {
                                extStore.mkdirs();
                            }
                            String path = extStore.getAbsolutePath() + "/" + fileName.getText().toString() + ".txt";
                            Log.i("ExternalStorageDemo", "Save to: " + path);
                            try {
                                File myFile = new File(path);
                                myFile.createNewFile();
                                FileOutputStream fOut = new FileOutputStream(myFile);
                                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                                myOutWriter.append(str);
                                myOutWriter.close();
                                fOut.close();
                                /*FileOutputStream fout = getActivity().openFileOutput(fileName.getText().toString() + ".txt", MODE_WORLD_READABLE);
                                fout.write(text.getText().toString().getBytes());*/
                                Snackbar snackbar1 = Snackbar.make(getView(), "Saved!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            } catch (Exception e) {
                                createNetErrorDialog();
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
                        String path = extStore.getAbsolutePath() + "/" + fileName.getText().toString() + ".txt";
                        Log.i("ExternalStorageDemo", "Save to: " + path);
                        int c;
                        text.setText("");
                        String s = "";
                        String fileContent = "";
                        try {

                            File myFile = new File(path);
                            FileInputStream fIn = new FileInputStream(myFile);
                            BufferedReader myReader = new BufferedReader(
                                    new InputStreamReader(fIn));

                            while ((s = myReader.readLine()) != null) {
                                fileContent += s + "\n";
                            }
                            myReader.close();

                            text.setText(fileContent);
                            titlefile.setText(fileName.getText().toString());
                        } catch (Exception e) {
                            createNetErrorDialog();
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
            case R.id.openButton:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 7);
                break;
            case R.id.readButton:
                String str1 = text.getText().toString().trim();
                if (TextUtils.isEmpty(str1)) {
                    Snackbar snackbar = Snackbar.make(getView(), "No data to read", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    speakOut();
                    break;
                }
        }
    }
    //call this method to speak text
    private void speakOut() {
        String txtText = text.getText().toString();
        if(result!=tts.setLanguage(Locale.US))
        {
            Snackbar snackbar1 = Snackbar.make(getView(), "Enter right Words...... ", Snackbar.LENGTH_SHORT);
            snackbar1.show();
        }else
        {
            //speak given text
            tts.speak(txtText, TextToSpeech.QUEUE_FLUSH, null);
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
                Snackbar snackbar1 = Snackbar.make(getView(), "Missing data", Snackbar.LENGTH_SHORT);
                snackbar1.show();
                //disable button
                readButton.setEnabled(false);
            } else {
                //if all is good than enable button convert text to speech
                readButton.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch(requestCode){

            case 7:

                if(resultCode==RESULT_OK){
                    String state = Environment.getExternalStorageState();
                    if (!(state.equals(Environment.MEDIA_MOUNTED))) {
                        Toast.makeText(getActivity(), "There is no any sd card", Toast.LENGTH_LONG).show();
                    }
                    String path = data.getData().getPath();
                    Log.i("ExternalStorageDemo", "Save to: " + path);
                    int c;
                    text.setText("");
                    String s = "";
                    String fileContent = "";
                    try {

                        File myFile = new File(path);
                        FileInputStream fIn = new FileInputStream(myFile);
                        BufferedReader myReader = new BufferedReader(
                                new InputStreamReader(fIn));

                        while ((s = myReader.readLine()) != null) {
                            fileContent += s + "\n";
                        }
                        myReader.close();

                        text.setText(fileContent);
                        titlefile.setText(path);
                    } catch (Exception e) {
                        createNetErrorDialog();
                    }

                }
            break;
            case 5:

        }
    }
    protected void createNetErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Go to"+"\n"+"Settings->Tap'Apps'->Choose App"+"\n"+"->App Permissions"+"\n"+"Enable it")
                .setTitle("storage or File not Exist!")
                .setCancelable(false)
                .setPositiveButton("Open",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
}