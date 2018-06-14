package com.jason.binderdemo;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by jason on 2018/6/12.
 */

public abstract class IBBookManagerImpl extends Binder implements IBBookManager {

    public IBBookManagerImpl() {
        //系统的构造方法
        this.attachInterface(this, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case TRANSACTION_getBookList:
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBookList(); //接收到客户端的请求后调用本地的方法
                reply.writeNoException();
                reply.writeTypedList(result); //在相应中写入数据
                return true;
            case TRANSACTION_addBook:
                data.enforceInterface(DESCRIPTOR);
                Book arg0;
                if (0!=data.readInt()) {
                    arg0 = Book.CREATOR.createFromParcel(data); //将参数反序列化为BOOK对象
                }else {
                    arg0 = null;
                }
                this.addBook(arg0); //调用本地方法
                reply.writeNoException();
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    public static IBBookManager asInterface(IBinder obj) { //为客户端暴露服务端提供的服务
        if (obj == null) {
            return null;
        }
        IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (iin != null && iin instanceof IBBookManager) {
            //跑在统一进程中，其实就是返回当前这个对象，即在构造函数中传入的this
            //调用方法时不需要通过transact过程
            return ((IBBookManager)iin);
        }else {
            //不同进程通过代理类来完成跨进程的调用
            return new Proxy(obj);
        }
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    private static class Proxy implements IBBookManager {

        private IBinder mRemote;

        public Proxy(IBinder remote) {
            this.mRemote = remote;
        }

        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Book> result;
            try {
                data.writeInterfaceToken(DESCRIPTOR); //写入Binder信息用于验证
                mRemote.transact(TRANSACTION_getBookList, data, reply, 0); //跨进程发生在这里
                reply.readException();
                result = reply.createTypedArrayList(Book.CREATOR); //获取服务端回应的数据
            }finally {
                reply.recycle();
                data.recycle();
            }
            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);//写入Binder信息用于验证
                if (book != null) {
                    data.writeInt(1);
                    book.writeToParcel(data, 0); //写入参数信息
                }else {
                    data.writeInt(0);
                }
                mRemote.transact(TRANSACTION_addBook, data, reply, 0);
                reply.readException();
            }finally {
                reply.recycle();
                data.recycle();
            }
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }

}
