package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.example.remotecontrol.CommandResult;

import java.util.List;

/**
 * Created by zhutiantao on 2015/3/30.
 */
public class NetDataUtils {
    public static CommandResult NetDataInfo(Context context) {
        StringBuilder result = new StringBuilder("\n");
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appliactaionInfos = pm.getInstalledApplications(0);
        for (ApplicationInfo applicationInfo : appliactaionInfos) {
            int uid = applicationInfo.uid;
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);
            if (tx == 0 && rx == 0) {
                continue;
            }
            result.append(applicationInfo.toString()).append(":Send(byte) ").append(tx)
                    .append(";Receive(byte) ").append(rx).append("\n");
        }
        result.append("Send Total(byte): ").append(TrafficStats.getMobileTxBytes()).append("\n");
        result.append("Receive Total(byte): ").append(TrafficStats.getMobileRxBytes()).append("\n");
        result.append("Send Total include wifi(byte): ").append(TrafficStats.getTotalTxBytes()).append("\n");
        result.append("Receive Total include wifi(byte): ").append(TrafficStats.getTotalRxBytes()).append("\n");

        return new CommandResult(0, result.toString(), null);
    }
}
