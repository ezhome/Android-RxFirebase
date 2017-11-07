[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[ ![Download](https://api.bintray.com/packages/ezhome/maven/rxfirebase/images/download.svg) ](https://bintray.com/ezhome/maven/rxfirebase/_latestVersion)
[![CircleCI](https://circleci.com/gh/ezhome/Android-RxFirebase.svg?style=shield)](https://circleci.com/gh/ezhome/Android-RxFirebase)

# RxFirebase

RxJava implementation by ezhome for the Android [Firebase client](https://www.firebase.com/docs/android/).

----
Contents
--------
- [Usage](#usage)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [License](#license)

Usage
-----

Currently library supports for new Google Firebase the followings:
- Firebase Authentication `RxFirebaseAuth`
- Firebase Database `RxFirebaseDatabase`

#### Use the project with your own Firebase instance

1. Clone this repository.
- Create a new project in the [Firebase console](https://console.firebase.google.com/).
- Click *Add Firebase to your Android app*
  * provide a **unique package name**
  * use the same package name for the **applicationId** in your `build.gradle`
  * insert SHA-1 fingerprint of your debug certificate, otherwise you won't be able to log in
- Copy the generated *google-services.json* to the `app` folder of your project which will replace the mock google services json file.
- You should be able to successfully sync the project now.
- Copy contents of the `./server/database.rules.json` into your *Firebase -> Database -> Rules* and publish them.
- Import data `./server/sample-data.json` into your *Firebase -> Database*
- Change the *Firebase URL path* in this [file](https://github.com/ezhome/Android-RxFirebase/blob/master/app/src/main/java/com/ezhome/rxfirebasedemo/PostsFragment.java#L30)
- Build and run the app.

#### Example

```java
    final Firebase firebaseRef = new Firebase("https://docs-examples.firebaseio.com/web/saving-data/fireblog/posts");
    RxFirebaseDatabase.getInstance().observeValueEvent(firebaseRef).subscribe(new GetPostsSubscriber());

    private final class GetPostsSubscriber extends Subscriber<DataSnapshot> {
      @Override public void onCompleted() {
        PostsFragment.this.showProgress(false);
      }

      @Override public void onError(Throwable e) {
        PostsFragment.this.showProgress(false);
        PostsFragment.this.showError(e.getMessage());
      }

      @SuppressWarnings("unchecked") @Override public void onNext(DataSnapshot dataSnapshot) {
        List<BlogPostEntity> blogPostEntities = new ArrayList<>();
        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
          blogPostEntities.add(childDataSnapshot.getValue(BlogPostEntity.class));
        }
        PostsFragment.this.renderBlogPosts(blogPostEntities);
      }
    }
```

Check the example application [here](https://github.com/ezhome/Android-RxFirebase/tree/master/app)

You can change scheduler for observing values in a different thread

#### Example
```
 RxFirebaseDatabase.getInstance().observeOn(Schedulers.io());
```
Download
--------
The project is available on jCenter. In your app build.gradle (or explicit module) you must add this:
```
dependencies {
  compile 'com.ezhome:rxfirebase:2.2.0'
}
```


Tests
-----

Tests are available in `rxfirebase/src/test/java/` directory and can be executed from Android Studio or CLI with the following command:

```
./gradlew test
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles.


License
-------

    Copyright 2016 Ezhome Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.