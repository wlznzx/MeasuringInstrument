package alauncher.cn.measuringinstrument.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class BuildUtils {

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getLinuxCore_Ver() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }
// get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);


        String result = "";
        String line;
// get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }

    public static String mac;

    public static String getLocalMacAddress(Context context) {
        String mac = "Unknow";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mac = getNewMac();
        } else {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null && info.getMacAddress() != null) {
                mac = info.getMacAddress().replace(":", "");
            }
        }
        if (mac != null) {
            mac = mac.toLowerCase();
        }
        // return "YU:QI:NO:1";
        return mac;
    }

    private static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * INNER-VER
     * 内部版本
     * return String
     */

    public static String getInner_Ver() {
        String ver = "";

        if (android.os.Build.DISPLAY.contains(android.os.Build.VERSION.INCREMENTAL)) {
            ver = android.os.Build.DISPLAY;
        } else {
            ver = android.os.Build.VERSION.INCREMENTAL;
        }
        return ver;
    }
}
