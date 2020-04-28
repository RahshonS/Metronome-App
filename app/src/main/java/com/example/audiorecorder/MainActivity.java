package com.example.audiorecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //Declare variables
    Button btnStartRec, btnStopRec, btnPlay, btnStop;
    String pathSave="";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer, player;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissionFromDevice())
            requestPermission();

        btnPlay = findViewById(R.id.btnPlay);
        btnStartRec = findViewById(R.id.btnStartRec);
        btnStopRec = findViewById(R.id.btnStopRec);
        btnStop = findViewById(R.id.btnStop);

        {
            btnStartRec.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){
                    if(checkPermissionFromDevice()) {


                        pathSave = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/"
                                + UUID.randomUUID().toString() + "_audio_record_3gp";
                        setupMediaRecorder();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        btnPlay.setEnabled(false);
                        btnStop.setEnabled(false);

                        Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        requestPermission();
                    }

                }
            });

            btnStopRec.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mediaRecorder.stop();
                    btnStopRec.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnStartRec.setEnabled(true);
                    btnStopRec.setEnabled(false);
                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    btnStop.setEnabled(true);
                    btnStopRec.setEnabled(false);
                    btnStartRec.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try{
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Playing ... ", Toast.LENGTH_SHORT).show();
                }
            });

            btnStop.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    btnStopRec.setEnabled(false);
                    btnStartRec.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnPlay.setEnabled(true);

                    if(mediaRecorder != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });



        }
    }

    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);

    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        },REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch(requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    //METRONOME

    public void play(View v){
        if(player == null){
            player = MediaPlayer.create(this,R.raw.metro);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void pause(View v){
        if(player != null){
            player.pause();
        }
    }

    public void stop(View v){
        super.onStop();
        stopPlayer();

    }

    private void stopPlayer(){
        if(player != null){
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }
}
