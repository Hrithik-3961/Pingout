package com.example.pingout;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

interface AuthenticationCallback {
    void onAuthError();

    void onAuthSucceeded();

    void onAuthFailed();
}

public class BiometricAuthentication {

    private Context context;
    private FragmentActivity activity;
    private AuthenticationCallback mListener;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    BiometricAuthentication(Context context, AuthenticationCallback listener) {
        this.context = context;
        this.mListener = listener;
        if (context instanceof HomeActivity)
            this.activity = (HomeActivity) context;
        else
            this.activity = (ChatActivity) context;
    }

    void setup() {
        executor = ContextCompat.getMainExecutor(context);
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using fingerprint or face")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt = new BiometricPrompt(activity,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        mListener.onAuthError();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        mListener.onAuthSucceeded();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        mListener.onAuthFailed();
                    }
                });
    }

    void authenticate() {
        biometricPrompt.authenticate(promptInfo);
    }

}
