package com.example.chymchatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatWindow extends AppCompatActivity {

    String receiverImg,receiverUid,receiverName,receiverStatus,senderUid;
    ImageView chat_userimage,moodPickerButton;
    TextView rName,rStatus;
    CardView send_button;
    EditText writemsg;
    FirebaseAuth auth;
    FirebaseDatabase database;
    public static String senderImg;
    public static String receiverImage;
    //create senderRoom and receiverRoom
    String senderRoom, receiverRoom;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    ArrayList<msgModelClass> messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);
        //gt the intent name passed by the UserAdapter
        receiverName=getIntent().getStringExtra("nameee");//gets the key value associated with the key="nameee" in UserAdapter class
        receiverImg=getIntent().getStringExtra("receiverImg");
        receiverUid=getIntent().getStringExtra("uid");
        receiverStatus=getIntent().getStringExtra("statusss");
        //check for null values
        if (receiverName == null) receiverName = "Unknown";
        if (receiverImg == null) receiverImg = "";
        if (receiverUid == null) receiverUid = "";
        if (receiverStatus == null) receiverStatus = "Offline";

        chat_userimage=findViewById(R.id.chat_userimage);
        rName=findViewById(R.id.receiver_name);
        rStatus=findViewById(R.id.chat_status);
        send_button=findViewById(R.id.send_button);
        writemsg=findViewById(R.id.writemsg);
        moodPickerButton=findViewById(R.id.moodPickerButton);

        send_button.setClickable(true);
        send_button.setFocusable(true);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        messagesArrayList=new ArrayList<>();//to store messages in ArrayList

        //get the senderUid from firebase
        senderUid=auth.getUid();
        senderRoom=senderUid+receiverUid;
        receiverRoom=receiverUid+senderUid;
        //create database reference
        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        //database reference for storing chats
        DatabaseReference chatReference=database.getReference().child("chats").child(senderRoom).child("message");//message of senderRoom child from below

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profilepic").exists()) {
                    senderImg = snapshot.child("profilepic").getValue(String.class);
                } else {
                    senderImg = "android.resource://" + getPackageName() + "/" + R.drawable.userimage; // Default or placeholder image URL
                }
                receiverImage=receiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView=findViewById(R.id.msgAdapter);
        //set the recycler view functionalities
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        //setStackFromEnd because we want our messages to display from the bottom as in instagram
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);//set the layout of recyclerView
        messageAdapter=new MessageAdapter(ChatWindow.this,messagesArrayList);
        recyclerView.setAdapter(messageAdapter);//set the adapter for recycler view

        //This ensures that the chat scrolls down when the keyboard opens, similar to Instagram.
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(() -> {
                        if (messagesArrayList.size() > 0) {
                            recyclerView.smoothScrollToPosition(messagesArrayList.size() - 1);
                        }
                    }, 100);
                }
            }
        });


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    msgModelClass message=dataSnapshot.getValue(msgModelClass.class);
                    //store in messagesArrayList
                    messagesArrayList.add(message);
                }
                if (messageAdapter != null) {
                    messageAdapter.notifyDataSetChanged();
                }
                recyclerView.postDelayed(() -> {
                    if (messagesArrayList.size() > 0) {
                        recyclerView.smoothScrollToPosition(messagesArrayList.size() - 1);//shifts the position of layout back when keyboard is closed
                    }
                }, 100);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //for setting image use picasso for that first dependency is needed to be added
        if (receiverImg != null && !receiverImg.isEmpty()) {
            Picasso.get()
                    .load(receiverImg)
                    .placeholder(R.drawable.userimage)
                    .error(R.drawable.userimage)
                    .into(chat_userimage);
        } else {
            chat_userimage.setImageResource(R.drawable.userimage); // Fallback
        }//get the image from receiverImg and set it to chat_userimage
        rName.setText(""+receiverName);
        rStatus.setText(""+receiverStatus);

        //get the message from editText and send it when clicked on send_button
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=writemsg.getText().toString();
                if(msg.isEmpty()){
                    Toast.makeText(ChatWindow.this,"Enter the message",Toast.LENGTH_SHORT).show();
                    return;
                }

                //when message has been send and empty the edit text area
                writemsg.setText("");
                Date date=new Date();
                msgModelClass messages=new msgModelClass(msg,senderUid,date.getTime());
                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats").child(senderRoom).child("message").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats").child(receiverRoom).child("message").push().setValue(messages);
                        recyclerView.postDelayed(() -> {
                            if (messagesArrayList.size() > 0) {
                                recyclerView.smoothScrollToPosition(messagesArrayList.size() - 1);
                            }
                        }, 100);

                    }
                });
            }
        });

        //adding moodPicker functionalities
        moodPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an instance of mood picker
                MoodPicker moodPicker=new MoodPicker(new MoodPicker.MoodSelectionListener() {
                    @Override
                    public void onMoodSelect(String mood, int audId, String audioFileName) {
                        //call function to send an audio message
                        sendAudioMessage(audId,audioFileName);
                    }
                });
                moodPicker.show(getSupportFragmentManager(),"Mood Picker");
            }
        });

    }
    private void sendAudioMessage(int audId,String audioFileName){
        //Toast.makeText(this, "Sending mood audio: " + audioFileName, Toast.LENGTH_SHORT).show();
        String senderId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date=new Date();

        //create audioMessage object
        msgModelClass audioMessage=new msgModelClass(audId,FirebaseAuth.getInstance().getCurrentUser().getUid());
        audioMessage.setAudio(true);
        audioMessage.setSenderId(senderId);
        audioMessage.setTimestamp(date.getTime());

        //save audio message in firebase
        database.getReference().child("chats").child(senderRoom).child("message").push().setValue(audioMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //setvalue in receiver's side also
                    database.getReference().child("chats").child(receiverRoom).child("message").push().setValue(audioMessage);
                    recyclerView.postDelayed(() -> {
                        if (messagesArrayList.size() > 0) {
                            recyclerView.smoothScrollToPosition(messagesArrayList.size() - 1);
                        }
                    }, 100);
                }
            }
        });
    }
}