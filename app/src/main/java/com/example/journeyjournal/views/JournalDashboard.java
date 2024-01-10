package com.example.journeyjournal.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journeyjournal.R;
import com.example.journeyjournal.views.adapters.NoteAdapters;
import com.example.journeyjournal.views.database.NoteDatabase;
import com.example.journeyjournal.views.entities.Notes;
import com.example.journeyjournal.views.listeners.NotesListeners;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class JournalDashboard extends AppCompatActivity implements NotesListeners {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 4;
    public static final int REQUEST_CODE_SHOW_NOTE = 2;
    private RecyclerView notesRecyleView;
    private GoogleSignInClient mGoogleSignInClient;
    private List<Notes> notesList;
    private NoteAdapters noteAdapters;
    private int noteClickedPosition = -1;
    BottomNavigationView homePage;
    boolean DoublePressToExit = false;
    SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {
        if(DoublePressToExit){
            finishAffinity();
        }
        else {
            DoublePressToExit = true;
            Toast.makeText(this, "Press again to exit",Toast.LENGTH_SHORT).show();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DoublePressToExit= false;
                }
            },1500);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaldashboard);

        ImageView imgLogOut = findViewById(R.id.imgLogOut);
        homePage = findViewById(R.id.homePage);
        sharedPreferences = getSharedPreferences("journal_pref", Context.MODE_PRIVATE);
        homePage.setSelectedItemId(R.id.home);
        homePage.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.calender:
                        startActivity(new Intent(getApplicationContext(),Calendar.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        return true;
                }
                return false;
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personEmail = acct.getEmail();
            Toast.makeText(this, "Username :" +personGivenName, Toast.LENGTH_SHORT).show();
        }

        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogOutDialog();
            }
        });

        ImageView addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateJournalEntryActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        notesRecyleView = findViewById(R.id.notesRecyleView);
        notesRecyleView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        notesList = new ArrayList<>();
        noteAdapters = new NoteAdapters(notesList, this);
        notesRecyleView.setAdapter(noteAdapters);
        getNotes(REQUEST_CODE_SHOW_NOTE, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteAdapters.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (notesList.size() != 0) {
                    noteAdapters.searchJournalNotes(s.toString());          }
            }
        });
    }

    void showLogOutDialog(){
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations
                = android.R.style.Animation_Dialog;
        dialog.setContentView(R.layout.layout_logout);
        TextView textCancel = dialog.findViewById(R.id.textCancel);
        TextView textLogOut = dialog.findViewById(R.id.log_out);
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        textLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("user_login", false).apply();
                signOut();
                Toast.makeText(view.getContext(), "Sign Out successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                Intent intent = new Intent(JournalDashboard.this, LogInPage.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onNotesClicked(Notes notes, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateJournalEntryActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", notes);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Notes>>{
            @Override
            protected List<Notes>doInBackground(Void... voids){
                return NoteDatabase
                        .getNoteDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Notes> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTE){
                    notesList.addAll(notes);
                    noteAdapters.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_NOTE){
                    notesList.add(0, notes.get(0));
                    noteAdapters.notifyItemInserted(0);
                    notesRecyleView.smoothScrollToPosition(0);
                }
                else if (requestCode == REQUEST_CODE_UPDATE_NOTE){
                    notesList.remove(noteClickedPosition);
                    if (isNoteDeleted){
                        noteAdapters.notifyItemRemoved(noteClickedPosition);
                    }
                    else {
                        notesList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        noteAdapters.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        }
        else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if (data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }
}