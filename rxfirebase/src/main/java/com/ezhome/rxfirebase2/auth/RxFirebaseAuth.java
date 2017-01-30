/**
 * Copyright 2016 Ezhome Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ezhome.rxfirebase2.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * The class is used as Decorator to
 * Firebase Authenticaiton functionality with RxJava
 */
public class RxFirebaseAuth {

  private final FirebaseAuth firebaseAuth;

  public static volatile RxFirebaseAuth instance;

  /**
   * Singleton pattern
   *
   * @param firebaseAuth {@link RxFirebaseAuth}
   * @return {@link RxFirebaseAuth}
   */
  public static RxFirebaseAuth getInstance(FirebaseAuth firebaseAuth) {
    if (instance == null) {
      synchronized (RxFirebaseAuth.class) {
        if (instance == null) {
          instance = new RxFirebaseAuth(firebaseAuth);
        }
      }
    }
    return instance;
  }

  protected RxFirebaseAuth(FirebaseAuth firebaseAuth) {
    this.firebaseAuth = firebaseAuth;
  }

  /**
   * Attempts to authenticate to Firebase with {@link com.google.firebase.auth.AuthCredential}
   *
   * @param authCredential {@link com.google.firebase.auth.AuthCredential} this is the credential
   * we need to pass for login
   * @return an {@link rx.Observable} of {@link com.google.firebase.auth.FirebaseUser} to use
   */
  public Observable<FirebaseUser> observeSignIn(final AuthCredential authCredential) {
    return Observable.create(new Observable.OnSubscribe<FirebaseUser>() {
      @Override public void call(final Subscriber<? super FirebaseUser> subscriber) {
        final Task<AuthResult> authResultTask = firebaseAuth.signInWithCredential(authCredential);
        attachListenSignIn(subscriber, authResultTask);
      }
    });
  }

  /**
   * Attempts to authenticate to Firebase with {@link com.google.firebase.auth.AuthCredential}
   *
   * @param token {@link String} a custom token for login
   * @return an {@link rx.Observable} of {@link com.google.firebase.auth.FirebaseUser} to use
   */
  public Observable<FirebaseUser> observeSignIn(final String token) {
    return Observable.create(new Observable.OnSubscribe<FirebaseUser>() {
      @Override public void call(final Subscriber<? super FirebaseUser> subscriber) {
        final Task<AuthResult> authResultTask = firebaseAuth.signInWithCustomToken(token);
        attachListenSignIn(subscriber, authResultTask);
      }
    });
  }

  /**
   * Attempts to sign-out from Firebase
   *
   * @return an {@link rx.Observable} of {@link Boolean}
   */
  public Observable<Boolean> observeSignOut() {
    return Observable.create(new Observable.OnSubscribe<Boolean>() {
      @Override public void call(final Subscriber<? super Boolean> subscriber) {
        firebaseAuth.signOut();
        final AuthStateListener authStateListener = new AuthStateListener() {
          @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null) {
              subscriber.onNext(true);
              subscriber.onCompleted();
            } else {
              FirebaseAuthErrorFactory.createSignOutError(subscriber);
            }
          }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        subscriber.add(Subscriptions.create(new Action0() {
          @Override public void call() {
            firebaseAuth.removeAuthStateListener(authStateListener);
          }
        }));
      }
    });
  }

  /**
   * Observes the authentication state for {@link com.google.firebase.auth.FirebaseAuth}
   *
   * @return {@link rx.Observable} of {@link com.google.firebase.auth.FirebaseUser}
   */
  public Observable<FirebaseUser> observeAuthState() {
    return Observable.create(new Observable.OnSubscribe<FirebaseUser>() {
      @Override public void call(final Subscriber<? super FirebaseUser> subscriber) {
        final AuthStateListener authStateListener = new AuthStateListener() {
          @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
              FirebaseAuthErrorFactory.createSignOutError(subscriber);
            } else {
              subscriber.onNext(firebaseUser);
            }
          }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        subscriber.add(Subscriptions.create(new Action0() {
          @Override public void call() {
            firebaseAuth.removeAuthStateListener(authStateListener);
          }
        }));
      }
    });
  }

  /**
   * Attaches the required listeners to observe the result
   *
   * @param subscriber {@link rx.Subscriber} of a {@link com.google.firebase.auth.FirebaseUser}
   * @param task {@link com.google.android.gms.tasks.Task}
   */
  private void attachListenSignIn(final Subscriber<? super FirebaseUser> subscriber,
      Task<AuthResult> task) {
    task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
      @Override public void onComplete(@NonNull Task<AuthResult> task) {
        if (!task.isSuccessful()) {
          FirebaseAuthErrorFactory.createSignInError(subscriber);
        } else {
          subscriber.onNext(task.getResult().getUser());
        }
        subscriber.onCompleted();
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception e) {
        FirebaseAuthErrorFactory.buildError(subscriber, e);
      }
    });
  }
}
