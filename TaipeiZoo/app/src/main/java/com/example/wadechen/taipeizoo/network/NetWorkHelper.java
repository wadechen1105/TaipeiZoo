package com.example.wadechen.taipeizoo.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class NetWorkHelper {

    private static NetWorkHelper instance;
    private Context mContext;
    private ArrayList<OnResponseListener> mList = new ArrayList<>();

    /**
     * get singleton instance
     *
     * @param ctx
     * @return
     */
    public static NetWorkHelper getHelper(Context ctx) {
        if (instance == null) {
            synchronized (NetWorkHelper.class) {
                if (instance == null) {
                    instance = new NetWorkHelper(ctx);
                }
            }
        }
        return instance;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conMgr.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private NetWorkHelper(Context ctx) {
        mContext = ctx;

        //init android network
        AndroidNetworking.initialize(mContext);
    }

    public void registerListener(OnResponseListener listener) {
        mList.add(listener);
    }

    public void unregisterListener(OnResponseListener listener) {
        mList.remove(listener);
    }

    public void getAsJSONArray(String url, String tag) {

        AndroidNetworking.get(url)
                .setTag(tag)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        for (OnResponseListener listener :
                                mList) {
                            listener.onJSONArray(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        for (OnResponseListener listener :
                                mList) {
                            listener.onError(error.getErrorCode(), error.getMessage());
                        }
                    }
                });
    }

    public void getAsJSONObject(String url, String tag) {

        AndroidNetworking.get(url)
                .setTag(tag)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        for (OnResponseListener listener :
                                mList) {
                            listener.onJSONObject(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        for (OnResponseListener listener :
                                mList) {
                            listener.onError(error.getErrorCode(), error.getMessage());
                        }
                    }
                });
    }

    public void getBitmap(String imageUrl) {
        AndroidNetworking.get(imageUrl)
                .setTag("imageRequest")
                .setPriority(Priority.MEDIUM)
                .setBitmapMaxHeight(100)
                .setBitmapMaxWidth(100)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // do anything with bitmap
                        for (OnResponseListener listener :
                                mList) {
                            listener.onBitamp(bitmap);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        for (OnResponseListener listener :
                                mList) {
                            listener.onError(error.getErrorCode(), error.getMessage());
                        }
                    }
                });
    }

    public void cancelDownloadImage() {
        AndroidNetworking.cancel("imageRequest");
    }

    public void cancelTask(String tag) {
        AndroidNetworking.cancel(tag);
    }

    public interface OnResponseListener {
        void onJSONArray(JSONArray response);

        void onJSONObject(JSONObject response);

        void onBitamp(Bitmap response);

        void onError(int code, String message);
    }
}