<<<<<<< Updated upstream:malmondTrialBook/app/src/androidTest/java/com/example/Experiment_To_The_Moon/ExampleInstrumentedTest.java
<<<<<<< Updated upstream:malmondTrialBook/app/src/androidTest/java/com/example/malmond_trialbook/ExampleInstrumentedTest.java
package com.example.malmond_trialbook;
=======
package com.example.Experiment_To_The_Moon;
>>>>>>> Stashed changes:malmondTrialBook/app/src/androidTest/java/com/example/Experiment_To_The_Moon/ExampleInstrumentedTest.java
=======
package com.example.Experiment_To_The_Moon;
>>>>>>> Stashed changes:Experiment To The Moon/app/src/androidTest/java/com/example/Experiment_To_The_Moon/ExampleInstrumentedTest.java

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.malmond_trialbook", appContext.getPackageName());
    }
}