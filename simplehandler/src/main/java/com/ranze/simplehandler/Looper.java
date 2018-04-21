package com.ranze.simplehandler;

/**
 * Created by ranze on 2018/3/9.
 */

public class Looper {
    MessageQueue mQueue;
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    private boolean mQuitAllowed;

    private Thread mThread;

    private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }

    public static void prepare() {
        prepare(true);
    }

    public static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(quitAllowed));
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }

        final MessageQueue queue = me.mQueue;

        for (; ; ) {
            Message msg = queue.next();
            if (msg == null || msg.target == null) {
                return;
            }
            msg.target.dispatcherMessage(msg);
            msg.recycleUnchecked();
        }
    }

    public void quit() {
        mQueue.quit();
    }

}
