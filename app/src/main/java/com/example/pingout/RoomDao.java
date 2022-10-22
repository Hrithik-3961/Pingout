package com.example.pingout;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(Users user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(UserMessages msg);

    @Query("DELETE FROM UserMessages WHERE roomId = :roomId")
    void deleteMessages(String roomId);

    @Query("SELECT * FROM Users")
    LiveData<List<Users>> getAllUsers();

    @Query("SELECT * FROM UserMessages WHERE roomId = :roomId")
    LiveData<UserMessages> getMessages(String roomId);

}
