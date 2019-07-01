package alauncher.cn.measuringinstrument.utils;

import java.text.DecimalFormat;

public class NumberUtils {
    public static DecimalFormat decimalFormat = new DecimalFormat("#,##0.0000");

    public static String get4bits(Double vaule){
        return decimalFormat.format(vaule);
    };
}
