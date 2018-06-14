package com.jason.binderdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteService extends Service {

    private CopyOnWriteArrayList<Book> mList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListeners = new CopyOnWriteArrayList<>();

    {
        mList.add(new Book(1, "got1"));
        mList.add(new Book(2, "got2"));
    }

    IBookManager.Stub mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.add(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {

        }
    };

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
