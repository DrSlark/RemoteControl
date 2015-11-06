package com.example.remotecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SocketService extends Service {
    private static final String TAG = SocketService.class.getSimpleName();

    private long rcvTime;
    private long sendTime = 0L;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Handler mHandler = new Handler();
    private ReadThread mReadThread;
    private WeakReference<Socket> mSocket;
    private WatchDog mWatchDog;

    private IMySendMsg.Stub mStub = new IMySendMsg.Stub() {
        @Override
        public boolean sendMessage(String message) throws RemoteException {
            return sendMsg(message);
        }
    };

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            boolean success;
            if (System.currentTimeMillis() - sendTime > GlobalConstants.HEART_BEAT_RATE) {
                success = sendMsg(GlobalConstants.HEARTBEAT_SENT);
                if (!success) {
                    mLocalBroadcastManager.sendBroadcast(new Intent(GlobalConstants.SOCKET_LOST_ACTION));
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    releaseLastSocket(mSocket);
                    new InitSocketThread().start();
                }
            }
            mHandler.postDelayed(this, GlobalConstants.HEART_BEAT_RATE);
        }
    };

    private void initSocket() {
        try {
            Socket so = new Socket(GlobalConstants.SERVER_IP, GlobalConstants.SERVER_PORT);
            mSocket = new WeakReference<>(so);
            mReadThread = new ReadThread(so);
            mReadThread.start();
            mWatchDog = new WatchDog();
            new Timer().schedule(mWatchDog, GlobalConstants.WATCHDOG_TIME, GlobalConstants.WATCHDOG_TIME);
            mHandler.postDelayed(heartBeatRunnable, GlobalConstants.HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
        } catch (IOException e) {
            e.printStackTrace();
            Intent intent = new Intent(GlobalConstants.INIT_SOCKET_FAILURE_ACTION);
            mLocalBroadcastManager.sendBroadcast(intent);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new InitSocketThread().start();
                }
            }, 20 * 1000);
        }
    }

    private void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            initSocket();
        }
    }

    public boolean sendMsg(String msg) {
        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        Socket soc = mSocket.get();
        try {
            if (!soc.isClosed() && !soc.isOutputShutdown()) {
                OutputStream os = soc.getOutputStream();
                String message = msg + "\r\n";
                os.write(message.getBytes());
                os.flush();
                sendTime = System.currentTimeMillis();
                if (msg.equals(GlobalConstants.HEARTBEAT_SENT)) {
                    Log.v(TAG, msg);
                } else {
                    Log.d(TAG, msg);
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //用来检测心跳是否正常
    class WatchDog extends TimerTask {

        @Override
        public void run() {
            if (System.currentTimeMillis() - rcvTime > GlobalConstants.SOCKET_TIMEOUT) {
                mLocalBroadcastManager.sendBroadcast(new Intent(GlobalConstants.SOCKET_LOST_ACTION));
                mHandler.removeCallbacks(heartBeatRunnable);
                mReadThread.release();
                releaseLastSocket(mSocket);
                new InitSocketThread().start();
            }

        }
    }

    class ReadThread extends Thread {
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<>(socket);
        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {
                try {
                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int length = 0;
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart && ((length = is.read(buffer)) != -1)) {
                        if (length > 0) {
                            rcvTime = System.currentTimeMillis();

                            String message = new String(Arrays.copyOf(buffer,
                                    length)).trim();
                            //收到服务器过来的消息，就通过Broadcast发送出去

                            if (message.contains(GlobalConstants.HEARTBEAT_RECEIVED)) {//处理心跳回复
                                Log.v(TAG, message + "  ");
                                Intent intent = new Intent(GlobalConstants.HEART_BEAT_ACTION);
                                mLocalBroadcastManager.sendBroadcast(intent);
                            } else {
                                Log.d(TAG, message + "  ");
                                //其他消息回复
                                Intent intent = new Intent(GlobalConstants.PUSH_MESSAGE_ACTION);
                                intent.putExtra("message", message);
                                mLocalBroadcastManager.sendBroadcast(intent);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        new InitSocketThread().start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMsg("Client: " + SocketService.this.getPackageName() + "Heart Beat Service Connect to Server");
            }
        }).start();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstants.HEART_BEAT_ACTION);
        intentFilter.addAction(GlobalConstants.PUSH_MESSAGE_ACTION);
        intentFilter.addAction(GlobalConstants.INIT_SOCKET_FAILURE_ACTION);
        intentFilter.addAction(GlobalConstants.SOCKET_LOST_ACTION);
        mLocalBroadcastManager.registerReceiver(new MySocketPushMessageReceiver(), intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

}