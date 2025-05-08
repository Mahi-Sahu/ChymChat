package com.example.chymchatapp;

import static com.example.chymchatapp.ChatWindow.receiverImage;
import static com.example.chymchatapp.ChatWindow.senderImg;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModelClass> messageAdapterArrayList;
    int ITEM_SEND_TEXT=1;
    int ITEM_RECEIVE_TEXT=2;
    int ITEM_SEND_AUDIO=3;
    int ITEM_RECEIVE_AUDIO=4;

    public MessageAdapter(Context context, ArrayList<msgModelClass> messageAdapterArrayList) {
        this.context = context;
        this.messageAdapterArrayList = messageAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND_TEXT){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewHolder(view);
        }
        else if(viewType==ITEM_RECEIVE_TEXT){
            View view=LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return new receiverViewHolder(view);
        } else {
            View view=LayoutInflater.from(context).inflate(R.layout.activity_audio_message_layout,parent,false);
            return new audioViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelClass messages=messageAdapterArrayList.get(position);
        if(holder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder=(senderViewHolder)holder ;
            viewHolder.msgText.setText(messages.getMsg());

            if (senderImg != null && !senderImg.isEmpty()) {
                Picasso.get().load(senderImg).into(viewHolder.imageView);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.userimage);
            }//from chat window.java import this static variable-senderImg

        }
        else if(holder.getClass()==receiverViewHolder.class){
            receiverViewHolder viewHolder=(receiverViewHolder) holder;
            viewHolder.msgText.setText(messages.getMsg());
            if (receiverImage != null && !receiverImage.isEmpty()) {
                Picasso.get().load(receiverImage).into(viewHolder.imageView);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.userimage);
            }
//from chat window import static variable-receiverImage
        } else if (holder.getClass()==audioViewHolder.class) {
            audioViewHolder viewHolder=(audioViewHolder) holder;
            viewHolder.playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer=MediaPlayer.create(context,messages.getAudId());
                    mediaPlayer.start();
                }
            });

        }
    }

    @Override
    public int getItemCount() {

        return messageAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelClass messages=messageAdapterArrayList.get(position);
        if(messages.isAudio()){
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
                return ITEM_SEND_AUDIO;
            }
            else {
                return ITEM_RECEIVE_AUDIO;
            }
        }
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.senderId)){
            return ITEM_SEND_TEXT;
        }
        else {
            return ITEM_RECEIVE_TEXT;
        }
    }

    //create sender viewHolder and receiver viewHolder
    class  senderViewHolder extends RecyclerView.ViewHolder {
        //this viewholder will hold the layout in which the message sent by sender will be viewed-"sender_layout"
        //so create the respective xmlLayout
        ImageView imageView;
        TextView msgText;
        public senderViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView=itemView.findViewById(R.id.sender_image);
            msgText=itemView.findViewById(R.id.senderTextMessage);
        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        //this viewholder will hold the layout in which the message received by the receiver will be viewed-"receiver_layout"
        //create the respective XML Layout-
        ImageView imageView;
        TextView msgText;
        public receiverViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView=itemView.findViewById(R.id.pro);
            msgText=itemView.findViewById(R.id.receiverTextMessage);
        }
    }

    class audioViewHolder extends RecyclerView.ViewHolder{
        ImageView playPauseButton;

        public audioViewHolder(@NonNull View itemView) {
            super(itemView);
            playPauseButton=itemView.findViewById(R.id.playPauseButton);
        }
    }

}
