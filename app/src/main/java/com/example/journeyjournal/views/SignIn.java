package com.example.journeyjournal.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journeyjournal.R;
import com.example.journeyjournal.views.dao.UserCredentialDao;
import com.example.journeyjournal.views.database.UserCredentialDatabase;
import com.example.journeyjournal.views.entities.UserCredential;


public class SignIn extends AppCompatActivity {
    private EditText email, password;
    private ImageView imgBack;
    private AppCompatButton btnSignIn;
    private TextView register;

    UserCredentialDao db;
    UserCredentialDatabase database;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sharedPreferences = getSharedPreferences("journal_pref", Context.MODE_PRIVATE);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.btnSignIn);
        register = findViewById(R.id.register);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, LogInPage.class));
            }
        });

        // getting email and password from database after registering details from signup page
        database = Room.databaseBuilder(this, UserCredentialDatabase.class, "UserCredential")
                .allowMainThreadQueries()
                .build();
        db = database.getUserCredentailDao();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String e = email.getText().toString().trim();
               String p = password.getText().toString().trim();

               //cheking validations & email/password after clicking on sign in button
                UserCredential userCredential = db.getUserCredentail(e, p);

                //checking validations
                if (e.equals("")||p.equals("")){
                    Toast.makeText(SignIn.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
               else if (userCredential !=null){

                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putBoolean("user_login", true).apply();

                   //navigate from sign in to sign up
                    Intent intent = new Intent(SignIn.this, JournalDashboard.class);
                    intent.putExtra("UserCredential", userCredential);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(SignIn.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}