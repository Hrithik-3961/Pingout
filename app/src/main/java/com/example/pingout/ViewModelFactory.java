package com.example.pingout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private String roomId;

    public ViewModelFactory(Application application, String roomId) {
        this.application = application;
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(roomId.equals(""))
            return (T) new com.example.pingout.ViewModel(application);
        return (T) new com.example.pingout.ViewModel(application, roomId);
    }
}
