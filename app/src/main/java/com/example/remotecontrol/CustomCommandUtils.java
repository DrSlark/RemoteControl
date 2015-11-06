package com.example.remotecontrol;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.example.remotecontrol.customcommandreceiver.ConnectInfoUtils;
import com.example.remotecontrol.customcommandreceiver.HeartBeatInfoUtils;
import com.example.remotecontrol.customcommandreceiver.LocationInfoUtils;
import com.example.remotecontrol.customcommandreceiver.NetDataUtils;
import com.example.remotecontrol.customcommandreceiver.PackageUtils;
import com.example.remotecontrol.customcommandreceiver.PhoneInfoUtils;
import com.example.remotecontrol.customcommandreceiver.WifiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhutiantao on 2015/3/26.
 */
public class CustomCommandUtils {
    private static final String TAG = "CustomCommandUtils";

    public interface Command {
        public CommandResult execute();
    }

    public static class PmCommand implements Command {

        private List<String> mArgs;
        private Context mContext;

        public PmCommand(Context context, List<String> args) {
            this.mArgs = args;
            this.mContext = context;
        }

        @Override
        public CommandResult execute() {
            if (mArgs.size() != 2) {
                return new CommandResult(-1, null, "参数错误");
            }
            if (mArgs.get(0).trim().equals("-i")) {
                return PackageUtils.install(mContext, mArgs.get(1));
            } else if (mArgs.get(0).trim().equals("-u")) {
                return PackageUtils.uninstall(mContext, mArgs.get(1));
            }
            return new CommandResult(-1, null, "-i是安装 -u是卸载");
        }
    }

    public static class PhoneInfoCommand implements Command {
        private Context mContext;

        public PhoneInfoCommand(Context context) {
            mContext = context;
        }

        @Override
        public CommandResult execute() {
            return PhoneInfoUtils.getPhoneInfo(mContext);
        }
    }

    public static class ConnectInfoCommand implements Command {
        private Context mContext;

        public ConnectInfoCommand(Context context) {
            mContext = context;
        }

        @Override
        public CommandResult execute() {
            return ConnectInfoUtils.getConnectInfo(mContext);
        }
    }

    public static class LocationInfoCommand implements Command {
        private Context mContext;

        public LocationInfoCommand(Context context) {
            mContext = context;
        }

        @Override
        public CommandResult execute() {
            return LocationInfoUtils.getLastLocation(mContext);
        }
    }

    public static class WifiInfoCommand implements Command {
        private Context mContext;

        public WifiInfoCommand(Context context) {
            mContext = context;
        }

        @Override
        public CommandResult execute() {
            return WifiUtils.getWifiInfo(mContext);
        }
    }

    public static class WifiSettingCommand implements Command {
        private Context mContext;
        private List<String> args;

        public WifiSettingCommand(Context context, List<String> args) {
            mContext = context;
            this.args = args;
        }

        @Override
        public CommandResult execute() {
            return WifiUtils.setWifiInfo(mContext, args);
        }
    }

    public static class HeartBeatHistoryCommand implements Command {
        @Override
        public CommandResult execute() {
            return HeartBeatInfoUtils.getHeartBeatInfo();
        }
    }

    public static class NetDataCommand implements Command {

        Context mContext;

        public NetDataCommand(Context context) {
            mContext = context;
        }

        @Override
        public CommandResult execute() {
            return NetDataUtils.NetDataInfo(mContext);
        }
    }

    //命令 空格 参数
    public static CommandResult dispatchCommand(Context context, List<String> commandandargs) {

        String commandType = commandandargs.get(0);
        List<String> args = null;
        if (commandandargs.size() > 1) {
            args = commandandargs.subList(1, commandandargs.size());
        }
        Command command = null;
        /*int firstSpaceLocation=commandString.indexOf(" ");
        if(firstSpaceLocation>commandString.length()||firstSpaceLocation==-1)
            firstSpaceLocation=commandString.length();

        String commandType=commandString.substring(0, firstSpaceLocation);
        String args=commandString.substring(firstSpaceLocation+1,commandString.length());
        Log.d(TAG,"commandType"+commandType);
        Command command=null;*/
        switch (commandType.toLowerCase().trim()) {
            case "pm":
                command = new PmCommand(context, args);
                break;
            case "phoneinfo":
                command = new PhoneInfoCommand(context);
                break;
            case "connectinfo":
                command = new ConnectInfoCommand(context);
                break;
            case "locationinfo":
                command = new LocationInfoCommand(context);
                break;
            case "wifiinfo":
                command = new WifiInfoCommand(context);
                break;
            case "setwifi":
                command = new WifiSettingCommand(context, args);
                break;
            case "heartbeat":
                command = new HeartBeatHistoryCommand();
                break;
            case "netdata":
                command = new NetDataCommand(context);
                break;
            default:
                break;
        }
        if (command == null) {
            return new CommandResult(-1, null, "INVALID COMMAND: " + commandType);
        }
        return command.execute();
    }
}
