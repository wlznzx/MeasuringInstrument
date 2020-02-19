package alauncher.cn.measuringinstrument.utils;

import android.app.AlertDialog;
import android.content.Context;

import alauncher.cn.measuringinstrument.R;

public class DialogUtils {

    public static void showDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show(); //构建AlertDialog并显示
    }

}
