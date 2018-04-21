package com.ranze.simplehandler;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by ranze on 2018/3/9.
 */

public class SimpleHandler {
    private Looper mLooper;
    private MessageQueue mQueue;

    public SimpleHandler() {
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException(
                    "Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;

    }

    public SimpleHandler(Looper looper) {
        mLooper = looper;
        mQueue = mLooper.mQueue;
    }

    public Message obtainMessage() {
        return Message.obtain(this);
    }

    public void handleMessage(Message msg) {

    }

    public void dispatcherMessage(Message msg) {
        handleMessage(msg);
    }

    public boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long when) {
        if (mQueue == null) {
            RuntimeException e = new RuntimeException(
                    this + " sendMessage called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;
        }

        return mQueue.enqueueMessage(msg, when);
    }


}
