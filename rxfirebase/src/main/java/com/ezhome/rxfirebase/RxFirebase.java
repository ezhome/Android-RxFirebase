package com.ezhome.rxfirebase;

import com.ezhome.rxfirebase.FirebaseChildEvent.EventType;
import com.ezhome.rxfirebase.exception.FirebaseAuthProviderDisabledException;
import com.ezhome.rxfirebase.exception.FirebaseExpiredTokenException;
import com.ezhome.rxfirebase.exception.FirebaseGeneralException;
import com.ezhome.rxfirebase.exception.FirebaseInvalidTokenException;
import com.ezhome.rxfirebase.exception.FirebaseLogoutException;
import com.ezhome.rxfirebase.exception.FirebaseNetworkErrorException;
import com.ezhome.rxfirebase.exception.FirebaseOperationFailedException;
import com.ezhome.rxfirebase.exception.FirebasePermissionDeniedException;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

import static com.firebase.client.Firebase.AuthResultHandler;
import static com.firebase.client.Firebase.AuthStateListener;

/**
 * The class is used as wrapper to firebase functionlity with
 * RxJava
 */
public class RxFirebase {

  private static RxFirebase instance;

  private Boolean connected = false;

  /**
   * Singleton
   *
   * @return {@link RxFirebase}
   */
  public static synchronized RxFirebase getInstance() {
    if (instance == null) {
      instance = new RxFirebase();
    }
    return instance;
  }

  //Prevent constructor initialisation
  protected RxFirebase() {

  }

  /**
   * Checks the Firebase's connection State
   *
   * @param firebaseRef {@link Firebase} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link Boolean}
   */
  public Observable<Boolean> observeConnectionState(final Firebase firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<Boolean>() {
      @Override public void call(final Subscriber<? super Boolean> subscriber) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot snapshot) {
            boolean newConnected = snapshot.getValue(Boolean.class);
            if (connected != newConnected) {
              connected = newConnected;
              subscriber.onNext(connected);
            }
          }

          @Override public void onCancelled(FirebaseError firebaseError) {
            attachErrorHandler(subscriber, firebaseError);
          }
        };
        firebaseRef.addValueEventListener(listener);

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
   * Attempts to authenticate to Firebase with an OAuth token from a provider supported by Firebase
   * Login. This method only works for providers that only require a 'access_token' as a parameter
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @param token {@link String} this is the access token for the give provider
   * @param provider {@link String} this is the given provider for login
   * @return an {@link rx.Observable} of {@link AuthData} to use
   */
  public Observable<AuthData> observeAuthWithOauthToken(final Firebase firebaseRef,
      final String token, final String provider) {
    return Observable.create(new Observable.OnSubscribe<AuthData>() {
      @Override public void call(final Subscriber<? super AuthData> subscriber) {
        firebaseRef.authWithOAuthToken(provider, token, new AuthResultHandler() {
          @Override public void onAuthenticated(AuthData authData) {
            subscriber.onNext(authData);
          }

          @Override public void onAuthenticationError(FirebaseError firebaseError) {
            attachErrorHandler(subscriber, firebaseError);
          }
        });
      }
    });
  }

  /**
   * Attempts to authenticate to Firebase with custom token.
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @param token {@link String} custom token to use for authentication
   * @return an {@link rx.Observable} of {@link AuthData}
   */
  public Observable<AuthData> observerWithCustomToken(final Firebase firebaseRef,
      final String token) {
    return Observable.create(new Observable.OnSubscribe<AuthData>() {
      @Override public void call(final Subscriber<? super AuthData> subscriber) {
        firebaseRef.authWithCustomToken(token, new AuthResultHandler() {
          @Override public void onAuthenticated(final AuthData authData) {
            subscriber.onNext(authData);
          }

          @Override public void onAuthenticationError(final FirebaseError firebaseError) {
            attachErrorHandler(subscriber, firebaseError);
          }
        });
      }
    });
  }

