package com.example.motoprimemusic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    ImageView fastrewind, previous, playbtn, next, fastforward;
    TextView txtsn, txtstart, txtstop;
    SeekBar seekbar;

    String sname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread seekbarupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        fastrewind = (ImageView) findViewById(R.id.fastrewind);
        previous = (ImageView) findViewById(R.id.previous);
        playbtn = (ImageView) findViewById(R.id.playbtn);
        next = (ImageView) findViewById(R.id.next);
        fastforward = (ImageView) findViewById(R.id.fastforward);
        txtsn = (TextView) findViewById(R.id.txtsn);
        txtstart = (TextView) findViewById(R.id.txtstart);
        txtstop = (TextView) findViewById(R.id.txtstop);
        seekbar = (SeekBar) findViewById(R.id.seekbar);


        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtsn.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtsn.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();



        seekbarupdate=new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;

                while (currentposition < totalDuration) {
                    try {
                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentposition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekbar.setMax(mediaPlayer.getDuration());
        seekbarupdate.start();


    //seekar update
     seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
         @Override
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

         }

         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {

         }

         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {
         mediaPlayer.seekTo(seekBar.getProgress());
         }
     });

     //song ending timer

     String endTime= createTime(mediaPlayer.getDuration());
     txtstop.setText(endTime);

     final Handler handler=new Handler();
     final int delay=1000;

     handler.postDelayed(new Runnable() {
         @Override
         public void run() {
             String currentTime= createTime(mediaPlayer.getCurrentPosition());
             txtstart.setText(currentTime);
             handler.postDelayed(this,delay);
         }
     },delay);

//fastforward
     fastforward.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if(mediaPlayer.isPlaying()){
                 mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
             }
         }
     });


     //fastbackward
        fastrewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });







        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playbtn.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    playbtn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }
        });






//next song

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=(position+1)%mySongs.size();
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtsn.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);
            }
        });


        //previous song
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtsn.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);
            }
        });
    }

    //song time
    public String createTime(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;

        time+=min+":";

        if(sec <10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }
}