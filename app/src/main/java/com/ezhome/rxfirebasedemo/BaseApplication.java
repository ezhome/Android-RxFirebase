package com.ezhome.rxfirebasedemo;

import android.app.Application;
import com.firebase.client.Firebase;

/**
 *  Base application
 */
public class BaseApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Firebase.setAndroidContext(this);
  }
}
