package com.example.isangeet;

import static java.lang.Thread.*;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mdplayer.stop();
        mdplayer.release();
        updateSeek.interrupt();
    }
    TextView sng_nme;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mdplayer;
    String txtcontent;
    int position;
    SeekBar bar;
    Thread updateSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_song);
        sng_nme = findViewById(R.id.textView2);
        play = findViewById(R.id.imageView5);
        previous = findViewById(R.id.imageView3);
        next = findViewById(R.id.imageView4);
        bar = findViewById(R.id.seekBar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songlist");
        txtcontent = intent.getStringExtra("currentSong");
        sng_nme.setText(txtcontent);
        sng_nme.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri url = Uri.parse(songs.get(position).toString());
        mdplayer = MediaPlayer.create(this,url);
        mdplayer.start();
        bar.setMax(mdplayer.getDuration());
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mdplayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mdplayer.getDuration()) {
                        currentPosition = mdplayer.getCurrentPosition();
                        bar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdplayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mdplayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.outline_autopause_24);
                    mdplayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdplayer.stop();
                mdplayer.release();
                if (position !=0) {
                    position = position-1;
                }
                else{
                    position = songs.size()-1;
                }
                Uri url = Uri.parse(songs.get(position).toString());
                mdplayer = MediaPlayer.create(getApplicationContext(),url);
                mdplayer.start();
                bar.setMax(mdplayer.getDuration());
                txtcontent = songs.get(position).getName().toString();
                sng_nme.setText(txtcontent);
                play.setImageResource(R.drawable.outline_autopause_24);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdplayer.stop();
                mdplayer.release();
                if (position !=songs.size()-1) {
                    position = position+1;
                }
                else{
                    position = 0;
                }
                Uri url = Uri.parse(songs.get(position).toString());
                mdplayer = MediaPlayer.create(getApplicationContext(),url);
                mdplayer.start();
                bar.setMax(mdplayer.getDuration());
                txtcontent = songs.get(position).getName().toString();
                sng_nme.setText(txtcontent);
                play.setImageResource(R.drawable.outline_autopause_24);
            }
        });
    }

}