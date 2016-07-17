package com.ezhome.rxfirebase.exception;

/**
 * This exception occurred when the firebase is disconnected
 * eg. network is turned off
 */
public class FirebaseDisconnectedException extends Exception {

  public FirebaseDisconnectedException() {
  }

  public FirebaseDisconnectedException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseDisconnectedException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseDisconnectedException(Throwable throwable) {
    super(throwable);
  }
}
