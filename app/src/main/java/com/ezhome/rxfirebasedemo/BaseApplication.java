package com.ezhome.rxfirebasedemo;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

/**
 *  Base application
 */
public class BaseApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  }
}
