package com.example.journeyjournal.views;

import static com.makeramen.roundedimageview.RoundedDrawable.drawableToBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journeyjournal.R;
import com.example.journeyjournal.views.database.NoteDatabase;
import com.example.journeyjournal.views.entities.Notes;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateJournalEntryActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputSubtitle, inputNote;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private String colorSelection;
    private ImageView imageNote;
    private String selectedImagePath;
    private Notes alreadyAvailableNote;
    private AlertDialog dialogDeleteNote;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal_entry);
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputSubtitle = findViewById(R.id.inputSubtitle);
        inputNote = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotes();
            }
        });
        colorSelection = "#333333";
        selectedImagePath = "";

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (Notes) getIntent().getSerializableExtra("note");
            setViewOrUpdate();
        }
        findViewById(R.id.imageRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(view.GONE);
                findViewById(R.id.imageRemove).setVisibility(view.GONE);
                selectedImagePath = "";
            }
        });
        drawableSelection();
        setSubtitleIndicatorColor();
    }
    private void setViewOrUpdate(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputSubtitle.setText(alreadyAvailableNote.getSubTitle());
        inputNote.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }
    }

    private void saveNotes() {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputSubtitle.getText().toString().trim().isEmpty() && inputNote.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter your notes", Toast.LENGTH_SHORT).show();
            return;
        }

        final Notes notes = new Notes();
        notes.setTitle(inputNoteTitle.getText().toString());
        notes.setSubTitle(inputSubtitle.getText().toString());
        notes.setNoteText(inputNote.getText().toString());
        notes.setDateTime(textDateTime.getText().toString());
        notes.setColor(colorSelection);
        notes.setImagePath(selectedImagePath);

        if (alreadyAvailableNote != null){
            notes.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class saveNotesTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().inserNote(notes);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
        new saveNotesTask().execute();
    }

    private void drawableSelection() {
        final LinearLayout layoutGroup = findViewById(R.id.layoutGroup);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutGroup);
        layoutGroup.findViewById(R.id.textGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        final ImageView imageColor = findViewById(R.id.imageColor);
        final ImageView imageColor2 = findViewById(R.id.imageColor2);
        final ImageView imageColor3 = findViewById(R.id.imageColor3);

        layoutGroup.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorSelection = "#333333";
                imageColor.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutGroup.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorSelection = "#8DFFEB3B";
                imageColor.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor3.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutGroup.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorSelection = "#FFFFFF";
                imageColor.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_baseline_done_24);
                setSubtitleIndicatorColor();
            }
        });
        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()){
        switch (alreadyAvailableNote.getColor()){
            case "#8DFFEB3B":
                layoutGroup.findViewById(R.id.viewColor2).performClick();
                break;
            case "#FFFFFF":
                layoutGroup.findViewById(R.id.viewColor3).performClick();
                break;
        }
        }
        // permission for camera/storage
        layoutGroup.findViewById(R.id.layoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //request for permission
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateJournalEntryActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });
        if (alreadyAvailableNote != null){
            layoutGroup.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutGroup.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteDialogue();
                }
            });
        }

        //sharing journal entries to social media platform
        if (alreadyAvailableNote != null){
            layoutGroup.findViewById(R.id.layoutShare).setVisibility(View.VISIBLE);
            layoutGroup.findViewById(R.id.layoutShare).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    BitmapDrawable drawable = (BitmapDrawable)imageNote.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    File f = new File(getExternalCacheDir()+"/"+getResources().getString(R.string.app_name)+ ".png");
                    Intent shareInt;
                    try {
                        String textTitle = inputNoteTitle.getText().toString();
                        String textDisc = inputNote.getText().toString();
                        FileOutputStream outputStream = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        shareInt = new Intent(Intent.ACTION_SEND);
                        shareInt.setType("text/plain");
                        shareInt.putExtra(Intent.EXTRA_SUBJECT, "Journey Journal");
                        shareInt.putExtra(Intent.EXTRA_TEXT, "Title: " + textTitle
                                +  ", Discription: "+ textDisc);
                        shareInt.setType("image/*");
                        shareInt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                        shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    }
                    catch (Exception e){
                            throw new RuntimeException(e);
                    }
                    startActivity(Intent.createChooser(shareInt, "Share via"));
                }
            });
        }

    }
    private void showDeleteDialogue(){
        if (dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateJournalEntryActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_notes,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
        dialogDeleteNote.setCanceledOnTouchOutside(false);
    }

    private void selectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateJournalEntryActivity.this);
        builder.setTitle("Add image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CAMERA);
                }
                else if (items[i].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }
                else if (items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } else if (requestCode == 3) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 3);
        }
        else {

            Intent intent = new Intent(Intent.ACTION_PICK);
            startActivityForResult(intent, 2);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //capture image and load to recycler view
        if(requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageNote.setImageBitmap(photo);
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            selectedImagePath = getPathFromUri(tempUri);
        }

       else if (requestCode == SELECT_FILE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }
                    catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

    }
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null){
            filePath = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }


    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(colorSelection));
    }
}