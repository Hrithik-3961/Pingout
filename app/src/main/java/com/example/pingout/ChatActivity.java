package com.example.pingout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vanniktech.emoji.EmojiPopup;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ChatActivity extends AppCompatActivity implements TimerOverCallback {

    private LinearLayout toolbar, sendLayout;
    private TextView user_name;
    private EditText editMessage;
    private ImageView sendBtn, back, emojiBtn;
    private RecyclerView recyclerView;

    private String name, receiverUid, senderUid;
    private String senderRoom, receiverRoom;
    private ArrayList<Messages> arrayList;
    private MessagesAdapter messagesAdapter;

    private ViewModel viewModel;
    private FirebaseFirestore database;

    private final byte[] encryptionKey = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher;
    private SecretKeySpec secretKeySpec;

    private BiometricAuthentication auth;
    private ThreadHandler threadHandler;
    public static TimerOverCallback mListener;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        threadHandler.stopThread();
        threadHandler.startThread();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!threadHandler.isRunning()) {
            mListener.onTimerOver();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_chat);

        mListener = this;
        threadHandler = new ThreadHandler(this);

        toolbar = findViewById(R.id.toolbar);
        sendLayout = findViewById(R.id.sendLayout);
        user_name = findViewById(R.id.name);
        editMessage = findViewById(R.id.message);
        sendBtn = findViewById(R.id.sendBtn);
        back = findViewById(R.id.back);
        emojiBtn = findViewById(R.id.emoji_btn);
        recyclerView = findViewById(R.id.recyclerView);

        AuthenticationCallback callback = new AuthenticationCallback() {
            @Override
            public void onAuthError() {
                finishAffinity();
                System.exit(0);
            }

            @Override
            public void onAuthSucceeded() {
                toolbar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                sendLayout.setVisibility(View.VISIBLE);
                threadHandler.startThread();
            }

            @Override
            public void onAuthFailed() {
                toolbar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                sendLayout.setVisibility(View.INVISIBLE);
            }
        };
        auth = new BiometricAuthentication(this, callback);
        auth.setup();

        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

        database = FirebaseFirestore.getInstance();

        name = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();
        arrayList = new ArrayList<>();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), senderRoom)).get(ViewModel.class);

        back.setOnClickListener(view -> onBackPressed());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        messagesAdapter = new MessagesAdapter(this, arrayList, getWindowManager());
        recyclerView.setAdapter(messagesAdapter);

        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom && messagesAdapter.getItemCount() != 0)
                recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
        });

        user_name.setText(name);

        viewModel.getMessages().observe(this, userMessages -> {
            if (userMessages != null) {
                arrayList.clear();
                arrayList.addAll(userMessages.getMsg());
                messagesAdapter.notifyDataSetChanged();
                if (messagesAdapter.getItemCount() != 0)
                    recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        });

        CollectionReference chatReference = database.collection("chats").document(senderRoom).collection("messages");

        chatReference.orderBy("timestamp").addSnapshotListener((value, error) -> {
            arrayList.clear();

            for (DocumentSnapshot snapshot : value.getDocuments()) {
                Messages msg = snapshot.toObject(Messages.class);
                arrayList.add(msg);
            }
            UserMessages senderMsg = new UserMessages(arrayList, senderRoom);
            UserMessages receiverMsg = new UserMessages(arrayList, receiverRoom);
            viewModel.insertMessage(senderMsg);
            viewModel.insertMessage(receiverMsg);
            messagesAdapter.notifyItemInserted(arrayList.size());
            if (messagesAdapter.getItemCount() != 0)
                recyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });

        sendBtn.setOnClickListener(view -> {
            String text = editMessage.getText().toString().trim();
            if (text.isEmpty()) {
                editMessage.setText(null);
                return;
            }
            String message = AESEncryptionMethod(text);
            if (!message.isEmpty()) {
                editMessage.setText(null);
                Date date = new Date();
                final Messages msg = new Messages(message, senderUid, date.getTime());
                arrayList.add(msg);
                database.collection("chats").document(senderRoom).collection("messages").document().set(msg)
                        .addOnCompleteListener(task -> database.collection("chats").document(receiverRoom).collection("messages").document().set(msg)
                                .addOnCompleteListener(task1 -> {
                                    messagesAdapter.notifyItemInserted(arrayList.size());
                                    UserMessages senderMsg = new UserMessages(arrayList, senderRoom);
                                    viewModel.insertMessage(senderMsg);
                                }));
            }
        });

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(
                findViewById(R.id.root_view)
        ).build(editMessage);

        emojiBtn.setOnClickListener(view -> popup.toggle());

        editMessage.setOnClickListener(view -> popup.dismiss());

    }

    private String AESEncryptionMethod(String string) {

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return new String(encryptedByte, StandardCharsets.ISO_8859_1);
    }

    @Override
    public void onTimerOver() {
        runOnUiThread(() -> {

            Toast.makeText(ChatActivity.this, "Session Expired. Please re-authenticate!", Toast.LENGTH_SHORT).show();
            toolbar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            sendLayout.setVisibility(View.INVISIBLE);
            auth.authenticate();
        });
    }
}