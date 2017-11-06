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
import java.util.Map;
import rx.Emitter;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.functions.Func1;

/**
 * The class is used as Decorator to
 * Firebase Database functionality with RxJava
 */
public class RxFirebaseDatabase {

  public static volatile RxFirebaseDatabase instance;

  /**
   * Observe Scheduler
   */
  private Scheduler observeOnScheduler;

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
   * This method will set specific Scheduler on what values will be Observed on
   *
   * @param observeOnScheduler {@link Scheduler} for observed on
   * @return {@link RxFirebaseDatabase}
   */
  public RxFirebaseDatabase observeOn(Scheduler observeOnScheduler) {
    this.observeOnScheduler = observeOnScheduler;
    return this;
  }

  /**
   * This methods observes data saving with push in order to generate the key
   * automatically according to Firebase hashing key rules.
   *
   * @param reference {@link Query} this is reference of a Firebase Query
   * @param object {@link Object} whatever object we want to save
   * @return an {@link rx.Observable} of the generated key after
   * the object persistence
   */
  public Observable<String> observeSetValuePush(final DatabaseReference reference,
      final Object object) {
    return Observable.create(new Action1<Emitter<String>>() {
      @Override public void call(final Emitter<String> emitter) {
        final DatabaseReference ref = reference.push();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            emitter.onNext(ref.getKey());
            emitter.onCompleted();
          }

          @Override public void onCancelled(DatabaseError error) {
            FirebaseDatabaseErrorFactory.buildError(emitter, error);
          }
        });
        ref.setValue(object);
      }
    }, Emitter.BackpressureMode.LATEST).compose(this.<String>applyScheduler());
  }

  /**
   * This methods observes data saving under the provided {@link DatabaseReference}
   *
   * @param reference {@link DatabaseReference} this is reference of a Firebase Query
   * @param object {@link Object} whatever object we want to save
   * @return an {@link rx.Observable} of the generated key after
   * the object persistence
   */
  public Observable<String> observeSetValue(final DatabaseReference reference,
      final Object object) {
    return Observable.create(new Action1<Emitter<String>>() {
      @Override public void call(final Emitter<String> emitter) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            emitter.onNext(reference.getKey());
            emitter.onCompleted();
          }

          @Override public void onCancelled(DatabaseError error) {
            FirebaseDatabaseErrorFactory.buildError(emitter, error);
          }
        };
        reference.addListenerForSingleValueEvent(listener);
        reference.setValue(object);

        // When the subscription is cancelled, remove the listener
        emitter.setCancellation(new Cancellable() {
          @Override public void cancel() throws Exception {
            reference.removeEventListener(listener);
          }
        });
      }
    }, Emitter.BackpressureMode.LATEST);
  }

  /**
   * This methods observes children update for provided {@link DatabaseReference}
   *
   * @param reference {@link DatabaseReference}
   * @param data {@link Map} the children items which should be updated
   * @return {@link rx.Observable} which emits the key of reference {@link String}
   */
  public Observable<String> observeUpdateChildren(final DatabaseReference reference,
      final Map<String, Object> data) {
    return Observable.create(new Action1<Emitter<String>>() {
      @Override public void call(final Emitter<String> emitter) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            emitter.onNext(reference.getKey());
            emitter.onCompleted();
          }

          @Override public void onCancelled(DatabaseError databaseError) {
            FirebaseDatabaseErrorFactory.buildError(emitter, databaseError);
          }
        };
        reference.addListenerForSingleValueEvent(listener);
        reference.updateChildren(data);

        // When the subscription is cancelled, remove the listener
        emitter.setCancellation(new Cancellable() {
          @Override public void cancel() throws Exception {
            reference.removeEventListener(listener);
          }
        });
      }
    }, Emitter.BackpressureMode.LATEST).compose(this.<String>applyScheduler());
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
    return observeValueEvent(firebaseRef, Emitter.BackpressureMode.BUFFER);
  }

  /**
   * This methods observes a firebase query and returns back
   * an Observable of the {@link DataSnapshot}
   * when the firebase client uses a {@link ValueEventListener}
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @param backPressureMode {@link Emitter.BackpressureMode} backpressure mode
   * @return an {@link rx.Observable} of datasnapshot to use
   */
  public Observable<DataSnapshot> observeValueEvent(final Query firebaseRef,
      Emitter.BackpressureMode backPressureMode) {
    return Observable.create(new Action1<Emitter<DataSnapshot>>() {
      @Override public void call(final Emitter<DataSnapshot> emitter) {
        final ValueEventListener listener =
            firebaseRef.addValueEventListener(new ValueEventListener() {
              @Override public void onDataChange(DataSnapshot dataSnapshot) {
                emitter.onNext(dataSnapshot);
              }

              @Override public void onCancelled(DatabaseError error) {
                FirebaseDatabaseErrorFactory.buildError(emitter, error);
              }
            });

        // When the subscription is cancelled, remove the listener
        emitter.setCancellation(new Cancellable() {
          @Override public void cancel() throws Exception {
            firebaseRef.removeEventListener(listener);
          }
        });
      }
    }, backPressureMode).compose(this.<DataSnapshot>applyScheduler());
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
    return observeSingleValue(firebaseRef, Emitter.BackpressureMode.BUFFER);
  }

  /**
   * This methods observes a firebase query and returns back ONCE
   * an Observable of the {@link DataSnapshot}
   * when the firebase client uses a {@link ValueEventListener}
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @param backPressureMode {@link Emitter.BackpressureMode} backpressure mode
   * @return an {@link rx.Observable} of datasnapshot to use
   */
  public Observable<DataSnapshot> observeSingleValue(final Query firebaseRef,
      Emitter.BackpressureMode backPressureMode) {
    return Observable.create(new Action1<Emitter<DataSnapshot>>() {
      @Override public void call(final Emitter<DataSnapshot> emitter) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            emitter.onNext(dataSnapshot);
            emitter.onCompleted();
          }

          @Override public void onCancelled(DatabaseError error) {
            FirebaseDatabaseErrorFactory.buildError(emitter, error);
          }
        };

        firebaseRef.addListenerForSingleValueEvent(listener);

        // When the subscription is cancelled, remove the listener
        emitter.setCancellation(new Cancellable() {
          @Override public void cancel() throws Exception {
            firebaseRef.removeEventListener(listener);

          }
        });
      }
    }, backPressureMode).compose(this.<DataSnapshot>applyScheduler());
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
    return this.observeChildEvent(firebaseRef, Emitter.BackpressureMode.BUFFER);
  }

  /**
   * This methods observes a firebase query and returns back
   * an Observable of the {@link DataSnapshot}
   * when the firebase client uses a {@link ChildEventListener}
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @param backPressureMode {@link Emitter.BackpressureMode} backpressure mode
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildEvent(final Query firebaseRef,
      Emitter.BackpressureMode backPressureMode) {
    return Observable.create(new Action1<Emitter<FirebaseChildEvent>>() {
      @Override public void call(final Emitter<FirebaseChildEvent> emitter) {
        final ChildEventListener childEventListener =
            firebaseRef.addChildEventListener(new ChildEventListener() {

              @Override
              public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                emitter.onNext(
                    new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.ADDED));
              }

              @Override
              public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                emitter.onNext(
                    new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.CHANGED));
              }

              @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                emitter.onNext(
                    new FirebaseChildEvent(dataSnapshot, EventType.REMOVED));
              }

              @Override
              public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                emitter.onNext(
                    new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.MOVED));
              }

              @Override public void onCancelled(DatabaseError error) {
                FirebaseDatabaseErrorFactory.buildError(emitter, error);
              }
            });
        // this is used to remove the listener when the subscriber is
        // cancelled (unsubscribe)
        emitter.setCancellation(new Cancellable() {
          @Override public void cancel() throws Exception {
            firebaseRef.removeEventListener(childEventListener);
          }
        });
      }
    }, backPressureMode).compose(this.<FirebaseChildEvent>applyScheduler());
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

  /**
   * Function that receives the current Observable and should apply scheduler
   *
   * @param <T> source Observable
   * @return an {@link rx.Observable} with new or the same observe on scheduler
   */
  @SuppressWarnings("unchecked") private <T> Observable.Transformer<T, T> applyScheduler() {
    return new Observable.Transformer<T, T>() {
      @Override public Observable<T> call(Observable<T> observable) {
        if (observeOnScheduler != null) {
          return observable.observeOn(observeOnScheduler);
        }
        return observable;
      }
    };
  }
}
