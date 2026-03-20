package com.tdt4240Grp04.clashofclaws.android;

import com.google.firebase.auth.FirebaseAuth;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class AndroidFirebaseSDK implements FirebaseSDK {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(task.getException().getMessage());
                }
            });
    }

    @Override
    public void register(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(task.getException().getMessage());
                }
            });
    }
}
