package com.example.pingout;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Users>> allUsers;
    private LiveData<UserMessages> messages;

    public ViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        allUsers = repository.getAllUsers();
    }

    public ViewModel(Application application, String roomId)  {
        super(application);
        repository = new Repository(application, roomId);
        messages = repository.getMessages();
    }

    public void insertUser(Users user){
        repository.insertUser(user);
    }

    public void insertMessage(UserMessages msg) {
        repository.insertMessage(msg);
    }

    public LiveData<List<Users>> getAllUsers() {
        return allUsers;
    }

    public LiveData<UserMessages> getMessages() {
        return messages;
    }
}
