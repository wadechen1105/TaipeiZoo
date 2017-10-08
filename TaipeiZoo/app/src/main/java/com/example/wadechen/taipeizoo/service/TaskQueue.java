package com.example.wadechen.taipeizoo.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue<T> {

    private Handler h;
    private HandlerThread thread;
    private OnExeProcessCallback mCallback;
    private boolean queueProcessing = false;
    /**
     * The queue of pending transmissions
     */
    private Queue<T> mQueue = new LinkedList<>();

    public TaskQueue(OnExeProcessCallback callback) {
        mCallback = callback;
        thread = new HandlerThread("__queue__" + hashCode(), Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        h = new Handler(thread.getLooper());
    }

    /**
     * Add a transaction item to transaction queue
     *
     * @param item
     */
    public void addToQueue(T item) {

        mQueue.add(item);

        // If there is no other transmission processing, go do this one!
        synchronized (this) {
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!queueProcessing) {
                        processQueue();
                    }
                }
            },200);
        }
    }

    public void clearQueue() {
        mQueue.clear();
        if (thread.isAlive()) {
            thread.quitSafely();
        }
    }

    /**
     * Call when a transaction has been completed.
     * Will process next transaction if queued (recursive)
     */
    private void processQueue() {
        if (mQueue.size() <= 0) {
            queueProcessing = false;
            if (thread.isAlive()) {
                thread.quitSafely();
            }
            return;
        }

        queueProcessing = true;
        T item = (T) mQueue.remove();
        mCallback.doSomeThing(item);
        processQueue();
    }

    public interface OnExeProcessCallback<T> {
        /**
         * which queue should do
         * @param item
         */
        void doSomeThing(T item);
    }
}
