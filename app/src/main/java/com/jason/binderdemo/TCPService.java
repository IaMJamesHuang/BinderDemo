package com.jason.binderdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPService extends Service {
    private static final String TAG = "TCPService";
    private boolean mIsServiceDestoryed = false;
    private String[] mDefinedMessagees = new String[] {
            "hello",
            "what's your name?",
            "how's the weather today",
            "you know what, I can talk to different person on same time"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TCPServer()).start();
    }

    public TCPService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class TCPServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestoryed) {
                try {
                    final Socket client = serverSocket.accept();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.write("welcome to chat room");
        while (!mIsServiceDestoryed) {
            String str = in.readLine();
            Log.d(TAG, "msg from client:" + str);
            if (str == null) {
                break;
            }
            int i = new Random().nextInt(mDefinedMessagees.length);
            String msg = mDefinedMessagees[i];
            out.println(msg);
            Log.d(TAG, "send msg:" + msg);
        }
        in.close();
        out.close();
        client.close();
    }
}
