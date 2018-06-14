package com.jason.binderdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ClientActivity extends AppCompatActivity {

    private IBookManager mRemote;
    private boolean isBind;
    private TextView mTvInfo;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mRemote != null) {
                mRemote.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mRemote = null;
                //重新绑定服务
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemote = IBookManager.Stub.asInterface(service);
        //    mRemote = IBBookManagerImpl.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemote = null;
            isBind = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        mTvInfo = findViewById(R.id.tv_info);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isBind) {
            Intent intent = new Intent(this, RemoteService.class);
            bindService(intent, connection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBind) {
            unbindService(connection);
        }
    }

    public void addBook(View v) {
        Book book = new Book(3, "got3");
        try {
            mRemote.addBook(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getBook(View v){
        try {
            Book book = mRemote.getBookList().get(0);
            mTvInfo.setText(book.bookId + "\n" + book.bookName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
