package com.example.chymchatapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class audio_message_layout extends AppCompatActivity {
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ImageView playPause;
    Context context;
    int audId;
    String audioFileName;
    private Handler handler=new Handler();

    public audio_message_layout(Context context, int audId, String audioFile) {
        this.context=context;
        this.audId=audId;
        this.audioFileName=audioFile;
        this.mediaPlayer=MediaPlayer.create(context,audId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audio_message_layout);

        seekBar=findViewById(R.id.seekBar);
        playPause=findViewById(R.id.playPauseButton);

        //functionalities for play-pause imageView
        playPause.setImageResource(R.drawable.play_button);//initially set to play
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playPause.setImageResource(R.drawable.play_button);
                }
                else{
                    mediaPlayer.start();
                    playPause.setImageResource(R.drawable.pause_button);
                    updateSeekBar();
                }
            }
        });

        //when audio is completed reset it
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0); // Reset SeekBar position
                playPause.setImageResource(R.drawable.play_button);
            }
        });

        //adding functionalities of seekBar
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
                mediaPlayer.pause();
            }
        });
        updateSeekBar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition()); // Update SeekBar position
        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            }, 1000); // Schedule next update every second
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}