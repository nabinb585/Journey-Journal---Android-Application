package com.example.journeyjournal.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journeyjournal.R;
import com.example.journeyjournal.views.dao.UserCredentialDao;
import com.example.journeyjournal.views.database.UserCredentialDatabase;
import com.example.journeyjournal.views.entities.UserCredential;

import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {
    private EditText fullName, emailRegister, pwd, confirmPass;
    private AppCompatButton btnSignUp;
    private TextView log;
    private UserCredentialDao userCredentialDao;
    public static final Pattern EMAIL_ADDRESS = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullName = findViewById(R.id.fullName);
        emailRegister = findViewById(R.id.emailRegister);
        pwd = findViewById(R.id.pwd);
        confirmPass = findViewById(R.id.confirmPass);
        btnSignUp = findViewById(R.id.btnSignUp);
        log = findViewById(R.id.log);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, LogInPage.class));
            }
        });
        userCredentialDao = Room.databaseBuilder(this, UserCredentialDatabase.class, "UserCredential")
                .allowMainThreadQueries()
                .build().getUserCredentailDao();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fullName.getText().toString().trim();
                String email = emailRegister.getText().toString().trim();
                String password = pwd.getText().toString().trim();
                String conPassword = confirmPass.getText().toString().trim();
                /*String regx = "[A-z]?";*/

                if (name.equals("") && email.equals("") && password.equals("") && conPassword.equals("")){
                        Toast.makeText(SignUp.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if (name.equals("")){
                    fullName.setError("Full Name is required");
                    fullName.requestFocus();
                }
              /*  else if (!regx.matches(name)){
                    fullName.setError("Enter Valid Name");
                    fullName.requestFocus();
                }*/
                else if (email.equals("")){
                    emailRegister.setError("Email is required");
                    emailRegister.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailRegister.setError("Please enter valid email address");
                    emailRegister.requestFocus();
                }
                else if (password.equals("")){
                    pwd.setError("Password is required");
                    pwd.requestFocus();
                }
                else if (password.equals(conPassword)){
                    UserCredential userCredential = new UserCredential(email, password);
                    userCredentialDao.insert(userCredential);
                    Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent moveToLogin = new Intent(SignUp.this, SignIn.class);
                    startActivity(moveToLogin);
                }
                else if(!password.equals(conPassword)){
                    confirmPass.setError("Password should be match");
                    confirmPass.requestFocus();
                }

            }
        });
    }

}