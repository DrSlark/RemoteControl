package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.example.remotecontrol.CommandResult;

/**
 * Created by zhutiantao on 2015/3/27.
 */
public class ConnectInfoUtils {
    public static CommandResult getConnectInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        StringBuilder sb = new StringBuilder("allSupportNetwork: \n");
        for (NetworkInfo info : cm.getAllNetworkInfo()) {
            sb.append("TypeName: ").append(info.getTypeName()).append("\n");
            sb.append("SubtypeName: ").append(info.getSubtypeName()).append("\n");
        }
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        String activeNetwork = activeNetworkInfo.getTypeName();

        sb.append("activeNetwork: ").append(activeNetwork).append("\n");

        return new CommandResult(0, sb.toString(), null);

    }
}
