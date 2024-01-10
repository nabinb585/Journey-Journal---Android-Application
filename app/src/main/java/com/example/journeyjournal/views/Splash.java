package com.example.journeyjournal.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.journeyjournal.R;

public class Splash extends AppCompatActivity {
    private TextView text1, text2;

    private static int splash_timeout = 5000;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

      /*  sharedPreferences = getSharedPreferences("journal_prefs", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("user_login",false);*/

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);

        //allows to send and process Message
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashintent = new Intent(Splash.this, LogInPage.class);
                startActivity(splashintent);
                finish();
            }
        }, splash_timeout);

        Animation myAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.animation2);
        text1.startAnimation(myAnimation);
        Animation myAnimation2 = AnimationUtils.loadAnimation(Splash.this, R.anim.animation);
        text2.startAnimation(myAnimation2);
    }
}