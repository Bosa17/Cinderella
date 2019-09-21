package com.getcinderella.app;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class Cinderella extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
