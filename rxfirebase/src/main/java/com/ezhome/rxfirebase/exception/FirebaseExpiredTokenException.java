package com.ezhome.rxfirebase.exception;

/**
 * Raised when the supplied auth token has expired
 */
public class FirebaseExpiredTokenException extends Exception {

  public FirebaseExpiredTokenException() {
    super();
  }

  public FirebaseExpiredTokenException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseExpiredTokenException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseExpiredTokenException(Throwable throwable) {
    super(throwable);
  }
}
