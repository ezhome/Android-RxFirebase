package com.ezhome.rxfirebase2.database;

import com.ezhome.rxfirebase2.auth.RxFirebaseAuth;
import com.ezhome.rxfirebase2.exception.FirebaseExpiredTokenException;
import com.ezhome.rxfirebase2.exception.FirebaseGeneralException;
import com.ezhome.rxfirebase2.exception.FirebaseInvalidTokenException;
import com.ezhome.rxfirebase2.exception.FirebaseNetworkErrorException;
import com.ezhome.rxfirebase2.exception.FirebaseOperationFailedException;
import com.ezhome.rxfirebase2.exception.FirebasePermissionDeniedException;
import com.google.firebase.database.DatabaseError;
import rx.Subscriber;

/**
 * An error factor for {@link RxFirebaseAuth}
 * and {@link RxFirebaseDatabase}
 */
public class FirebaseDatabaseErrorFactory {

  private FirebaseDatabaseErrorFactory() {
    //empty constructor prevent initialisation
  }

  /**
   * This method add to subsriber the proper error according to the
   *
   * @param subscriber {@link rx.Subscriber}
   * @param error {@link DatabaseError}
   * @param <T> generic subscriber
   */
  static <T> void buildError(Subscriber<T> subscriber, DatabaseError error) {
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
        break;
    }
  }
}
