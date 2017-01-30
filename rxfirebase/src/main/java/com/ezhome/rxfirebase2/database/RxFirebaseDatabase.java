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
package com.ezhome.rxfirebase2.database;

import com.ezhome.rxfirebase2.FirebaseChildEvent;
import com.ezhome.rxfirebase2.FirebaseChildEvent.EventType;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * The class is used as Decorator to
 * Firebase Database functionality with RxJava
 */
public class RxFirebaseDatabase {

  public static volatile RxFirebaseDatabase instance;

  /**
   * Singleton pattern
   *
   * @return {@link RxFirebaseDatabase}
   */
  public static RxFirebaseDatabase getInstance() {
    if (instance == null) {
      synchronized (RxFirebaseDatabase.class) {
        if (instance == null) {
          instance = new RxFirebaseDatabase();
        }
      }
    }
    return instance;
  }

  protected RxFirebaseDatabase() {
    //empty constructor, prevent initialisation
  }

  /**
   * This methods observes data saving with push in order to generate the key
   * automatically according to Firebase hashing key rules.
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @param object {@link Object} whatever object we want to save
   * @return an {@link rx.Observable} of the generated key after
   * the object persistence
   */
  public Observable<String> observeSetValuePush(final DatabaseReference firebaseRef,
      final Object object) {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(final Subscriber<? super String> subscriber) {
        final DatabaseReference ref = firebaseRef.push();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(ref.getKey());
            subscriber.onCompleted();
          }

          @Override public void onCancelled(DatabaseError error) {
            FirebaseDatabaseErrorFactory.buildError(subscriber, error);
          }
        });
        ref.setValue(object);
      }
    });
  }

  /**
   * This methods observes a firebase query and returns back
   * an Observable of the {@link DataSnapshot}
   * when the firebase client uses a {@link ValueEventListener}
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of datasnapshot to use
   */
  public Observable<DataSnapshot> observeValueEvent(final Query firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
      @Override public void call(final Subscriber<? super DataSnapshot> subscriber) {
        final ValueEventListener listener =
            firebaseRef.addValueEventListener(new ValueEventListener() {
              @Override public void onDataChange(DataSnapshot dataSnapshot) {
                subscriber.onNext(dataSnapshot);
              }

              @Override public void onCancelled(DatabaseError error) {
                FirebaseDatabaseErrorFactory.buildError(subscriber, error);
              }
            });

        // When the subscription is cancelled, remove the listener
        subscriber.add(Subscriptions.create(new Action0() {
          @Override public void call() {
            firebaseRef.removeEventListener(listener);
          }
        }));
      }
    });
  }

  /**
   * This methods observes a firebase query and returns back ONCE
   * an Observable of the {@link DataSnapshot}
   * when the firebase client uses a {@link ValueEventListener}
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of datasnapshot to use
   */
  public Observable<DataSnapshot> observeSingleValue(final Query firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
      @Override public void call(final Subscriber<? super DataSnapshot> subscriber) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(dataSnapshot);
            subscriber.onCompleted();
          }

          @Override public void onCancelled(DatabaseError error) {
            FirebaseDatabaseErrorFactory.buildError(subscriber, error);
          }
        };

        firebaseRef.addListenerForSingleValueEvent(listener);

        // When the subscription is cancelled, remove the listener
        subscriber.add(Subscriptions.create(new Action0() {
          @Override public void call() {
            firebaseRef.removeEventListener(listener);
          }
        }));
      }
    });
  }

  /**
   * This methods observes a firebase query and returns back
   * an Observable of the {@link DataSnapshot}
   * when the firebase client uses a {@link ChildEventListener}
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildEvent(final Query firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<FirebaseChildEvent>() {
      @Override public void call(final Subscriber<? super FirebaseChildEvent> subscriber) {
        final ChildEventListener childEventListener =
            firebaseRef.addChildEventListener(new ChildEventListener() {

              @Override
              public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                subscriber.onNext(
                    new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.ADDED));
              }

              @Override
              public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                subscriber.onNext(
                    new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.CHANGED));
              }

              @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                subscriber.onNext(new FirebaseChildEvent(dataSnapshot, EventType.REMOVED));
              }

              @Override
              public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                subscriber.onNext(
                    new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.MOVED));
              }

              @Override public void onCancelled(DatabaseError error) {
                FirebaseDatabaseErrorFactory.buildError(subscriber, error);
              }
            });
        // this is used to remove the listener when the subscriber is
        // cancelled (unsubscribe)
        subscriber.add(Subscriptions.create(new Action0() {
          @Override public void call() {
            firebaseRef.removeEventListener(childEventListener);
          }
        }));
      }
    });
  }

  /**
   * Creates an observable only for the child changed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildAdded(final Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.ADDED));
  }

  /**
   * Creates an observable only for the child changed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildChanged(final Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.CHANGED));
  }

  /**
   * Creates an observable only for the child removed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildRemoved(final Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.REMOVED));
  }

  /**
   * Creates an observable only for the child removed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildMoved(final Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.MOVED));
  }

  /**
   * Functions which filters a stream of {@link Observable} according to firebase
   * child event type
   *
   * @param type {@link FirebaseChildEvent}
   * @return {@link rx.functions.Func1} a function which returns a boolean if the type are equals
   */
  private Func1<FirebaseChildEvent, Boolean> filterChildEvent(final EventType type) {
    return new Func1<FirebaseChildEvent, Boolean>() {
      @Override public Boolean call(FirebaseChildEvent firebaseChildEvent) {
        return firebaseChildEvent.getEventType() == type;
      }
    };
  }
}
