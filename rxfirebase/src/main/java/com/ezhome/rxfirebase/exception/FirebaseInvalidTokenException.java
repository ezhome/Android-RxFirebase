package com.ezhome.rxfirebase.exception;

/**
 * Raised when the specified authentication token is invalid.
 */
public class FirebaseInvalidTokenException extends Exception {

  public FirebaseInvalidTokenException() {
    super();
  }

  public FirebaseInvalidTokenException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseInvalidTokenException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseInvalidTokenException(Throwable throwable) {
    super(throwable);
  }
}
