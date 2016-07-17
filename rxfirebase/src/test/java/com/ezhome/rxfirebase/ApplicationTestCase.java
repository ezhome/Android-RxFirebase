package com.ezhome.rxfirebase;

import android.os.Build;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(value = RobolectricGradleTestRunner.class)
@Config(application = ApplicationStub.class, constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public abstract class ApplicationTestCase {
}