  /**
   * Attempts to logout from Firebase
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link Boolean}
   */
  public Observable<Boolean> observeLogout(final Firebase firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<Boolean>() {
      @Override public void call(final Subscriber<? super Boolean> subscriber) {
        //start logout process from firebase
        firebaseRef.unauth();

        //checks if the un-auth action has been done successfully
        final AuthStateListener authStateListener =
            firebaseRef.addAuthStateListener(new AuthStateListener() {
              @Override public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                  subscriber.onNext(true);
                  subscriber.onCompleted();
                } else {
                  subscriber.onError(
                      new FirebaseLogoutException("An error occurred during logout."));
                }
              }
            });

        // When the subscription is cancelled, remove the listener
        subscriber.add(Subscriptions.create(new Action0() {
          @Override public void call() {
            firebaseRef.removeAuthStateListener(authStateListener);
          }
        }));
      }
    });
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
  public Observable<String> observeSetValuePush(final Firebase firebaseRef, final Object object) {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(final Subscriber<? super String> subscriber) {
        final Firebase ref = firebaseRef.push();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(ref.getKey());
            subscriber.onCompleted();
          }

          @Override public void onCancelled(FirebaseError firebaseError) {
            attachErrorHandler(subscriber, firebaseError);
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

              @Override public void onCancelled(FirebaseError error) {
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
  public Observable<DataSnapshot> observeSingleValue(final Query firebaseRef) {
    return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
      @Override public void call(final Subscriber<? super DataSnapshot> subscriber) {
        final ValueEventListener listener = new ValueEventListener() {
          @Override public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(dataSnapshot);
            subscriber.onCompleted();
          }

          @Override public void onCancelled(FirebaseError error) {
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

              @Override public void onCancelled(FirebaseError error) {
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
   * Creates an observable only for the child added method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildAdded(final Query firebaseRef) {
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
                //ignore
              }

              @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                //ignore
              }

              @Override
              public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                //ignore
              }

              @Override public void onCancelled(FirebaseError error) {
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
  public Observable<FirebaseChildEvent> observeChildChanged(Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.CHANGED));
  }

  /**
   * Creates an observable only for the child removed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildRemoved(Query firebaseRef) {
    return observeChildEvent(firebaseRef).filter(filterChildEvent(EventType.REMOVED));
  }

  /**
   * Creates an observable only for the child removed method
   *
   * @param firebaseRef {@link Query} this is reference of a Firebase Query
   * @return an {@link rx.Observable} of {@link FirebaseChildEvent}
   * to use
   */
  public Observable<FirebaseChildEvent> observeChildMoved(Query firebaseRef) {
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
   * This method add to subsriber the proper error according to the
   *
   * @param subscriber {@link rx.Subscriber}
   * @param firebaseError {@link FirebaseError}
   * @param <T> generic subscriber
   */
  private <T> void attachErrorHandler(Subscriber<T> subscriber, FirebaseError firebaseError) {
    switch (firebaseError.getCode()) {
      case FirebaseError.INVALID_TOKEN:
        subscriber.onError(new FirebaseInvalidTokenException(firebaseError.getMessage()));
        break;
      case FirebaseError.AUTHENTICATION_PROVIDER_DISABLED:
        subscriber.onError(new FirebaseAuthProviderDisabledException(firebaseError.getMessage()));
        break;
      case FirebaseError.EXPIRED_TOKEN:
        subscriber.onError(new FirebaseExpiredTokenException(firebaseError.getMessage()));
        break;
      case FirebaseError.NETWORK_ERROR:
        subscriber.onError(new FirebaseNetworkErrorException(firebaseError.getMessage()));
        break;
      case FirebaseError.PERMISSION_DENIED:
        subscriber.onError(new FirebasePermissionDeniedException(firebaseError.getMessage()));
        break;
      case FirebaseError.OPERATION_FAILED:
        subscriber.onError(new FirebaseOperationFailedException(firebaseError.getMessage()));
        break;
      default:
        subscriber.onError(new FirebaseGeneralException(firebaseError.getMessage()));
    }
  }
}
