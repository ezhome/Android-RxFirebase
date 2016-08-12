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
