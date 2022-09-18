package com.example.pingout;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UserMessages {

    @TypeConverters(Converter.class)
    ArrayList<Messages> msg;

    @PrimaryKey
    @NonNull
    String roomId = "";

    @Ignore
    public UserMessages() {
        //required for firebase
    }

    public UserMessages(ArrayList<Messages> msg, @NonNull String roomId) {
        this.msg = msg;
        this.roomId = roomId;
    }

    public List<Messages> getMsg() {
        return msg;
    }

    @NonNull
    public String getRoomId() {
        return roomId;
    }
}
