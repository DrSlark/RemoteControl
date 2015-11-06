package com.example.remotecontrol;

import android.os.Environment;

/**
 * Created by zhutiantao on 2015/3/30.
 */
public class GlobalConstants {
    public static final String SERVER_IP = "172.22.237.74";
    public static final int SERVER_PORT = 9899;
    public static final long HEART_BEAT_RATE = 3000;
    public static final String HEARTBEAT_SENT = "heartbeatsent";
    public static final String HEARTBEAT_RECEIVED = "heartbeatreceived";
    public static final String HEART_BEAT_ACTION = "com.zhutiantao.action.HEART_BEAT_ACTION";
    public static final String PUSH_MESSAGE_ACTION = "com.zhutiantao.action.PUSH_MESSAGE_ACTION";
    public static final String INIT_SOCKET_FAILURE_ACTION = "com.zhutiantao.action.INIT_SOCKET_FAILURE";
    public static final String SOCKET_LOST_ACTION = "com.zhutiantao.action.SOCKET_LOST_ACTION";
    public static final String DEFAULT_INSTALL_DIR = Environment.getExternalStorageDirectory() + "/Download/";
    public static final String HEART_BEAT_RECORD_FILENAME = DEFAULT_INSTALL_DIR + "heartbeatlog.txt";
    public static final int THREE_MINUTES = 60;

    public static final int WATCHDOG_TIME = 2 * 60 * 1000;
    public static final int SOCKET_TIMEOUT = 2 * 60 * 1000;
}
