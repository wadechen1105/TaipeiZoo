package com.example.wadechen.taipeizoo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.wadechen.taipeizoo.network.NetWorkHelper;
import com.example.wadechen.taipeizoo.service.Cache;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
                Cache a = Animal.getAnimalsFromJSONAndSaveToCache(response);
                assertTrue(a.size() > 0);
                cdl.countDown();
            }

            @Override
            public void onBitamp(Bitmap response, String url, Object id) {

            }

            @Override
            public void onError(int code, String message) {
                cdl.countDown();
                assertTrue(false);
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
