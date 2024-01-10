package com.example.journeyjournal.views.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.journeyjournal.views.dao.UserCredentialDao;
import com.example.journeyjournal.views.entities.UserCredential;

@Database(entities = {UserCredential.class}, version = 1, exportSchema = false)
public abstract class UserCredentialDatabase extends RoomDatabase {
    public abstract UserCredentialDao getUserCredentailDao();
}
