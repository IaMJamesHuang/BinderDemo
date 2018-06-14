package com.jason.binderdemo;

import android.os.RemoteException;

/**
 * Created by jason on 2018/6/14.
 */

public class ComputeImpl extends Icompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
