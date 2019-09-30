package alauncher.cn.measuringinstrument;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import alauncher.cn.measuringinstrument.utils.StepUtils;

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
        Context appContext = InstrumentationRegistry.getTargetContext();
        int baseValue = 255;
        for (int i = 0; i < 8; i++) {
            baseValue = StepUtils.setChannel(i, baseValue, false);
            Log.d("wlDebug", "channel_" + i + " = " + Integer.toBinaryString(baseValue));
        }
        assertEquals("alauncher.cn.measuringinstrument", appContext.getPackageName());
    }
}
