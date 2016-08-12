package com.ezhome.rxfirebase2.auth;

import com.ezhome.rxfirebase2.database.RxFirebaseDatabase;
import com.ezhome.rxfirebase2.exception.FirebaseSignInException;
import com.ezhome.rxfirebase2.exception.FirebaseSignOutException;
import rx.Subscriber;

/**
 * An error factor for {@link RxFirebaseAuth}
 * and {@link RxFirebaseDatabase}
 */
public class FirebaseAuthErrorFactory {

  private FirebaseAuthErrorFactory() {
    //empty constructor prevent initialisation
  }

  /**
   * This method add to subscriber the proper error according to the
   *
   * @param subscriber {@link Subscriber}
   * @param exception {@link Exception}
   * @param <T> generic subscriber
   */
  static <T> void buildError(Subscriber<T> subscriber, Exception exception) {
    subscriber.onError(exception);
  }

  /**
   * This method adds to subscriber {@link FirebaseSignOutException}
   *
   * @param subscriber {@link Subscriber}
   * @param <T> generic subscriber
   */
  static <T> void createSignOutError(Subscriber<T> subscriber) {
    subscriber.onError(new FirebaseSignOutException("User didn't sign out successfully"));
  }

  /**
   * This method adds to subscriber {@link FirebaseSignOutException}
   *
   * @param subscriber {@link Subscriber}
   * @param <T> generic subscriber
   */
  static <T> void createSignInError(Subscriber<T> subscriber) {
    subscriber.onError(new FirebaseSignInException("User signed out"));
  }
}
