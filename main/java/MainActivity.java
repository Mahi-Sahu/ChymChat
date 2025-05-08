package com.example.chymchatapp;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    MediaPlayer mediaPlayer;
    RecyclerView mainUser;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView logoutButton;
    ImageView settingIcon,cameraIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mainUser=findViewById(R.id.mainUser_recyclerView);
        logoutButton=findViewById(R.id.logoutButton);
        settingIcon=findViewById(R.id.settingicon);
        cameraIcon=findViewById(R.id.cameraicon);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null){
            //if no user signed in -> goto login activity
            Log.d("AuthCheck", "No user signed in. Redirecting to Login.");
            playChime();
            Intent intent=new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
        else{
            Log.d("AuthCheck", "User signed in");
        }

        DatabaseReference reference=database.getReference().child("user");//this data is going to be stored in the form of arrayList
        //allocate memory to userArrayList
        usersArrayList=new ArrayList<>();

        //this listener will act as listener for real time updates and any change in database will trigger the onDataChange()
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //for each snapshot
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    //dataSnapshot represents a snapshot of data from Firebase.
                    //.getValue(Users.class) converts the snapshot into an instance of the Users class.
                    //Firebase automatically maps the data fields to the properties of the Users class.
                    //so this is how data is retrieved from realtime database into user class object
                    Users users=dataSnapshot.getValue(Users.class);
                    if (users != null) {
                        usersArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();//used when data is changing dynamically to notify and refresh recycler view
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

        //setLinearLayout will arrange the item of recycler view in linearlayout(either horizontal or vertical)
        mainUser.setLayoutManager(new LinearLayoutManager(this));
        adapter=new UserAdapter(MainActivity.this,usersArrayList);
        mainUser.setAdapter(adapter);


        //logout user using logoutButton
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a dialog of yes or no to logout
                playChime();
                Dialog dialog=new Dialog(MainActivity.this,R.style.dialogBox);//dialog box style created in themes.xml
                dialog.setContentView(R.layout.dialog_layout);
                if (dialog.isShowing()) {
                    dialog.dismiss(); // Dismiss before switching activity
                }
                //find id of buttons and set on click listeners on that
                Button yes, no;
                yes=dialog.findViewById(R.id.yes_button);
                no=dialog.findViewById(R.id.no_button);
                //set on click listener on yes
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        playChime();
                        Intent intent=new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                //set on click listener for no button
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dismiss dialog
                        playChime();
                        dialog.dismiss();
                    }
                });
                //else you have to show the dialog
                dialog.show();
            }
        });

        settingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,10);
            }
        });
    }

    private void playChime() {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release old instance
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.chime_ting);
        mediaPlayer.start();
    }

}