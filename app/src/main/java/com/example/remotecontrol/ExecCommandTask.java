package com.example.remotecontrol;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhutiantao on 2015/3/31.
 */
public class ExecCommandTask extends AsyncTask<String,Void,CommandResult> {
    private static final String TAG=ExecCommandTask.class.getSimpleName();
    private String result;
    private Context mContext;
    public ExecCommandTask(Context context)
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
                commands.addAll(Arrays.asList(allCommandMsg).subList(1, allCommandMsg.length));
                commandResult=CustomCommandUtils.dispatchCommand(mContext,commands);
                break;
            //shell的命令
            case 's':
                StringBuilder tmp=new StringBuilder();
                for(int i=1;i<allCommandMsg.length;i++)
                    tmp.append(" ").append(allCommandMsg[i]);
                String command=tmp.toString();
                Log.d(TAG, "command" + command);
                commandResult=ShellUtils.execCommand(command,true);
                break;
            default: Log.d(TAG,"命令格式不正确");return new CommandResult(-1,null,"Command must start with c or s");
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
