package com.jason.binderdemo;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import static com.jason.binderdemo.BinderPool.BINDER_COMPUTE;

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
    }


    public void doWork(View v) {
        new Thread(){
            @Override
            public void run() {
                Icompute icompute = Icompute.Stub.asInterface(BinderPool.getInstance(BinderPoolActivity.this).queryBinder(BINDER_COMPUTE));
                try {
                    int sum = icompute.add(1 ,2);
                    Log.d(TAG, "run: " + sum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
