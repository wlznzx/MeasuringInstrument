package alauncher.cn.measuringinstrument.utils;

public class CommonUtil {
    public static boolean isNull(String s) {
        if (s == null || "".equals(s)) {
            return true;
        }
        return false;
    }
}
