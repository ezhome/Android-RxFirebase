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
package com.ezhome.rxfirebase2;

import com.ezhome.rxfirebase2.auth.RxFirebaseAuth;
import com.ezhome.rxfirebase2.exception.FirebaseSignInException;
import com.ezhome.rxfirebase2.exception.FirebaseSignOutException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RxFirebaseAuthTest extends ApplicationTestCase {

  public static final String FAKE_TOKEN = "fakeToken";

  private RxFirebaseAuth rxFirebaseAuth;
  private RxFirebaseAuth spyRxFirebaseAuth;
  @Mock FirebaseAuth mockFirebaseAuth;
  @Mock private AuthCredential mockAuthCredential;
  @Mock private FirebaseUser mockFirebaseUser;
  @Mock private DatabaseReference mockRef;
  @Mock private DataSnapshot mockDataSnapshot;
  @Mock private FirebaseChildEvent mockFirebaseChildEvent;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    rxFirebaseAuth = RxFirebaseAuth.getInstance(mockFirebaseAuth);
    spyRxFirebaseAuth = spy(rxFirebaseAuth);
  }

  @After public void destroy() {
    rxFirebaseAuth = null;
    spyRxFirebaseAuth = null;
  }

  @Test public void testObserveSignIn() throws InterruptedException {
    when(spyRxFirebaseAuth.observeSignIn(mockAuthCredential)).thenReturn(
        Observable.just(mockFirebaseUser));

    TestSubscriber<FirebaseUser> testSubscriber = new TestSubscriber<>();
    spyRxFirebaseAuth.observeSignIn(mockAuthCredential)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseUser));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();
  }

  @Test public void testObserveSignInError() throws InterruptedException {
    when(spyRxFirebaseAuth.observeSignIn(mockAuthCredential)).thenReturn(
        Observable.<FirebaseUser>error(new FirebaseSignInException()));

    TestSubscriber<FirebaseUser> testSubscriber = new TestSubscriber<>();
    spyRxFirebaseAuth.observeSignIn(mockAuthCredential)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertError(FirebaseSignInException.class);
    testSubscriber.unsubscribe();
  }

  @Test public void testObserveSignInToken() throws InterruptedException {
    when(spyRxFirebaseAuth.observeSignIn(FAKE_TOKEN)).thenReturn(Observable.just(mockFirebaseUser));

    TestSubscriber<FirebaseUser> testSubscriber = new TestSubscriber<>();
    spyRxFirebaseAuth.observeSignIn(FAKE_TOKEN)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseUser));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();
  }

  @Test public void testObserveSignInTokenError() throws InterruptedException {
    when(spyRxFirebaseAuth.observeSignIn(FAKE_TOKEN)).thenReturn(
        Observable.<FirebaseUser>error(new FirebaseSignInException()));

    TestSubscriber<FirebaseUser> testSubscriber = new TestSubscriber<>();
    spyRxFirebaseAuth.observeSignIn(FAKE_TOKEN)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertError(FirebaseSignInException.class);
    testSubscriber.unsubscribe();
  }

  @Test public void testObserveSignOut() throws InterruptedException {
    when(spyRxFirebaseAuth.observeSignOut()).thenReturn(
        Observable.<Boolean>error(new FirebaseSignOutException()));

    TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
    spyRxFirebaseAuth.observeSignOut()
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertError(FirebaseSignOutException.class);
    testSubscriber.unsubscribe();
  }

  @Test public void testObserveSignOutError() throws InterruptedException {
    when(spyRxFirebaseAuth.observeSignOut()).thenReturn(Observable.just(true));

    TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
    spyRxFirebaseAuth.observeSignOut()
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(true));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();
  }
}
