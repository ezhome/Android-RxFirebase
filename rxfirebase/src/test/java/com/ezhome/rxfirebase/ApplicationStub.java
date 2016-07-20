package com.ezhome.rxfirebase;

import android.app.Application;
import com.firebase.client.Firebase;

public class ApplicationStub extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Firebase.setAndroidContext(this);
  }
}
