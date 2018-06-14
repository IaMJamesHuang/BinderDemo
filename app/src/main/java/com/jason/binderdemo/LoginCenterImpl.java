package com.jason.binderdemo;

import android.os.RemoteException;

/**
 * Created by jason on 2018/6/14.
 */

public class LoginCenterImpl extends ILoginCenter.Stub {
    @Override
    public String encrypt(String content) throws RemoteException {
        return content.toLowerCase();
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
