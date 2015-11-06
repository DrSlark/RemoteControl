package com.example.remotecontrol;

/**
 * Created by zhutiantao on 2015/3/26.
 */
public class CommandResult {
    public int result;
    public String successMsg;
    public String errorMsg;
    public boolean isReturn = true;

    public CommandResult(int result) {
        this.result = result;
    }

    public CommandResult(int result, String successMsg, String errorMsg) {
        this.result = result;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
        if (successMsg == null && errorMsg == null) {
            isReturn = false;
        }
    }

    @Override
    public String toString() {
        return "result:  " + (isReturn ? (result + (result == 0 ? " success: " + successMsg : "error: " + errorMsg))
                                      : " Command has 0 return value") + "  \n";
    }
}
