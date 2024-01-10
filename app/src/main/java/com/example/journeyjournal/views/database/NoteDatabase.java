package com.example.journeyjournal.views.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.journeyjournal.views.dao.NoteDao;
import com.example.journeyjournal.views.entities.Notes;

@Database(entities = Notes.class, exportSchema = false, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    public static NoteDatabase noteDatabase;
    public static synchronized NoteDatabase getNoteDatabase (Context context){
        if (noteDatabase == null){
            noteDatabase = Room.databaseBuilder(
                    context,
                    NoteDatabase.class,
                    "notes_db"
            ).build();
        }
        return noteDatabase;
    }
    public abstract NoteDao noteDao();
}
