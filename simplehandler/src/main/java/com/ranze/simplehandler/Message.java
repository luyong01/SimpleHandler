package com.ranze.simplehandler;

/**
 * Created by ranze on 2018/3/9.
 */

public class Message {
    public int what = -1;
    public Object object;
    public boolean inUse = false;

    long when;

    SimpleHandler target;

    Message next;

    private static final Object sPoolSync = new Object();
    private static Message sPool;
    private static int sPoolSize = 0;

    private static final int MAX_POOL_SIZE = 50;

    public Message() {

    }

    public static Message obtain(SimpleHandler handler) {
        Message msg = obtain();
        msg.target = handler;
        return msg;
    }

    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = sPool.next;
                m.next = null;
                m.inUse = false;
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    void recycle() {
        if (inUse) {
            throw new IllegalStateException("This message cannot be recycled because it "
                    + "is still in use.");
        }

        recycleUnchecked();
    }

    void recycleUnchecked() {
        what = -1;
        inUse = false;
        object = null;
        next = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    public SimpleHandler getTarget() {
        return target;
    }

    public void setTarget(SimpleHandler target) {
        this.target = target;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void markInUse() {
        inUse = true;
    }

    @Override
    public String toString() {
        return "Message{" +
                "what=" + what +
                ", object=" + object +
                ", inUse=" + inUse +
                ", when=" + when +
                ", target=" + target +
                ", next=" + next +
                ", inUse=" + inUse +
                '}';
    }
}
