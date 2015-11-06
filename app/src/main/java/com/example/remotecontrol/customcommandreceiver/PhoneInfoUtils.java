package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;

import android.location.LocationManager;
import android.telephony.TelephonyManager;

import com.example.remotecontrol.CommandResult;

/**
 * Created by zhutiantao on 2015/3/27.
 */
public class PhoneInfoUtils {
    public static CommandResult getPhoneInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String Imei = tm.getDeviceId();
        String Location = tm.getCellLocation().toString();
        String PhoneNum = tm.getLine1Number();
        String simOperatorName = tm.getSimOperatorName();
        int dataActivity = tm.getDataActivity();
        String NetType = convertNetWorkTypeInt2String(tm.getNetworkType());

        StringBuilder sb = new StringBuilder();
        sb.append("\nImei:").append(Imei).append("\n").append("; Location: ").append(Location).append("\n")
                .append("; Phonenum: ").append(PhoneNum).append("\n").append("; simOperatorName: ")
                .append(simOperatorName).append("\n")
                .append("; dataActivity: ").append(convertDataInt2String(dataActivity)).append("\n")
                .append("; deviceId: ").append(tm.getDeviceId()).append("\n")
                .append("; NetType: ").append(NetType).append("\n");
        return new CommandResult(0, sb.toString(), null);
    }

    private static String convertDataInt2String(int dataActivity) {
        switch (dataActivity) {
            case TelephonyManager.DATA_ACTIVITY_IN:
                return "DATA_ACTIVITY_IN";
            case TelephonyManager.DATA_ACTIVITY_OUT:
                return "DATA_ACTIVITY_OUT";
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                return "DATA_ACTIVITY_INOUT";
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                return "DATA_ACTIVITY_DORMANT(Data connection is active, but physical link is down)";
            case TelephonyManager.DATA_ACTIVITY_NONE:
                return "DATA_ACTIVITY_NONE";
        }
        return null;
    }

    private static String convertNetWorkTypeInt2String(int type) {
        switch (type) {
            /** Network type is unknown */
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "NETWORK_TYPE_UNKNOWN";
            /** Current network is GPRS */
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "NETWORK_TYPE_GPRS 2G";
            /** Current network is EDGE */
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "NETWORK_TYPE_EDGE";
            /** Current network is UMTS */
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "NETWORK_TYPE_UMTS 3G";
            /** Current network is CDMA: Either IS95A or IS95B*/
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "NETWORK_TYPE_CDMA 2G";
            /** Current network is EVDO revision 0*/
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "NETWORK_TYPE_EVDO_0";
            /** Current network is EVDO revision A*/
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "NETWORK_TYPE_EVDO_A";
            /** Current network is 1xRTT*/
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "NETWORK_TYPE_1xRTT";
            /** Current network is HSDPA */
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "NETWORK_TYPE_HSDPA";
            /** Current network is HSUPA */
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "NETWORK_TYPE_HSUPA";
            /** Current network is HSPA */
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "NETWORK_TYPE_HSPA";
            /** Current network is iDen */
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "NETWORK_TYPE_IDEN";
            /** Current network is EVDO revision B*/
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "NETWORK_TYPE_EVDO_B";
            /** Current network is LTE */
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "NETWORK_TYPE_LTE 4G";
            /** Current network is eHRPD */
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "NETWORK_TYPE_EHRPD";
            /** Current network is HSPA+ */
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "NETWORK_TYPE_HSPAP";
            /** Current network is GSM {@hide} */

        }
        return null;
    }
}
