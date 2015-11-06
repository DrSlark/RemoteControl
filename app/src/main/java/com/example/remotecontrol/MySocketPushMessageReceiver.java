package com.example.remotecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.remotecontrol.customcommandreceiver.PackageUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhutiantao on 2015/3/29.
 */
public class MySocketPushMessageReceiver extends BroadcastReceiver {
    private static final String COUNTING_TAG = "IMCOUNTING";
    private static final String TAG = MySocketPushMessageReceiver.class.getSimpleName();
    private static long sCounts;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(GlobalConstants.HEART_BEAT_ACTION)) {
            Log.v(COUNTING_TAG, "HEART_BEAT_ACTION" + sCounts);
            synchronized(this) {
                if ((++sCounts) % GlobalConstants.THREE_MINUTES == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "HEART_BEAT_ACTION_WRITING");
                            write2File("");
                        }
                    }).start();
                }
            }
        }
        if (intent.getAction().equals(GlobalConstants.PUSH_MESSAGE_ACTION)) {
            String message = intent.getStringExtra("message");
            Log.d(TAG, "PUSH_MESSAGE_ACTION" + message);
            if (message.trim().startsWith("c") || message.trim().startsWith("s")) {
                ExecCommandTask task = new ExecCommandTask(context);
                task.execute(message);
            }
        }
        if (intent.getAction().equals(GlobalConstants.INIT_SOCKET_FAILURE_ACTION)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    write2File("  INIT_SOCKET_FAILURE");
                }
            }).start();
        }
        if (intent.getAction().equals(GlobalConstants.SOCKET_LOST_ACTION)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    write2File("  SOCKET_LOST");
                }
            }).start();
        }
    }

    private void write2File(String content) {
        Log.d(TAG, "write2File");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(GlobalConstants.HEART_BEAT_RECORD_FILENAME, true);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            String time = simpleDateFormat.format(new Date()) + "  " + content + "\n";
            fos.write(time.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
