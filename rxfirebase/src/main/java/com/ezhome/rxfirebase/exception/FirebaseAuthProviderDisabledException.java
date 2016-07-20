package com.ezhome.rxfirebase.exception;

/**
 * When The requested authentication provider is disabled for this Firebase.
 */
public class FirebaseAuthProviderDisabledException extends Exception {

  public FirebaseAuthProviderDisabledException() {
  }

  public FirebaseAuthProviderDisabledException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseAuthProviderDisabledException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseAuthProviderDisabledException(Throwable throwable) {
    super(throwable);
  }
}
