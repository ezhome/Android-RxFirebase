package com.ezhome.rxfirebase.exception;

public class FirebaseGeneralException extends Exception {

  public FirebaseGeneralException() {
  }

  public FirebaseGeneralException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseGeneralException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseGeneralException(Throwable throwable) {
    super(throwable);
  }
}
