package com.ezhome.rxfirebase.exception;

/**
 * This exception occurred when an operation has been failed
 */
public class FirebaseOperationFailedException extends Exception {

  public FirebaseOperationFailedException() {
    super();
  }

  public FirebaseOperationFailedException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseOperationFailedException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseOperationFailedException(Throwable throwable) {
    super(throwable);
  }
}
