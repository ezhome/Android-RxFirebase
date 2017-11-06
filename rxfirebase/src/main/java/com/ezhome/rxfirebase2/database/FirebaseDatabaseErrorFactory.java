package com.ezhome.rxfirebase2.database;

import com.ezhome.rxfirebase2.auth.RxFirebaseAuth;
import com.ezhome.rxfirebase2.exception.FirebaseExpiredTokenException;
import com.ezhome.rxfirebase2.exception.FirebaseGeneralException;
import com.ezhome.rxfirebase2.exception.FirebaseInvalidTokenException;
import com.ezhome.rxfirebase2.exception.FirebaseNetworkErrorException;
import com.ezhome.rxfirebase2.exception.FirebaseOperationFailedException;
import com.ezhome.rxfirebase2.exception.FirebasePermissionDeniedException;
import com.google.firebase.database.DatabaseError;
import rx.Emitter;
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
   * This method add to emitter the proper error according to the
   *
   * @param emitter {@link rx.Emitter}
   * @param error {@link DatabaseError}
   * @param <T> generic subscriber
   */
  static <T> void buildError(Emitter<T> emitter, DatabaseError error) {
    switch (error.getCode()) {
      case DatabaseError.INVALID_TOKEN:
        emitter.onError(new FirebaseInvalidTokenException(error.getMessage()));
        break;
      case DatabaseError.EXPIRED_TOKEN:
        emitter.onError(new FirebaseExpiredTokenException(error.getMessage()));
        break;
      case DatabaseError.NETWORK_ERROR:
        emitter.onError(new FirebaseNetworkErrorException(error.getMessage()));
        break;
      case DatabaseError.PERMISSION_DENIED:
        emitter.onError(new FirebasePermissionDeniedException(error.getMessage()));
        break;
      case DatabaseError.OPERATION_FAILED:
        emitter.onError(new FirebaseOperationFailedException(error.getMessage()));
        break;
      default:
        emitter.onError(new FirebaseGeneralException(error.getMessage()));
        break;
    }
  }
}
