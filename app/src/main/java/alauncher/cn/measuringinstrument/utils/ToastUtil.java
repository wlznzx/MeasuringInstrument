package alauncher.cn.measuringinstrument.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class ToastUtil {

    private static ArrayList<Toast> toastList = new ArrayList<Toast>();

    public static void newToast(Context context, String content,int duration) {
        cancelAll();
        Toast toast = Toast.makeText(context,content,duration);
        toastList.add(toast);
        toast.show();
    }

    public static void cancelAll() {
        if (!toastList.isEmpty()){
            for (Toast t : toastList) {
                t.cancel();
            }
            toastList.clear();
        }
    }
}
