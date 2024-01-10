package com.example.journeyjournal.views.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.journeyjournal.views.entities.UserCredential;

@Dao
public interface UserCredentialDao {

    @Query("SELECT * FROM UserCredential WHERE email = :email and password= :password")
    UserCredential getUserCredentail(String email, String password);

    @Insert
    void insert (UserCredential userCredential);
}
