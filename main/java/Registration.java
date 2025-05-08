package com.example.chymchatapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Registration extends AppCompatActivity {

    EditText emailAddress;
    ImageView userimage;
    EditText username;
    EditText password,confirmPassword;
    TextView loginLink;
    Button createAccount;
    FirebaseAuth auth;
    MediaPlayer mediaPlayer;
    Uri imageURI;
    String imageUri;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;//represents Firebase realtime database
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        emailAddress=findViewById(R.id.signUpEmail);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.ConfirmPassword);
        loginLink=findViewById(R.id.loginLink);
        createAccount=findViewById(R.id.createAccountButton);
        userimage=findViewById(R.id.userimage);

        //on clicking profile image open it in gallery to choose an image for profile pic
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");//this will on;y allow user to pic an image in format like png,jpg etc
                intent.setAction(Intent.ACTION_GET_CONTENT);//it helps the system to open file picer so that user can choose image for profile
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 10);
                //create chooser helps to pick option from where image is to be chosen like file manager,gallery etc
                //select picture will be the title appearing at the top
                //10(can be any number) is th code of this request being made which is returned toonActivityResult()
            }
        });

        //create account
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the information provied by the user and then store it in database
                String namee=username.getText().toString();
                String emaill=emailAddress.getText().toString();
                String passwordd= password.getText().toString();
                String cPasswordd=confirmPassword.getText().toString();
                String status="Hey! I am using this application";//default status

                //check if textFields are not empty
                if(TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(passwordd) || TextUtils.isEmpty(cPasswordd)) {
                    Toast.makeText(Registration.this, "Please enter valid information", Toast.LENGTH_SHORT).show();
                    return;//to ensure further tasks are not carried out
                } else if (!emaill.matches(emailPattern)) {
                    emailAddress.setError("Type a valid email");
                } else if (passwordd.length()<6) {
                    password.setError("Provide a password having more than six characters");
                            Toast.makeText(Registration.this,"Password should be longer than six characters",Toast.LENGTH_SHORT).show();
                } else if (!passwordd.equals(cPasswordd)) {
                    password.setError("Password doesn't match");
                }
                else{
                    auth.createUserWithEmailAndPassword(emaill,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //if user is successfully authenticated then get its unique id
                                //getResult() fetches the result of the authentication operation, which is an AuthResult object.
                                //getUser() is called on the AuthResult object to get the authenticated FirebaseUser
                                //getUid() fetches the unique user ID (UID) assigned by Firebase Authentication to the authenticated user.
                                String id=task.getResult().getUser().getUid();
                                //it will return database reference and helps to read and write there
                                //navigates to the user node in database
                                //navigates to specific user with the given unique id
                                DatabaseReference reference=database.getReference().child("user").child(id);
                                StorageReference storageReference=storage.getReference().child("Upload").child(id);

                                //if user image uri is not null then store it in storage
                                if(imageURI!=null){
                                    //store the file in storage and upload the image and on completion execute the given task
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                //if task is successful then Retrieve the download URL of the uploaded image from Firebase Storage
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        //on success the downloaded uri is retrieved
                                                        imageUri=uri.toString();
                                                        //creating new object of user and storing it in realtime database
                                                        Users users=new Users(imageUri,emaill,namee,passwordd,id,status,cPasswordd);//the Driver class we have created
                                                        //stores this object value at given reference
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Intent intent=new Intent(Registration.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else{
                                                                    Toast.makeText(Registration.this,"Error in creating the user account",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                //if image is null
                                else {
                                    String status="Hey! I am using this application";
                                    //you can provide the uri for default image stored in storage of firebase
                                    //imgeuri="   ";
                                    String defaultImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.userimage).toString();
                                    Users users=new Users(defaultImageUri,emaill,namee,passwordd,id,status,cPasswordd);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent=new Intent(Registration.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(Registration.this,"Error in creating the user",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            //if not authorised
                            else{
                                Toast.makeText(Registration.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = MediaPlayer.create(Registration.this, R.raw.chime_ting);
                mediaPlayer.start();

                loginLink.setTextColor(Color.parseColor("#237FC6"));
                Intent intent=new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
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
        if(requestCode==10){
            if(data!=null && data.getData()!=null){
                //get the image data and set it in the user image
                imageURI=data.getData();
                userimage.setImageURI(imageURI);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}