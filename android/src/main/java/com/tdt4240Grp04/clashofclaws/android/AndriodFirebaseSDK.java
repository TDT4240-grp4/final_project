package com.tdt4240Grp04.clashofclaws.android;

import com.google.firebase.auth.FirebaseAuth;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class AndriodFirebaseSDK implements FirebaseSDK {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) System.out.println("Login Successful");
                else System.out.println("Login Failed: " + task.getException().getMessage());
            });
    }

    @Override
    public void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) System.out.println("Register Successful");
                else System.out.println("Register Failed");
            });
    }
}
