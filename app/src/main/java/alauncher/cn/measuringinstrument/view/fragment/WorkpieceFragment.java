package alauncher.cn.measuringinstrument.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.utils.UriToPathUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WorkpieceFragment extends Fragment {

    private Unbinder unbinder;


    @BindView(R.id.workpiece_iv)
    public ImageView wIV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workpiece_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
        if (_bean != null && _bean.getWorkpiecePic() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(_bean.getWorkpiecePic(), 0, _bean.getWorkpiecePic().length, null);
            wIV.setImageBitmap(bitmap);
        }
    }

    @OnClick({R.id.add_btn, R.id.clean_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.clean_btn:
                wIV.setImageDrawable(null);
                CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
                _bean.setWorkpiecePic(null);
                App.getDaoSession().getCodeBeanDao().insertOrReplace(_bean);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String _path = UriToPathUtil.getFilePathByUri(getActivity(), uri);
            // _path = getDataColumn(this, uri, null, null);
            android.util.Log.d("wlDebug", "_path = " + _path);


            File file = new File(_path);
            if (!file.exists()) {
                return;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
//设置为true,代表加载器不加载图片,而是把图片的宽高读出来
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

            int imageWidth = opts.outWidth;
            int imageHeight = opts.outHeight;
//得到屏幕的宽高
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
//获得像素大小
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            int widthScale = imageWidth / screenWidth;
            int heightScale = imageHeight / screenHeight;
            int scale = widthScale > heightScale ? widthScale : heightScale;
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            wIV.setImageBitmap(bm);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
                    _bean.setWorkpiecePic(getBitmapByte(bm));
                    App.getDaoSession().getCodeBeanDao().insertOrReplace(_bean);
                }
            }).start();
        }
    }

    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
