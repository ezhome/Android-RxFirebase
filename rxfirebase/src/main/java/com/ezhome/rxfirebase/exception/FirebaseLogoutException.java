package com.ezhome.rxfirebase.exception;

/**
 * This exception occurred when something went wrong during
 * the logout process
 */
public class FirebaseLogoutException extends Exception {

  public FirebaseLogoutException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseLogoutException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseLogoutException(Throwable throwable) {
    super(throwable);
  }
}
