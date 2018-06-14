package com.jason.binderdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jason on 2018/6/14.
 */

public class BinderPool {

    public static final int BINDER_LOGIN_CODE = 0;
    public static final int BINDER_COMPUTE = 1;

    private Context mContext;
    private CountDownLatch mConnectBinderCountDownLatch;
    private static volatile BinderPool mInstance;
    private IBinderPool mBinderPool;

    private IBinder.DeathRecipient mBinderDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mBinderDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    public static BinderPool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BinderPool.class) {
                if (mInstance == null) {
                    mInstance = new BinderPool(context);
                }
            }
        }
        return mInstance;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();

    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    public static class BinderPoolImpl extends IBinderPool.Stub {

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_LOGIN_CODE:
                    binder = new LoginCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
                default:
            }
            return binder;
        }
    }
}
