package com.ranze.simplehandler;

import android.os.SystemClock;
import android.util.Log;


/**
 * Created by ranze on 2018/3/9.
 */

public class MessageQueue {
    private static final String TAG = "MessageQueue";
    private boolean mQuitAllowed;
    private boolean mQuitting;
    private Message mMessages;


    public MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
    }

    public Message next() {
        int nextPollTimeoutMillis = 0;
        for (; ; ) {
            pollOnce(nextPollTimeoutMillis);
            synchronized (this) {
                final long now = SystemClock.uptimeMillis();
                final Message msg = mMessages;
                if (msg != null) {
                    final long when = msg.when;
                    if (now > when) {
                        mMessages = msg.next;
                        msg.next = null;
                        return msg;
                    } else {
                        nextPollTimeoutMillis = (int) Math.min(when - now, Integer.MAX_VALUE);
                    }
                } else {
                    nextPollTimeoutMillis = -1;
                }

                if (mQuitting) {
                    return null;
                }
            }
        }
    }

    private void pollOnce(long nextPollTimeoutMillis) {
        synchronized (this) {
            if (nextPollTimeoutMillis < 0) {
                nextPollTimeoutMillis = 0;
            }
            try {
                wait(nextPollTimeoutMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void quit() {
        if (!mQuitAllowed) {
            return;
        }
        mQuitting = true;

        notify();
    }

    public boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }
        synchronized (this) {
            if (mQuitting) {
                IllegalStateException e = new IllegalStateException(
                        msg.target + " sending message to a Handler on a dead thread");
                Log.w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }
            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            boolean needWake = false;
            if (p == null || when == 0 || when <= p.when) {
                msg.next = p;
                mMessages = msg;
                needWake = true;
            } else {
                Message prev = p;
                p = p.next;
                while (p != null && p.when < when) {
                    prev = p;
                    p = p.next;
                }
                prev.next = msg;
                msg.next = p;
            }
            printMessage(mMessages);
            if (needWake) {
                notify();
            }
        }
        return true;
    }

    void printMessage(Message msg) {
        while (msg != null) {
            Log.d(TAG, msg + "");
            msg = msg.next;
        }
    }
}
