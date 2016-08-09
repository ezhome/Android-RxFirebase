package com.ezhome.rxfirebase;

import com.ezhome.rxfirebase.FirebaseChildEvent.EventType;
import com.ezhome.rxfirebase.exception.FirebaseExpiredTokenException;
import com.ezhome.rxfirebase.exception.FirebaseGeneralException;
import com.ezhome.rxfirebase.exception.FirebaseInvalidTokenException;
import com.ezhome.rxfirebase.exception.FirebaseNetworkErrorException;
import com.ezhome.rxfirebase.exception.FirebaseOperationFailedException;
import com.ezhome.rxfirebase.exception.FirebasePermissionDeniedException;
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
 * The class is used as wrapper to firebase functionlity with
 * RxJava
 */
public final class RxFirebaseDatabase {

  private RxFirebaseDatabase() {
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
  public static Observable<String> observeSetValuePush(final DatabaseReference firebaseRef,
      final Object object) {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(final Subscriber<? super String> subscriber) {
        final DatabaseReference ref = firebaseRef.push();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(ref.getKey());
            subscriber.onCompleted();
          }

          @Override public void onCancelled(DatabaseError DatabaseError) {
            attachErrorHandler(subscriber, DatabaseError);
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
  public static Observable<DataSnapshot> observeValueEvent(final Query firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
      @Override public void call(final Subscriber<? super DataSnapshot> subscriber) {
        final ValueEventListener listener =
            firebaseRef.addValueEventListener(new ValueEventListener() {
              @Override public void onDataChange(DataSnapshot dataSnapshot) {
                subscriber.onNext(dataSnapshot);
              }

              @Override public void onCancelled(DatabaseError error) {
                attachErrorHandler(subscriber, error);
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
  public static Observable<DataSnapshot> observeSingleValue(final Query firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
      @Override public void call(final Subscriber<? super DataSnapshot> subscriber) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(dataSnapshot);
            subscriber.onCompleted();
          }

          @Override public void onCancelled(DatabaseError error) {
            attachErrorHandler(subscriber, error);
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
  public static Observable<FirebaseChildEvent> observeChildEvent(final Query firebaseRef) {
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
                attachErrorHandler(subscriber, error);
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
  public static Observable<FirebaseChildEvent> observeChildAdded(Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.ADDED));
  }

  /**
   * Creates an observable only for the child changed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public static Observable<FirebaseChildEvent> observeChildChanged(Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.CHANGED));
  }

  /**
   * Creates an observable only for the child removed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public static Observable<FirebaseChildEvent> observeChildRemoved(Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.REMOVED));
  }

  /**
   * Creates an observable only for the child removed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public static Observable<FirebaseChildEvent> observeChildMoved(Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.MOVED));
  }

  /**
   * Functions which filters a stream of {@link Observable} according to firebase
   * child event type
   *
   * @param type {@link FirebaseChildEvent}
   * @return {@link rx.functions.Func1} a function which returns a boolean if the type are equals
   */
  private static Func1<FirebaseChildEvent, Boolean> filterChildEvent(final EventType type) {
    return new Func1<FirebaseChildEvent, Boolean>() {
      @Override public Boolean call(FirebaseChildEvent firebaseChildEvent) {
        return firebaseChildEvent.getEventType() == type;
      }
    };
  }

  /**
   * This method add to subsriber the proper error according to the
   *
   * @param subscriber {@link rx.Subscriber}
   * @param error {@link DatabaseError}
   * @param <T> generic subscriber
   */
  private static <T> void attachErrorHandler(Subscriber<T> subscriber, DatabaseError error) {
    switch (error.getCode()) {
      case DatabaseError.INVALID_TOKEN:
        subscriber.onError(new FirebaseInvalidTokenException(error.getMessage()));
        break;
      case DatabaseError.EXPIRED_TOKEN:
        subscriber.onError(new FirebaseExpiredTokenException(error.getMessage()));
        break;
      case DatabaseError.NETWORK_ERROR:
        subscriber.onError(new FirebaseNetworkErrorException(error.getMessage()));
        break;
      case DatabaseError.PERMISSION_DENIED:
        subscriber.onError(new FirebasePermissionDeniedException(error.getMessage()));
        break;
      case DatabaseError.OPERATION_FAILED:
        subscriber.onError(new FirebaseOperationFailedException(error.getMessage()));
        break;
      default:
        subscriber.onError(new FirebaseGeneralException(error.getMessage()));
    }
  }
}
