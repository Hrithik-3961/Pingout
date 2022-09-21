package com.example.pingout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements TimerOverCallback {

    private FirebaseFirestore database;
    private CollectionReference reference;

    private LinearLayout toolbar;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ImageView logout;
    private TextView appName;

    private ArrayList<Users> arrayList;
    private ViewModel viewModel;

    private BiometricAuthentication auth;
    private ThreadHandler threadHandler;
    public static TimerOverCallback mListener;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, SignIn.class));
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!threadHandler.isRunning()) {
            mListener.onTimerOver();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        threadHandler.stopThread();
        threadHandler.startThread();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_home);

        mListener = this;
        threadHandler = new ThreadHandler(this);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        appName = findViewById(R.id.app_name);
        logout = findViewById(R.id.logout);

        AuthenticationCallback callback = new AuthenticationCallback() {
            @Override
            public void onAuthError() {
                finishAffinity();
                System.exit(0);
            }

            @Override
            public void onAuthSucceeded() {
                recyclerView.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                threadHandler.startThread();
            }

            @Override
            public void onAuthFailed() {
                recyclerView.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.INVISIBLE);
            }
        };
        auth = new BiometricAuthentication(this, callback);
        auth.setup();
        auth.authenticate();

        Shader shader = new LinearGradient(100, 0, 175, appName.getLineHeight(), new int[]{
                ContextCompat.getColor(this, R.color.startColor),
                ContextCompat.getColor(this, R.color.centerColor),
                ContextCompat.getColor(this, R.color.endColor),
        },
                new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
        appName.getPaint().setShader(shader);

        arrayList = new ArrayList<>();

        database = FirebaseFirestore.getInstance();
        reference = database.collection("user");
        reference.addSnapshotListener((value, error) -> {
            for (DocumentSnapshot snapshot : value.getDocuments()) {
                Users user = snapshot.toObject(Users.class);
                viewModel.insertUser(user);
            }
        });

        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), "")).get(ViewModel.class);
        viewModel.getAllUsers().observe(this, users -> {
            arrayList.clear();
            for (Users user : users) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getUid()))
                    arrayList.add(user);
            }
            userAdapter.notifyDataSetChanged();
        });

        logout.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(HomeActivity.this, R.style.Dialog);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setCancelable(false);
            dialog.show();

            Button yesBtn = dialog.findViewById(R.id.yesBtn);
            Button noBtn = dialog.findViewById(R.id.noBtn);

            yesBtn.setOnClickListener(view1 -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, SignIn.class));
                finishAffinity();
            });

            noBtn.setOnClickListener(view12 -> dialog.dismiss());
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        userAdapter = new UserAdapter(this, arrayList);
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    public void onTimerOver() {
        runOnUiThread(() -> {
            Toast.makeText(HomeActivity.this, "Session Expired. Please re-authenticate!", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
            auth.authenticate();
        });
    }
}