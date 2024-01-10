package com.example.journeyjournal.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.journeyjournal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Calendar extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1 ;
    BottomNavigationView calendarNavigation;
    FloatingActionButton message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        /*message=findViewById(R.id.journalText);*/
        calendarNavigation = findViewById(R.id.calendarNavigation);
        calendarNavigation.setSelectedItemId(R.id.calender);

        calendarNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.calender:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), JournalDashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;

            }
        });

        /*message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateJournalEntryActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });*/
    }
}