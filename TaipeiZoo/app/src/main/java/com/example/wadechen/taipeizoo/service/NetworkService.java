package com.example.wadechen.taipeizoo.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.example.wadechen.taipeizoo.Animal;
import com.example.wadechen.taipeizoo.L;
import com.example.wadechen.taipeizoo.network.NetWorkHelper;
import com.example.wadechen.taipeizoo.util.ImageProcess;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkService extends Service implements NetWorkHelper.OnResponseListener,
        TaskQueue.OnExeProcessCallback<Animal> {

    public static final int MSG_REGISTER_CLIENT = 0;
    public static final int MSG_UNREGISTER_CLIENT = 1;
    public static final int MSG_DOWNLOAD_COMPLETED = 3;
    public static final int MSG_GET_JSON_FILE = 5;
    public static final int MSG_GET_IMAGE_FILE = 6;
    public static final int TASK_DOWNLOAD_IMAGE = 10;
    /**
     * Target we publish for clients to send messages to NetworkTaskHandler.
     */
    final Messenger mMessenger = new Messenger(new TaskHandler());
    /**
     * Keeps track of all current registered clients.
     */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    private int mCount = 0;
    private TaskQueue<Animal> mQueue;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private NetWorkHelper mHelper;


    @Override
    public void onJSONArray(JSONArray response) {

    }

    @Override
    public void onJSONObject(JSONObject response) {
        Animal.getAnimalsFromJSONAndSaveToCache(response);
        sendEventToUI(MSG_GET_JSON_FILE);
        sendTaskEvent(TASK_DOWNLOAD_IMAGE);
    }

    @Override
    public void onBitamp(final Bitmap response, final String url, final Object id) {

        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                Cache cache = Animal.getCache();
                ImageProcess imageProcess = new ImageProcess(NetworkService.this);
                Animal a = (Animal) id;

                //save bitmap to local folder
                if (url.equals(a.pic1_url)) {
                    imageProcess.saveImageToApp(response, String.valueOf(a.mId + "_pic1"));
                } else if (url.equals(a.pic2_url)) {
                    imageProcess.saveImageToApp(response, String.valueOf(a.mId + "_pic2"));
                }

                ++mCount;
                // download 5 items to notify UI
                if (mCount % 5 == 0) {
                    sendEventToUI(MSG_GET_IMAGE_FILE);
                } else if (mCount >= cache.getData().size()) {
                    Message m = Message.obtain();
                    m.what = MSG_DOWNLOAD_COMPLETED;
                    try {
                        mMessenger.send(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                response.recycle();
                L.d("download image count : " + mCount);
            }
        });
    }

    @Override
    public void onError(int code, String description) {
        L.w("err code : " + code + " / msg : " + description);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        L.v("onBind");
        return mMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        L.v("onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.v("onUnbind");
        return super.onUnbind(intent);
    }

    private void sendTaskEvent(int what) {
        mServiceHandler.sendEmptyMessage(what);
    }

    private void sendEventToUI(int what) {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            Message m = Message.obtain();
            m.what = what;
            try {
                mClients.get(i).send(m);
            } catch (RemoteException e) {
                e.printStackTrace();
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("___workThread__", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mQueue = new TaskQueue<>(this);
        mHelper = NetWorkHelper.getHelper(this);
        mHelper.registerListener(this);
        L.i("Service onCreate");
    }

    @Override
    public void onDestroy() {
        L.v("onDestroy");
        super.onDestroy();
        mHelper.unregisterListener(this);
        mHelper.shutDown();
        mClients.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d("onStartCommand---");
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        mHelper.getAsJSONObject(Animal.WEB_URL, "getJson");

        return START_STICKY;
    }

    @Override
    public void doSomeThing(Animal item) {
        mHelper.getBitmap(item.pic1_url, item, 256, 192);
        mHelper.getBitmap(item.pic2_url, item, 256, 192);
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASK_DOWNLOAD_IMAGE:
                    List<Animal> list = Animal.getCache().getData();
                    for (Animal a : list) {
                        L.d("id: " + a.mId);
                        mQueue.addToQueue(a);
                    }
                    break;
            }
        }
    }

    /**
     * Handler of UI messages from clients.
     */
    class TaskHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_DOWNLOAD_COMPLETED:
                    L.d("download complete ...");
                    stopSelf();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}