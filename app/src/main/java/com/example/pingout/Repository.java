package com.example.pingout;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private RoomDao roomDao;
    private LiveData<List<Users>> allUsers;
    private LiveData<UserMessages> messages;
    private RoomDatabase roomDatabase;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Repository(Application application) {
        roomDatabase = RoomDatabase.getInstance(application);
        roomDao = roomDatabase.roomDao();
        allUsers = roomDao.getAllUsers();
    }

    public Repository(Application application, String roomId) {
        roomDatabase = RoomDatabase.getInstance(application);
        roomDao = roomDatabase.roomDao();
        messages = roomDao.getMessages(roomId);
    }

    public void insertUser(final Users user) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                roomDao.insertUser(user);
            }
        });
    }

    public void insertMessage(final UserMessages msg){
        executorService.execute(() -> roomDao.insertMessage(msg));
    }

    public LiveData<List<Users>> getAllUsers() {
        return allUsers;
    }

    public LiveData<UserMessages> getMessages() {
        return messages;
    }
}
