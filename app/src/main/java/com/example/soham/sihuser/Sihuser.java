package com.example.soham.sihuser;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Soham on 24-03-2017.
 */

public class Sihuser extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
//        Firebase.setAndroidContext(this);

        if(!com.google.firebase.FirebaseApp.getApps(this).isEmpty())
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

    }
}
