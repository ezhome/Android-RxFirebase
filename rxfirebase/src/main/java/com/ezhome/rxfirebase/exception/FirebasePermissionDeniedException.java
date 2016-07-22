package com.ezhome.rxfirebase.exception;

/**
 * Raised when the Firebase returns a permission
 * denied error
 */
public class FirebasePermissionDeniedException extends Exception {

  public FirebasePermissionDeniedException() {
    super();
  }

  public FirebasePermissionDeniedException(String detailMessage) {
    super(detailMessage);
  }

  public FirebasePermissionDeniedException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebasePermissionDeniedException(Throwable throwable) {
    super(throwable);
  }
}
