package com.jason.binderdemo;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by jason on 2018/6/12.
 */

public interface IBBookManager extends IInterface{

    static final String DESCRIPTOR = "com.jason.binderdemo.IBookManager"; //Binder的标记

    static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0; //标记调用哪个方法
    static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

    List<Book> getBookList() throws RemoteException; //aidl文件中声明的接口
    void addBook(Book book) throws RemoteException;

}
