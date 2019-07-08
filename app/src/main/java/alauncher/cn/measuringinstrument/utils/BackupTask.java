package alauncher.cn.measuringinstrument.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BackupTask extends AsyncTask<String, Void, Integer> {
    private static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restroeDatabase";
    private Context mContext;
    private boolean isBackup;
    private BackupInterface mInterface;

    public BackupTask(Context context, BackupInterface _interface, boolean isBackup) {
        this.mContext = context;
        this.isBackup = isBackup;
        mInterface = _interface;
    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub

        // 获得正在使用的数据库路径，我的是 sdcard 目录下的 /dlion/db_dlion.db
        // 默认路径是 /data/data/(包名)/databases/*.db
        File dbFile = mContext.getDatabasePath("mi.db");
        File exportDir = new File(Environment.getExternalStorageDirectory(),
                "ETBackup");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        Log.d("backup", dbFile.getAbsolutePath());

        File backup = new File(exportDir, dbFile.getName());

        Log.d("backup", backup.getAbsolutePath());
        String command = params[0];
        if (command.equals(COMMAND_BACKUP)) {
            try {
                backup.createNewFile();
                fileCopy(dbFile, backup);
                return Log.d("backup", "ok");
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return Log.d("backup", "fail");
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                backup = new File(params[1]);
                fileCopy(backup, dbFile);
                return Log.d("restore", "success");
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return Log.d("restore", "fail");
            }
        } else {
            return null;
        }
    }

    private void fileCopy(File dbFile, File backup) throws IOException {
        // TODO Auto-generated method stub
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mInterface.onPreExecute(isBackup ? "导出中." : "导入中.");
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mInterface.onPostExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface BackupInterface {
        void onPreExecute(String tips);

        void onPostExecute();
    }
}
