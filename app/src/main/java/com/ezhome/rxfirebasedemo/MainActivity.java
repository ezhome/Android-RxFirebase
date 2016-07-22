package com.ezhome.rxfirebasedemo;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The {@link AppCompatActivity} for the main screen
 */
public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_layout);

    final FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.fragmentContainer, PostsFragment.newInstance());
    fragmentTransaction.commit();
  }
}
