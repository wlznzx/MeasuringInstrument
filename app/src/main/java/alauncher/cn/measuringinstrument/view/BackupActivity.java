package alauncher.cn.measuringinstrument.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.utils.BackupTask;
import butterknife.BindView;
import butterknife.OnClick;


public class BackupActivity extends BaseActivity implements BackupTask.BackupInterface {


    @BindView(R.id.select_path_btn)
    Button selectPathBtn;

    @BindView(R.id.in_btn)
    Button inBtn;

    @BindView(R.id.out_path_tv)
    TextView outPathTV;

    @BindView(R.id.out_btn)
    Button outBtn;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_backup);
    }

    @Override
    protected void initView() {
        // dataBackup();
        // dataRecover();
    }

    @OnClick({R.id.select_path_btn, R.id.in_btn, R.id.out_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_path_btn:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.in_btn:
                final AlertDialog builder = new AlertDialog.Builder(this)
                        .create();
                builder.show();
                if (builder.getWindow() == null) return;
                builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
                Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
                Button sure = (Button) builder.findViewById(R.id.btn_sure);
                if (msg == null || cancle == null || sure == null) return;
                msg.setText("确认导入数据？");
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataRecover();
                        builder.dismiss();
                    }
                });
                break;
            case R.id.out_btn:
                final AlertDialog outbuilder = new AlertDialog.Builder(this)
                        .create();
                outbuilder.show();
                if (outbuilder.getWindow() == null) return;
                outbuilder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                TextView _msg = (TextView) outbuilder.findViewById(R.id.tv_msg);
                Button _cancle = (Button) outbuilder.findViewById(R.id.btn_cancle);
                Button _sure = (Button) outbuilder.findViewById(R.id.btn_sure);
                if (_msg == null || _cancle == null || _sure == null) return;
                _msg.setText("确认导出数据？");
                _cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        outbuilder.dismiss();
                    }
                });
                _sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataBackup();
                        outbuilder.dismiss();
                    }
                });
                break;
        }
    }

    // 数据恢复
    private void dataRecover() {
        new BackupTask(this, this, false).execute("restroeDatabase", selectPathBtn.getText().toString().trim());
    }

    // 数据备份
    private void dataBackup() {
        new BackupTask(this, this, true).execute("backupDatabase");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String _path = getPath(this, uri);
            android.util.Log.d("wlDebug", "_path = " + _path);
            if (_path == null || _path.equals("")) _path = uri.getPath();
            android.util.Log.d("wlDebug", "_path2 = " + _path);
            selectPathBtn.setText(_path);
        }
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    @Override
    public void onPreExecute(String tips) {
        dialog = new ProgressDialog(BackupActivity.this);
        dialog.setTitle("系统备份");
        dialog.setMessage(tips);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onPostExecute(boolean isBackup, String str) {
        dialog.dismiss();
        if (str != null) {
            if (isBackup) {
                outPathTV.setText(str);
            } else {
                BackupActivity.this.finish();
                startActivity(new Intent(this, LoginActivity.class));
            }
            Toast.makeText(BackupActivity.this, "操作成功.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(BackupActivity.this, "操作失败.", Toast.LENGTH_SHORT).show();
        }
    }

}