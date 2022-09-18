package com.example.pingout;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Users {

    @PrimaryKey
    @NonNull
    private String uid = "";

    private String name;
    private String emailId;

    @Ignore
    public Users() {
        //required for firebase
    }

    public Users(@NonNull String uid, String name, String emailId) {
        this.uid = uid;
        this.name = name;
        this.emailId = emailId;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }
}
