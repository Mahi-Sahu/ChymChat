package com.example.chymchatapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button loginButton;
    TextView signUp;
    EditText emailAddress;
    EditText password;
    FirebaseAuth auth;
    //email pattern storing variable
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        auth=FirebaseAuth.getInstance();
        loginButton=findViewById(R.id.loginButton);
        emailAddress=findViewById(R.id.emailAddress);
        password=findViewById(R.id.loginpassword);
        signUp=findViewById(R.id.signUp);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailAddress.getText().toString();
                String pass=password.getText().toString();

                //in case email is empty, i.e. no email is entered then display appropriate message
                if(TextUtils.isEmpty(email.trim())){
                    Toast.makeText(Login.this,"Enter the email",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(pass)){
                    Toast.makeText(Login.this,"Enter the password",Toast.LENGTH_SHORT).show();
                }
                //if email pattern is not valid i.e. it does not match the valid pattern of email
                else if(!email.matches(emailPattern)){
                    emailAddress.setError("Give proper email address");
                }
                //check the password length
                else if(pass.length()<6){
                    password.setError("Provide a password having more than six characters");
                    Toast.makeText(Login.this,"Password should be longer than six characters",Toast.LENGTH_SHORT).show();
                }
                else {
                    //check the authentication of data
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //check whether the authentication id done successfully or not
                            if (task.isSuccessful()) {
                                try {
                                    //if the provided data is authenticated(matched successfully from the database)
                                    //then redirect to next screen
                                    mediaPlayer=MediaPlayer.create(Login.this,R.raw.chime_ting);
                                    mediaPlayer.start();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    //finish this else after doing back of main screen the app will not close and will lead to login activity
                                    finish();
                                } catch (Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //in case of no success
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setTextColor(Color.parseColor("#237FC6"));
                //set the color back to original;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = MediaPlayer.create(Login.this, R.raw.chime_ting);
                mediaPlayer.start();
                Intent intent=new Intent(Login.this, Registration.class);
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
    protected void onResume() {
        super.onResume();
        signUp.setTextColor(Color.BLACK); // Reset to default color
    }

}