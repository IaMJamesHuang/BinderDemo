package com.jason.binderdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";
    private Messenger mServiceMessenger;
    private Messenger mReplyMessenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessengerService.MSG_FROM_SERVICE:
                    Log.d(TAG, "handleMessage: " + msg.getData().getString("reply"));
                    break;
            }
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "hello service");
            Message message = Message.obtain(null, MessengerService.MSG_FROM_CLIENT);
            message.setData(bundle);
            message.replyTo = mReplyMessenger;
            try {
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
