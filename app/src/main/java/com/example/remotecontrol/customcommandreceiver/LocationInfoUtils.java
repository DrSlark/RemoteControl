package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.example.remotecontrol.CommandResult;

/**
 * Created by zhutiantao on 2015/3/27.
 */
public class LocationInfoUtils {

    public static CommandResult getLastLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        StringBuilder sb = new StringBuilder("LastKnownLocation: \n");
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            for (String p : lm.getAllProviders()) {
                location = lm.getLastKnownLocation(p);
                sb.append(p).append(": ").append(location == null ? " null" : location.toString()).append("\n");
            }
        }
        if (location != null) {
            return new CommandResult(0, (sb.append(location.toString())).toString(), null);
        }
        return new CommandResult(-1, null, sb.toString());

    }
   /* public static CommandResult TrackLocation(Context context)
    {
        return null;
    }
    public static CommandResult openGps(Context context,boolean open)
    {
        LocationManager lm= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isOpen=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isOpen)
        {
            return new CommandResult(0,"GPS IS OPEN",null);
        }
        //lm.setTestProviderEnabled();
        return null;
    }*/
}
