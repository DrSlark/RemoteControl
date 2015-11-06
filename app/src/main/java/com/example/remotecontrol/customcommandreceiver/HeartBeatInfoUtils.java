package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;

import com.example.remotecontrol.CommandResult;
import com.example.remotecontrol.GlobalConstants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zhutiantao on 2015/3/30.
 */
public class HeartBeatInfoUtils {
    public static CommandResult getHeartBeatInfo() {
        BufferedReader br = null;
        try {

            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(GlobalConstants.HEART_BEAT_RECORD_FILENAME)));
            String s = null;
            StringBuilder result = new StringBuilder();
            while ((s = br.readLine()) != null) {
                result.append("\n").append(s);
            }
            return new CommandResult(0, result.toString(), null);
        } catch (IOException e) {
            e.printStackTrace();
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return new CommandResult(-1, null, e.toString());
        }

    }
}
