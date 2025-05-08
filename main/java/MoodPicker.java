package com.example.chymchatapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MoodPicker extends BottomSheetDialogFragment {//BottomSheetDialogFragment will open a dialog at the bottom in the form of sheet instead of a classical floating dialog
    private MoodSelectionListener moodSelectionListener;
    MediaPlayer mediaPlayer;
    //Interface to communicate the selected mood back to the activity
    public interface MoodSelectionListener{
        void onMoodSelect(String mood,int audId, String audioFileName);
    }

    public MoodPicker(MoodSelectionListener listener){
        this.moodSelectionListener=listener;
    }

    //when the bottom sheet is created inflate it(convert to view) and set click listeners for moods

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.mood_picker_layout,container,false);

        TextView happyMood=view.findViewById(R.id.moodHappy);
        TextView sadMood=view.findViewById(R.id.moodSad);
        TextView angryMood=view.findViewById(R.id.angryMood);
        TextView attentionMood=view.findViewById(R.id.attentionMood);
        TextView celebrationMood=view.findViewById(R.id.celebrationMood);
        TextView disagreeMood=view.findViewById(R.id.disagreeMood);
        TextView relaxedMood=view.findViewById(R.id.relaxedMood);
        TextView shatteredMood=view.findViewById(R.id.shatteredMood);
        TextView thinkMood=view.findViewById(R.id.thinkMood);

        happyMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83D\uDE0A Happy",R.raw.happy_sound,"Happy");
            }
        });
        sadMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83D\uDE22 Sad",R.raw.sad_sound,"Sad");
            }
        });
        angryMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83D\uDE21 Angry",R.raw.angry_sound,"Angry");
            }
        });
        attentionMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83D\uDCE2 Attention",R.raw.attention_sound,"Attention");
            }
        });
        celebrationMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83C\uDF89 Celebration",R.raw.celebration_sound,"Celebration");
            }
        });
        disagreeMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("❌ Disagree",R.raw.disagreement_sound,"Disagreed");
            }
        });
        relaxedMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83D\uDE0C Relaxed",R.raw.calm_sound,"Relaxed");
            }
        });
        shatteredMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83D\uDE1E Shattered",R.raw.shattering_sound,"Shattered");
            }
        });
        thinkMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("\uD83E\uDD14 Think",R.raw.thinking_sound,"Thinking");
            }
        });

        return  view;
    }

    private void selectMood(String mood,int audId,String audioFile){
        if(moodSelectionListener !=null){
            //Toast.makeText(getContext(), "Selected mood: " + mood, Toast.LENGTH_SHORT).show();
            moodSelectionListener.onMoodSelect(mood,audId,audioFile);
        }
        if(audId!=0){
            audio_message_layout audioMessageLayout=new audio_message_layout(getContext(),audId,audioFile);
            View view=getLayoutInflater().inflate(R.layout.activity_audio_message_layout,null);
            TextView fileName=view.findViewById(R.id.audioFileName);
            fileName.setText(audioFile);
            dismiss();
        }
        else{
            Toast.makeText(getContext(),"No mood selected. Please select the mood",Toast.LENGTH_SHORT).show();
        }
        dismiss();//close the dialog
    }
}
