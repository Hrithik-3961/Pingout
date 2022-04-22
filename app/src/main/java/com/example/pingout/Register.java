package com.example.pingout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private TextView signin;
    private EditText register_name, register_email, register_password, register_confirmPassword;
    private Button register_btn;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseFirestore database;

    private String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        register_name = findViewById(R.id.name);
        register_email = findViewById(R.id.emailId);
        register_password = findViewById(R.id.password);
        register_confirmPassword = findViewById(R.id.confirmPassword);
        register_btn = findViewById(R.id.register_btn);
        signin = findViewById(R.id.signin);

        register_btn.setOnClickListener(view -> {

            final String name = register_name.getText().toString();
            final String email = register_email.getText().toString();
            String password = register_password.getText().toString();
            String confirmPassword = register_confirmPassword.getText().toString();

            if (TextUtils.isEmpty(name)) {
                register_name.setError("Please enter valid name");
                return;
            } else
                register_name.setError(null);

            if (!email.matches(regex) || TextUtils.isEmpty(email)) {
                register_email.setError("Invalid email id");
                return;
            } else
                register_email.setError(null);

            if (!password.equals(confirmPassword)) {
                register_password.setError("Password does not match");
                register_confirmPassword.setError("Password does not match");
                return;
            } else {
                register_password.setError(null);
                register_confirmPassword.setError(null);
            }

            if (password.length() < 6) {
                register_password.setError("Password should be at least 6 characters");
                return;
            } else
                register_password.setError(null);

            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentReference reference = database.collection("user").document(auth.getCurrentUser().getUid());
                    Log.d("myTag", reference.toString());
                    final Users user = new Users(auth.getCurrentUser().getUid(), name, email);
                    reference.set(user).addOnCompleteListener(task1 -> {
                        progressDialog.dismiss();
                        if (task1.isSuccessful())
                            startActivity(new Intent(Register.this, HomeActivity.class));
                        else
                            Toast.makeText(Register.this, "Unable to Register user: " + task1.getException(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "Unable to Register user: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        signin.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, SignIn.class));
            finishAffinity();
        });
    }
}