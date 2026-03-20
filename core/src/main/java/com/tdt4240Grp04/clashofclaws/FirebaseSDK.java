package com.tdt4240Grp04.clashofclaws;

public interface FirebaseSDK {
    interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }

    void login(String email, String password, AuthCallback callback);
    void register(String email, String password, AuthCallback callback);
}
