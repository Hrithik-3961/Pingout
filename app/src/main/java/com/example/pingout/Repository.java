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
        executorService.execute(() -> roomDao.insertUser(user));
    }

    public void insertMessages(final UserMessages msg){
        executorService.execute(() -> roomDao.insertMessages(msg));
    }

    public void deleteMessages(final String roomId) {
        executorService.execute(() -> roomDao.deleteMessages(roomId));
    }

    public Users getUser(String uid) {
        return roomDao.getUser(uid);
    }

    public LiveData<List<Users>> getAllUsers() {
        return allUsers;
    }

    public LiveData<UserMessages> getMessages() {
        return messages;
    }

    public UserMessages getSingleMessages(String roomId) {
        return roomDao.getSingleMessages(roomId);
    }
}
