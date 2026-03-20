package com.tdt4240Grp04.clashofclaws.lwjgl3;

import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class DesktopFirebaseSDK implements FirebaseSDK {
    @Override
    public void login(String email, String password, AuthCallback callback) {
        System.out.println("Desktop login: " + email);
        callback.onSuccess();
    }

    @Override
    public void register(String email, String password, AuthCallback callback) {
        System.out.println("Desktop register: " + email);
        callback.onSuccess();
    }
}
