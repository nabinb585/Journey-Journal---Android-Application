package com.example.journeyjournal.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.journeyjournal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class About extends AppCompatActivity {

    BottomNavigationView imagePage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        imagePage = findViewById(R.id.imagePage);
        imagePage.setSelectedItemId(R.id.about);
        imagePage.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.calender:
                        startActivity(new Intent(getApplicationContext(),Calendar.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), JournalDashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}