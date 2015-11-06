package com.example.remotecontrol.customcommandreceiver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.example.remotecontrol.CommandResult;
import com.example.remotecontrol.GlobalConstants;
import com.example.remotecontrol.ShellUtils;

import java.io.File;

public class PackageUtils {

    public static final String TAG = "PackageUtils";

    public static final int APP_INSTALL_AUTO = 0;
    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int APP_INSTALL_EXTERNAL = 2;

    //安装应用 根据是否是系统应用和是否具有root权限选择安装方式
    public static CommandResult install(Context context, String filename) {
        String filePath = GlobalConstants.DEFAULT_INSTALL_DIR + filename;
        if (PackageUtils.isSystemApplication(context) || ShellUtils.isRoot()) {
            return installSilent(context, filePath);
        }
        return installNormal(context, filePath);
    }

    //非静默安装
    public static CommandResult installNormal(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return new CommandResult(-1, null, "INSTALL_FAILED_INVALID_URI");
        }
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return new CommandResult(0, "INSTALL_SUCCEEDED", null);
    }

    public static CommandResult installSilent(Context context, String filePath) {
        return installSilent(context, filePath, " -r " + getInstallLocationParams());
    }

    public static CommandResult installSilent(Context context, String filePath, String pmParams) {
        if (filePath == null || filePath.length() == 0) {
            return new CommandResult(-1, null, "INSTALL_FAILED_INVALID_URI");
        }

        File file = new File(filePath);
        if (file == null || file.length() <= 0 || !file.exists() || !file.isFile()) {
            return new CommandResult(-1, null, "INSTALL_FAILED_INVALID_URI");
        }

        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
                .append(pmParams == null ? "" : pmParams).append(" ").append(filePath.replace(" ", "\\ "));
        CommandResult commandResult = ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true);
        return commandResult;
        /*if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return new CommandResult(0,null,"INSTALL_FAILED_INVALID_URI");
        }
        Log.e(TAG,
                new StringBuilder().append("installSilent successMsg:").append(commandResult.successMsg)
                        .append(", ErrorMsg:").append(commandResult.errorMsg).toString());
        if (commandResult.errorMsg == null) {
            return INSTALL_FAILED_OTHER;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
            return INSTALL_FAILED_ALREADY_EXISTS;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_APK")) {
            return INSTALL_FAILED_INVALID_APK;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_URI")) {
            return INSTALL_FAILED_INVALID_URI;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
            return INSTALL_FAILED_INSUFFICIENT_STORAGE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_DUPLICATE_PACKAGE")) {
            return INSTALL_FAILED_DUPLICATE_PACKAGE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_NO_SHARED_USER")) {
            return INSTALL_FAILED_NO_SHARED_USER;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
            return INSTALL_FAILED_UPDATE_INCOMPATIBLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_SHARED_USER_INCOMPATIBLE")) {
            return INSTALL_FAILED_SHARED_USER_INCOMPATIBLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
            return INSTALL_FAILED_MISSING_SHARED_LIBRARY;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
            return INSTALL_FAILED_REPLACE_COULDNT_DELETE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_DEXOPT")) {
            return INSTALL_FAILED_DEXOPT;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_OLDER_SDK")) {
            return INSTALL_FAILED_OLDER_SDK;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
            return INSTALL_FAILED_CONFLICTING_PROVIDER;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_NEWER_SDK")) {
            return INSTALL_FAILED_NEWER_SDK;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_TEST_ONLY")) {
            return INSTALL_FAILED_TEST_ONLY;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE")) {
            return INSTALL_FAILED_CPU_ABI_INCOMPATIBLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_FEATURE")) {
            return INSTALL_FAILED_MISSING_FEATURE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CONTAINER_ERROR")) {
            return INSTALL_FAILED_CONTAINER_ERROR;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_INSTALL_LOCATION")) {
            return INSTALL_FAILED_INVALID_INSTALL_LOCATION;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MEDIA_UNAVAILABLE")) {
            return INSTALL_FAILED_MEDIA_UNAVAILABLE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_VERIFICATION_TIMEOUT")) {
            return INSTALL_FAILED_VERIFICATION_TIMEOUT;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_VERIFICATION_FAILURE")) {
            return INSTALL_FAILED_VERIFICATION_FAILURE;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_PACKAGE_CHANGED")) {
            return INSTALL_FAILED_PACKAGE_CHANGED;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_UID_CHANGED")) {
            return INSTALL_FAILED_UID_CHANGED;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NOT_APK")) {
            return INSTALL_PARSE_FAILED_NOT_APK;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_MANIFEST")) {
            return INSTALL_PARSE_FAILED_BAD_MANIFEST;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION")) {
            return INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES")) {
            return INSTALL_PARSE_FAILED_NO_CERTIFICATES;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES")) {
            return INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING")) {
            return INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME")) {
            return INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID")) {
            return INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_MANIFEST_MALFORMED")) {
            return INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        }
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_MANIFEST_EMPTY")) {
            return INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
        }
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INTERNAL_ERROR")) {
            return INSTALL_FAILED_INTERNAL_ERROR;
        }
        return INSTALL_FAILED_OTHER;*/
    }

    public static CommandResult uninstall(Context context, String packageName) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.isRoot()) {
            return uninstallSilent(context, packageName);
        }
        return uninstallNormal(context, packageName) ? new CommandResult(0)
                : new CommandResult(-1, null, "DELETE_FAILED_INVALID_PACKAGE");
    }

    public static boolean uninstallNormal(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse(new StringBuilder(32).append("package:")
                .append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    public static CommandResult uninstallSilent(Context context, String packageName) {
        return uninstallSilent(context, packageName, true);
    }

    public static CommandResult uninstallSilent(Context context, String packageName, boolean isKeepData) {
        if (packageName == null || packageName.length() == 0) {
            return new CommandResult(-1, null, "DELETE_FAILED_INVALID_PACKAGE");
        }

        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
                .append(isKeepData ? " -k " : " ").append(packageName.replace(" ", "\\ "));
        CommandResult commandResult = ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return new CommandResult(0, "DELETE_SUCCEEDED", null);
        }
        Log.e(TAG,
                new StringBuilder().append("uninstallSilent successMsg:").append(commandResult.successMsg)
                        .append(", ErrorMsg:").append(commandResult.errorMsg).toString());
        if (commandResult.errorMsg == null) {
            return new CommandResult(-1, null, "DELETE_FAILED_INTERNAL_ERROR");
        }
        if (commandResult.errorMsg.contains("Permission denied")) {
            return new CommandResult(-1, null, "DELETE_FAILED_PERMISSION_DENIED");
        }
        return new CommandResult(-1, null, "DELETE_FAILED_INTERNAL_ERROR");
    }

    public static boolean isSystemApplication(Context context) {
        return context != null && isSystemApplication(context, context.getPackageName());
    }

    public static boolean isSystemApplication(Context context, String packageName) {
        return context != null && isSystemApplication(context.getPackageManager(), packageName);

    }

    public static boolean isSystemApplication(PackageManager packageManager, String packageName) {
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public static int getInstallLocation() {
        CommandResult commandResult = ShellUtils.execCommand(
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location", false, true);
        if (commandResult.result == 0 && commandResult.successMsg != null && commandResult.successMsg.length() > 0) {
            try {
                int location = Integer.parseInt(commandResult.successMsg.substring(0, 1));
                switch (location) {
                    case APP_INSTALL_INTERNAL:
                        return APP_INSTALL_INTERNAL;
                    case APP_INSTALL_EXTERNAL:
                        return APP_INSTALL_EXTERNAL;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e(TAG, "pm get-install-location error");
            }
        }
        return APP_INSTALL_AUTO;
    }

    private static String getInstallLocationParams() {
        int location = getInstallLocation();
        switch (location) {
            case APP_INSTALL_INTERNAL:
                return "-f";
            case APP_INSTALL_EXTERNAL:
                return "-s";
        }
        return "";
    }

    public static void startInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        int sdkVersion = Build.VERSION.SDK_INT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", packageName, null));
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra((sdkVersion == Build.VERSION_CODES.FROYO ? "pkg"
                                     : "com.android.settings.ApplicationPkgName"), packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

/*
    public static final int INSTALL_SUCCEEDED                              = 1;

    public static final int INSTALL_FAILED_ALREADY_EXISTS                  = -1;


    public static final int INSTALL_FAILED_INVALID_APK                     = -2;


    public static final int INSTALL_FAILED_INVALID_URI                     = -3;


    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE            = -4;


    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE               = -5;


    public static final int INSTALL_FAILED_NO_SHARED_USER                  = -6;


    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE             = -7;


    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE        = -8;


    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY          = -9;


    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE          = -10;


    public static final int INSTALL_FAILED_DEXOPT                          = -11;

    public static final int INSTALL_FAILED_OLDER_SDK                       = -12;


    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER            = -13;


    public static final int INSTALL_FAILED_NEWER_SDK                       = -14;


    public static final int INSTALL_FAILED_TEST_ONLY                       = -15;

    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE            = -16;


    public static final int INSTALL_FAILED_MISSING_FEATURE                 = -17;


    public static final int INSTALL_FAILED_CONTAINER_ERROR                 = -18;


    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION        = -19;


    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE               = -20;


    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT            = -21;


    public static final int INSTALL_FAILED_VERIFICATION_FAILURE            = -22;


    public static final int INSTALL_FAILED_PACKAGE_CHANGED                 = -23;


    public static final int INSTALL_FAILED_UID_CHANGED                     = -24;


    public static final int INSTALL_PARSE_FAILED_NOT_APK                   = -100;


    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST              = -101;


    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION      = -102;


    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES           = -103;


    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;


    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING      = -105;

    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME          = -106;


    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID        = -107;


    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED        = -108;


    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY            = -109;


    public static final int INSTALL_FAILED_INTERNAL_ERROR                  = -110;

    public static final int INSTALL_FAILED_OTHER                           = -1000000;


    public static final int DELETE_SUCCEEDED                               = 1;


    public static final int DELETE_FAILED_INTERNAL_ERROR                   = -1;


    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER            = -2;


    public static final int DELETE_FAILED_INVALID_PACKAGE                  = -3;


    public static final int DELETE_FAILED_PERMISSION_DENIED                = -4;*/
}
