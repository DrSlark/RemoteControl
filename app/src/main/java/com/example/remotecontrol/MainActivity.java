package com.example.remotecontrol;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startService(new Intent(this, SocketService.class));
        //bindService(new Intent(this, SocketService.class), new MyServiceConnection(), BIND_AUTO_CREATE);
        setContentView(R.layout.activity_main);
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "service connected");
            final IMySendMsg mBinder = IMySendMsg.Stub.asInterface(service);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mBinder.sendMessage("Client:" + MainActivity.this.getPackageName() + " connecting Server");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
