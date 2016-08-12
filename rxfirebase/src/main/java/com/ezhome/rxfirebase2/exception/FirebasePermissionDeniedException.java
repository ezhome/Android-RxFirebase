/**
 * Copyright 2016 Ezhome Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ezhome.rxfirebase2.exception;

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
