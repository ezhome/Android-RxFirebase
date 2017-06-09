package com.ezhome.rxfirebasedemo;

import android.support.multidex.MultiDexApplication;
import com.google.firebase.database.FirebaseDatabase;

/**
 *  Base application
 */
public class BaseApplication extends MultiDexApplication {

  @Override public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  }
}
