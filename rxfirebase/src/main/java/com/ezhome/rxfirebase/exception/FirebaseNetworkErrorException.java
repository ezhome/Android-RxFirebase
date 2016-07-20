package com.ezhome.rxfirebase.exception;

/**
 * Raised when the operation could not be performed due to a network error.
 */
public class FirebaseNetworkErrorException extends Exception {

  public FirebaseNetworkErrorException() {
  }

  public FirebaseNetworkErrorException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseNetworkErrorException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseNetworkErrorException(Throwable throwable) {
    super(throwable);
  }
}
