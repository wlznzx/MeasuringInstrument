package alauncher.cn.measuringinstrument.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BackupTask extends AsyncTask<String, Void, String> {

    private static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restroeDatabase";
    private Context mContext;
    private boolean isBackup;
    private BackupInterface mInterface;
    private String backUpPath;

    public BackupTask(Context context, BackupInterface _interface, boolean isBackup) {
        this.mContext = context;
        this.isBackup = isBackup;
        mInterface = _interface;
    }

    @Override
    protected String doInBackground(String... params) {

        // 获得正在使用的数据库路径，我的是 sdcard 目录下的 /dlion/db_dlion.db
        // 默认路径是 /data/data/(包名)/databases/*.db
        File dbFile = mContext.getDatabasePath("mi.db");
//        File exportDir = new File(Environment.getExternalStorageDirectory(),
//                "NTBackup");
//        if (!exportDir.exists()) {
//            exportDir.mkdirs();
//        }
        File backup;
        String command = params[0];
        if (command.equals(COMMAND_BACKUP)) {
            try {
                File exportDir = new File(params[1]);
                backup = new File(exportDir, "mi" + DateUtils.getFileDate(System.currentTimeMillis()) + ".db");
                backup.createNewFile();
                fileCopy(dbFile, backup);
                return backup.getAbsolutePath();
            } catch (Exception e) {
                android.util.Log.d("wlDebug", "backupDatabase", e);
                e.printStackTrace();
                return null;
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                backup = new File(params[1]);
                if (!backup.exists()) {
                    String _path = params[1];
                    backup = new File(_path.replace("sdcard0", "usbotg"));
                }
                fileCopy(backup, dbFile);
                return backup.getAbsolutePath();
            } catch (Exception e) {
                android.util.Log.d("wlDebug", "restroeDatabase", e);
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private void fileCopy(File dbFile, File backup) throws IOException {
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mInterface.onPreExecute(isBackup ? "导出中." : "导入中.");
    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
        mInterface.onPostExecute(isBackup, str);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface BackupInterface {
        void onPreExecute(String tips);

        void onPostExecute(boolean isBackup, String str);
    }
}
