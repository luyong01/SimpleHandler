package com.ranze.simplehandlerdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ranze.simplehandler.Looper;
import com.ranze.simplehandler.Message;
import com.ranze.simplehandler.SimpleHandler;

/**
 * Created by ranze on 2018/3/9.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText mMessageWhatEt;
    private EditText mMessageDeleyEt;
    private TextView mMessageTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageWhatEt = findViewById(R.id.et_msg_what);
        mMessageDeleyEt = findViewById(R.id.et_message_delay_time);
        mMessageTv = findViewById(R.id.tv_message);

        TestHandlerThread testHandlerThread = new TestHandlerThread();
        testHandlerThread.start();
        final SimpleHandler simpleHandler = new SimpleHandler(testHandlerThread.getLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                final String msgStr = msg.toString();
                Log.d(TAG, "handle message: " + msgStr);
                mMessageTv.post(new Runnable() {
                    @Override
                    public void run() {
                        mMessageTv.setText("Message is: " + msgStr);
                    }
                });
            }
        };

        findViewById(R.id.btn_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = simpleHandler.obtainMessage();
                int what = 123;
                try {
                    what = Integer.valueOf(mMessageWhatEt.getText().toString());
                } catch (NumberFormatException e) {
                    Log.d(TAG, e.toString());
                }
                msg.what = what;

                long delayTime = 0;
                try {
                    delayTime = Integer.valueOf(mMessageDeleyEt.getText().toString());
                } catch (NumberFormatException e) {
                    Log.d(TAG, e.toString());
                }

                simpleHandler.sendMessageDelayed(msg, delayTime);
            }
        });
    }

    class TestHandlerThread extends Thread {
        private Looper mLooper;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            synchronized (this) {
                mLooper = Looper.myLooper();
                notifyAll();
            }
            Looper.loop();
        }

        public Looper getLooper() {
            if (!isAlive()) {
                return null;
            }

            synchronized (this) {
                while (isAlive() && mLooper == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return mLooper;
        }
    }
}
