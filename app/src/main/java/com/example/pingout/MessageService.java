package com.example.pingout;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MessageService extends FirebaseMessagingService {

    private Repository repository;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();
            String type = data.get("type");
            Log.d("myTag", data.get("message"));
            assert type != null;
            switch (type) {
                case "chat": {
                    String message = data.get("message");
                    String senderId = data.get("senderId");
                    long timestamp = Long.parseLong(Objects.requireNonNull(data.get("timestamp")));
                    String roomId = data.get("roomId");
                    Messages msg = new Messages(message, senderId, timestamp);
                    repository = new Repository(getApplication(), roomId);

                    new Thread(() -> {
                        UserMessages userMessages = repository.getSingleMessages(roomId);
                        userMessages.getMsg().add(msg);
                        repository.insertMessages(userMessages);
                    }).start();
                    break;
                }
                case "command": {
                    String command = data.get("message");
                    String roomId = data.get("roomId");
                    assert command != null;
                    if (command.equals("/clear"))
                        repository.deleteMessages(roomId);
                    break;
                }
                case "key":

                    break;
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        new Thread(() -> {
            String uid = FirebaseAuth.getInstance().getUid();
            Users user = repository.getUser(uid);
            user.setToken(token);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert uid != null;
            DocumentReference document = db.collection("users").document(uid);
            document.update("token", token);
            repository.insertUser(user);
        }).start();
    }
}
