package com.example.wadechen.taipeizoo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.wadechen.taipeizoo.network.NetWorkHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    // Context of the app under test.
    Context appContext = InstrumentationRegistry.getTargetContext();
    CountDownLatch cdl = new CountDownLatch(1);

    @Test
    public void useAppContext() throws Exception {
        assertEquals("com.example.wadechen.taipeizoo", appContext.getPackageName());
    }

    @Test
    public void downloadFromZooURL() {
        NetWorkHelper helper = NetWorkHelper.getHelper(appContext);
        //check network okay
        assertEquals(helper.isNetworkAvailable(), true);


        helper.registerListener(new NetWorkHelper.OnResponseListener() {
            @Override
            public void onJSONArray(JSONArray response) {

            }

            @Override
            public void onJSONObject(JSONObject response) {
                Log.d("onJSONObject", "result : " + response.toString());
                cdl.countDown();
            }

            @Override
            public void onBitamp(Bitmap response) {

            }

            @Override
            public void onError(int code, String message) {
                cdl.countDown();
            }
        });

        helper.getAsJSONObject(Animal.WEB_URL, "getJsonTest");
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
