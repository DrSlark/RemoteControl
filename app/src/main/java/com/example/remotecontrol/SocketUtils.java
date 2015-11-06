package com.example.remotecontrol;

import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhutiantao on 2015/3/29.
 */
public class SocketUtils {
    private static final String TAG = "SocketUtils";

    public static boolean sendMessage(String message) {
        Socket socket = null;
        BufferedWriter bw = null;
        try {
            socket = new Socket(GlobalConstants.SERVER_IP, GlobalConstants.SERVER_PORT);
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            if (message != null) {
                bw.write(message);
            } else {
                bw.write(" ");
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void HeartBeatTest(final Socket socket, int time) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Socket socket = null;
                BufferedWriter bw = null;
                try {
                    //socket = new Socket("192.168.1.15", 5674);
                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    Date currentTime = new Date();
                    String s = (String) DateFormat.format("yyyy-mm-dd hh:mm:ss z", currentTime);
                    bw.write("Heart beat send at: " + s + "\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, time);
    }

    public static void ServerReplyListenet(final BufferedReader reader) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                try {
                    while ((response = reader.readLine()) != null) {
                        Log.d(TAG, "Server Response " + response);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
