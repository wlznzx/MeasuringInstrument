package alauncher.cn.measuringinstrument.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String getFileDate(long time) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        Date date = new Date();
        return dateFormater.format(date);
    }

    public static String getDate(long time) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(time);
        return dateFormater.format(date);
    }

}
