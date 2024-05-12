package com.smd.surmaiya;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SurmaiyaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
