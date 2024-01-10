package com.example.journeyjournal.views.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.journeyjournal.views.entities.Notes;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("select * from Notes ORDER BY id desc")
    List<Notes> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserNote(Notes notes);

    @Delete
    void deleteNote(Notes notes);
}
