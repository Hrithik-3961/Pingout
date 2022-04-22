package com.example.pingout;

import androidx.annotation.NonNull;
import androidx.room.Entity;
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
    String roomId;

    public UserMessages() {
        //required for firebase
    }

    public UserMessages(ArrayList<Messages> msg, String roomId) {
        this.msg = msg;
        this.roomId = roomId;
    }

    public List<Messages> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<Messages> msg) {
        this.msg = msg;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
