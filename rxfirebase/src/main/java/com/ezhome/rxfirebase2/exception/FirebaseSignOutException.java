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
 * This exception occurred when something went wrong during
 * the logout process
 */
public class FirebaseSignOutException extends Exception {

  public FirebaseSignOutException() {
    super();
  }

  public FirebaseSignOutException(String detailMessage) {
    super(detailMessage);
  }

  public FirebaseSignOutException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public FirebaseSignOutException(Throwable throwable) {
    super(throwable);
  }
}
