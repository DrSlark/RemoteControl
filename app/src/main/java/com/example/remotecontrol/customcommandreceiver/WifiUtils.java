package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.example.remotecontrol.CommandResult;
import com.example.remotecontrol.ShellUtils;

import java.util.List;

/**
 * Created by zhutiantao on 2015/3/27.
 */
public class WifiUtils {
    public static CommandResult getWifiInfo(Context context)
    {
        StringBuilder sb=new StringBuilder();
        WifiManager wifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        sb.append("Wi-Fi info: ").append(wifiManager.getConnectionInfo().toString()).append("\n");
        return new CommandResult(0,sb.toString(),null);
    }

    public static CommandResult setWifiInfo(Context context, List<String> args) {
        if(args==null||args.size()<2)
        {
            return new CommandResult(-1,null,"INVALID ARGUMENTS");
        }
        WifiManager wm= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        /*WifiConfiguration wifiConfiguration=new WifiConfiguration();
        wifiConfiguration.SSID=args[0];
        wifiConfiguration.
        wm.addNetwork()*/
        return null;
    }
}
