// IOnNewBookArrivedListener.aidl
package com.jason.binderdemo;

// Declare any non-default types here with import statements
import com.jason.binderdemo.Book;
interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   void onNewBookArrived(in Book newBook);
}
