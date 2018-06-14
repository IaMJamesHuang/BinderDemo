package com.jason.binderdemo;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class TCPActivity extends AppCompatActivity {

    private static final String TAG = "TCPActivity";
    private Socket mClientSocket;
    private PrintWriter mPrintWriter;
    private String[] mMsg = new String[] {
            "hello service",
            "i love u",
            "how about u"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);
        Intent intent = new Intent(this, TCPService.class);
        startService(intent);
        new Thread() {
            @Override
            public void run() {
                connectToTCP();
            }
        }.start();
    }

    private void connectToTCP() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            } catch (IOException e) {
                SystemClock.sleep(1000);
                Log.d(TAG, "failed to connect to service , retry ... ");
            }
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPActivity.this.isFinishing()) {
                String msg = br.readLine();
                Log.d(TAG, "msg from service:" + msg);
            }
            br.close();
            mPrintWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(View view) {
        new Thread() {
            @Override
            public void run() {
                int i = new Random().nextInt(mMsg.length);
                String msg = mMsg[i];
                mPrintWriter.println(msg);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
