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

import android.os.Build;
import com.ezhome.rxfirebase.BuildConfig;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(value = RobolectricGradleTestRunner.class)
@Config(application = ApplicationStub.class, constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public abstract class ApplicationTestCase {
}
