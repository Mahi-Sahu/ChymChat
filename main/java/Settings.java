package com.example.chymchatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class Settings extends AppCompatActivity {

    ImageView settingProfile;
    EditText settingName, settingStatus;
    Button updateButton;
    String email,password;
    Uri setImageUri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        settingName=findViewById(R.id.settingName);
        settingProfile=findViewById(R.id.addImageIcon);
        settingStatus=findViewById(R.id.settingStatus);
        updateButton=findViewById(R.id.updateButton);

        //first fetch the actual data from the firebase and then user may or may not change it
        DatabaseReference databaseReference=database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("Upload").child(auth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("mail").getValue() != null) {
                    email = snapshot.child("mail").getValue().toString();
                }
                if (snapshot.child("password").getValue() != null) {
                    password = snapshot.child("password").getValue().toString();
                }
                String name = snapshot.child("username").getValue() != null ?
                        snapshot.child("username").getValue().toString() : "";
                String profilepic = snapshot.child("profilepic").getValue() != null ?
                        snapshot.child("profilepic").getValue().toString() : "";
                String status = snapshot.child("status").getValue() != null ?
                        snapshot.child("status").getValue().toString() : "";
                settingName.setText(name);
                settingStatus.setText(status);
                Picasso.get().load(profilepic).into(settingProfile);//set the image
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //add clickListener on imageView
        //on clicking the gallery option should open to set the image
        settingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the updated data
                String name=settingName.getText().toString();
                String status=settingStatus.getText().toString();

                //if image has been selected by the user
                if(setImageUri!=null){
                    //store this data in Firebase storage
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri=uri.toString();
                                    Users users=new Users(finalImageUri,email,name,password,auth.getUid(),status,password);
                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //if image is successfully stored i.e., task is successfull
                                            if(task.isSuccessful()){
                                                Toast.makeText(Settings.this,"Data is saved",Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(Settings.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            //if data is not stored
                                            else{
                                                Toast.makeText(Settings.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                //when imageUri==null
                else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri=uri.toString();
                            Users users=new Users(finalImageUri,email,name,password,auth.getUid(),status,password);
                            databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Settings.this,"Data is saved",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(Settings.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(Settings.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if(data!=null && data.getData()!=null){//if user has selected image from gallery then set it
                setImageUri=data.getData();
                settingProfile.setImageURI(setImageUri);
            }
        }
    }
}