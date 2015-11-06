package com.example.remotecontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhutiantao on 2015/3/25.
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
    private static final String TAG = MyPushMessageReceiver.class.getSimpleName();
    public static final String COMMAND_KEY = "command_key";
    public static final String RESULT_KEY = "result_key";

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);
        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
            Utils.setBind(context, true);
        }
    }

    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
    }

    @Override
    public void onDelTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
    }

    @Override
    public void onListTags(Context context, int i, List<String> strings, String s) {

    }

    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);
        ExecCommandTask task = new ExecCommandTask(context);
        task.execute(message);
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s2, String s3) {

    }

   /* private class ExecCommandTask extends AsyncTask<String,Void,CommandResult>{

        private String result;
        private Context mContext;
        ExecCommandTask(Context context)
        {
            mContext=context;
        }
        @Override
        protected CommandResult doInBackground(String... params) {
            String message=params[0];
            String[] allCommandMsg=message.split(" +");
            char commandType=allCommandMsg[0].charAt(0);

            CommandResult commandResult=null;
            switch (commandType)
            {
                //自定义的一些命令
                case 'c':
                    ArrayList<String> commands=new ArrayList<>();
                    for(int i=1;i<allCommandMsg.length;i++)
                    {
                        commands.add(allCommandMsg[i]);
                    }
                    commandResult=CustomCommandUtils.dispatchCommand(mContext,commands);
                    break;
                //shell的命令
                case 's':
                    StringBuilder tmp=new StringBuilder();
                    for(int i=1;i<allCommandMsg.length;i++)
                        tmp.append(" ").append(allCommandMsg[i]);
                    String command=tmp.toString();
                    Log.d(TAG,"command"+command);
                    commandResult=ShellUtils.execCommand(command,true);
                    break;
                default: Log.d(TAG,"命令格式不正确");break;
            }
            return commandResult;
        }

        @Override
        protected void onPostExecute(CommandResult commandResult) {
            super.onPostExecute(commandResult);
            if(commandResult!=null)
                result=commandResult.toString();
            Log.d(TAG,"result"+result);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean r= SocketUtils.sendMessage(result);
                    Log.d(TAG,"SocketUtils.sendMessage  "+r);
                }
            }).start();
        }
    }
*/

}
