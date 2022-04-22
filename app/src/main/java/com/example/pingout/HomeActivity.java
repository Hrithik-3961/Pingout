package com.example.pingout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private CollectionReference reference;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ImageView logout;

    private ArrayList<Users> arrayList;
    private ViewModel viewModel;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, SignIn.class));
            finishAffinity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView appName = findViewById(R.id.app_name);
        Shader shader = new LinearGradient(100, 0, 175, appName.getLineHeight(), new int[]{
                ContextCompat.getColor(this, R.color.startColor),
                ContextCompat.getColor(this, R.color.centerColor),
                ContextCompat.getColor(this, R.color.endColor),
        },
                new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
        appName.getPaint().setShader(shader);

        arrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
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
            for(Users user : users) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getUid()))
                    arrayList.add(user);
            }
            userAdapter.notifyDataSetChanged();
        });

        logout = findViewById(R.id.logout);
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        userAdapter = new UserAdapter(this, arrayList);
        recyclerView.setAdapter(userAdapter);
    }
}